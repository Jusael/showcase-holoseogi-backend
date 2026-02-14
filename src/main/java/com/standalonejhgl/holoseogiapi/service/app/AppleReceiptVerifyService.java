package com.standalonejhgl.holoseogiapi.service.app;

import com.standalonejhgl.holoseogiapi.dtos.app.SubscriptionResponseDto;

public interface AppleReceiptVerifyService {

    SubscriptionResponseDto handleSubscription(String userId, String receiptData);
    SubscriptionResponseDto checkSubscription(String userId);
}
