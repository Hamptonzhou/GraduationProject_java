package com.zhou.organizationSystem.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/**
 * 人员信息实体类
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年1月16日
 * @Version:1.1.0
 */
@Entity
@Table(name = "user_info")
@Data
@DynamicUpdate
public class UserInfo implements Serializable {
    
    private static final long serialVersionUID = 9151583225432767178L;
    
    /**
     * 人员标识.
     */
    @Id
    @GenericGenerator(name = "AutoUUID", strategy = "uuid")
    @GeneratedValue(generator = "AutoUUID")
    @Column(name = "userId", length = 36)
    private String userId;
    
    /**
     * 人员登录名.
     * 
     * 登录系统用的账号，要求系统内唯一，不允许为空。 不区分大小写，数据库内保存的登录名如果包含字母，一律为小写。
     */
    @Column(name = "login_name", length = 50, unique = true, nullable = false)
    private String loginName;
    
    /**
     * MD5加密后的人员登录密码。 用户密码经过两层加密： 1 将原始密码进行sha1方式加密（一般由客户端处理）； 2
     * 将sha1加密后的密码进行MD5加密。 数据库内保存的是MD5加密后的密码。
     */
    @JsonIgnore
    @Basic
    @Column(name = "user_password", length = 100, nullable = false)
    private String userPassword;
    
    /**
     * 人员真实姓名.
     */
    @Column(name = "real_name", length = 100, nullable = false)
    private String realName;
    
    /**
     * 工号
     */
    @Column(name = "job_number", length = 10, nullable = false)
    private Integer jobNumber;
    
    /**
     * 人员性别
     */
    @Column(name = "gender", length = 4, nullable = false)
    private String gender;
    
    /**
     * 身份证号码
     */
    @Column(name = "identity_number", length = 20, nullable = false)
    private String identityNumber;
    
    /**
     * 人员的电话号码.
     */
    @Column(name = "phone", length = 20, nullable = false)
    private String phone;
    
    /**
     * 人员的电子邮箱.
     */
    @Column(name = "email", length = 100)
    private String email;
    
    /**
     * 人员的联系地址.
     */
    @Column(name = "address", length = 200)
    private String address;
    
    /**
     * 人员所属民族.
     */
    @Column(name = "ethnicity", length = 20)
    private String ethnicity;
    
    /**
     * 人员的学历.
     */
    @Column(name = "education", length = 100)
    private String education;
    
    /**
     * 注册时间.
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "register_time", length = 30)
    private Date registerTime;
    
    /**
     * 入职时间.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "join_time")
    private Date joinTime;
    
    /**
     * 人员的岗位.
     */
    @Column(name = "job_position", length = 100)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String jobPosition;
    
    /**
     * 人员身份。
     */
    @Column(name = "parentId", length = 36)
    private String parentId;
    
    /**
     * 该账号是否可用
     */
    @Column(name = "is_enable", length = 1)
    private String enable;
    
    /**
     * 人员描述
     */
    @Column(name = "user_description", length = 300)
    private String userDescription;
    
}
