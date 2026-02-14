

package com.standalonejhgl.holoseogiapi.controller.app;


import java.util.Map;

import com.standalonejhgl.holoseogiapi.dtos.app.ResultResponseDto;
import com.standalonejhgl.holoseogiapi.dtos.app.SubscriptionResponseDto;
import com.standalonejhgl.holoseogiapi.service.app.AppleReceiptVerifyService;
import com.standalonejhgl.holoseogiapi.service.app.UserPlantService;
import org.slf4j.Logger;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscription")
public class SubscriptController {

    private static final Logger log = LoggerFactory.getLogger(SubscriptController.class);
    private final UserPlantService.SubscriptionService subscriptionService;
    private final AppleReceiptVerifyService appleReceiptVerifyService;

    @PostMapping("/ios-user-subscriptions")
    public ResponseEntity<SubscriptionResponseDto> verifySubscription(@RequestHeader("UserId") String userId, @RequestBody Map<String, String> body) {

        String receiptData = body.get("receiptData");

        log.info(String.format("구독 요청 백엔드 처리 시작  userId %s receiptData %s", userId ,  receiptData.length()));

        return ResponseEntity.ok(appleReceiptVerifyService.handleSubscription(userId,receiptData));
    }

    @GetMapping("/check-ios-user-subscription")
    public ResponseEntity<SubscriptionResponseDto> checkSubscription(@RequestHeader("UserId") String userId) {

        log.info(String.format("로그인 시 활성, 구독 상태 조회  userId %s ", userId));

        return ResponseEntity.ok(appleReceiptVerifyService.checkSubscription(userId));
    }


}