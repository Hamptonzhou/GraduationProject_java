package com.zhou.service;

import com.zhou.entity.UserInfo;
import com.zhou.utils.PageQueryData;

/**
 * 用户模块操作接口
 * 
 * @Title:
 * @Description: 接口类中的方法和属性不要加任何修饰符号(public 也不要加)保持代码的简洁 性，并加上有效的 Javadoc注释。
 * @Author:zhou
 * @Since:2019年1月17日
 * @Version:1.1.0
 */
public interface IUserService {
    
    /**
     * 根据指定id查询该部门或岗位下的所有用户
     * 
     * @param pageQueryData 分页对象，封装了需要查询的id
     * @return
     * @Description:
     */
    void getUserByQueryId(PageQueryData<UserInfo> pageQueryData);
    
    /**
     * 根据登陆名或用户名模糊查询
     * 
     * @param pageQueryData searchText封装了登陆名或用户名
     * @return
     * @Description:
     */
    void fuzzySearchByLoginNameOrRealName(PageQueryData<UserInfo> pageQueryData);
    
    /**
     * 添加用户
     * 
     * @param userInfo 封装用户实体
     * @return
     * @Description:
     */
    UserInfo saveOrUpdateUser(UserInfo userInfo);
    
    /**
     * 删除用户，多个用户使用逗号分隔
     * 
     * @param ids 一个或多个用户id
     * @return
     * @Description:
     */
    void deleteUserByIds(String[] ids);
    
    /**
     * 分配一个或多个人员到指定的部门或岗位(分配人员的角色)
     * 
     * @param ids 一个或多个人员的id
     * @param parentId 部门或岗位的id
     * @return
     * @Description:
     */
    void assignDepartmentByUserIds(String[] ids, String parentId);
    
    /**
     * 检查登陆账号是否存在
     * 
     * @param loginName 登陆账号
     * @return
     * @Description:
     */
    boolean checkLoginNameExist(String loginName);
}
