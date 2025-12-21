package com.jinishop.jinishop.websocket.service;

import com.jinishop.jinishop.websocket.dto.OrderStatusPushMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderPushService {
    // 특정 유저에게만 보내기 위해 사용
    
    private final SimpMessagingTemplate messagingTemplate;

    // userEmail = JWT subject(email)
    public void pushToUser(String userEmail, OrderStatusPushMessage payload) {
        messagingTemplate.convertAndSendToUser(
                userEmail,
                "/queue/orders",
                payload
        );
    }

    // 주문 상세 페이지에서 orderId 토픽으로 확인용
    public void pushToOrderTopic(Long orderId, OrderStatusPushMessage payload) {
        messagingTemplate.convertAndSend(
                "/topic/orders/" + orderId,
                payload
        );
    }
}
