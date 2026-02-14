package com.standalonejhgl.holoseogiapi.models;

import java.time.LocalDateTime;

public record AppleReceipt(
        String productId,
        LocalDateTime expiresAt,
        String transactionId,
        String originalTransactionId
) {

    /** 구독 만료 여부 */
    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }

    /** 활성 구독 여부 */
    public boolean isActive() {
        return !isExpired();
    }
}