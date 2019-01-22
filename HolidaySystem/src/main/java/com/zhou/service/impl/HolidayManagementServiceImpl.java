package com.zhou.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zhou.entity.HolidayDefinition;
import com.zhou.service.IHolidayManagementService;
import com.zhou.service.IHolidayService;
import com.zhou.utils.CheckUtil;

/**
 * 
 * @Title:
 * @Description:
 * @Author:hgc
 * @Since:2018年9月11日
 * @Version:1.0.0
 */
@Service
public class HolidayManagementServiceImpl implements IHolidayManagementService {
    
    @Autowired
    private IHolidayService holidayService;
    
    private Date returnDay = null;
    
    @Override
    public List<Map<String, Object>> saveHolidayManagement(String title, String dayType, String holidayBegtime,
        String holidayEndtime) {
        Map<String, Object> holidayMap = null;
        List<Map<String, Object>> holidayList = new ArrayList<>();
        
        List<HolidayDefinition> holidayDefinitions = new ArrayList<HolidayDefinition>();
        Calendar c = Calendar.getInstance();
        Date begTime = formateToDate(holidayBegtime);
        Date endTime = formateToDate(holidayEndtime);
        HolidayDefinition definition = null;
        if (begTime != null && endTime != null) {
            int days = (int)((endTime.getTime() - begTime.getTime()) / (1000 * 3600 * 24)) + 1;
            
            c.setTime(begTime);
            int day = c.get(Calendar.DATE);
            
            for (int i = 0; i < days; i++) {
                definition = new HolidayDefinition();
                c.set(Calendar.DATE, day + i);
                
                String dayAfter = formaateToString(c.getTime());
                definition.setYearDay(dayAfter);
                
                if ("0".equals(dayType) && !isWeekend(dayAfter)) {
                    holidayMap = new HashMap<>(16);
                    definition.setDayType(0);
                    definition.setDayDescribe("正常上班时间");
                    holidayMap.put("cdate", dayAfter);
                    holidayMap.put("state", "");
                } else if ("1".equals(dayType) && isWeekend(dayAfter)) {
                    holidayMap = new HashMap<>(16);
                    definition.setDayType(1);
                    definition.setDayDescribe("周末");
                    holidayMap.put("cdate", dayAfter);
                    holidayMap.put("state", "");
                } else if ("0".equals(dayType) && isWeekend(dayAfter)) {
                    holidayMap = new HashMap<>(16);
                    definition.setDayType(0);
                    definition.setIsModify(1);
                    if (CheckUtil.isNullorEmpty(title)) {
                        title = "调整为上班";
                    }
                    definition.setDayDescribe(title);
                    holidayMap.put("cdate", dayAfter);
                    holidayMap.put("state", "ban");
                } else {
                    holidayMap = new HashMap<>(16);
                    definition.setDayType(1);
                    definition.setIsModify(1);
                    if (CheckUtil.isNullorEmpty(title)) {
                        title = "调整为放假";
                    }
                    definition.setDayDescribe(title);
                    holidayMap.put("cdate", dayAfter);
                    holidayMap.put("state", "xiu");
                }
                holidayList.add(holidayMap);
                holidayDefinitions.add(definition);
            }
            holidayService.updateHoliday(holidayDefinitions);
        }
        return holidayList;
    }
    
