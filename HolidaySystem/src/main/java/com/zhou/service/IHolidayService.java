package com.zhou.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zhou.entity.HolidayDefinition;
import com.zhou.entity.WorkHourByWeek;

/**
 * 节假日管理及工作日计算服务
 * 
 * @Title:
 * @Description:
 * @Author:hgc
 * @Since:2018年9月11日
 * @Version:1.0.0
 */
public interface IHolidayService {
    
    /**
     * 初始化指定的年假期设置.
     * 
     * 初始化时，周工时必须定义过后才能进行日期定义，日期定义依赖于周工时的定义
     * 
     * @param year 开始初始化年份。
     * @param howYear 多少年的数据。
     */
    public void initializeHoliday(int year, int howYear);
    
    /**
     * 获取周一到周日的工时集合
     * 
     * @return 周工时定义数据集合。
     */
    public List<WorkHourByWeek> getWorkHourByWeeks();
    
    /**
     * 保存或修改周工时定义. 如果数据存在就修改，如果不存在就插入保存。
     * 
     * @param weeks 周工时定义。
     * @return
     * @Description:
     */
    public Map<String, Object> saveOrUpdateWorkHourByWeek(List<WorkHourByWeek> workHourByWeekList);
    
    /**
     * 修改节假日信息.
     * 
     * 修改前会对数据有效性进行判断， 时间格式必须为yyyy-MM-dd、节假日类型.
     * 
     * @param holidayDefinitions 要修改的节假日定义集合.
     */
    public void updateHoliday(List<HolidayDefinition> holidayDefinitions);
    
    /**
     * 获取节假日定义数据.
     * 
     * @param stDate 开始时间范围。
     * @param enDate 结束时间范围。
     * 
     * @return 节假日定义数据集合（包含两个时间边界）。
     */
    public List<HolidayDefinition> getHoliday(Date stDate, Date enDate);
    
}
