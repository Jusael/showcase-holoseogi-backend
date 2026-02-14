package com.standalonejhgl.holoseogiapi.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppleSubscriptionVerifyResult {
    private boolean active;
    private long expiresMs;
    private String transactionId;
    private String productId;
}