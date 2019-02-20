package com.zhou.messageSystem.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import com.zhou.messageSystem.service.IWebsockSessionContainer;

/**
 * websock中session容器的实现
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年1月21日
 * @Version:1.1.0
 */
@Service
public class WebsockSessionContainerImpl implements IWebsockSessionContainer {
    /**
     * 可能存在一个用户多个系统登陆的情况，用List<Session>存储不同系统连接的session
     */
    public static Map<String, List<WebSocketSession>> wsSessions =
        new ConcurrentHashMap<String, List<WebSocketSession>>();
    
    @Override
    public void putReceiver(String userId, Object receiver) {
        //一个用户可能在多个系统中登陆，所以可能存在多个session，获取用户对应的session列表
        List<WebSocketSession> sessionList = wsSessions.get(userId);
        if (receiver instanceof WebSocketSession) {
            WebSocketSession session = (WebSocketSession)receiver;
            if (sessionList == null) {
                //Map中还没有该用户id和session
                List<WebSocketSession> sessions = new ArrayList<>();
                sessions.add(session);
                wsSessions.put(userId, sessions);
            } else {
                //该用户在其他系统已经登陆，Map中保存了多个session
                sessionList.add(session);
                wsSessions.put(userId, sessionList);
            }
            //System.out.println("容器的状态为：" + wsSessions.toString());
        }
    }
    
    @Override
    public Object removeReceiver(String userId, Object receiver) {
        List<WebSocketSession> sessionList = new ArrayList<>();
        WebSocketSession session = null;
        if (receiver instanceof WebSocketSession) {
            session = (WebSocketSession)receiver;
            //获取用户对应的session列表
            sessionList = wsSessions.get(userId);
            if (sessionList != null) {
                //用户下线，移除对应的session
                sessionList.remove(session);
                //用户可能在多个系统登陆，在最后一个系统下线时，移除用户，表示该用户离线
                if (sessionList.size() == 0) {
                    wsSessions.remove(userId);
                }
            }
        }
        //System.out.println("容器的状态为：" + wsSessions.toString());
        return session;
    }
    
    @Override
    public List<WebSocketSession> getSessionListByUserId(String userId) {
        return wsSessions.get(userId);
    }
    
    @Override
    public Set<String> getOnlineUserSet() {
        return wsSessions.keySet();
    }
    
}
