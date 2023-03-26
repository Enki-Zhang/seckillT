package com.enki.seckillt.util;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Enki
 * @Version 1.0
 */
@Slf4j
public class CookieUtil {
    private final static String COOKIE_DOMAIN = "localhost";
    private final static String COOKIE_NAME = "seckill_login_token";

    /**
     * 获取session中的token
     *
     * @param request
     * @return
     */
    public static String readLoginToken(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String loginToken = (String) session.getAttribute("loginToken");
//        log.info("session{}", loginToken);
        if (!StrUtil.isBlank(loginToken)) {
            return loginToken;
        }
        return null;
    }

    //X:domain=".hfbin.cn"
    //a:A.hfbin.cn            cookie:domain=A.hfbin.cn;path="/"
    //b:B.hfbin.cn            cookie:domain=B.hfbin.cn;path="/"
    //c:A.hfbin.cn/test/cc    cookie:domain=A.hfbin.cn;path="/test/cc"
    //d:A.hfbin.cn/test/dd    cookie:domain=A.hfbin.cn;path="/test/dd"
    //e:A.hfbin.cn/test       cookie:domain=A.hfbin.cn;path="/test"

    /**
     * 保存登录sessionID
     *
     * @param response
     * @param token
     */
//    public static void writeLoginToken(HttpServletResponse response, String token) {
//
//        String token = UUID.randomUUID().toString(true);
////            写入token到session中
//        session.setAttribute("loginToken", token);
//
//        Cookie ck = new Cookie(COOKIE_NAME, token);
//        ck.setDomain(COOKIE_DOMAIN);
//        ck.setPath("/");//代表设置在根目录
//        ck.setHttpOnly(true);
//        //单位是秒。
//        //如果这个maxage不设置的话，cookie就不会写入硬盘，而是写在内存。只在当前页面有效。
//        ck.setMaxAge(60 * 60 * 24 * 365);//如果是-1，代表永久
//        response.addCookie(ck);
//    }

    /**
     * 向session写入token
     *
     * @param session
     */
    public static void writeLoginToken(HttpSession session) {
        String token = UUID.randomUUID().toString(true);
//            写入token到session中
        session.setAttribute("loginToken", token);
    }


    public static void delLoginToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cks = request.getCookies();
        if (cks != null) {
            for (Cookie ck : cks) {
                if (StringUtils.equals(ck.getName(), COOKIE_NAME)) {
                    ck.setDomain(COOKIE_DOMAIN);
                    ck.setPath("/");
                    ck.setMaxAge(0);//设置成0，代表删除此cookie。
                    // log.info("del cookieName:{},cookieValue:{}",ck.getName(),ck.getValue());
                    response.addCookie(ck);
                    return;
                }
            }
        }
    }
}
