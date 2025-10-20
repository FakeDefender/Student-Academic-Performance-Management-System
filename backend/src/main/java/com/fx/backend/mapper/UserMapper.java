package com.fx.backend.mapper;

import com.fx.backend.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;
public interface UserMapper {
    int insert(User user);
    Optional<User> findById(@Param("id") Long id);
    Optional<User> findByUsername(@Param("username") String username);
}


