package com.zhou.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zhou.entity.DepartmentInfo;

/**
 * 数据库表department_info的数据访问层
 * 
 * @Title:
 * @Description:department_info设计用于存放部门信息和岗位信息，区分标志为：部门的parentId='#'，岗位的parentId='部门id'
 * @Author:zhou
 * @Since:2019年1月17日
 * @Version:1.1.0
 */
public interface IDepartmentDao extends JpaRepository<DepartmentInfo, String> {
    /**
     * 根据parentId查询父级
     * 
     * @param id
     * @return
     * @Description:
     */
    List<DepartmentInfo> findByParentId(String id);
    
}
