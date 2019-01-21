package com.zhou.service;

import java.io.IOException;
import java.text.ParseException;

import com.zhou.entity.MessageItem;
import com.zhou.utils.PageQueryData;

/**
 * 消息服务接口
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年1月21日
 * @Version:1.1.0
 */
public interface IMessageService {
    
    /**
     * 获取当前用户未读消息的总数
     * 
     * @param userId
     * @return
     * @Description:
     */
    int countUnreadMessage(String userId);
    
    /**
     * 获取用户已读的消息总数
     * 
     * @param userId
     * @return
     * @Description:用于历史消息的分页查询
     */
    int countReadedMessage(String userId);
    
    /**
     * 获取所有未读消息
     * 
     * @param pageQueryData 分页对象，里面包含了userId属性记录ReceiverId
     * @Description:
     */
    void listUnreadMessageByReceiverId(PageQueryData<MessageItem> pageQueryData);
    
    /**
     * 获取指定消息的详细信息
     * 
     * @param userId
     * @param messageItemRid
     * @return
     * @Description:
     */
    MessageItem readMessageById(String userId, String messageItemIds);
    
    /**
     * 给指定用户发送消息
     * 
     * @param messageItem
     * @throws IOException
     * @Description:
     */
    void sendMessageToSomeone(MessageItem messageItem)
        throws IOException;
    
    /**
     * 给所有用户发送消息，即发送广播消息，广播消息的接收人为#
     * 
     * @param messageItem
     * @throws IOException
     * @Description:
     */
    void sendMessageToAll(MessageItem messageItem)
        throws IOException;
    
    /**
     * 根据消息id和消息类型删除已读消息
     * 
     * @param messageItemRids 消息主键
     * @Description:
     */
    void deletePersonalMessage(String messageItemRids);
    
    /**
     * 获取当前用户的所有历史消息，包括已读消息和未读消息
     * 
     * @param pageQueryData 分页对象，里面包含了userId属性
     * @Description:
     */
    void listAllMessage(PageQueryData<MessageItem> pageQueryData);
    
    /**
     * 获取所有已发送的消息，包括个人消息和广播消息
     * 
     * @param pageQueryData 分页对象，同时包含了userId属性
     * @Description:
     */
    void listSentMessage(PageQueryData<MessageItem> pageQueryData);
    
    /**
     * 检查该用户是否有未读的广播消息，如果存在，则新增一条消息记录
     * 
     * @param userId
     * @throws ParseException
     * @Description:
     */
    void checkUnreadBroadcastMessage(String userId)
        throws ParseException;
    
}
