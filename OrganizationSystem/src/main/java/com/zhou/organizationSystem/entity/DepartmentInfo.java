package com.zhou.organizationSystem.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

/**
 * 部门信息表，部门与岗位共用一张表，部门的parentId='#'，岗位的parentId='部门的id'
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年1月16日
 * @Version:1.1.0
 */
@Entity
@Table(name = "department_info")
@DynamicUpdate
@Data
public class DepartmentInfo {
    /**
     * 部门id或岗位id
     */
    @Id
    @GenericGenerator(name = "AutoUUID", strategy = "uuid")
    @GeneratedValue(generator = "AutoUUID")
    @Column(name = "id", length = 36)
    private String id;
    
    /**
     * 部门名
     */
    @Column(name = "department_name", length = 30)
    private String departmentName;
    
    /**
     * 岗位名
     */
    @Column(name = "jobPosition_name", length = 30)
    private String jobPositionName;
    
    /**
     * 部门或岗位传真
     */
    @Column(name = "fax", length = 30)
    private String fax;
    
    /**
     * 部门或岗位负责人
     */
    @Column(name = "contact_userId", length = 36)
    private String contactUserId;
    
    /**
     * 部门或岗位描述
     */
    @Column(name = "department_description", length = 300)
    private String departmentDescription;
    
    /**
     * 父级id，部门的父级id='#',岗位的父级id='部门的id'
     */
    @Column(name = "parentId", length = 36, nullable = false)
    private String parentId;
    
    /**
     * 当代表部门的时候，该属性用于存放部门下的所有岗位，不持久化，用于构建部门---岗位树返回到前端
     */
    @Transient
    private List<DepartmentInfo> children;
    
}
