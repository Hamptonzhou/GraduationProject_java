package com.zhou.service;

import java.util.List;
import java.util.Set;

import org.springframework.web.socket.WebSocketSession;

/**
 * 操作websock的session容器接口
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年1月21日
 * @Version:1.1.0
 */
public interface IWebsockSessionContainer {
    /**
     * 设置接收者的接收对象（不同消息类型需要的接收对象不同，比如在线消息，使用websocket时，需要添加相应WebSocket对象）
     * 
     * @param userId
     * @param receiver
     */
    void putReceiver(String userId, Object receiver);
    
    /**
     * 移除接收者的接收对象（比如用户离线后，相应的WebSocket对象已无效）
     * 
     * @param userId
     * @return 被移除的接收对象
     */
    Object removeReceiver(String userId, Object receiver);
    
    /**
     * 根据用户id返回指定用户在容器中的session列表
     * 
     * @param userId
     * @return
     * @Description:
     */
    List<WebSocketSession> getSessionListByUserId(String userId);
    
    /**
     * 获取所有在线用户的id
     * 
     * @return
     * @Description:
     */
    Set<String> getOnlineUserSet();
}
