package cn.zhihe.ccpp.proj.task.interceptor;

import cn.zhihe.ccpp.func.cst.ExceptionConstant;
import cn.zhihe.ccpp.func.util.exception.CfppException;
import cn.zhihe.ccpp.func.util.annotation.RateLimiter;
import cn.zhihe.ccpp.func.cst.CommonConstant;
import cn.zhihe.ccpp.func.util.log.LOG;
import cn.zhihe.ccpp.proj.tool.limit.RedisRaterLimiter;
import cn.zhihe.ccpp.proj.tool.utils.IpInfoUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 限流拦截器
 * @author Exrickx
 */
@Slf4j
@Component
public class LimitRaterInterceptor extends HandlerInterceptorAdapter {

    @Value("${cfpp.rateLimit.enable}")
    private boolean rateLimitEnable;

    @Value("${cfpp.rateLimit.limit}")
    private Integer limit;

    @Value("${cfpp.rateLimit.timeout}")
    private Integer timeout;

    @Autowired
    private RedisRaterLimiter redisRaterLimiter;

    @Autowired
    private IpInfoUtil ipInfoUtil;

    /**
     * 预处理回调方法，实现处理器的预处理（如登录检查）
     * 第三个参数为响应的处理器，即controller
     * 返回true，表示继续流程，调用下一个拦截器或者处理器
     * 返回false，表示流程中断，通过response产生响应
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

        LOG.D("--->请求控制URL:" + request.getMethod() + " : " + request.getRequestURI());

//        response.setHeader("Access-Control-Allow-Headers", "Content-Type,Content-Length, Authorization,accessToken, Accept,X-Requested-With");
//        response.setHeader("Access-Control-Allow-Methods","PUT,POST,GET,DELETE,OPTIONS");
//        response.setHeader("Access-Control-Allow-Origin", "*");

        if (request.getMethod().equals("OPTIONS")){

            response.setStatus(200);
            return false;
        }

        // IP限流 在线Demo所需 一秒限5个请求
        String token1 = redisRaterLimiter.acquireTokenFromBucket(ipInfoUtil.getIpAddr(request), 5, 1000);
        if (StrUtil.isBlank(token1)) {
            throw new CfppException(ExceptionConstant.CLICK_TOO_QUICKLY);
        }

        if(rateLimitEnable){
            String token2 = redisRaterLimiter.acquireTokenFromBucket(CommonConstant.LIMIT_ALL, limit, timeout);
            if (StrUtil.isBlank(token2)) {
                throw new CfppException(ExceptionConstant.ACCESS_TOO_MANY);
            }
        }


        if (handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            LOG.I("请求：" + handlerMethod.getMethod().getName() + " " + handlerMethod.getBean().getClass().getName());

            Method method = handlerMethod.getMethod();
            RateLimiter rateLimiter = method.getAnnotation(RateLimiter.class);

            if (rateLimiter != null) {
                int limit = rateLimiter.limit();
                int timeout = rateLimiter.timeout();
                String token3 = redisRaterLimiter.acquireTokenFromBucket(method.getName(), limit, timeout);
                if (StrUtil.isBlank(token3)) {
                    throw new CfppException(ExceptionConstant.ACCESS_TOO_MANY_2);
                }
            }
        }
        else{
            LOG.W("请求控制器类：" + handler.getClass());
        }


        return true;
    }

    /**
     * 当前请求进行处理之后，也就是Controller方法调用之后执行，
     * 但是它会在DispatcherServlet 进行视图返回渲染之前被调用。
     * 此时我们可以通过modelAndView对模型数据进行处理或对视图进行处理。
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 方法将在整个请求结束之后，也就是在DispatcherServlet渲染了对应的视图之后执行。
     * 这个方法的主要作用是用于进行资源清理工作的。
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
    }

}
