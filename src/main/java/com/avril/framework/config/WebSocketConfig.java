package com.avril.framework.config;

import com.avril.common.utils.security.ShiroUtils;
import com.avril.framework.consolelog.ConsoleLog;
import com.avril.framework.manager.AsyncManager;
import com.avril.framework.manager.factory.AsyncFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * 配置WebSocket消息代理端点，即stomp服务端
 *
 * @author Ykyh
 * @link https://cloud.tencent.com/developer/article/1096792
 */
@EnableWebSocketMessageBroker
@Configuration
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket")
                .addInterceptors(new HandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
                        return ShiroUtils.getSubject().isPermitted(ConsoleLog.VIEW_PERM);
                    }

                    @Override
                    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

                    }
                })
                .setAllowedOrigins("*")
                .withSockJS();
    }

    /**
     * 推送日志到/topic/pullLogger
     */
    @PostConstruct
    public void pushLogger() {
        AsyncManager.me().execute(AsyncFactory.pushLogger());
    }
}