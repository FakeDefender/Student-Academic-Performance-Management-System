package com.fx.backend.common.jwt;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RoleRequired {
    String[] value(); // 允许的角色列表，如 {"ADMIN","TEACHER"}
}


