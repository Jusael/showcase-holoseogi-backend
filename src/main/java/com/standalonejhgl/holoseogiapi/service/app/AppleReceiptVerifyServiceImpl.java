package com.standalonejhgl.holoseogiapi.service.app;

import com.standalonejhgl.holoseogiapi.dtos.app.SubscriptionResponseDto;
import com.standalonejhgl.holoseogiapi.entity.User;
import com.standalonejhgl.holoseogiapi.entity.UserSubscription;
import com.standalonejhgl.holoseogiapi.exception.BusinessException;
import com.standalonejhgl.holoseogiapi.exception.ErrorCode;
import com.standalonejhgl.holoseogiapi.models.AppleReceipt;
import com.standalonejhgl.holoseogiapi.parser.AppleReceiptParser;
import com.standalonejhgl.holoseogiapi.repository.UserRepository;
import com.standalonejhgl.holoseogiapi.repository.UserSerialKeyRepository;
import com.standalonejhgl.holoseogiapi.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppleReceiptVerifyServiceImpl implements AppleReceiptVerifyService {

    private enum SubscriptionStatus {
        FREE,        // 무료 사용자 (구독 이력 없음)
        ACTIVE,      // 구독 중 (유효기간 남음)
        CANCELLED,   // 구독 취소됨 (기간은 남아있음)
        EXPIRED      // 구독 완전 만료
    }

    ;

    private final UserSerialKeyRepository userSerialKeyRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public SubscriptionResponseDto handleSubscription(String userId, String receiptData) {

        //Receipt 를 파서를 통해 정제하여 구조체에 할당
        AppleReceipt receipt = AppleReceiptParser.parse(receiptData);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        UserSubscription subscription =
                userSubscriptionRepository.findByUserId(userId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.SUBSCRIPTION_NOT_FOUND));

        //유효기간이 경과한 경우
        if (receipt.isExpired()) {
            user.applySubscription(1);
            subscription.downGrade(receipt);
        } else {
            user.applySubscription(5);
            subscription.upGrade(receipt);
        }

        log.info("Subscription verified userId={} productId={}",
                userId, receipt.productId());

        return new SubscriptionResponseDto(true, user.getUserLevel());
    }

    public SubscriptionResponseDto checkSubscription(String userId) {

        //시리얼 등록된 유저면 무조건 admin
        if (null != (userSerialKeyRepository.findByUserId(userId).orElse(null)))
            return new SubscriptionResponseDto(true, 10);

        UserSubscription userSubscription = userSubscriptionRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SUBSCRIPTION_NOT_FOUND));

        SubscriptionStatus subscriptionStatus = this.checkUserSubscriptionStatus(userSubscription);

        return switch (subscriptionStatus) {
            case FREE, EXPIRED -> new SubscriptionResponseDto(true, 1);
            case ACTIVE -> new SubscriptionResponseDto(true, 5);
            default -> new SubscriptionResponseDto(false, 1);
        };
    }


    private SubscriptionStatus checkUserSubscriptionStatus(UserSubscription userSubscription) {

        // case 1 : 무료 상태에서 계속 사용
        // 만약 구독을 했다면, 이미 Basic 이상 상태값
        if (userSubscription.getPlan() == UserSubscription.Plan.FREE || userSubscription.getTransactionId() == null)
            return SubscriptionStatus.FREE;

            // case 2 : 취소를 했던 안했던, 한번이라도 구독한 경우 유료 유효기간 존재
            // case 3 : 구독 이후 현재 계속해서 구독중인경우
        else if (userSubscription.getStatus() == UserSubscription.Status.ACTIVE)
            return SubscriptionStatus.ACTIVE;

            // case 4 : 구독 이후, 유효기간이 경과한 경우
            // 취소 여부 Or 추가적인 구독이 되었는지 확인이 필요
        else if (userSubscription.getStatus() == UserSubscription.Status.EXPIRED)
            return SubscriptionStatus.EXPIRED;

        throw new BusinessException(ErrorCode.SUBSCRIPTION_NOT_FOUND);
    }
}