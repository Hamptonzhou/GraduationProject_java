package com.zhou.holidaySystem.service;

import java.util.List;
import java.util.Map;

/**
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年1月21日
 * @Version:1.1.0
 */
public interface IHolidayManagementService {
    
    /**
     * 保存节日，调休日期信息
     * 
     * @param title 日期描述
     * @param dayType 日期类型
     * @param holidayBegtime 节日和调休开始时间
     * @param holidayEndtime 节日和调休结束时间
     * @return
     */
    public List<Map<String, Object>> saveHolidayManagement(String title, String dayType, String holidayBegtime,
        String holidayEndtime);
    
    /**
     * 获取指定日期详细信息
     * 
     * @param date 日期
     * @return
     */
    public List<Map<String, Object>> getDayDetail(String date);
    
    /**
     * 获取某时间段节日，调休日期信息
     * 
     * @param stDate 节日和调休开始时间
     * @param enDate 节日和调休结束时间
     * @return 节日和调休日期信息的Map集合
     */
    public List<Map<String, Object>> getHolidays(String stDate, String enDate);
}
