package com.zhou.service.impl;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.zhou.common.WeekEnum;
import com.zhou.dao.IHolidayDefinitionDao;
import com.zhou.dao.IWorkHourByWeekDao;
import com.zhou.entity.HolidayDefinition;
import com.zhou.entity.WorkHourByWeek;
import com.zhou.service.IHolidayService;
import com.zhou.utils.exception.ArgumentNullException;

/**
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年1月21日
 * @Version:1.1.0
 */
@Service
public class HolidayServiceImpl implements IHolidayService {
    
    @Autowired
    private IWorkHourByWeekDao workHourByWeekDao;
    
    @Autowired
    private IHolidayDefinitionDao holidayDefinitionDao;
    
    SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
    
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    
    @Override
    public void initializeHoliday(int year, int count) {
        //验证周工时是否正确定义
        List<WorkHourByWeek> weeks = Lists.newArrayList(this.workHourByWeekDao.findAll());
        if (year < 1970) {
            throw new RuntimeException("年份输入不对，年份必须大于1970");
        }
        if (weeks.isEmpty()) {
            throw new RuntimeException("周工时未定义");
        } else if (weeks.size() < 7) {
            throw new RuntimeException("周工时定义不全");
        }
        //循环count年
        List<HolidayDefinition> holidayDefinitions = new ArrayList<>();
        for (int currentYear = year; currentYear < (count + year); currentYear++) {
            //获取count年的总天数
            int yearDate = LocalDate.of(currentYear, 1, 1).lengthOfYear();
            for (int day = 1; day <= yearDate; day++) {
                HolidayDefinition holidayDefinition = new HolidayDefinition();
                //返回年-月-日
                LocalDate localDate = LocalDate.ofYearDay(currentYear, day);
                //开始设置日期实体类
                holidayDefinition.setYearDay(localDate.toString());
                //由当前年-月-日得出星期几
                String weekName = WeekEnum.getName(localDate.getDayOfWeek().getValue());
                int dayType = 0;
                for (WorkHourByWeek week : weeks) {
                    if (weekName.equals(week.getWeekName())) {
                        dayType = week.getWorkType();
                        break;
                    }
                }
                holidayDefinition.setDayType(dayType);
                holidayDefinition.setDayDescribe(dayType == 0 ? "正常上班时间" : "周末");
                holidayDefinitions.add(holidayDefinition);
            }
        }
        holidayDefinitionDao.saveAll(holidayDefinitions);
    }
    
    @Override
    public List<WorkHourByWeek> getWorkHourByWeeks() {
        //获取所有存在的周工时定义
        List<WorkHourByWeek> existWeekHourDefintion = Lists.newArrayList(workHourByWeekDao.findAll());
        
        //获取所有存在的周工时定义的名称，如：星期一
        List<String> existWeekHourName = new ArrayList<>();
        for (WorkHourByWeek workHourByWeek : existWeekHourDefintion) {
            existWeekHourName.add(workHourByWeek.getWeekName());
        }
        
        //排除存在的周工时定义，筛选出没有定义的，然后进行添加
        List<String> missingWeekHourNameList = WeekEnum.getWeekNames();
        missingWeekHourNameList.removeAll(existWeekHourName);
        
        // 添加缺失的周工时定义
        List<WorkHourByWeek> missingWeekHourDefintionList = new ArrayList<>();
        for (String missingWeekHourName : missingWeekHourNameList) {
            WorkHourByWeek missingWeekHourDefintion = new WorkHourByWeek();
            missingWeekHourDefintion.setWeekName(missingWeekHourName);
            //对缺失的周工时进行默认工时设置
            if (missingWeekHourName.equals("星期六") || missingWeekHourName.equals("星期日")) {
                missingWeekHourDefintion.setWorkAMStart(0);
                missingWeekHourDefintion.setWorkAMEnd(0);
                missingWeekHourDefintion.setWorkPMStart(0);
                missingWeekHourDefintion.setWorkPMEnd(0);
                missingWeekHourDefintion.setWorkType(1);
                missingWeekHourDefintion.setWorkHour(0);
            } else {
                missingWeekHourDefintion.setWorkAMStart(540);
                missingWeekHourDefintion.setWorkAMEnd(720);
                missingWeekHourDefintion.setWorkPMStart(840);
                missingWeekHourDefintion.setWorkPMEnd(1110);
                missingWeekHourDefintion.setWorkType(0);
                missingWeekHourDefintion.setWorkHour(7.5f);
            }
            missingWeekHourDefintionList.add(missingWeekHourDefintion);
        }
        saveOrUpdateWorkHourByWeek(missingWeekHourDefintionList);
        
        //对未工作的工时统一处理设置为0
        existWeekHourDefintion = Lists.newArrayList(this.workHourByWeekDao.findAll());
        existWeekHourDefintion.stream().forEach((week) -> {
            if (week.getWorkAMStart() == week.getWorkAMEnd()) {
                week.setWorkAMStart(0);
                week.setWorkAMEnd(0);
            }
            if (week.getWorkPMStart() == week.getWorkPMEnd()) {
                week.setWorkPMStart(0);
                week.setWorkPMEnd(0);
            }
        });
        return existWeekHourDefintion;
    }
    
