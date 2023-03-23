package com.enki.seckillt.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enki.seckillt.entity.User;
import com.enki.seckillt.mapper.UserMapper;
import com.enki.seckillt.param.LoginParam;
import com.enki.seckillt.result.CodeMsg;
import com.enki.seckillt.result.Result;
import com.enki.seckillt.service.UserService;
import com.enki.seckillt.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author Enki
 * @Version 1.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {
    /**
     * 登录验证 session共享
     * @param loginParam
     * @return
     */
    @Override
    public Result<User> login(LoginParam loginParam) {
        //        校验手机号码
        String phone = loginParam.getMobile();
        User user = query().eq("phone", phone).one();
        if(user == null){
            return Result.error(CodeMsg.MOBILE_NOT_EXIST);
        }
        String dbPwd= user.getPassword();
        String saltDB = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(loginParam.getPassword(), saltDB);
        if(!StringUtils.equals(dbPwd , calcPass)){
            return Result.error(CodeMsg.PASSWORD_ERROR);
        }
        user.setPassword(StringUtils.EMPTY);
        return Result.success(user);
    }
}
