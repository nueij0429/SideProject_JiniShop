package com.jinishop.jinishop.eventlog.repository;

import java.util.Optional;

import com.jinishop.jinishop.eventlog.entity.OrderEventLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderEventLogRepository extends JpaRepository<OrderEventLog, Long> {
    // 이벤트 로그 조회 / 중복 방지용 Repository

    Optional<OrderEventLog> findByEventId(String eventId);
}