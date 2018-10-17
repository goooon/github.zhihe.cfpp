package cn.zhihe.ccpp.proj.task.handler;

import cn.zhihe.ccpp.func.cst.ResponseString;
import cn.zhihe.ccpp.func.util.exception.LoginFailLimitException;
import cn.zhihe.ccpp.func.util.log.LOG;
import cn.zhihe.ccpp.proc.tool.utils.ResponseUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Exrickx
 */
@Component
public class AuthenticationFailHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${cfpp.loginTimeLimit}")
    private Integer loginTimeLimit;

    @Value("${cfpp.loginAfterTime}")
    private Integer loginAfterTime;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {

        if (e instanceof UsernameNotFoundException || e instanceof BadCredentialsException) {
            String username = request.getParameter("username");
            recordLoginTime(username);
            String key = "loginTimeLimit:"+username;
            String value = redisTemplate.opsForValue().get(key);
            if(StrUtil.isBlank(value)){
                value = "0";
            }
            //获取已登录错误次数
            int loginFailTime = Integer.parseInt(value);
            int restLoginTime = loginTimeLimit - loginFailTime;
            LOG.I("user:'" + username + "' login failed，and has " + restLoginTime + " chance");
            if(restLoginTime<=3&&restLoginTime>0){
                ResponseUtil.out(response, ResponseUtil.resultMap(false,500,String.format(ResponseString.MAX_USERNAME_OR_PASSWORD,restLoginTime)));
            } else if(restLoginTime<=0) {
                ResponseUtil.out(response, ResponseUtil.resultMap(false,500, String.format(ResponseString.MAX_ERROR_REACHED,loginAfterTime)));
            } else {
                ResponseUtil.out(response, ResponseUtil.resultMap(false,500, ResponseString.USERNAME_OR_PASSWORD_ERROR));
            }
        } else if (e instanceof DisabledException) {
            ResponseUtil.out(response, ResponseUtil.resultMap(false,500,ResponseString.ACCOUNT_FORBIDDEN));
        } else if (e instanceof LoginFailLimitException){
            ResponseUtil.out(response, ResponseUtil.resultMap(false,500,((LoginFailLimitException) e).getMsg()));
        } else {
            ResponseUtil.out(response, ResponseUtil.resultMap(false,500,ResponseString.OTHER_ONTERNAL_ERROR));
        }
    }

    /**
     * 判断用户登陆错误次数
     */
    public boolean recordLoginTime(String username){

        String key = "loginTimeLimit:"+username;
        String flagKey = "loginFailFlag:"+username;
        String value = redisTemplate.opsForValue().get(key);
        if(StrUtil.isBlank(value)){
            value = "0";
        }
        //获取已登录错误次数
        int loginFailTime = Integer.parseInt(value) + 1;
        redisTemplate.opsForValue().set(key, String.valueOf(loginFailTime), loginAfterTime, TimeUnit.MINUTES);
        if(loginFailTime>=loginTimeLimit){
            redisTemplate.opsForValue().set(flagKey, "fail", loginAfterTime, TimeUnit.MINUTES);
            return false;
        }
        return true;
    }
}
