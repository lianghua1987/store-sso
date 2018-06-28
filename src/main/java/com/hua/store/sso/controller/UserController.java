package com.hua.store.sso.controller;

import com.hua.store.common.pojo.Result;
import com.hua.store.common.utils.ExceptionUtil;
import com.hua.store.pojo.User;
import com.hua.store.sso.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService service;


    @RequestMapping("/check/{param}/{type}")
    @ResponseBody
    public Object check(@PathVariable("param") String content, @PathVariable Integer type, String callback) {

        Result result = null;

        if (StringUtils.isBlank(content)) {
            result = Result.build(400, "校验内容不能为空。");
        }

        if (type == null) {
            result = Result.build(400, "校验类型不能为空。");
        }

        if (type != 1 && type != 2 && type != 3) {
            result = Result.build(400, "校验类型错误。");
        }

        if (result != null) {
            return callback(result, callback);
        }

        try {
            result = service.check(content, type);
        } catch (Exception e) {
            result = Result.build(500, ExceptionUtil.getStackTrace(e));
        }

        return callback(result, callback);

    }

    private Object callback(Result result, String callback) {
        if (callback != null) {
            MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
            mappingJacksonValue.setJsonpFunction(callback);
            return mappingJacksonValue;
        } else {
            return result;
        }
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public Result register(User user) {

        try {
            return service.register(user);
        } catch (Exception e) {
            return Result.build(500, ExceptionUtil.getStackTrace(e));
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Result register(String username, String password) {

        try {
            return service.login(username, password);
        } catch (Exception e) {
            return Result.build(500, ExceptionUtil.getStackTrace(e));
        }
    }

    @RequestMapping(value = "/token/{token}")
    @ResponseBody
    public Object validate(@PathVariable String token, String callback) {

        Result result = null;

        try {
            result = service.validate(token);
        } catch (Exception e) {
            result = Result.build(500, ExceptionUtil.getStackTrace(e));
        }

        return callback(result, callback);
    }

    @RequestMapping(value = "/logout/{token}")
    @ResponseBody
    public Object logout(@PathVariable String token, String callback) {

        Result result = null;

        try {
            result = service.logout(token);
        } catch (Exception e) {
            result = Result.build(500, ExceptionUtil.getStackTrace(e));
        }

        return callback(result, callback);
    }
}
