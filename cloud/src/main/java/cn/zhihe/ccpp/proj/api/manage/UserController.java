package cn.zhihe.ccpp.proj.api.manage;

import cn.zhihe.ccpp.func.cst.CommonConstant;
import cn.zhihe.ccpp.proc.tool.utils.PageUtil;
import cn.zhihe.ccpp.proc.tool.utils.ResultUtil;
import cn.zhihe.ccpp.proj.tool.vo.PageVo;
import cn.zhihe.ccpp.proj.tool.vo.Result;
import cn.zhihe.ccpp.proj.tool.vo.SearchVo;
import cn.zhihe.ccpp.proj.data.det.Department;
import cn.zhihe.ccpp.proj.data.det.Role;
import cn.zhihe.ccpp.proj.data.det.User;
import cn.zhihe.ccpp.proj.data.det.UserRole;
import cn.zhihe.ccpp.proj.task.service.def.IDepartmentService;
import cn.zhihe.ccpp.proj.task.service.def.IRoleService;
import cn.zhihe.ccpp.proj.task.service.def.IUserRoleService;
import cn.zhihe.ccpp.proj.task.service.def.IUserService;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;


/**
 * @author Exrickx
 */
@Slf4j
@RestController
@Api(description = "用户接口")
@RequestMapping("/cfpp/user")
@CacheConfig(cacheNames = "user")
@Transactional
public class UserController {

    @Autowired
    private IUserService IUserService;

    @Autowired
    private IRoleService IRoleService;

    @Autowired
    private IDepartmentService IDepartmentService;

    @Autowired
    private cn.zhihe.ccpp.proj.task.service.def.mybatis.IUserRoleService iUserRoleService;

    @Autowired
    private IUserRoleService IUserRoleService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    @RequestMapping(value = "/regist",method = RequestMethod.POST)
    @ApiOperation(value = "注册用户")
    public Result<Object> regist(@ModelAttribute User u,
                                 @RequestParam String verify,
                                 @RequestParam String captchaId){

        if(StrUtil.isBlank(verify)|| StrUtil.isBlank(u.getUsername())
                || StrUtil.isBlank(u.getPassword())){
            return new ResultUtil<Object>().setErrorMsg("缺少必需表单字段");
        }

        //验证码
        String code=redisTemplate.opsForValue().get(captchaId);
        if(StrUtil.isBlank(code)){
            return new ResultUtil<Object>().setErrorMsg("验证码已过期，请重新获取");
        }

        if(!verify.toLowerCase().equals(code.toLowerCase())) {
            log.error("注册失败，验证码错误：code:"+ verify +",redisCode:"+code.toLowerCase());
            return new ResultUtil<Object>().setErrorMsg("验证码输入错误");
        }

        if(IUserService.findByUsername(u.getUsername())!=null){
            return new ResultUtil<Object>().setErrorMsg("该用户名已被注册");
        }
        //删除缓存
        redisTemplate.delete("user::"+u.getUsername());

        String encryptPass = new BCryptPasswordEncoder().encode(u.getPassword());
        u.setPassword(encryptPass);
        u.setType(CommonConstant.USER_TYPE_NORMAL);
        User user= IUserService.save(u);
        if(user==null){
            return new ResultUtil<Object>().setErrorMsg("注册失败");
        }
        // 默认角色
        List<Role> roleList = IRoleService.findByDefaultRole(true);
        if(roleList!=null&&roleList.size()>0){
            for(Role role : roleList){
                UserRole ur = new UserRole();
                ur.setUserId(user.getId());
                ur.setRoleId(role.getId());
                iUserRoleService.insert(ur);
            }
        }

        return new ResultUtil<Object>().setData(user);
    }

    @RequestMapping(value = "/info",method = RequestMethod.GET)
    @ApiOperation(value = "获取当前登录用户接口")
    public Result<User> getUserInfo(){

        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User u = IUserService.findByUsername(user.getUsername());
        // 清除持久上下文环境 避免后面语句导致持久化
        entityManager.clear();
        u.setPassword(null);
        return new ResultUtil<User>().setData(u);
    }