    @Override
    public List<Map<String, Object>> deleteHolidayManagement(String title, String dayType, String holidayBegtime,
        String holidayEndtime) {
        Map<String, Object> holidayMap = null;
        List<Map<String, Object>> holidayList = new ArrayList<>();
        
        List<HolidayDefinition> holidayDefinitions = new ArrayList<HolidayDefinition>();
        Calendar c = Calendar.getInstance();
        Date begTime = formateToDate(holidayBegtime);
        Date endTime = formateToDate(holidayEndtime);
        HolidayDefinition definition = null;
        if (begTime != null && endTime != null) {
            int days = (int)((endTime.getTime() - begTime.getTime()) / (1000 * 3600 * 24)) + 1;
            
            c.setTime(begTime);
            int day = c.get(Calendar.DATE);
            
            for (int i = 0; i < days; i++) {
                definition = new HolidayDefinition();
                c.set(Calendar.DATE, day + i);
                
                String dayAfter = formaateToString(c.getTime());
                definition.setYearDay(dayAfter);
                
                if (isWeekend(dayAfter)) {
                    holidayMap = new HashMap<>(16);
                    definition.setDayType(1);
                    definition.setDayDescribe("周末");
                    holidayMap.put("cdate", dayAfter);
                    holidayMap.put("state", "");
                } else if (!isWeekend(dayAfter)) {
                    holidayMap = new HashMap<>(16);
                    definition.setDayType(0);
                    definition.setDayDescribe("正常上班时间");
                    holidayMap.put("cdate", dayAfter);
                    holidayMap.put("state", "");
                }
                holidayList.add(holidayMap);
                holidayDefinitions.add(definition);
            }
            holidayService.updateHoliday(holidayDefinitions);
        }
        return holidayList;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Map<String, Object>> getDayDetail(String date) {
        List<HolidayDefinition> holidays = holidayService.getHoliday(formateToDate(date), formateToDate(date));
        List<Map<String, Object>> holidayList = new ArrayList<>();
        if (holidays.isEmpty()) {
            return holidayList;
        }
        HolidayDefinition curDayDetail = holidays.get(0);
        Map<String, Object> holidayMap = null;
        if (curDayDetail.getDayType() == 1 && !"周末".equals(curDayDetail.getDayDescribe())) {
            holidayMap = new HashMap<>(16);
            getHolidayArea(formateToDate(curDayDetail.getYearDay()), curDayDetail.getDayDescribe(), true);
            holidayMap.put("cdate", formaateToString(returnDay));
            getHolidayArea(formateToDate(curDayDetail.getYearDay()), curDayDetail.getDayDescribe(), false);
            holidayMap.put("enddate", formaateToString(returnDay));
            holidayMap.put("state", "xiu");
            holidayMap.put("type", curDayDetail.getDayType());
            holidayMap.put("title", curDayDetail.getDayDescribe());
            holidayList.add(holidayMap);
        } else if (curDayDetail.getDayType() == 0 && !"正常上班时间".equals(curDayDetail.getDayDescribe())) {
            holidayMap = new HashMap<>(16);
            getHolidayArea(formateToDate(curDayDetail.getYearDay()), curDayDetail.getDayDescribe(), true);
            holidayMap.put("cdate", formaateToString(returnDay));
            getHolidayArea(formateToDate(curDayDetail.getYearDay()), curDayDetail.getDayDescribe(), false);
            holidayMap.put("enddate", formaateToString(returnDay));
            holidayMap.put("state", "ban");
            holidayMap.put("type", curDayDetail.getDayType());
            holidayMap.put("title", curDayDetail.getDayDescribe());
            holidayList.add(holidayMap);
        } else {
            holidayMap = new HashMap<>(16);
            if (isWeekend(date)) {
                holidayMap.put("weekend", "1");
            } else {
                holidayMap.put("weekend", "0");
            }
            holidayMap.put("cdate", curDayDetail.getYearDay());
            holidayMap.put("enddate", curDayDetail.getYearDay());
            holidayMap.put("type", curDayDetail.getDayType());
            holidayList.add(holidayMap);
        }
        return holidayList;
    }
    
    /**
     * 将String类型的日期转换成Date类型的日期
     * 
     * @param dateString String类型日期（2017-01-01）
     * @return Date类型的日期
     */
    private Date formateToDate(String dateString) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    
    /**
     * 将Date类型的日期转换成String类型的日期
     * 
     * @param date Date类型的日期
     * @return String类型日期（2017-01-01）
     * 
     */
    private String formaateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
    
    /**
     * 判断日期是否是周末
     * 
     * @param dateStr 字符串日期
     * @return
     */
    private boolean isWeekend(String dateStr) {
        Date date = formateToDate(dateStr);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return true;
        }
        return false;
    }
    
    /**
     * 递归查询节假日范围
     * 
     * @param curDay 当前日期
     * @param curDayDescribe 日期描述
     * @param type true:向前查找；false:向后查找。
     * 
     * @return
     */
    private void getHolidayArea(Date curDay, String curDayDescribe, boolean type) {
        Calendar calendar = Calendar.getInstance();
        List<HolidayDefinition> holidays = null;
        // 把当前时间赋给日历
        calendar.setTime(curDay);
        holidays = holidayService.getHoliday(curDay, curDay);
        if (type && holidays.size() > 0) {
            
            String dayDescribe = holidays.get(0).getDayDescribe();
            if (dayDescribe.equals(curDayDescribe)) {
                calendar.setTime(curDay);
                calendar.add(Calendar.DATE, -1);
                curDay = calendar.getTime();
                getHolidayArea(curDay, curDayDescribe, true);
            } else {
                calendar.setTime(curDay);
                calendar.add(Calendar.DATE, 1);
                returnDay = calendar.getTime();
            }
        } else if (!type && holidays.size() > 0) {
            
            String dayDescribe = holidays.get(0).getDayDescribe();
            if (dayDescribe.equals(curDayDescribe)) {
                calendar.setTime(curDay);
                calendar.add(Calendar.DATE, 1);
                curDay = calendar.getTime();
                getHolidayArea(curDay, curDayDescribe, false);
            } else {
                calendar.setTime(curDay);
                calendar.add(Calendar.DATE, -1);
                returnDay = calendar.getTime();
            }
        } else {
            calendar.add(Calendar.DATE, 1);
            returnDay = calendar.getTime();
        }
    }
}
