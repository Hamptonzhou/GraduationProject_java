package com.zhou.messageSystem.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhou.messageSystem.entity.MessageItem;
import com.zhou.messageSystem.service.IMessageService;
import com.zhou.utils.CheckUtil;
import com.zhou.utils.PageQueryData;
import com.zhou.utils.common.model.Result;
import com.zhou.utils.common.util.ResultUtil;
import com.zhou.utils.exception.ArgumentNullException;

/**
 * 消息处理Controller
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年1月21日
 * @Version:1.1.0
 */
@RequestMapping("MessageController")
@RestController
public class MessageController {
    @Autowired
    private IMessageService messageService;
    
    /**
     * 根据当前用户的userId获取用户的未读消息总数，包括个人消息和广播消息的总数
     * 
     * @param request
     * @return
     * @Description:
     */
    @RequestMapping("countUnreadMessage")
    public Result countUnreadMessage(String userId) {
        //获取当前用户ID
        if (CheckUtil.isNullorEmpty(userId)) {
            //用户未登录
            throw new ArgumentNullException("userId");
        }
        //获取当前用户未读消息的总数
        int count = messageService.countUnreadMessage(userId);
        return ResultUtil.success(count);
    }
    
    /**
     * 根据当前用户的userId获取用户的个人未读消息。
     * 
     * @param request
     * @return
     * @Description: 前端传递userId、page、rows封装在pageQueryData对象中
     */
    @RequestMapping("listUnreadMessage")
    public Result listUnreadMessage(PageQueryData<MessageItem> pageQueryData) {
        //获取当前用户ID
        String userId = pageQueryData.getQueryId();
        if (CheckUtil.isNullorEmpty(userId)) {
            throw new ArgumentNullException("userId");
        }
        //获取当前用户未读消息
        messageService.listUnreadMessageByReceiverId(pageQueryData);
        return ResultUtil.success(pageQueryData.getResult());
    }
    
    /**
     * 获取已读的个人消息
     * 
     * @param pageQueryData
     * @return
     * @Description:
     */
    @RequestMapping("listReadedMessage")
    public Result listReadedMessage(PageQueryData<MessageItem> pageQueryData) {
        //获取当前用户ID
        String userId = pageQueryData.getQueryId();
        if (CheckUtil.isNullorEmpty(userId)) {
            throw new ArgumentNullException("userId");
        }
        //获取当前用户已读的个人消息
        messageService.listReadedMessageByReceiverId(pageQueryData);
        return ResultUtil.success(pageQueryData.getResult());
    }
    
    /**
     * 获取所有的广播消息
     * 
     * @param pageQueryData
     * @return
     * @Description:
     */
    @RequestMapping("listBroadcastMessage")
    public Result listBroadcastMessage(PageQueryData<MessageItem> pageQueryData) {
        //获取所有的广播消息
        messageService.listBroadcastMessage(pageQueryData);
        return ResultUtil.success(pageQueryData.getResult());
    }
    
    /**
     * 获取所有的已删除的消息，即删除标识为1
     * 
     * @param pageQueryData
     * @return
     * @Description:
     */
    @RequestMapping("listTrashMessage")
    public Result listTrashMessage(PageQueryData<MessageItem> pageQueryData) {
        //获取当前用户ID
        String userId = pageQueryData.getQueryId();
        if (CheckUtil.isNullorEmpty(userId)) {
            throw new ArgumentNullException("userId");
        }
        //获取所有的广播消息
        messageService.listTrashMessage(pageQueryData);
        return ResultUtil.success(pageQueryData.getResult());
    }
    
    /**
     * 根据消息id返回消息实体
     * 
     * @param request
     * @param messageItemRid
     * @return
     * @Description:
     */
    @RequestMapping("getContentByMsgId")
    public Result getContentByMsgId(String userId, String messageItemIds) {
        //获取当前用户ID
        if (CheckUtil.isNullorEmpty(userId)) {
            throw new ArgumentNullException("userId");
        }
        //获取指定消息
        MessageItem messageItem = messageService.readMessageById(userId, messageItemIds);
        return ResultUtil.success(messageItem);
    }
    
