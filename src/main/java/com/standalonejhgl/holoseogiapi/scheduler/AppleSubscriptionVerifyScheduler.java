package com.standalonejhgl.holoseogiapi.scheduler;

import com.standalonejhgl.holoseogiapi.daos.AppleSubscriptionVerifyDao;
import com.standalonejhgl.holoseogiapi.models.AppleSubscriptionVerifyResult;
import com.standalonejhgl.holoseogiapi.models.UserSubscriptionVerifyList;
import com.standalonejhgl.holoseogiapi.service.app.AppleSubscriptionApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@ConditionalOnProperty(
        name = "app.scheduler.enabled",
        havingValue = "true",
        matchIfMissing = false
)
@Slf4j
@Component
@RequiredArgsConstructor
public class AppleSubscriptionVerifyScheduler {

    private final AppleSubscriptionVerifyDao appleSubscriptionVerifyDao;
    private final AppleSubscriptionApiService appleSubscriptionApiService;

    @Scheduled(cron = "0 0 2 * * *", zone = "Asia/Seoul")
    public void CheckSubscriptValid() {
        log.info("[AppleSubscriptionVerifyScheduler] tick: {}", java.time.LocalDateTime.now());
        List<UserSubscriptionVerifyList> arr = appleSubscriptionVerifyDao.getListVerifyUserSubscriptions();

        for (UserSubscriptionVerifyList item : arr) {
            try {

                AppleSubscriptionVerifyResult result = appleSubscriptionApiService.verifyFromApple(item.getOriginalTransactionId());

                appleSubscriptionApiService.updateSubscription(item.getUserId(), result);

            } catch (Exception e) {
                log.error(
                        "[AppleSubscriptionVerify] userId={}, originalTxId={} verify failed",
                        item.getUserId(),
                        item.getOriginalTransactionId(),
                        e
                );
            }
        }

    }

}
