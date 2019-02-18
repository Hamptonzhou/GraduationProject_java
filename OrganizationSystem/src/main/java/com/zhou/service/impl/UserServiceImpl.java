package com.zhou.service.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zhou.dao.IUserDao;
import com.zhou.entity.UserInfo;
import com.zhou.service.IUserService;
import com.zhou.utils.PageQueryData;

/**
 * 用户模块操作接口实现类
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年1月17日
 * @Version:1.1.0
 */
@Service("UserService")
public class UserServiceImpl implements IUserService {
    
    @Autowired
    private IUserDao userDao;
    
    @Override
    public void getUserByQueryId(PageQueryData<UserInfo> pageQueryData) {
        int total = userDao.countByParentId(pageQueryData.getQueryId());
        pageQueryData.setTotal(total);
        List<UserInfo> list = userDao.findByParentId(pageQueryData.getQueryId(), pageQueryData.getPageable());
        pageQueryData.setQueryList(list);
    }
    
    @Override
    public void fuzzySearchByLoginNameOrRealName(PageQueryData<UserInfo> pageQueryData) {
        int total = userDao.countByLoginNameOrRealNameLike("%" + pageQueryData.getSearchText() + "%");
        pageQueryData.setTotal(total);
        List<UserInfo> list = userDao.findByLoginNameOrRealNameLike("%" + pageQueryData.getSearchText() + "%");
        pageQueryData.setQueryList(list);
    }
    
    // TODO 不允许为空的参数在前端校验，不允许重复的字段在后端校验，如登录名，另外写一个方法，用于校验字段是否重复
    @Override
    public UserInfo saveOrUpdateUser(UserInfo userInfo) {
        UserInfo user = userDao.save(userInfo);
        return user;
    }
    
    @Override
    public void deleteUserByIds(String[] ids) {
        List<String> idList = Arrays.asList(ids);
        for (String id : idList) {
            if (userDao.findById(id).orElse(null) != null) {
                userDao.deleteById(id);
            }
        }
    }
    
    @Override
    public void assignDepartmentByUserIds(String[] ids, String parentId) {
        List<String> idList = Arrays.asList(ids);
        for (String userId : idList) {
            UserInfo user = userDao.findById(userId).orElse(null);
            user.setParentId(parentId);
            userDao.save(user);
        }
    }
    
    @Override
    public boolean checkLoginNameExist(String loginName) {
        int count = userDao.countByLoginName(loginName);
        if (count == 0) {
            return false;
        } else {
            return true;
        }
    }
    
    @Override
    public UserInfo getUserInfoByLoginName(String loginName) {
        return userDao.findByLoginName(loginName);
    }
}
