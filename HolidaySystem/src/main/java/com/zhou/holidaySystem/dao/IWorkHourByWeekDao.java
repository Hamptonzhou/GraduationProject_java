package com.zhou.holidaySystem.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zhou.holidaySystem.entity.WorkHourByWeek;

/**
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年1月21日
 * @Version:1.1.0
 */
@Repository
public interface IWorkHourByWeekDao extends JpaRepository<WorkHourByWeek, String> {
    
}
