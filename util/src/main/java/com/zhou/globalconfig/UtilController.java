package com.zhou.globalconfig;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhou.utils.common.model.Result;
import com.zhou.utils.common.util.ResultUtil;

@RestController
@RequestMapping("UtilController")
public class UtilController {
    
    @RequestMapping("testController")
    public Result test() {
        return ResultUtil.success("访问成功");
    }
}
