package cn.zhihe.ccpp.proj.api.manage;

import cn.zhihe.ccpp.proc.tool.utils.PageUtil;
import cn.zhihe.ccpp.proc.tool.utils.ResultUtil;
import cn.zhihe.ccpp.proj.tool.vo.PageVo;
import cn.zhihe.ccpp.proj.tool.vo.Result;
import cn.zhihe.ccpp.proj.tool.vo.SearchVo;
import cn.zhihe.ccpp.proj.data.det.Log;
import cn.zhihe.ccpp.proj.task.service.def.ILogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


/**
 * @author Exrickx
 */
@Slf4j
@RestController
@Api(description = "日志管理接口")
@RequestMapping("/cfpp/log")
@Transactional
public class LogController{

    @Value("${cfpp.logRecord.es}")
    private Boolean esRecord;


    @Autowired
    private ILogService ILogService;

    @RequestMapping(value = "/getAllByPage",method = RequestMethod.GET)
    @ApiOperation(value = "分页获取全部")
    public Result<Object> getAllByPage(@ModelAttribute PageVo pageVo){

        if(esRecord){
            Page<Log> log = ILogService.findAll(PageUtil.initPage(pageVo));
            return new ResultUtil<Object>().setData(log);
        }else{
            Page<Log> log = ILogService.findAll(PageUtil.initPage(pageVo));
            return new ResultUtil<Object>().setData(log);
        }
    }

    @RequestMapping(value = "/search",method = RequestMethod.GET)
    @ApiOperation(value = "分页搜索")
    public Result<Object> search(@RequestParam String key,
                                 @ModelAttribute SearchVo searchVo,
                                 @ModelAttribute PageVo pageVo){

        if(esRecord){
            Page<Log> log = ILogService.searchLog(key, searchVo, PageUtil.initPage(pageVo));
            return new ResultUtil<Object>().setData(log);
        }else{
            Page<Log> log = ILogService.searchLog(key, searchVo, PageUtil.initPage(pageVo));
            return new ResultUtil<Object>().setData(log);
        }
    }

    @RequestMapping(value = "/delByIds/{ids}",method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除")
    public Result<Object> delByIds(@PathVariable String[] ids){

        for(String id : ids){
            if(esRecord){
                //esLogService.deleteLog(id);
            }else{
                ILogService.delete(id);
            }
        }
        return new ResultUtil<Object>().setSuccessMsg("删除成功");
    }

    @RequestMapping(value = "/delAll",method = RequestMethod.DELETE)
    @ApiOperation(value = "全部删除")
    public Result<Object> delAll(){

        if(esRecord){
            //esLogService.deleteAll();
        }else{
            ILogService.deleteAll();
        }
        return new ResultUtil<Object>().setSuccessMsg("删除成功");
    }
}
