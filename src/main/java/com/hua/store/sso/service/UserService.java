package com.hua.store.sso.service;

import com.hua.store.common.pojo.Result;
import com.hua.store.pojo.User;

public interface UserService {

    public Result check(String content, Integer type);

    public Result register(User user);

    public Result login(String username, String password);

    public Result validate(String token);

    public Result logout(String token);

}
