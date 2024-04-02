package com.myapp.web.springboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


/**
 * <pre>
 *     설명: ChatConfig
 *     작성자: kimjinyoung
 *     작성일: 2023. 2. 21.
 * </pre>
 */
@Configuration
@EnableWebSocketMessageBroker
public class ChatConfig implements WebSocketMessageBrokerConfigurer {

    // stomp 접속 주소 url -> /ws/chat
    // setAllowedOriginPatterns("*") cors 때문에 허용된 도메인 설정, 실제로는 이렇게 하면 안됨
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 웹용
        registry.addEndpoint("/ws/chat")    // 연결될 엔드포인트
                .setAllowedOriginPatterns("*")
                .withSockJS();  // SocketJS를 연결한다는 설정
        // 앱용
        registry.addEndpoint("/ws/app/chat")    // 연결될 엔드포인트
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메세지를 구독하는 요청 URL => 즉 메세지를 받을 때
        registry.enableSimpleBroker("/queue", "/topic/chat");

        // 메세지를 발행하는 요청 URL -> 즉 메세지를 보낼 때
        registry.setApplicationDestinationPrefixes("/app");
    }
}
