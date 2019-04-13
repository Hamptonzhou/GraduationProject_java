package com.zhou.messageSystem.common;

/**
 * 消息类型常量，对应实体的type字段
 *
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年2月18日
 * @Version:1.1.0
 */
public class MessageCode {

    /**
     * 未读的个人消息
     */
    public static final Integer PERSONAL_MESSAGE_UNREAD = 0;

    /**
     * 已读的个人消息
     */
    public static final Integer PERSONAL_MESSAGE_READED = 1;

    /**
     * 未读的广播消息
     */
    public static final Integer BROADCAST_MESSAGE = 2;

    /**
     * 广播消息的接收者设置为#
     */
    public static final String BROADCAST_MESSAGE_RECEIVERID = "#";

}
