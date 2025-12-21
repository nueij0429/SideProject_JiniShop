package com.jinishop.jinishop.order.event;

import com.jinishop.jinishop.websocket.dto.OrderStatusPushMessage;
import com.jinishop.jinishop.websocket.service.OrderPushService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderStatusChangedEventListener {

    private final OrderPushService orderPushService;

    @EventListener
    public void handle(OrderStatusChangedEvent event) {
        String text = switch (event.status()) {
            case CREATED -> "주문 생성";
            case PAYMENT_PENDING -> "결제 대기 중";
            case PAYMENT_SUCCESS -> "결제 성공";
            case PAYMENT_FAILED -> "결제 실패";
            case CANCELED -> "주문 취소";
        };

        var payload = OrderStatusPushMessage.of(
                event.orderId(),
                event.userId(),
                event.status().name(),
                text
        );

        // 사용자에게만 push
        orderPushService.pushToUser(event.userEmail(), payload);

        // 주문 토픽으로도 push
        orderPushService.pushToOrderTopic(event.orderId(), payload);
    }
}