    @Override
    public Map<String, Object> saveOrUpdateWorkHourByWeek(List<WorkHourByWeek> weeks) {
        Map<String, Object> resultMap = new HashMap<>(16);
        if (weeks == null) {
            throw new ArgumentNullException("weeks");
        }
        for (WorkHourByWeek weekHourDefintion : weeks) {
            // 允许用户定义半天的情况
            if (weekHourDefintion.getWorkAMStart() == 0 || weekHourDefintion.getWorkAMEnd() == 0) {
                weekHourDefintion.setWorkAMStart(0);
                weekHourDefintion.setWorkAMEnd(0);
            }
            if (weekHourDefintion.getWorkPMStart() == 0 || weekHourDefintion.getWorkPMEnd() == 0) {
                weekHourDefintion.setWorkPMStart(weekHourDefintion.getWorkAMEnd());
                weekHourDefintion.setWorkPMEnd(weekHourDefintion.getWorkAMEnd());
            }
            if (!WeekEnum.equalsName(weekHourDefintion.getWeekName())) {
                resultMap.put("isError", true);
                resultMap.put("msg",
                    weekHourDefintion.getWeekName() + "值有误，值只能设置成集合（星期一、星期二、星期三、星期四、星期五、星期六、星期日）内的的值。");
                return resultMap;
            }
            boolean legalStatus = (0 <= weekHourDefintion.getWorkAMStart())
                && (weekHourDefintion.getWorkAMStart() <= weekHourDefintion.getWorkAMEnd())
                && (weekHourDefintion.getWorkAMEnd() <= weekHourDefintion.getWorkPMStart())
                && (weekHourDefintion.getWorkPMStart() <= weekHourDefintion.getWorkPMEnd())
                && (weekHourDefintion.getWorkPMEnd() <= 1440);
            if (!legalStatus) {
                resultMap.put("isError", true);
                resultMap.put("msg", "周定义的时间不符合规范");
                return resultMap;
            }
            float hour = ((weekHourDefintion.getWorkAMEnd() - weekHourDefintion.getWorkAMStart())
                + (weekHourDefintion.getWorkPMEnd() - weekHourDefintion.getWorkPMStart())) / (float)60;
            if (hour < 0 || hour > 10) {
                resultMap.put("isError", true);
                resultMap.put("msg", "一天总工时必须大于0小于10");
                return resultMap;
            }
            weekHourDefintion.setWorkHour(hour);
        }
        workHourByWeekDao.saveAll(weeks);
        resultMap.put("isSuccess", true);
        resultMap.put("msg", "保存成功,可到节假日设置进行初始化。");
        return resultMap;
    }
    
    @Override
    public void updateHoliday(List<HolidayDefinition> holidayDefinitions) {
        if (holidayDefinitions == null) {
            return;
        }
        holidayDefinitions.stream().forEach((holiday) -> {
            if (holiday.getDayType() != 0) {
                holiday.setDayType(1);
            }
            if (holiday.getYearDay() == null) {
                throw new ArgumentNullException("时间不能为空");
            }
            try {
                format.setLenient(false);
                Date dtTemp = format.parse(holiday.getYearDay());
                holiday.setYearDay(format.format(dtTemp));
            } catch (Exception e) {
                throw new RuntimeException(holiday.getYearDay() + "时间格式修改错误,格式为yyyy-MM-dd如2016-02-02");
            }
        });
        
        this.holidayDefinitionDao.saveAll(holidayDefinitions);
    }
    
    @Override
    public List<HolidayDefinition> getHoliday(Date stDate, Date enDate) {
        if (stDate == null) {
            throw new ArgumentNullException("stDate");
        }
        if (enDate == null) {
            throw new ArgumentNullException("enDate");
        }
        return this.holidayDefinitionDao.findByYearDayBetweenOrderByYearDay(format.format(stDate),
            format.format(enDate));
    }
}