    @RequestMapping(value = "/unlock",method = RequestMethod.POST)
    @ApiOperation(value = "解锁验证密码")
    public Result<Object> unLock(@RequestParam String password){

        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User u = IUserService.findByUsername(user.getUsername());
        if(!new BCryptPasswordEncoder().matches(password, u.getPassword())){
            return new ResultUtil<Object>().setErrorMsg("密码不正确");
        }
        return new ResultUtil<Object>().setData(null);
    }

    @RequestMapping(value = "/edit",method = RequestMethod.POST)
    @ApiOperation(value = "修改用户自己资料",notes = "用户名密码不会修改 需要通过id获取原用户信息 需要username更新缓存")
    @CacheEvict(key = "#u.username")
    public Result<Object> editOwn(@ModelAttribute User u){

        User old = IUserService.get(u.getId());
        u.setUsername(old.getUsername());
        u.setPassword(old.getPassword());
        User user= IUserService.update(u);
        if(user==null){
            return new ResultUtil<Object>().setErrorMsg("修改失败");
        }
        return new ResultUtil<Object>().setSuccessMsg("修改成功");
    }

    /**
     * @param u
     * @param roles
     * @return
     */
    @RequestMapping(value = "/admin/edit",method = RequestMethod.POST)
    @ApiOperation(value = "修改资料",notes = "需要通过id获取原用户信息 需要username更新缓存")
    @CacheEvict(key = "#u.username")
    public Result<Object> edit(@ModelAttribute User u,
                               @RequestParam(required = false) String[] roles){

        User old = IUserService.get(u.getId());
        //所修改了用户名
        if(!old.getUsername().equals(u.getUsername())){
            //若修改用户名删除原用户名缓存
            redisTemplate.delete("user::"+old.getUsername());
            //判断新用户名是否存在
            if(IUserService.findByUsername(u.getUsername())!=null){
                return new ResultUtil<Object>().setErrorMsg("该用户名已被存在");
            }
            //删除缓存
            redisTemplate.delete("user::"+u.getUsername());
        }

        u.setPassword(old.getPassword());
        User user= IUserService.update(u);
        if(user==null){
            return new ResultUtil<Object>().setErrorMsg("修改失败");
        }
        //删除该用户角色
        IUserRoleService.deleteByUserId(u.getId());
        if(roles!=null&&roles.length>0){
            //新角色
            for(String roleId : roles){
                UserRole ur = new UserRole();
                ur.setRoleId(roleId);
                ur.setUserId(u.getId());
                IUserRoleService.save(ur);
            }
        }
        //手动删除缓存
        redisTemplate.delete("userRole::"+u.getId());
        return new ResultUtil<Object>().setSuccessMsg("修改成功");
    }

    /**
     * 线上demo仅允许ADMIN权限改密码
     * @param id
     * @param password
     * @param newPass
     * @return
     */
    @RequestMapping(value = "/modifyPass",method = RequestMethod.POST)
    @ApiOperation(value = "修改密码")
    public Result<Object> modifyPass(@ApiParam("需用户id获取原用户数据") @RequestParam String id,
                                     @ApiParam("password") @RequestParam String password,
                                     @ApiParam("新密码") @RequestParam String newPass){

        User old = IUserService.get(id);

        if(!new BCryptPasswordEncoder().matches(password,old.getPassword())){
            return new ResultUtil<Object>().setErrorMsg("旧密码不正确");
        }

        //在线DEMO所需
        if("test".equals(old.getUsername())||"test2".equals(old.getUsername())){
            return new ResultUtil<Object>().setErrorMsg("演示账号不支持修改密码");
        }

        String newEncryptPass= new BCryptPasswordEncoder().encode(newPass);
        old.setPassword(newEncryptPass);
        User user= IUserService.update(old);
        if(user==null){
            return new ResultUtil<Object>().setErrorMsg("修改失败");
        }

        //手动更新缓存
        redisTemplate.delete("user::"+user.getUsername());

        return new ResultUtil<Object>().setData(user);
    }

