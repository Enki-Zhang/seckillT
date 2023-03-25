package com.enki.seckillt.controller;

import com.enki.seckillt.common.Const;
import com.enki.seckillt.entity.User;
import com.enki.seckillt.param.LoginParam;
import com.enki.seckillt.redis.RedisService;
import com.enki.seckillt.redis.UserKey;
import com.enki.seckillt.result.Result;
import com.enki.seckillt.service.UserService;
import com.enki.seckillt.util.CookieUtil;
import lombok.extern.slf4j.Slf4j;
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
     * @param loginParam
     * @return
     */
    @RequestMapping("/login")
    public Result<User> doLogin(HttpServletResponse response, HttpSession session, LoginParam loginParam) {
        Result<User> login = userService.login(loginParam);
        if (login.isSuccess()) {
            CookieUtil.writeLoginToken(response, session.getId());
//            保存用户信息
            redisService.set(UserKey.getByName, session.getId(), login.getData(), Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        return login;
    }

    @RequestMapping("/logout")
    public String doLogout(HttpServletRequest request, HttpServletResponse response) {
        String token = CookieUtil.readLoginToken(request);
        CookieUtil.delLoginToken(request, response);
        redisService.del(UserKey.getByName, token);
        return "login";
    }
}
