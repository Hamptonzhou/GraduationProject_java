package com.zhou.organizationSystem.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.zhou.organizationSystem.entity.UserInfo;

/**
 * 数据库表user_info的数据访问层
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年1月17日
 * @Version:1.1.0
 */
@Repository
public interface IUserDao extends JpaRepository<UserInfo, String> {
    
    /**
     * 根据父级id,查询部门或者岗位下的所有用户的数量
     * 
     * @param queryId 父级id
     * @param pageable 分页对象
     * @return
     * @Description:
     */
    int countByParentId(String queryId);
    
    /**
     * 根据父级id,查询部门或者岗位下的所有用户
     * 
     * @param queryId 父级id
     * @param pageable 分页对象
     * @return
     * @Description:
     */
    List<UserInfo> findByParentId(String queryId, Pageable pageable);
    
    /**
     * 根据登陆名或用户名模糊查询
     * 
     * @param pageQueryData searchText封装了登陆名或用户名
     * @return
     * @Description:
     */
    @Query("from UserInfo u where (u.loginName like :searchText) or (u.realName like :searchText)")
    List<UserInfo> findByLoginNameOrRealNameLike(String searchText);
    
    /**
     * 根据登陆名或用户名模糊查询符合条件的数量
     * 
     * @param pageQueryData searchText封装了登陆名或用户名
     * @return
     * @Description:
     */
    @Query("select count(*) from UserInfo u where (u.loginName like :searchText) or (u.realName like :searchText)")
    int countByLoginNameOrRealNameLike(String searchText);
    
    /**
     * 检查登陆账号是否存在
     * 
     * @param loginName 登陆账号
     * @return
     * @Description:
     */
    int countByLoginName(String loginName);
    
    /**
     * 根据登陆名查询用户信息
     * 
     * @param loginName
     * @return
     * @Description:
     */
    UserInfo findByLoginName(String loginName);
    
}
