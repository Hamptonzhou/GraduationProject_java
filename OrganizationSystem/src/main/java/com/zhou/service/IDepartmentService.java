package com.zhou.service;

import java.util.List;

import com.zhou.entity.DepartmentInfo;

/**
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年1月17日
 * @Version:1.1.0
 */
public interface IDepartmentService {
    /**
     * 获取完整的组织机构树：部门---岗位
     * 
     * @return
     * @Description:
     */
    List<DepartmentInfo> getDepartmentTree();
    
    /**
     * 新增或修改部门或岗位的信息
     * 
     * @param departmentInfo 部门或岗位的实体
     * @return
     * @Description:
     */
    void saveOrUpdateDepartment(DepartmentInfo departmentInfo);
    
    /**
     * 删除部门或岗位
     * 
     * @param id 部门或岗位的id
     * @return
     * @Description:
     */
    void deleteDepartment(String id);
    
    /**
     * 获取部门名称列表，用于添加岗位时，可以使用下拉框选择
     * 
     * @return
     * @Description parentId='#'的记录代表部门
     */
    List<DepartmentInfo> getDepartmentList(String parentId);
    
}
