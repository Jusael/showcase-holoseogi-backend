package com.standalonejhgl.holoseogiapi.scheduler;

import com.standalonejhgl.holoseogiapi.daos.NotificationDao;
import com.standalonejhgl.holoseogiapi.daos.UserItemExpiryDao;
import com.standalonejhgl.holoseogiapi.daos.UserPlantDao;
import com.standalonejhgl.holoseogiapi.entity.NotificationQueue;
import com.standalonejhgl.holoseogiapi.models.UserItemExpirySummary;
import com.standalonejhgl.holoseogiapi.models.UserPlantQueueItem;
import com.standalonejhgl.holoseogiapi.models.UserScheduleQueueItem;
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
public class NotificationInsertScheduler {

    private final NotificationDao notificationDao;
    private final UserItemExpiryDao userItemExpiryDao;
    private final NotificationService notificationService;
    private final UserPlantDao userPlantDao;

    /**
     * 매일 새벽 5시 실행
     * cron: 초 분 시 일 월 요일
     */
    @Scheduled(cron = "0 0 5 * * *", zone = "Asia/Seoul")
    public void insertNotificationQueue() {

        String beanId = Integer.toHexString(System.identityHashCode(this));
        String thread = Thread.currentThread().getName();

        log.info("🕔 [InsertScheduler] START beanId={} thread={}", beanId, thread);
        log.info("🕔 [InsertScheduler] 알림 데이터 생성 시작");

        List<UserScheduleQueueItem> arr = notificationDao.createOnceNotificationList();

        //금일 스케쥴 알람 데이터 삽입
        arr.forEach(item -> {
            try {
                notificationService.insertUserOnceNotification(item, NotificationQueue.EnqueueSourceStatus.SCHEDULER.name());
            } catch (Exception e) {
                log.error("금일 스케쥴 삽입중 오류 발생 %s", e);
            }
        });

        arr = notificationDao.createWeekNotificationList();

        //금일 스케쥴 알람 데이터 삽입
        arr.forEach(item -> {
            try {
                notificationService.insertUserWeekNotification(item, NotificationQueue.EnqueueSourceStatus.SCHEDULER.name());
            } catch (Exception e) {
                log.error("주간 반복 스케쥴 삽입중 오류 발생 %s", e);
            }
        });


        arr = notificationDao.createMonthNotificationList();

        //금일 스케쥴 알람 데이터 삽입
        arr.forEach(item -> {
            try {
                notificationService.insertUserMonthNotification(item, NotificationQueue.EnqueueSourceStatus.SCHEDULER.name());
            } catch (Exception e) {
                log.error("월간 반복 스케쥴 삽입중 오류 발생 %s", e);
            }
        });

        List<UserItemExpirySummary> userItems = userItemExpiryDao.createFridgeNotificationList();
        userItems.forEach(item -> {
            try {
                notificationService.insertUserItemExpiryNotification(item);
            } catch (Exception e) {
                log.error("유통기한 임박 스케쥴 오류 발생 %s", e);
            }

        });

        List<UserPlantQueueItem> userPlants = userPlantDao.createUserPlantNotificationList();
        userPlants.forEach(item -> {
            try{
                notificationService.insertUserPlantWaterRemainNotification(item);
            }catch (Exception e){
                log.error("식물 물주기 스케쥴 오류 발생 %s", e);
            }

        });
    }
}