package com.zhou.messageSystem.service.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.alibaba.fastjson.JSON;
import com.zhou.messageSystem.dao.IMessageInfoDao;
import com.zhou.messageSystem.entity.MessageItem;
import com.zhou.messageSystem.service.IMessageService;
import com.zhou.messageSystem.service.IWebsockSessionContainer;
import com.zhou.utils.PageQueryData;

/**
 * 消息服务实现类
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年1月21日
 * @Version:1.1.0
 */
@Service
public class MessageServiceImpl implements IMessageService {
    
    /**
     * 收发消息dao
     */
    @Autowired
    private IMessageInfoDao messageInfoDao;
    
    @Autowired
    private IWebsockSessionContainer sessionContainer;
    
    /**
     * 查询接收者为当前用户的消息
     */
    @Override
    public void listUnreadMessageByReceiverId(PageQueryData<MessageItem> pageQueryData) {
        String userId = pageQueryData.getQueryId();
        //设置分页对象
        Pageable pageable = this.setPageableObject(pageQueryData);
        //获取未读消息总数
        int total = messageInfoDao.countUnreadMessage(userId);
        //获取未读消息信息，包括个人消息
        List<MessageItem> mesagItemList = messageInfoDao.findUnreadMessageByReceiverId(userId, pageable);
        pageQueryData.setTotal(total);
        pageQueryData.setQueryList(mesagItemList);
    }
    
    /**
     * 获取所有已读的个人消息
     */
    @Override
    public void listReadedMessageByReceiverId(PageQueryData<MessageItem> pageQueryData) {
        String userId = pageQueryData.getQueryId();
        //设置分页对象
        Pageable pageable = this.setPageableObject(pageQueryData);
        //获取已读消息总数
        int total = this.countReadedMessage(userId);
        //获取已读消息列表，包括个人消息
        List<MessageItem> mesagItemList = messageInfoDao.findReadedMessageByReceiverId(userId, pageable);
        pageQueryData.setTotal(total);
        pageQueryData.setQueryList(mesagItemList);
        
    }
    
    /**
     * 获取所有广播消息
     */
    @Override
    public void listBroadcastMessage(PageQueryData<MessageItem> pageQueryData) {
        //设置分页对象
        Pageable pageable = this.setPageableObject(pageQueryData);
        //获取广播消息的总数
        int total = messageInfoDao.countBroadcastdMessage();
        List<MessageItem> mesagItemList = messageInfoDao.findBroadcastMessage();
        pageQueryData.setTotal(total);
        pageQueryData.setQueryList(mesagItemList);
        
    }
    
    /**
     * 获取当前用户删除的消息，删除标志为1
     */
    @Override
    public void listTrashMessage(PageQueryData<MessageItem> pageQueryData) {
        String userId = pageQueryData.getQueryId();
        //设置分页对象
        Pageable pageable = this.setPageableObject(pageQueryData);
        //获取广播消息的总数
        int total = messageInfoDao.countTrashMessage(userId);
        List<MessageItem> mesagItemList = messageInfoDao.findTrashMessage(userId, pageable);
        pageQueryData.setTotal(total);
        pageQueryData.setQueryList(mesagItemList);
    }
    
    /**
     * 将消息放入回收站，修改删除标志为1即可
     */
    @Override
    @Transactional
    public void deletePersonalMessage(String messageItemRids) {
        //messageInfoDao.deleteInBatch(null);
        MessageItem messageItem = messageInfoDao.findById(messageItemRids).orElse(null);
        if (messageItem != null && messageItem.getType() == 1 && messageItem.getMessageStatus() == 1) {
            //设置已删除标志，放入回收站
            messageItem.setIsDelete(1);
            messageInfoDao.save(messageItem);
        }
    }
    
