package com.zhou.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.zhou.service.IMessageService;
import com.zhou.service.IWebsockSessionContainer;
import com.zhou.utils.CheckUtil;
import com.zhou.utils.exception.ArgumentNullException;

/**
 * websock消息Controller
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年1月21日
 * @Version:1.1.0
 */
public class MessageWebsocketHandler extends TextWebSocketHandler {
    
    protected static final Logger LOGGER = LoggerFactory.getLogger(MessageWebsocketHandler.class);
    
    @Autowired
    private IMessageService messageService;
    
    @Autowired
    private IWebsockSessionContainer sessionContainer;
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session)
        throws Exception {
        //LOGGER.info("进入onopen");
        //获取用户Id
        String uri = session.getUri().toString();
        int beginIndex = uri.indexOf("userId") + "userId".length() + 1;
        int endIndex = uri.indexOf("&", beginIndex);
        String userId = null;
        if (endIndex == -1) {
            userId = uri.substring(beginIndex);
        } else {
            userId = uri.substring(beginIndex, endIndex);
        }
        //添加userId到session，触发OnClose的时候需要获取
        session.getAttributes().put("userId", userId);
        if (CheckUtil.isNullorEmpty(userId)) {
            throw new ArgumentNullException("userId");
        }
        //用户一登录添加用户id和session到Map集合中，记录在线状态
        sessionContainer.putReceiver(userId, session);
        //检查该用户是否有未读的广播消息，如果有，新增一条记录
        messageService.checkUnreadBroadcastMessage(userId);
        //获取未读消息总数
        int count = messageService.countUnreadMessage(userId);
        //通知在线用户，上线
        //UserInfo userinfo = organInfoService.getUserInfo(userId);
        //session.getBasicRemote().sendText(JsonUtil.toJsonString(userinfo.getRealName() + "上线了"));
        session.sendMessage(new TextMessage("" + count));
        //LOGGER.info("退出onopen,session对象=" + session.toString() + ",count=" + count);
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status)
        throws Exception {
        //LOGGER.info("进入onclose");
        //获取用户Id
        String userId = (String)session.getAttributes().get("userId");
        if (userId == null) {
            return;
        }
        sessionContainer.removeReceiver(userId, session);
        //LOGGER.info("退出onclose");
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message)
        throws Exception {
        //LOGGER.info("进入handleTextMessage");
        // session.sendMessage(new TextMessage("服务端发送消息"));
    }
    
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message)
        throws Exception {
        super.handleMessage(session, message);
        // LOGGER.info("进入handleMessage");
    }
    
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception)
        throws Exception {
        super.handleTransportError(session, exception);
    }
}
