package com.zhou.common;

import java.util.ArrayList;
import java.util.List;

/**
 * 星期一到星期日的枚举类
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年1月21日
 * @Version:1.1.0
 */
public enum WeekEnum {
    /**
     * 星期一
     */
    Monday("星期一"),
    /**
     * 星期二
     */
    Tuesday("星期二"),
    /**
     * 星期三
     */
    Wednesday("星期三"),
    /**
     * 星期四
     */
    Thursday("星期四"),
    /**
     * 星期五
     */
    Friday("星期五"),
    /**
     * 星期六
     */
    Saturday("星期六"),
    /**
     * 星期日
     */
    Sunday("星期日");
    
    private String name;
    
    /**
     * 构造方法
     * 
     * @param name 星期几
     */
    private WeekEnum(String name) {
        this.name = name;
    }
    
    public static String getName(int index) {
        
        switch (index) {
            case 1:
                return WeekEnum.Monday.name;
            case 2:
                return WeekEnum.Tuesday.name;
            case 3:
                return WeekEnum.Wednesday.name;
            case 4:
                return WeekEnum.Thursday.name;
            case 5:
                return WeekEnum.Friday.name;
            case 6:
                return WeekEnum.Saturday.name;
            default:
                return WeekEnum.Sunday.name;
        }
    }
    
    public static boolean equalsName(String name) {
        boolean result = false;
        for (WeekEnum week : values()) {
            if (week.name.equals(name)) {
                result = true;
                break;
            }
        }
        return result;
    }
    
    public static List<String> getWeekNames() {
        List<String> weekNames = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            weekNames.add(WeekEnum.getName(i));
        }
        return weekNames;
    }
}
