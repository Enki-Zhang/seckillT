package com.enki.seckillt.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.enki.seckillt.entity.User;
import com.enki.seckillt.param.LoginParam;
import com.enki.seckillt.result.Result;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Enki
 * @Version 1.0
 */
public interface UserService extends IService<User> {
    Result<User> login(LoginParam loginParam);

    Result<User> login2(HttpServletRequest request, LoginParam loginParam);

}
