package com.zhou.holidaySystem.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;

/**
 * 日期定义实体
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年1月21日
 * @Version:1.1.0
 */
@Entity
@Table(name = "holiday_definition")
@Data
@DynamicUpdate
public class HolidayDefinition {
    
    /**
     * 定义某年某天.
     */
    @Id
    @Column(name = "yearDay", length = 20)
    private String yearDay;
    
    /**
     * 日期类型，0---正常上班时间，1---周末或者节假日或者调休。是否是节假日（1节假日，0工作日）
     */
    @Column(name = "dayType", columnDefinition = "int DEFAULT 0")
    private int dayType = 0;
    
    /**
     * 对于当前日期的描述.
     */
    @Column(name = "dayDescribe", length = 100)
    private String dayDescribe;
    
    /**
     * 获取当前状态值，是否人为修改的类型
     */
    @Column(name = "isModify", columnDefinition = "int DEFAULT 0")
    private int isModify = 0;
    
    /**
     * 是否经过人为修改.
     * 
     * @return 状态值。
     */
    public void setIsModify(int isModify) {
        if (isModify == 0 || isModify == 1) {
            this.isModify = isModify;
        } else {
            throw new RuntimeException("状态值必须为0或者1");
        }
    }
    
    /**
     * 设置节假日类型（1节假日，0工作日），默认值非节假日{@code 0}。
     * 
     * @param dayTpye 如果是非节假日{@code 0}，否则当前日期是节假日{@code 1}。
     */
    public void setDayType(int dayType) {
        if (dayType == 0 || dayType == 1) {
            this.dayType = dayType;
        } else {
            throw new RuntimeException("节假日必须为0或者1");
        }
    }
    
}