    @RequestMapping(value = "/getByCondition",method = RequestMethod.GET)
    @ApiOperation(value = "多条件分页获取用户列表")
    public Result<Page<User>> getByCondition(@ModelAttribute User user,
                                             @ModelAttribute SearchVo searchVo,
                                             @ModelAttribute PageVo pageVo){

        Page<User> page = IUserService.findByCondition(user, searchVo, PageUtil.initPage(pageVo));
        for(User u: page.getContent()){
            // 关联部门
            if(StrUtil.isNotBlank(u.getDepartmentId())){
                Department department = IDepartmentService.get(u.getDepartmentId());
                u.setDepartmentTitle(department.getTitle());
            }
            // 关联角色
            List<Role> list = iUserRoleService.findByUserId(u.getId());
            u.setRoles(list);
            // 清除持久上下文环境 避免后面语句导致持久化
            entityManager.clear();
            u.setPassword(null);
        }
        return new ResultUtil<Page<User>>().setData(page);
    }

    @RequestMapping(value = "/getAll",method = RequestMethod.GET)
    @ApiOperation(value = "获取全部用户数据")
    public Result<List<User>> getByCondition(){

        List<User> list = IUserService.getAll();
        for(User u: list){
            // 关联部门
            if(StrUtil.isNotBlank(u.getDepartmentId())){
                Department department = IDepartmentService.get(u.getDepartmentId());
                u.setDepartmentTitle(department.getTitle());
            }
            // 清除持久上下文环境 避免后面语句导致持久化
            entityManager.clear();
            u.setPassword(null);
        }
        return new ResultUtil<List<User>>().setData(list);
    }

    @RequestMapping(value = "/admin/add",method = RequestMethod.POST)
    @ApiOperation(value = "添加用户")
    public Result<Object> regist(@ModelAttribute User u,
                                 @RequestParam(required = false) String[] roles){

        if(StrUtil.isBlank(u.getUsername()) || StrUtil.isBlank(u.getPassword())){
            return new ResultUtil<Object>().setErrorMsg("缺少必需表单字段");
        }

        if(IUserService.findByUsername(u.getUsername())!=null){
            return new ResultUtil<Object>().setErrorMsg("该用户名已被注册");
        }
        //删除缓存
        redisTemplate.delete("user::"+u.getUsername());

        String encryptPass = new BCryptPasswordEncoder().encode(u.getPassword());
        u.setPassword(encryptPass);
        User user= IUserService.save(u);
        if(user==null){
            return new ResultUtil<Object>().setErrorMsg("添加失败");
        }
        if(roles!=null&&roles.length>0){
            //添加角色
            for(String roleId : roles){
                UserRole ur = new UserRole();
                ur.setUserId(u.getId());
                ur.setRoleId(roleId);
                IUserRoleService.save(ur);
            }
        }

        return new ResultUtil<Object>().setData(user);
    }

    @RequestMapping(value = "/admin/disable/{userId}",method = RequestMethod.POST)
    @ApiOperation(value = "后台禁用用户")
    public Result<Object> disable(@ApiParam("用户唯一id标识") @PathVariable String userId){

        User user= IUserService.get(userId);
        if(user==null){
            return new ResultUtil<Object>().setErrorMsg("通过userId获取用户失败");
        }
        user.setStatus(CommonConstant.USER_STATUS_LOCK);
        IUserService.update(user);
        //手动更新缓存
        redisTemplate.delete("user::"+user.getUsername());
        return new ResultUtil<Object>().setData(null);
    }

    @RequestMapping(value = "/admin/enable/{userId}",method = RequestMethod.POST)
    @ApiOperation(value = "后台启用用户")
    public Result<Object> enable(@ApiParam("用户唯一id标识") @PathVariable String userId){

        User user= IUserService.get(userId);
        if(user==null){
            return new ResultUtil<Object>().setErrorMsg("通过userId获取用户失败");
        }
        user.setStatus(CommonConstant.USER_STATUS_NORMAL);
        IUserService.update(user);
        //手动更新缓存
        redisTemplate.delete("user::"+user.getUsername());
        return new ResultUtil<Object>().setData(null);
    }

    @RequestMapping(value = "/delByIds/{ids}",method = RequestMethod.DELETE)
    @ApiOperation(value = "批量通过ids删除")
    public Result<Object> delAllByIds(@PathVariable String[] ids){

        for(String id:ids){
            User u = IUserService.get(id);
            //删除缓存
            redisTemplate.delete("user::"+u.getUsername());
            IUserService.delete(id);
            //删除关联角色
            IUserRoleService.deleteByUserId(id);
        }
        return new ResultUtil<Object>().setSuccessMsg("批量通过id删除数据成功");
    }

}
