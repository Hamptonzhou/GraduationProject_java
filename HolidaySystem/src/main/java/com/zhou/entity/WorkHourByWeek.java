package com.zhou.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;

/**
 * 一周的工时定义实体类
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年1月21日
 * @Version:1.1.0
 */
@Entity
@Table(name = "working_hour")
@Data
@DynamicUpdate
public class WorkHourByWeek {
    
    /**
     * 星期几.
     */
    @Id
    @Column(name = "weekName", length = 36)
    private String weekName;
    
    /**
     * 早上开始上班时间.
     */
    @Column(name = "workAMStart", columnDefinition = "int DEFAULT 0")
    private int workAMStart;
    
    /**
     * 早上下班时间.
     */
    @Column(name = "workAMEnd", columnDefinition = "int DEFAULT 0")
    private int workAMEnd;
    
    /**
     * 下午上班时间.
     */
    @Column(name = "workPMStart", columnDefinition = "int DEFAULT 0")
    private int workPMStart;
    
    /**
     * 下午下班时间.
     */
    @Column(name = "workPMEnd", columnDefinition = "int DEFAULT 0")
    private int workPMEnd;
    
    /**
     * 一天总工时.
     */
    @Column(name = "workHour")
    private float workHour = 0;
    
    /**
     * 是否是假期.
     */
    @Column(name = "workType", columnDefinition = "int DEFAULT 0")
    private int workType = 0;
    
    /**
     * 设置是否是假期 ,默认是非假期 0
     * 
     * @param workType 如果是正常的上班时间type=0，否则当前日期为假期type=1
     */
    public void setWorkType(int workType) {
        if (!(workType == 0 || workType == 1)) {
            throw new RuntimeException("节假日类型定义错误");
        }
        this.workType = workType;
    }
    
    /**
     * 设置一天总工时.
     * 
     * @param workHour 一天总工时。
     */
    public void setWorkHour(float workHour) {
        if (workHour < 0 || workHour > 10) {
            throw new RuntimeException("一天总工时必须大于0小于10");
        }
        this.workHour = workHour;
    }
}
