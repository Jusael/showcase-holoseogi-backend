package com.standalonejhgl.holoseogiapi.eventListner;

import com.standalonejhgl.holoseogiapi.entity.UserPlant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import com.standalonejhgl.holoseogiapi.event.UserDeletedEvent;
import com.standalonejhgl.holoseogiapi.repository.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDeletedEventListener {

    private final UserScheduleRepository userScheduleRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final UserItemExpiryRepository userItemExpiryRepository;
    private final UserSubscriptionAuditRepository userSubscriptionAuditRepository;
    private final UserScheduleAuditRepository userScheduleAuditRepository;
    private final UserItemExpiryAuditRepository userItemExpiryAuditRepository;
    private final UserPlantRepository userPlantRepository;
    private final UserPlantAuditRepository userPlantAuditRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserDeleted(UserDeletedEvent event) {
        String userId = event.getUserId();

        try {

            // 사용자 스케쥴 정보삭제
            userScheduleRepository.softDeleteByUserSchedule(userId);

            //사용자 냉장고 정보 삭제
            userItemExpiryRepository.softDeleteUserItemExpiry(userId);

            //사용자 식물 정보 삭제
            userPlantRepository.softDeleteUserPlant(userId);

            // 사용자 오디트 로그 삭제
            userSubscriptionAuditRepository.deleteByUserId(userId);
            userScheduleAuditRepository.deleteByUserId(userId);
            userItemExpiryAuditRepository.deleteByUserId(userId);
            userPlantAuditRepository.deleteByUserId(userId);

            log.info("✅ 회원탈퇴 후 데이터 정리 완료 userId={}", userId);
        } catch (Exception e) {
            log.error("❌ 회원탈퇴 후처리 중 오류 userId={}, msg={}", userId, e);
        }
    }
}