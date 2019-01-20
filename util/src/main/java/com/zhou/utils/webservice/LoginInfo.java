package com.zhou.utils.webservice;

import java.io.Serializable;

/**
 * 保存当前登录信息，一般保存在session中，在注销时会清除
 * 
 * @author hgc
 *
 */
public final class LoginInfo implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -7272696472155467275L;
    
    /**
     * 登录子系统ID
     */
    public String SysId;
    
    /**
     * 登录子系统代码
     */
    public String SysCode;
    
    /**
     * 登录名
     */
    public String LoginName;
    
    /**
     * 真实名
     */
    public String RealName;
    
    /**
     * 当前用户身份（用于权限管理）
     */
    public int Qua;
    
    /**
     * 所在部门名
     */
    public String OrganName;
    
}