    /**
     * 读取消息内容，并把消息状态设置成已读
     * 
     * @param request
     * @param messageItemRid
     * @return
     * @Description:
     */
    @RequestMapping("setHasRead")
    public Result setHasRead(String userId, String messageItemIds) {
        //获取当前用户ID
        if (CheckUtil.isNullorEmpty(userId)) {
            throw new ArgumentNullException("userId");
        }
        //把对应的消息记录设消息状态字段设置成已读
        MessageItem messageItem = messageService.setHasRead(userId, messageItemIds);
        return ResultUtil.success(messageItem);
    }
    
    /**
     * 根据消息id修改删除标志为1，放入回收站，等待定时任务删除
     * 
     * @param request
     * @param messageItemRid 消息id
     * @param messageItemType
     * @Description:
     */
    @RequestMapping("deletePersonalMessage")
    public Result deletePersonalMessage(String userId, String messageItemIds) {
        if (CheckUtil.isNullorEmpty(userId)) {
            throw new ArgumentNullException("userId");
        }
        //删除消息
        messageService.deletePersonalMessage(messageItemIds);
        return ResultUtil.success();
    }
    
    /**
     * 根据消息id修改删除标志为0，从回收站状态返回到已读状态
     * 
     * @param request
     * @param messageItemRid 消息id
     * @param messageItemType
     * @Description:
     */
    @RequestMapping("restoreDeleteMessage")
    public Result restoreDeleteMessage(String userId, String messageItemIds) {
        if (CheckUtil.isNullorEmpty(userId)) {
            throw new ArgumentNullException("userId");
        }
        //还原消息
        messageService.restoreDeleteMessage(messageItemIds);
        return ResultUtil.success();
    }
    
    /**
     * 给指定一个或多个用户发送消息
     * 
     * @param messageItem 消息体，包含接收人id、消息内容等信息
     * @throws IOException
     * @Description: 前端传递多个接收人时，使用逗号分隔
     */
    @RequestMapping("sendMessageToSomeone")
    public Result sendMessageToSomeone(MessageItem messageItem)
        throws IOException {
        if (CheckUtil.isNullorEmpty(messageItem.getReceiverId())) {
            throw new ArgumentNullException("RECEIVERID");
        }
        messageService.sendMessageToSomeone(messageItem);
        return ResultUtil.success();
    }
    
    /**
     * 给所有用户发送消息，即广播消息
     * 
     * @param messageItem 消息体，广播消息的接收人为必须为#
     * @throws IOException
     * @Description: 前端传递多个接收人时，使用逗号分隔
     */
    @RequestMapping("sendMessageToAll")
    public Result sendMessageToAll(MessageItem messageItem)
        throws IOException {
//        if (!"#".equals(messageItem.getReceiverId())) {
//            throw new IllegalArgumentException("广播消息的接收人必须等于#");
//        }
        messageService.sendMessageToAll(messageItem);
        return ResultUtil.success();
    }
    
    /**
     * 获取所有历史消息，包括已读消息和未读消息
     * 
     * @param request
     * @Description:前端传递queryId、page、rows封装在pageQueryData对象中
     */
    @RequestMapping("listAllMessage")
    public Result listAllMessage(PageQueryData<MessageItem> pageQueryData) {
        //获取当前用户ID
        String userId = pageQueryData.getQueryId();
        if (CheckUtil.isNullorEmpty(userId)) {
            throw new ArgumentNullException("userId");
        }
        //获取所有历史消息
        messageService.listAllMessage(pageQueryData);
        return ResultUtil.success(pageQueryData.getResult());
    }
    
    /**
     * 获取所有已发送的消息，包括个人消息和广播消息
     * 
     * @param request
     * @Description:前端传递userId、page、rows封装在pageQueryData对象中
     */
    @RequestMapping("listSentMessage")
    public Result listSentMessage(PageQueryData<MessageItem> pageQueryData) {
        //获取当前用户ID
        String userId = pageQueryData.getQueryId();
        if (CheckUtil.isNullorEmpty(userId)) {
            throw new ArgumentNullException("userId");
        }
        //获取所有由用户已发送的消息
        messageService.listSentMessage(pageQueryData);
        return ResultUtil.success(pageQueryData.getResult());
    }
}
