
package com.zhou.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zhou.entity.UserInfo;
import com.zhou.service.IUserService;
import com.zhou.utils.CheckUtil;
import com.zhou.utils.common.model.Result;
import com.zhou.utils.common.util.ResultUtil;

/**
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年2月18日
 * @Version:1.1.0
 */
@RestController
@RequestMapping(value = "/LoginController")
public class LoginController {
    
    @Autowired
    private IUserService userService;
    
    /**
     * 存放在seesion的userId名称
     */
    private final static String USERID_IN_SESSION_NAME = "userId";
    
    private final static String USERINFO_IN_SESSION_NAME = "userInfo";
    
    /**
     * 登录
     * 
     * @param model
     * @param loginName
     * @param password
     * @param request
     * @param response
     * @return
     * @Description:
     */
    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    public Result login(@RequestParam(name = "loginName") String loginName, @RequestParam String password,
        HttpServletRequest request, HttpServletResponse response) {
        boolean isExist = userService.checkLoginNameExist(loginName);
        if (!isExist) {
            return ResultUtil.fail("该登陆名不存在");
        }
        // 用户名不能包含特殊符
        if (loginName.contains(">") || loginName.contains("<") || loginName.contains("--")) {
            return ResultUtil.fail("用户名异常:");
        }
        if (password == null) {
            return ResultUtil.fail("密码不能为空");
        }
        UserInfo userInfo = userService.getUserInfoByLoginName(loginName);
        
        if (!password.equals(userInfo.getUserPassword())) {
            return ResultUtil.fail("密码错误");
        }
        //把用户id和用户的所有信息保存在session中
        HttpSession session = request.getSession(true);
        session.setAttribute(USERID_IN_SESSION_NAME, userInfo.getUserId());
        session.setAttribute(USERINFO_IN_SESSION_NAME, userInfo);
        session.setMaxInactiveInterval(1800);
        //把用户id和用户实体信息返回给前端
        Map<String, Object> userInfoAndUserIdMap = new HashMap<String, Object>(3);
        userInfoAndUserIdMap.put(USERID_IN_SESSION_NAME, userInfo.getUserId());
        userInfoAndUserIdMap.put(USERINFO_IN_SESSION_NAME, userInfo);
        return ResultUtil.success(userInfoAndUserIdMap);
    }
    
    /**
     * 注销
     * 
     * @param request
     * @return
     * @Description:
     */
    @RequestMapping("/logout")
    public Result logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(USERID_IN_SESSION_NAME);
            session.removeAttribute(USERINFO_IN_SESSION_NAME);
        }
        return ResultUtil.success();
    }
    
    /**
     * 检查登录
     * 
     * @param request
     * @return
     * @Description: 全部功能完成之后，可以移动到service层，提供一个接口，然后使用AOP，每一个接口的调用都先检查是否登陆
     */
    @RequestMapping("/checkLogin")
    public Result checkLogin(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String userId = (String)session.getAttribute(USERID_IN_SESSION_NAME);
        if (CheckUtil.isNullorEmpty(userId)) {
            return ResultUtil.fail("登陆失效，请重新登陆");
        }
        return ResultUtil.success();
    }
}
