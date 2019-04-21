package com.zhou.messageSystem.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

/**
 * 消息对象实体
 *
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年1月21日
 * @Version:1.1.0
 */
@Entity
@Table(name = "message_item")
@Data
@DynamicUpdate
public class MessageItem {
    
    /**
     * 消息id
     */
    @Id
    @GenericGenerator(name = "AutoUUID", strategy = "uuid")
    @GeneratedValue(generator = "AutoUUID")
    @Column(name = "id", length = 36)
    private String id;
    
    /**
     * 发送者Id
     */
    @Column(name = "sender_id", length = 36)
    private String senderId;
    
    /**
     * 发送者真实姓名
     */
    @Column(name = "sender_name", length = 30)
    private String senderName;
    
    /**
     * 消息发送时间
     */
    //@JsonFormat主要是后台到前台的时间格式的转换 ，@DataFormat主要是前后到后台的时间格式的转换，可能在修改的时候时间逻辑出现错误
    @Column(name = "send_time", length = 30)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss" )
    private Date sendTime;
    
    /**
     * 接收者Id，广播消息的接收者id设置为'#'
     */
    @Column(name = "receiver_id", length = 36)
    private String receiverId;
    
    /**
     * 接收者真实姓名
     */
    @Column(name = "receiver_name", length = 30)
    private String receiverName;
    
    /**
     * 消息标题
     */
    @Column(name = "title", length = 60)
    private String title;
    
    /**
     * 消息内容
     */
    @Column(name = "content", length = 3000)
    private String content;
    
    /**
     * 消息状态。0表示未读，1表示已读
     */
    @Column(name = "message_status")
    private int messageStatus;
    
    /**
     * 消息类型。0---广播消息，1---个人消息
     */
    @Column(name = "type")
    private int type;
    
    /**
     * 是否删除。0---未被删除，1---已被删除，用于区别哪些消息在回收站中
     */
    @Column(name = "is_delete")
    private int isDelete;
    
}
