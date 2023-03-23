package com.enki.seckillt.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enki.seckillt.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
/**
 * @author Enki
 * @Version 1.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    User selectByPhoneAndPassword(@Param("phone") String phone , @Param("password") String password);

    User checkPhone(@Param("phone") String phone );
}
