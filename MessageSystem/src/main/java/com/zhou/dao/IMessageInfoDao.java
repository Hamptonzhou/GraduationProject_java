package com.zhou.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.zhou.entity.MessageItem;

/**
 * 消息数据库表message_item的操作类
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年1月21日
 * @Version:1.1.0
 */
@Repository
public interface IMessageInfoDao extends JpaRepository<MessageItem, String> {
    
    /**
     * 获取未读消息总数
     * 
     * @param userId
     * @return
     * @Description:
     */
    @Query(value = "select count(*) from MessageItem m where m.receiverId = :userId and m.messageStatus = 0 order by m.sendTime DESC")
    public int countUnreadMessage(String userId);
    
    /**
     * 根据当前用户Id，查询当前用户未读的个人消息和广播消息.
     * 
     * @param userId
     * @param pageable 分页对象
     * @return
     */
    @Query(value = "from MessageItem m where m.receiverId = :userId and m.messageStatus = 0 order by m.sendTime DESC")
    public List<MessageItem> findUnreadMessageByReceiverId(String userId, Pageable pageable);
    
    /**
     * 获取已读消息总数
     * 
     * @param userId
     * @return
     * @Description:
     */
    @Query(value = "select count(*) from MessageItem m where m.receiverId = :userId and m.messageStatus = 1 order by m.sendTime desc")
    public int countReadedMessage(String userId);
    
    /**
     * 获取当前用户的所有已读消息，包括已读消息和未读消息
     * 
     * @param userId
     * @param pageable 分页对象
     * @return
     * @Description:
     */
    @Query(value = "from MessageItem m where m.receiverId = :userId order by m.sendTime desc")
    public List<MessageItem> getAllMessageByReceiverId(String userId, Pageable pageable);
    
    /**
     * 根据消息id和消息类型删除已读消息
     * 
     * @param messageItemRid
     * @param messageItemType
     * @Description:
     */
    public void deleteByIdAndType(String messageItemId, Integer messageItemType);
    
    /**
     * 获取发送者为该用户id的所有消息
     * 
     * @param userId
     * @param pageable
     * @return
     * @Description:
     */
    @Query(value = "from MessageItem m where (m.senderId = :userId and m.type=1) or (m.senderId = :userId and m.receiverId='#')  order by m.sendTime desc")
    public List<MessageItem> findBySenderId(String userId, Pageable pageable);
    
    /**
     * 获取该用户已发送的消息的总数，用于分页
     * 
     * @param userId senderId字段等于该userId的记录即是该用户发送过的消息
     * @return
     * @Description:
     */
    @Query(value = "select count(*) from MessageItem m where (m.senderId = :userId and m.type=1) or (m.senderId = :userId and m.receiverId='#')  order by m.sendTime desc")
    public int countBySenderId(String userId);
    
    /**
     * 检查该用户是否有未读的广播消息，如果存在，则新增一条消息记录
     * 
     * @param userId
     * @return
     * @Description:
     */
    @Query(value = "from MessageItem m where m.type = 0 and m.receiverId = '#' and m.sendTime > :latestBoracastTime")
    public List<MessageItem> listUnreadBroadcastMessage(Date latestBoracastTime);
    
    /**
     * 查询用户最后接收广播的时间。所有的广播消息中，时间比该用户最后接收广播时间大的，则是该用户尚未接收的广播消息。
     * 
     * @param userId
     * @return
     * @Description:
     */
    @Query(value = "select MAX(mm.sendTime) from MessageItem mm where mm.receiverId = :userId and mm.type = 0 ")
    public Date findLatestBoracastTime(String userId);
}