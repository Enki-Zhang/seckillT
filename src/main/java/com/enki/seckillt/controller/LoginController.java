package com.enki.seckillt.controller;

import cn.hutool.core.lang.UUID;
import com.enki.seckillt.common.Const;
import com.enki.seckillt.entity.User;
import com.enki.seckillt.param.LoginParam;
import com.enki.seckillt.redis.RedisService;
import com.enki.seckillt.redis.UserKey;
import com.enki.seckillt.result.CodeMsg;
import com.enki.seckillt.result.Result;
import com.enki.seckillt.service.UserService;
import com.enki.seckillt.util.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 登录
 *
 * @author Enki
 * @Version 1.0
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class LoginController {

    @Resource
    private RedisService redisService;
    @Resource
    private UserService userService;

    /**
     * 用户登录
     *
     * @param response
     * @param session
     * @param loginParam 登录表单信息
     * @return
     */
    @PostMapping("/login")
    public Result<User> doLogin(HttpServletResponse response,HttpServletRequest request, HttpSession session, LoginParam loginParam) {
        Result<User> login = userService.login(loginParam);
//        登录成功创建sessionid存入cookie中
        if (login.isSuccess()) {
            String token = UUID.randomUUID().toString(true);
//            写入token
            session.setAttribute("login",token);
            log.info("token保存{}",session.getAttribute("login"));
//            request.getSession().setAttribute("login",token);
//            CookieUtil.writeLoginToken(response, session.getId());
//            redis保存用户session value为用户登录信息
//            redisService.set(UserKey.getByName, session.getId(), login.getData(), Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
            redisService.set(UserKey.getByName, token, login.getData(), Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        return login;
    }

    /**
     * 登录设置
     * @param request
     * @param loginParam
     * @return
     */
    @PostMapping("/login2")
    public Result<User> login(HttpServletRequest request, LoginParam loginParam) {
       return userService.login2(request,loginParam);

    }

    @RequestMapping("/logout")
    public String doLogout(HttpServletRequest request, HttpServletResponse response) {
        String token = CookieUtil.readLoginToken(request);
        CookieUtil.delLoginToken(request, response);
        redisService.del(UserKey.getByName, token);
        return "login";
    }
}
