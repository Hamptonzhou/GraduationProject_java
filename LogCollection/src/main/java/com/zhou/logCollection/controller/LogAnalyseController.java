package com.zhou.logCollection.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.zhou.logCollection.entity.OperationLoglist;
import com.zhou.logCollection.service.IOperationLogAnalyseService;
import com.zhou.utils.PageQueryData;
import com.zhou.utils.common.model.Result;
import com.zhou.utils.common.util.ResultUtil;

/**
 * 日志分析，目前仅用于获取日志列表用于查看
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年1月15日
 * @Version:1.1.0
 */
@RestController
@RequestMapping("LogAnalyseController")
public class LogAnalyseController {
    
    @Autowired
    private IOperationLogAnalyseService<OperationLoglist> operationLogAnalyseService;
    
    /**
     * 获取日志记录
     * 
     * @param pageQueryData
     * @param keyword
     * @param startDate
     * @param endDate
     * @return
     * @Description:
     */
    @RequestMapping("getOperationLogList")
    public Result getOperationLogList(PageQueryData<OperationLoglist> pageQueryData, String keyword, String startDate,
        String endDate) {
        //获取页面的查询条件
        operationLogAnalyseService.find(pageQueryData, keyword, startDate, endDate);
        //TODO 根据当前session或mongodb返回数据中的userId获取用户的真实名称        
        return ResultUtil.success(pageQueryData.getResult());
    }
}
