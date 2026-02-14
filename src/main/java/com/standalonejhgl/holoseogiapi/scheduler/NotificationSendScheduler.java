package com.standalonejhgl.holoseogiapi.scheduler;

import com.standalonejhgl.holoseogiapi.daos.NotificationDao;
import com.standalonejhgl.holoseogiapi.dtos.app.NotificationDispatchDto;
import com.standalonejhgl.holoseogiapi.service.common.FcmService;
import com.standalonejhgl.holoseogiapi.service.common.NotificationService;
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
public class NotificationSendScheduler {

    private final NotificationDao notificationDao;
    private final NotificationService notificationService;
    private final FcmService fcmService;

    /**
     * 매일 6시부터 23시까지 매 정시마다 실행
     * cron: 초 분 시 일 월 요일
     */

    @Scheduled(cron = "0 0 6-23 * * *", zone = "Asia/Seoul")
    public void sendFcmNotifications() {
        log.info("⏰ [SendScheduler] tick: {}", java.time.LocalDateTime.now());
        try {
            List<NotificationDispatchDto> arr = notificationDao.selectReadyNotificationItems();
            if (arr.isEmpty()) {
                log.info("⏳ 발송 대기건 없음");
                return;
            }
            arr.forEach(dto -> {
                try {
                    fcmService.sendMessage(dto);
                } catch (Exception e) {
                    log.error("FCM 전송 오류: {}", dto, e);
                }
            });
        } catch (Exception e) {
            log.error("스케줄러 전체 오류", e); // ← 이게 찍히면 다음 주기도 못 돌아간 것처럼 보일 수 있음
        }
    }
}