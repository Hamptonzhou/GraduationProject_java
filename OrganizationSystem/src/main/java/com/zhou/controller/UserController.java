package com.zhou.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhou.entity.UserInfo;
import com.zhou.service.IUserService;
import com.zhou.utils.PageQueryData;
import com.zhou.utils.common.model.Result;
import com.zhou.utils.common.util.ResultUtil;

/**
 * 用户模块Controller
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年1月17日
 * @Version:1.1.0
 */
@RestController
@RequestMapping("UserController")
public class UserController {
    
    @Autowired
    private IUserService userService;
    
    /**
     * 根据指定id查询该部门或岗位下的所有用户
     * 
     * @param pageQueryData 分页对象，queryId封装了需要查询的id
     * @return
     * @Description:
     */
    @RequestMapping("getUserByParentId")
    public Result getUserByParentId(PageQueryData<UserInfo> pageQueryData) {
        userService.getUserByQueryId(pageQueryData);
        return ResultUtil.success(pageQueryData.getResult());
    }
    
    /**
     * 根据登陆名或用户名模糊查询
     * 
     * @param pageQueryData searchText封装了登陆名或用户名
     * @return
     * @Description:
     */
    @RequestMapping("fuzzySearchByLoginNameOrRealName")
    public Result fuzzySearchByLoginNameOrRealName(PageQueryData<UserInfo> pageQueryData) {
        userService.fuzzySearchByLoginNameOrRealName(pageQueryData);
        return ResultUtil.success(pageQueryData.getResult());
    }
    
    /**
     * 添加用户
     * 
     * @param userInfo 封装用户实体
     * @return
     * @Description:当主键存在时，save方法进行update操作
     */
    @RequestMapping("saveOrUpdateUser")
    public Result saveOrUpdateUser(UserInfo userInfo) {
        UserInfo user = userService.saveOrUpdateUser(userInfo);
        return ResultUtil.success(user);
    }
    
    /**
     * 删除用户，多个用户使用逗号分隔
     * 
     * @param ids 一个或多个用户id
     * @return
     * @Description:
     */
    @RequestMapping("deleteUserByIds")
    public Result deleteUserByIds(String[] ids) {
        userService.deleteUserByIds(ids);
        return ResultUtil.success();
    }
    
    /**
     * 分配一个或多个人员到指定的部门或岗位(分配人员的角色)，也可以用于修改人员的部门或岗位
     * 
     * @param ids 一个或多个人员的id
     * @param parentId 部门或岗位的id
     * @return
     * @Description:
     */
    @RequestMapping("assignDepartmentByUserIds")
    public Result assignDepartmentByUserIds(String[] ids, String parentId) {
        userService.assignDepartmentByUserIds(ids, parentId);
        return ResultUtil.success();
    }
}
