package com.zhou.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zhou.entity.HolidayDefinition;

/**
 * 
 * @Title:
 * @Description:
 * @Author:hgc
 * @Since:2018年9月11日
 * @Version:1.0.0
 */
@Repository(IHolidayDefinitionDao.DAO_BEAN_NAME)
public interface IHolidayDefinitionDao extends JpaRepository<HolidayDefinition, String> {
    /**
     * 用于Spring映射的bean名称.
     */
    public static final String DAO_BEAN_NAME = "holidayDefinitionDao";
    
    /**
     * 获取两个世界点之间的数据.
     * 
     * @param startTime 开始时间。
     * @param endTime 结束时间。
     * 
     * @return 节假日定义数据集。
     * 
     * @throws Exception 参数为空异常。
     */
    public List<HolidayDefinition> findByYearDayBetweenOrderByYearDay(String startTime, String endTime);
    
    /**
     * 获取两个世界点之间的数据.
     * 
     * @param startTime 开始时间。
     * @param endTime 结束时间。
     * @param dayType 节假日类型。
     * 
     * @return 节假日定义数据集。
     */
    public List<HolidayDefinition> findByDayTypeAndYearDayBetweenOrderByYearDayDesc(int dayType, String startTime,
        String endTime);
    
    /**
     * 根据某个时间查询指定条数数据.
     * 
     * @param startTime 时间格式为 yyyy-mm-dd。
     * @param dayType
     * @param page 分页对象
     * @return 节假日定义数据集。
     * @Description:
     */
    public List<HolidayDefinition> findByYearDayLessThanEqualAndDayTypeOrderByYearDayDesc(String startTime, int dayType,
        Pageable page);
    
    /**
     * 根据某个时间查询指定条数数据.
     * 
     * @param startTime 时间格式为 yyyy-mm-dd。
     * @param dayType
     * @param page
     * @return 节假日定义数据集。
     * @Description:
     */
    public List<HolidayDefinition> findByYearDayIsGreaterThanEqualAndDayTypeOrderByYearDayAsc(String startTime,
        int dayType, Pageable page);
}
