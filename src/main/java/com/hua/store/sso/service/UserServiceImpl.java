package com.hua.store.sso.service;

import com.hua.store.common.pojo.Result;
import com.hua.store.common.utils.JsonUtils;
import com.hua.store.mapper.UserMapper;
import com.hua.store.pojo.User;
import com.hua.store.pojo.UserExample;
import com.hua.store.sso.dao.JedisClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper mapper;

    @Autowired
    private JedisClient client;

    @Value("${REDIS_USER_SESSION_KEY}")
    private String REDIS_USER_SESSION_KEY;

    @Value("${SSO_SESSION_EXPIRE}")
    private Integer SSO_SESSION_EXPIRE;

    @Override
    public Result check(String content, Integer type) {

        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();

        switch (type) {
            case 1:
                criteria.andUsernameEqualTo(content);
                break;
            case 2:
                criteria.andPhoneEqualTo(content);
                break;
            case 3:
                criteria.andEmailEqualTo(content);
                break;
        }

        List<User> users = mapper.selectByExample(example);
        if (users == null || users.isEmpty()) {
            return Result.OK(true);
        }
        return Result.OK(false);
    }

    @Override
    public Result register(User user) {
        user.setCreated(new Date());
        user.setUpdated(new Date());
        user.setPassword(md5(user.getPassword()));
        mapper.insert(user);
        return Result.OK();
    }

    @Override
    public Result login(String username, String password) {
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(username);
        criteria.andPasswordEqualTo(md5(password));
        List<User> users = mapper.selectByExample(example);

        if (users == null || users.isEmpty()) {
            return Result.build(400, "用户名或密码错误！");
        }

        User user = users.get(0);
        user.setPassword(null);
        String token = UUID.randomUUID().toString().replace("-", "");
        System.out.println(token);
        // 如果写入缓存失败，表示登录失败，回滚
        client.set(REDIS_USER_SESSION_KEY + ":" + token, JsonUtils.objectToJson(user));
        client.expire(REDIS_USER_SESSION_KEY + ":" + token, SSO_SESSION_EXPIRE);
        return Result.OK(token);
    }

    private String md5(String str) {
        return DigestUtils.md5DigestAsHex(str.getBytes());
    }

    @Override
    public Result validate(String token) {
        String json = client.get(REDIS_USER_SESSION_KEY + ":" + token);

        if (StringUtils.isBlank(json)) {
            return Result.build(400, "此session已经过期，请重新登录");
        }
        
        User user = JsonUtils.jsonToPojo(json, User.class);

        client.expire(REDIS_USER_SESSION_KEY + ":" + token, SSO_SESSION_EXPIRE);
        return Result.OK(user);
    }

    @Override
    public Result logout(String token) {
        String json = client.get(REDIS_USER_SESSION_KEY + ":" + token);

        if (StringUtils.isBlank(json)) {
            return Result.build(400, "此session已经过期，请重新登录");
        }
        client.del(REDIS_USER_SESSION_KEY + ":" + token);
        return Result.OK();
    }

}
