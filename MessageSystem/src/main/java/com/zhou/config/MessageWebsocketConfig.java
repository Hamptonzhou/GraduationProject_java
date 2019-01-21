package com.zhou.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.zhou.controller.MessageWebsocketHandler;

/**
 * websocket配置类
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年1月21日
 * @Version:1.1.0
 */
@Configuration
@EnableWebSocket
public class MessageWebsocketConfig implements WebSocketConfigurer {
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler(), "/MessageWebsocket").setAllowedOrigins("*");
    }
    
    @Bean
    public MessageWebsocketHandler webSocketHandler() {
        return new MessageWebsocketHandler();
    }
}
