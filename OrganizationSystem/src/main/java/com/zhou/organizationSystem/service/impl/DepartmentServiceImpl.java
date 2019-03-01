package com.zhou.organizationSystem.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zhou.organizationSystem.dao.IDepartmentDao;
import com.zhou.organizationSystem.entity.DepartmentInfo;
import com.zhou.organizationSystem.entity.DepartmentTree;
import com.zhou.organizationSystem.service.IDepartmentService;

/**
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年1月17日
 * @Version:1.1.0
 */
@Service
public class DepartmentServiceImpl implements IDepartmentService {
    
    @Autowired
    private IDepartmentDao departmentDao;
    
    @Override
    public DepartmentTree getDepartmentTree() {
        //获取所有的部门
        List<DepartmentInfo> list = departmentDao.findByParentId("#");
        List<DepartmentTree> treeList = new ArrayList<>();
        for (DepartmentInfo departmentInfo : list) {
            DepartmentTree departmentTree =
                new DepartmentTree(departmentInfo.getId(), departmentInfo.getDepartmentName());
            List<DepartmentTree> positionList = this.getPositionList(departmentInfo.getId());
            departmentTree.setChildren(positionList);
            treeList.add(departmentTree);
        }
        //增加一个顶级分类，否则前端删除时出错
        DepartmentTree parentTreeNode = new DepartmentTree();
        parentTreeNode.setId("1111-1111-1111");
        parentTreeNode.setTitle("所有部门");
        parentTreeNode.setChildren(treeList);
        return parentTreeNode;
    }
    
    //获取指定部门下的所有岗位
    private List<DepartmentTree> getPositionList(String id) {
        List<DepartmentInfo> departmentPeople = departmentDao.findByParentId(id);
        List<DepartmentTree> peopleList = new ArrayList<>();
        for (DepartmentInfo departmentInfo : departmentPeople) {
            DepartmentTree departmentTree =
                new DepartmentTree(departmentInfo.getId(), departmentInfo.getJobPositionName());
            peopleList.add(departmentTree);
        }
        return peopleList;
    }
    
    @Override
    public void saveOrUpdateDepartment(DepartmentInfo departmentInfo) {
        departmentDao.save(departmentInfo);
    }
    
    @Override
    public void deleteDepartment(String id) {
        if (departmentDao.findById(id).orElse(null) != null) {
            departmentDao.deleteById(id);
        }
    }
    
    @Override
    public List<DepartmentInfo> getDepartmentList(String parentId) {
        return departmentDao.findByParentId(parentId);
    }
}
