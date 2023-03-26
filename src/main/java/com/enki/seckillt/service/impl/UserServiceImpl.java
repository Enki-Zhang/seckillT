package com.enki.seckillt.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enki.seckillt.entity.User;
import com.enki.seckillt.mapper.UserMapper;
import com.enki.seckillt.param.LoginParam;
import com.enki.seckillt.redis.UserKey;
import com.enki.seckillt.result.CodeMsg;
import com.enki.seckillt.result.Result;
import com.enki.seckillt.service.UserService;
import com.enki.seckillt.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Enki
 * @Version 1.0
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 登录验证 登录成功的用户保存在redis中
     *
     * @param loginParam
     * @return
     */
    @Override
    public Result<User> login(LoginParam loginParam) {
        //        校验手机号码
        String phone = loginParam.getMobile();
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(phone);
        log.info("登录参数{}", entries);
//        if (entries != null) {
//            return Result.success(user);
//        }
        User user = query().eq("phone", phone).one();
        if (user == null) {
            return Result.error(CodeMsg.MOBILE_NOT_EXIST);
        }
        String dbPwd = user.getPassword();
//固定盐值
        String saltDB = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(loginParam.getPassword(), saltDB);
        if (!StringUtils.equals(dbPwd, calcPass)) {
            return Result.error(CodeMsg.PASSWORD_ERROR);
        }

//       将user对象存贮为LoginParm
        Map<String, Object> map = BeanUtil.beanToMap(loginParam, new HashMap<>(), CopyOptions.create().setIgnoreNullValue(true)
                .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));
        stringRedisTemplate.opsForHash().putAll(phone, map);
        stringRedisTemplate.expire(phone, 30L, TimeUnit.MINUTES);
        user.setPassword(StringUtils.EMPTY);
        return Result.success(user);
    }

    /**
     * 登录
     * @param request
     * @param loginParam
     * @return
     */
    @Override
    public Result<User> login2(HttpServletRequest request, LoginParam loginParam) {
        String mobile = loginParam.getMobile();
        User user = query().eq("phone", mobile).one();
        String salt = user.getSalt();
//        String calcPass = MD5Util.formPassToDBPass(loginParam.getPassword(), saltDB);
        String finPW = MD5Util.formPassToDBPass(loginParam.getPassword(), salt);
        if (!StrUtil.equals(finPW,loginParam.getPassword())){
            return Result.error(CodeMsg.PASSWORD_EMPTY);
        }
//        保存用户
//        String userID = UUID.randomUUID().toString(true);
//        String token =


        return null;
    }
}
