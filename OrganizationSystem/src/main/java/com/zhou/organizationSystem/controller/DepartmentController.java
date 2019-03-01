package com.zhou.organizationSystem.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhou.organizationSystem.entity.DepartmentInfo;
import com.zhou.organizationSystem.entity.DepartmentTree;
import com.zhou.organizationSystem.service.IDepartmentService;
import com.zhou.utils.common.model.Result;
import com.zhou.utils.common.util.ResultUtil;

/**
 * 部门或岗位模块Controller
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年1月17日
 * @Version:1.1.0
 */
@RestController
@RequestMapping("/DepartmentController")
public class DepartmentController {
    
    @Autowired
    private IDepartmentService departmentService;
    
    /**
     * 获取完整的组织机构树：部门---岗位
     * 
     * @return
     * @Description:如果使用异步加载组织结构树，则该接口冗余。改而使用getDepartmentList接口
     */
    @RequestMapping("/getDepartmentTree")
    public Result getDepartmentTree() {
        DepartmentTree departmentTree = departmentService.getDepartmentTree();
        return ResultUtil.success(departmentTree);
    }
    
    /**
     * 新增或修改部门或岗位的信息
     * 
     * @param departmentInfo 部门或岗位的实体
     * @return
     * @Description:
     */
    @RequestMapping("/saveOrUpdateDepartment")
    public Result saveOrUpdateDepartment(DepartmentInfo departmentInfo) {
        departmentService.saveOrUpdateDepartment(departmentInfo);
        return ResultUtil.success();
    }
    
    /**
     * 删除部门或岗位
     * 
     * @param id 部门或岗位的id
     * @return
     * @Description:
     */
    @RequestMapping("/deleteDepartment")
    public Result deleteDepartment(String id) {
        departmentService.deleteDepartment(id);
        return ResultUtil.success();
    }
    
    /**
     * 获取部门名称列表，用于添加岗位时，可以使用下拉框选择
     * 
     * @return
     * @Description:
     */
    @RequestMapping("/getDepartmentList")
    public Result getDepartmentList(String parentId) {
        List<DepartmentInfo> DepartmentList = departmentService.getDepartmentList(parentId);
        return ResultUtil.success(DepartmentList);
    }
}
