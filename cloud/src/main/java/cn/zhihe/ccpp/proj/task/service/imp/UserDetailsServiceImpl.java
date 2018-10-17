package cn.zhihe.ccpp.proj.task.service.imp;

import cn.zhihe.ccpp.func.cst.ExceptionConstant;
import cn.zhihe.ccpp.func.util.log.LOG;
import cn.zhihe.ccpp.proj.conf.security.permission.MySecurityUserDetails;
import cn.zhihe.ccpp.proj.data.det.User;
import cn.zhihe.ccpp.func.util.exception.LoginFailLimitException;
import cn.zhihe.ccpp.proj.task.service.def.IUserService;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author Exrickx
 */
@Slf4j
@Component
public class UserDetailsServiceImpl implements UserDetailsService{

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private IUserService IUserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        String flagKey = "loginFailFlag:"+username;
        String value = redisTemplate.opsForValue().get(flagKey);
        Long timeRest = redisTemplate.getExpire(flagKey, TimeUnit.MINUTES);
        if(StrUtil.isNotBlank(value)){
            //超过限制次数
            throw new LoginFailLimitException(String.format(ExceptionConstant.MAX_ACCESS_REACHED_,timeRest));
        }
        User user = IUserService.findByUsername(username);
        if(user == null){
            LOG.W("can not get username : " + username);
        }
        return new MySecurityUserDetails(user);
    }
}
