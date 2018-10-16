package cn.zhihe.ccpp.func.util.handler;

import cn.zhihe.ccpp.proc.tool.utils.ResultUtil;
import cn.zhihe.ccpp.proj.tool.vo.Result;
import cn.zhihe.ccpp.func.util.exception.CfppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Exrickx
 */
@Slf4j
@RestControllerAdvice
public class RestCtrlExceptionHandler {

    @ExceptionHandler(CfppException.class)
    @ResponseStatus(value = HttpStatus.OK)
    public Result<Object> handleXCloudException(CfppException e) {

        String errorMsg="Xboot exception";
        if (e!=null){
            errorMsg=e.getMsg();
            log.error(e.toString());
        }
        return new ResultUtil<>().setErrorMsg(500, errorMsg);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.OK)
    public Result<Object> handleException(Exception e) {

        String errorMsg="Exception";
        if (e!=null){
            errorMsg=e.getMessage();
            log.error(e.toString());
        }
        return new ResultUtil<>().setErrorMsg(500, errorMsg);
    }
}
