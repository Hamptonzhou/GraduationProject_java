package com.zhou.organizationSystem.entity;

import java.util.List;

import lombok.Data;

/**
 * 组织结构树
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年2月28日
 * @Version:1.1.0
 */
@Data
public class DepartmentTree {
    private String id;
    
    private String title;
    
    private List<DepartmentTree> children;
    
    public DepartmentTree() {
        super();
    }
    
    public DepartmentTree(String id, String title) {
        super();
        this.id = id;
        this.title = title;
    }
}
