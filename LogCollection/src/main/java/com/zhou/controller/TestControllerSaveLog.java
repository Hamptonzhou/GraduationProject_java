package com.zhou.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhou.utils.common.model.Result;
import com.zhou.utils.common.util.ResultUtil;

/**
 * 测试使用AOP收集日志
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年1月15日
 * @Version:1.1.0
 */
@RestController
@RequestMapping(value = "TestControllerSaveLog")
public class TestControllerSaveLog {
    
    @SuppressWarnings("unused")
    @RequestMapping(value = "testController")
    public Result testController(String string, int i, String[] stringArray) {
        String a = "a";
        String b = "b";
        String c = "b";
        return ResultUtil.success();
    }
}
