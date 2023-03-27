package com.enki.seckillt.interceptor;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.enki.seckillt.annotations.AccessLimit;
import com.enki.seckillt.entity.User;
import com.enki.seckillt.redis.AccessKey;
import com.enki.seckillt.redis.RedisService;
import com.enki.seckillt.redis.UserKey;
import com.enki.seckillt.result.CodeMsg;
import com.enki.seckillt.result.Result;
import com.enki.seckillt.util.CookieUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * 限流拦截器
 *
 * @author Enki
 * @Version 1.0
 */
@Component
public class AuthorityInterceptor implements HandlerInterceptor {
    @Autowired
    RedisService redisService;

    private final Logger logger = LoggerFactory.getLogger(AuthorityInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            //请求controller中的方法名
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //解析HandlerMethod
            String methodName = handlerMethod.getMethod().getName();
            String className = handlerMethod.getBean().getClass().getSimpleName();

            StringBuffer requestParamBuffer = new StringBuffer();
            Map paramMap = request.getParameterMap();
            Iterator it = paramMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                String mapKey = (String) entry.getKey();
                String mapValue = "";

                //request的这个参数map的value返回的是一个String[]
                Object obj = entry.getValue();
                if (obj instanceof String[]) {
                    String[] strs = (String[]) obj;
                    mapValue = Arrays.toString(strs);
                }
                requestParamBuffer.append(mapKey).append("=").append(mapValue);
            }
//查看接口方法是否有限流注解
            //接口限流  得到AccessLimit注解的实例
            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null) {
                return true;
            }
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
//            获取请求地址
            String key = request.getRequestURI();


            //对于拦截器中拦截manage下的login.do的处理,对于登录不拦截，直接放行
            if (!StringUtils.equals(className, "SeckillController")) {
                //如果是拦截到登录请求，不打印参数，因为参数里面有密码，全部会打印到日志中，防止日志泄露
                logger.info("权限拦截器拦截到请求 SeckillController ,className:{},methodName:{}", className, methodName);
                return true;
            }

            logger.info("--> 权限拦截器拦截到请求,className:{},methodName:{},param:{}", className, methodName, requestParamBuffer);
            User user = null;
            String loginToken = CookieUtil.readLoginToken(request);
            if (StringUtils.isNotEmpty(loginToken)) {
                user = redisService.get(UserKey.getByName, loginToken, User.class);
            }
//用户未登录
            if (needLogin) {
                if (user == null) {
                    render(response, CodeMsg.USER_NO_LOGIN);
                    return false;
                }
//                用户存在拼接redis中的key
                key += "_" + user.getId();
            } else {
                //do nothing
            }
            AccessKey ak = AccessKey.withExpire;
            Integer count = redisService.get(ak, key, Integer.class);
//            redis 中key存在 查看是否达到最大请求数目
            if (count == null) {
                redisService.set(ak, key, 1, seconds);
            } else if (count < maxCount) {
                redisService.incr(ak, key);
            } else {
                render(response, CodeMsg.ACCESS_LIMIT_REACHED);
                return false;
            }

        /*if (user == null) {
            //重置 重写response一定要重置 这里要添加reset，否则报异常 getWriter() has already been called for this response
            response.reset();
            //geelynote 这里要设置编码，否则会乱码
            response.setCharacterEncoding("UTF-8");
            // 这里要设置返回值类型，因为全部是json接口。
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.print(JsonUtil.obj2String(Result.error(CodeMsg.USER_NO_LOGIN)));
            //response.sendRedirect(request.getContextPath()+"/page/login");
            // 这里要关闭流
            out.flush();
            out.close();
            return false;
        }*/
        }

        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }

    private void render(HttpServletResponse response, CodeMsg cm) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        String str = JSON.toJSONString(Result.error(cm));
        out.write(str.getBytes("UTF-8"));
        out.flush();
        out.close();
    }

}
