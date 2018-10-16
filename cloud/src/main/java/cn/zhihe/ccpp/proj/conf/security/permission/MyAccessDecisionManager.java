package cn.zhihe.ccpp.proj.conf.security.permission;

import cn.zhihe.ccpp.func.cst.ExceptionConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;

/**
 * 权限管理决断器
 * 判断用户拥有的权限或角色是否有资源访问权限
 */
@Slf4j
@Component
public class MyAccessDecisionManager implements AccessDecisionManager {

    @Override
    public void decide(Authentication authentication, Object o, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {

        if(configAttributes==null){
            return;
        }
        Iterator<ConfigAttribute> iterator = configAttributes.iterator();
        while (iterator.hasNext()){
            ConfigAttribute c = iterator.next();
            String needPerm = c.getAttribute();
            for(GrantedAuthority ga : authentication.getAuthorities()) {
                // 匹配用户拥有的ga 和 系统中的needPerm
                if(needPerm.trim().equals(ga.getAuthority())) {
                    return;
                }
            }
        }
        throw new AccessDeniedException(ExceptionConstant.NO_PRIVILLEGE);
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
