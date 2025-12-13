package com.jinishop.jinishop.messaging.support;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class TxAfterCommitExecutor {
    // 트랜잭션 커밋 이후에만 특정 작업을 실행하기 위한 유틸 컴포넌트

    // 현재 트랜잭션이 존재하면 커밋 이후 실행
    // 트랜잭션이 없으면 즉시 실행
    public void runAfterCommit(Runnable action) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            action.run();
            return;
        }

        // Spring 트랜잭션에 훅을 걸어 afterCommit() 시점에만 action 실행
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                action.run();
            }
        });
    }
}