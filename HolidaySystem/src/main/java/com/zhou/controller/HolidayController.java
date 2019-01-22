package com.zhou.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.zhou.common.WeekEnum;
import com.zhou.entity.WorkHourByWeek;
import com.zhou.service.IHolidayManagementService;
import com.zhou.service.IHolidayService;
import com.zhou.utils.PageQueryData;
import com.zhou.utils.common.model.Result;
import com.zhou.utils.common.util.ResultUtil;

/**
 * 节假日Controller
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2018年12月29日
 * @Version:1.1.0
 */
@RestController
@RequestMapping("HolidayController")
public class HolidayController {
    @Autowired
    private IHolidayService holidayService;
    
    @Autowired
    private IHolidayManagementService holidayManagementService;
    
    /**
     * 获取一周的工时
     * 
     * @param pageQueryData 分页对象
     * @return
     * @Description:
     */
    @RequestMapping("/listWorkHourByWeek")
    public Result listWorkHourByWeek(PageQueryData<WorkHourByWeek> pageQueryData) {
        List<WorkHourByWeek> workHourByWeeks = holidayService.getWorkHourByWeeks();
        //对日期进行排序
        Collections.sort(workHourByWeeks, new Comparator<WorkHourByWeek>() {
            @Override
            public int compare(WorkHourByWeek week1, WorkHourByWeek week2) {
                List<String> weekNameList = WeekEnum.getWeekNames();
                return Integer.compare(weekNameList.indexOf(week1.getWeekName()),
                    weekNameList.indexOf(week2.getWeekName()));
            }
        });
        pageQueryData.setQueryList(workHourByWeeks);
        return ResultUtil.success(pageQueryData.getResult());
    }
    
    /**
     * 保存或修改工时的定义
     * 
     * @param workHourByWeek
     * @return
     * @Description:
     */
    @RequestMapping(value = "/saveOrUpdateWorkHourByWeek")
    public Result saveOrUpdateWorkHourByWeek(WorkHourByWeek workHourByWeek) {
        List<WorkHourByWeek> workHourByWeekList = Lists.newArrayList(workHourByWeek);
        Map<String, Object> map = holidayService.saveOrUpdateWorkHourByWeek(workHourByWeekList);
        return ResultUtil.success(map);
    }
    
    /**
     * 初始化节假日定义信息
     * 
     * @param startYear 开始年份
     * @param num 初始化几年
     * @return 初始化几年
     * @Description:
     */
    @RequestMapping("/initializeHoliday")
    public Result initializeHoliday(int startYear, int num) {
        holidayService.initializeHoliday(startYear, num);
        return ResultUtil.success(num);
    }
    
    /**
     * 新增节假日或调休。把上班时间设置为节假日或调休。去掉删除节假日接口，删除节假日也可以使用该接口来实现。删除节假日也就是dayType=1修改为0即可。
     * 
     * @param title
     * @param dayType
     * @param holidayBegtime 节日开始时间
     * @param holidayEndtime 节日结束时间
     * @return
     * @Description:
     */
    @RequestMapping("/saveOrUpdateHoliday")
    public Result saveOrUpdateHoliday(String title, String dayType, String holidayBegtime, String holidayEndtime) {
        List<Map<String, Object>> holidayList =
            holidayManagementService.saveHolidayManagement(title, dayType, holidayBegtime, holidayEndtime);
        return ResultUtil.success(holidayList);
    }
    
    /**
     * 获取指定日期的详细信息
     * 
     * @param date
     * @return
     * @Description:
     */
    @RequestMapping(value = "/getDayDetail")
    public Result getDayDetail(String date) {
        List<Map<String, Object>> dayDetail = holidayManagementService.getDayDetail(date);
        return ResultUtil.success(dayDetail);
    }
    
}
