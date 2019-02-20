package com.zhou.logCollection.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.zhou.logCollection.entity.OperationLoglist;
import com.zhou.logCollection.service.IOperationLogAnalyseService;
import com.zhou.utils.CheckUtil;
import com.zhou.utils.PageQueryData;

@Service
public class OperationLogAnalyseServiceImpl implements IOperationLogAnalyseService<OperationLoglist> {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    //    @Inject
    //    @Named(IOrganInfoService.SERVICE_BEAN_NAME)
    //    private IOrganInfoService organInfoService;
    
    @Override
    public void find(PageQueryData<OperationLoglist> pageQueryData, String keyword, String startDate, String endDate) {
        Query query = new Query();
        //根据关键字查询，关键字的内容可以是真实姓名或方法名
        //        if (!CheckUtil.isNullorEmpty(keyword)) {
        //            //根据操作用户名称查询用户信息，匹配查询出该操作用户的操作日志
        //            UserInfo userInfos = this.organInfoService.getUserInfoByRealName(keyword);
        //            if (userInfos != null) {
        //                Criteria criteria = new Criteria();
        //                query.addCriteria(criteria.orOperator(Criteria.where("methodName").regex(".*?" + keyword + ".*"),
        //                    Criteria.where("userId").is(userInfos.getUserId())));
        //            } else {
        //                query.addCriteria(Criteria.where("methodName").regex(".*?" + keyword + ".*"));
        //            }
        //        }
        //根据开始时间范围查询
        if (!CheckUtil.isNullorEmpty(startDate) && CheckUtil.isNullorEmpty(endDate)) {
            //整理开始时间的当天最小值
            startDate += " 00:00:00";
            query.addCriteria(Criteria.where("date").gte(startDate));
        }
        //根据结束时间范围查询
        if (!CheckUtil.isNullorEmpty(endDate) && CheckUtil.isNullorEmpty(startDate)) {
            //整理结束时间的当天最大值
            endDate += " 23:59:59";
            query.addCriteria(Criteria.where("date").lte(endDate));
        }
        //根据开始时间和结束时间范围查询
        if (!CheckUtil.isNullorEmpty(startDate) && !CheckUtil.isNullorEmpty(endDate)) {
            //整理开始时间和结束时间的当天最小和最大值
            startDate += " 00:00:00";
            endDate += " 23:59:59";
            query.addCriteria(Criteria.where("date").gte(startDate).lt(endDate));
        }
        query.with(new Sort(Direction.DESC, "date"));
        pageQueryData.setTotal((int)mongoTemplate.count(query, OperationLoglist.TABLE_NAME));
        if (pageQueryData.getRows() >= 0) {
            query.skip(pageQueryData.getRows() * (pageQueryData.getPage() - 1));
            query.limit(pageQueryData.getRows());
        }
        List<OperationLoglist> loglists =
            mongoTemplate.find(query, OperationLoglist.class, OperationLoglist.TABLE_NAME);
        pageQueryData.setQueryList(loglists);
    }
    
}
