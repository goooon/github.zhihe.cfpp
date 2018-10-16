package cn.zhihe.ccpp.proj.api.manage;

import cn.zhihe.ccpp.func.cst.CommonConstant;
import cn.zhihe.ccpp.proc.tool.utils.ResultUtil;
import cn.zhihe.ccpp.proj.tool.vo.Result;
import cn.zhihe.ccpp.proj.data.det.Department;
import cn.zhihe.ccpp.proj.data.det.User;
import cn.zhihe.ccpp.proj.task.service.def.IDepartmentService;
import cn.zhihe.ccpp.proj.task.service.def.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


/**
 * @author Exrick
 */
@Slf4j
@RestController
@Api(description = "部门管理接口")
@RequestMapping("/cfpp/department")
@CacheConfig(cacheNames = "department")
@Transactional
public class DepartmentController {

    @Autowired
    private IDepartmentService IDepartmentService;

    @Autowired
    private IUserService IUserService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RequestMapping(value = "/getByParentId/{parentId}",method = RequestMethod.GET)
    @ApiOperation(value = "通过id获取")
    @Cacheable(key = "#parentId")
    public Result<List<Department>> getByParentId(@PathVariable String parentId){

        List<Department> list = IDepartmentService.findByParentIdOrderBySortOrder(parentId);
        // lambda表达式
        list.forEach(item -> {
            if(!CommonConstant.PARENT_ID.equals(item.getParentId())){
                Department parent = IDepartmentService.get(item.getParentId());
                item.setParentTitle(parent.getTitle());
            }else{
                item.setParentTitle("一级部门");
            }
        });
        return new ResultUtil<List<Department>>().setData(list);
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ApiOperation(value = "添加")
    @CacheEvict(key = "#department.parentId")
    public Result<Department> add(@ModelAttribute Department department){

        Department d = IDepartmentService.save(department);
        // 如果不是添加的一级 判断设置上级为父节点标识
        if(!CommonConstant.PARENT_ID.equals(department.getParentId())){
            Department parent = IDepartmentService.get(department.getParentId());
            if(parent.getIsParent()==null||!parent.getIsParent()){
                parent.setIsParent(true);
                IDepartmentService.update(parent);
                // 更新上级节点的缓存
                redisTemplate.delete("department::" + parent.getParentId());
            }
        }
        return new ResultUtil<Department>().setData(d);
    }

    @RequestMapping(value = "/edit",method = RequestMethod.POST)
    @ApiOperation(value = "编辑")
    public Result<Department> edit(@ModelAttribute Department department){

        Department d = IDepartmentService.update(department);
        // 手动删除所有部门缓存
        Set<String> keys = redisTemplate.keys("department:" + "*");
        redisTemplate.delete(keys);
        // 删除所有用户缓存
        Set<String> keysUser = redisTemplate.keys("user:" + "*");
        redisTemplate.delete(keysUser);
        return new ResultUtil<Department>().setData(d);
    }

    @RequestMapping(value = "/delByIds/{ids}",method = RequestMethod.DELETE)
    @ApiOperation(value = "批量通过id删除")
    public Result<Object> delByIds(@PathVariable String[] ids){

        for(String id:ids){
            List<User> list = IUserService.findByDepartmentId(id);
            if(list!=null&&list.size()>0){
                return new ResultUtil<Object>().setErrorMsg("删除失败，包含正被用户使用关联的部门");
            }
        }
        for(String id:ids){
            IDepartmentService.delete(id);
        }
        // 手动删除所有部门缓存
        Set<String> keys = redisTemplate.keys("department:" + "*");
        redisTemplate.delete(keys);
        return new ResultUtil<Object>().setSuccessMsg("批量通过id删除数据成功");
    }

}