    /**
     * 将消息从回收站还原到已读消息，修改删除标志为0即可
     */
    @Override
    @Transactional
    public void restoreDeleteMessage(String messageItemRids) {
        MessageItem messageItem = messageInfoDao.findById(messageItemRids).orElse(null);
        //检查消息存在，并且消息类型为个人消息
        boolean firstCheck = messageItem != null && messageItem.getType() == 1;
        //检查消息为已读，并且删除表示为1(表示在回收站)
        boolean secondCheck = messageItem.getMessageStatus() == 1 && messageItem.getIsDelete() == 1;
        if (firstCheck && secondCheck) {
            //设置删除标志，从回收站返回至已读消息
            messageItem.setIsDelete(0);
            messageInfoDao.save(messageItem);
        }
    }
    
    /**
     * 根据pageQueryData中的页数、每页显示的条数设置分页对象
     * 
     * @param pageQueryData
     * @return
     * @Description:
     */
    private Pageable setPageableObject(PageQueryData<MessageItem> pageQueryData) {
        int page = (pageQueryData.getPage() <= 0) ? 0 : pageQueryData.getPage();
        int rows = pageQueryData.getRows();
        //zero-based page index.
        Pageable pageable = PageRequest.of(page - 1, rows);
        return pageable;
    }
    
    @Override
    public MessageItem readMessageById(String userId, String messageItemIds) {
        return messageInfoDao.findById(messageItemIds).orElse(null);
    }
    
    /**
     * 读取消息内容，并且修改消息状态
     */
    @Override
    public MessageItem setHasRead(String userId, String messageItemIds) {
        List<String> rids = Arrays.asList(messageItemIds.split(","));
        MessageItem messageItem = null;
        for (String messageItemRid : rids) {
            messageItem = messageInfoDao.findById(messageItemRid).orElse(null);
            //如果是未读消息，修改消息状态为已读,0表示未读，1表示已读
            if (messageItem != null && messageItem.getMessageStatus() == 0
                && messageItem.getReceiverId().equals(userId)) {
                messageItem.setMessageStatus(1);
                //修改了部分属性，save方法对存在的记录进行update操作
                messageInfoDao.save(messageItem);
            }
        }
        return messageItem;
    }
    
    /**
     * 给一个或多个用户发送消息
     */
    @Override
    public void sendMessageToSomeone(MessageItem messageItem)
        throws IOException {
        String receiverIds = messageItem.getReceiverId();
        //获取所有接收人的ID
        List<String> receiverIdList = Arrays.asList(receiverIds.split(","));
        //receiverId是等于userId的
        for (String receiverId : receiverIdList) {
            //根据用户id去Map集合中查找对应的session，session不为空则表示用户在线
            List<WebSocketSession> sessionList = sessionContainer.getSessionListByUserId(receiverId);
            //创建实体保存修改后的属性进行持久化
            MessageItem newMessageItem = new MessageItem();
            BeanUtils.copyProperties(messageItem, newMessageItem);
            //接收消息的用户不在线，存库
            if (sessionList == null) {
                newMessageItem.setReceiverId(receiverId);
                newMessageItem.setType(2);
                messageInfoDao.save(newMessageItem);
            } else {
                //接收消息的用户在线，给每个客户端的用户发送消息，修改消息状态为已读，再存库
                for (WebSocketSession session : sessionList) {
                    session.sendMessage(new TextMessage(JSON.toJSONString(newMessageItem)));
                }
                newMessageItem.setReceiverId(receiverId);
                newMessageItem.setMessageStatus(1);
                messageInfoDao.save(newMessageItem);
            }
        }
    }
    
    /**
     * 发送广播消息
     * 
     * 将广播消息保存在消息实体表中，接收人设置为#
     */
    @Override
    public void sendMessageToAll(MessageItem messageItem)
        throws IOException {
        messageItem.setReceiverId("#");
        messageItem.setReceiverName("#");
        messageInfoDao.save(messageItem);
//        MessageItem newMessageItem = new MessageItem();
//        BeanUtils.copyProperties(messageItem, newMessageItem);
        //获取所有在线用户
        Set<String> onlineUserIdSet = sessionContainer.getOnlineUserSet();
        for (String userId : onlineUserIdSet) {
            List<WebSocketSession> sessionlist = sessionContainer.getSessionListByUserId(userId);
            for (WebSocketSession session : sessionlist) {
                session.sendMessage(new TextMessage(JSON.toJSONString(messageItem)));
            }
//            //修改消息状态为已读，并入库
//            newMessageItem.setMessageStatus(1);
//            newMessageItem.setReceiverId(userId);
//            messageInfoDao.save(newMessageItem);
        }
        //对于离线用户
        //处理方式一：获取所有用户的用户id，遍历排除在线的用户，然后为每个用户新增一条接收人为该用户id，状态为未读的消息记录
        //处理方式二：在用户上线的时候查询未读的广播消息，当存在未读的广播消息时，新增一条接收人为该用户，消息为未读的消息记录
        
        //毕设的处理方案:
        //广播消息记录只在数据库存在一条，存在于每个用户的广播列表，不允许删除，没有设置已读等功能！
    }
    
    /**
     * 获取用户的未读消息的数量
     */
    @Override
    public int countUnreadMessage(String userId) {
        return messageInfoDao.countUnreadMessage(userId);
    }
    
    /**
     * 获取用户的已读消息的数量
     */
    @Override
    public int countReadedMessage(String userId) {
        return messageInfoDao.countReadedMessage(userId);
    }
    
    /**
     * 获取所有已读的历史消息，包括已读消息和未读消息
     */
    @Override
    public void listAllMessage(PageQueryData<MessageItem> pageQueryData) {
        //设置分页对象
        Pageable pageable = this.setPageableObject(pageQueryData);
        //获取所有已读消息和未读消息
        List<MessageItem> messageItemList =
            messageInfoDao.getAllMessageByReceiverId(pageQueryData.getQueryId(), pageable);
        int total =
            this.countReadedMessage(pageQueryData.getQueryId()) + this.countUnreadMessage(pageQueryData.getQueryId());
        pageQueryData.setTotal(total);
        pageQueryData.setQueryList(messageItemList);
    }
    
    /**
     * 获取所有已发送的消息，包括个人消息和广播消息
     */
    @Override
    public void listSentMessage(PageQueryData<MessageItem> pageQueryData) {
        //设置分页对象
        Pageable pageable = this.setPageableObject(pageQueryData);
        //获取所有已发送的消息，包括个人消息和广播消息
        List<MessageItem> messageItemList = messageInfoDao.findBySenderId(pageQueryData.getQueryId(), pageable);
        int total = messageInfoDao.countBySenderId(pageQueryData.getQueryId());
        pageQueryData.setTotal(total);
        pageQueryData.setQueryList(messageItemList);
    }
    
    /**
     * 检查该用户是否有未读的广播消息，如果存在，则新增一条消息记录。
     * 
     * 存在用户从未上线的情况，则该用户从未没有接收过广播消息，所以无法获取用户最后接收广播的时间进行比较，所以为其新增1970年之后的所有广播消息记录。
     * 
     * @throws ParseException
     */
    @Override
    public void checkUnreadBroadcastMessage(String userId)
        throws ParseException {
        //查询该用户最后接收到的广播消息的时间
        Date latestBoracastTime = messageInfoDao.findLatestBoracastTime(userId);
        if (latestBoracastTime == null) {
            //时间为null表示用户从未上线，最后接收广播的时间为空，则为其新增1970年之后的所有广播消息
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            latestBoracastTime = sdf.parse("1970-01-01 01:00:00");
        }
        //需要新增的广播消息
        List<MessageItem> unreadBroadcastMessageList = messageInfoDao.listUnreadBroadcastMessage(latestBoracastTime);
        if (!CollectionUtils.isEmpty(unreadBroadcastMessageList)) {
            for (MessageItem messageItem : unreadBroadcastMessageList) {
                MessageItem newMessageItem = new MessageItem();
                BeanUtils.copyProperties(messageItem, newMessageItem);
                newMessageItem.setId(null);
                newMessageItem.setReceiverId(userId);
                messageInfoDao.save(newMessageItem);
            }
        }
    }
    
}
