
package com.standalonejhgl.holoseogiapi.eventListner;

import com.standalonejhgl.holoseogiapi.daos.UserScheduleDao;
import com.standalonejhgl.holoseogiapi.entity.NotificationQueue;
import com.standalonejhgl.holoseogiapi.entity.UserSchedule;
import com.standalonejhgl.holoseogiapi.event.UserScheduleTodayInsertEvent;
import com.standalonejhgl.holoseogiapi.event.UserScheduleTodayUpdateEvent;
import com.standalonejhgl.holoseogiapi.models.UserScheduleQueueItem;
import com.standalonejhgl.holoseogiapi.repository.NotificationQueueRepository;
import com.standalonejhgl.holoseogiapi.service.common.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserScheduleTodayEventListener {

    private final NotificationService notificationService;
    private final NotificationQueueRepository notificationQueueRepository;
    private final UserScheduleDao userScheduleDao;


    // 금일 삽입한 스케쥴은 알림 데이터가 생성되지 않아, 비동기로 삽입되도록 처리한다.
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handelUserTodayInsertSchedule(UserScheduleTodayInsertEvent event) {
        UserSchedule userSchedule = event.getUserSchedule();

        try {
            UserScheduleQueueItem dto = userScheduleDao.createUserNotificationItem(userSchedule.getUserScheduleId());

            LocalTime currentTime = LocalTime.now();

            if (dto != null && dto.getTimeOfDay().isBefore(currentTime))
                return;

            switch (dto.getRepeatType()) {
                case "ONCE":
                    notificationService.insertUserOnceNotification(dto, NotificationQueue.EnqueueSourceStatus.USER_UPDATE.name());
                    break;
                case "WEEKLY":
                    notificationService.insertUserWeekNotification(dto, NotificationQueue.EnqueueSourceStatus.USER_UPDATE.name());
                    break;
                case "MONTHLY":
                    notificationService.insertUserMonthNotification(dto, NotificationQueue.EnqueueSourceStatus.USER_UPDATE.name());
                    break;
            }

            log.info("✅ 오늘 알림 삽입 이벤트 처리 완료: userScheduleId={}", userSchedule.getUserScheduleId());
        } catch (Exception e) {
            log.error("❌ 오늘 알림 이벤트 삽입 처리 중 오류 발생: userScheduleId={}, msg={}", userSchedule.getUserScheduleId(), e.getMessage());
        }
    }

    // 금일 수정한 스케쥴이 금일 알림을 받아야할경우 사용된다.
    // 수정 이후, 금일 알람이 필요한경우 기존 Que삭제 후 새로운 Que를 삽입한다.
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handelUserTodayUpdateSchedule(UserScheduleTodayUpdateEvent event) {
        UserSchedule userSchedule = event.getUserSchedule();
        try {
            Long userScheduleId = userSchedule.getUserScheduleId();

            //기존 알람 que삭제
            notificationQueueRepository.deleteNotificationQueue(userScheduleId);

            UserScheduleQueueItem dto = userScheduleDao.createUserNotificationItem(userScheduleId);

            LocalTime currentTime = LocalTime.now();

            if (dto != null && dto.getTimeOfDay().isBefore(currentTime))
                return;

            //새로운 시간의 알람을 삽입
            switch (dto.getRepeatType()) {
                case "ONCE":
                    notificationService.insertUserOnceNotification(dto, NotificationQueue.EnqueueSourceStatus.USER_UPDATE.name());
                    break;
                case "WEEKLY":
                    notificationService.insertUserWeekNotification(dto ,NotificationQueue.EnqueueSourceStatus.USER_UPDATE.name());
                    break;
                case "MONTHLY":
                    notificationService.insertUserMonthNotification(dto, NotificationQueue.EnqueueSourceStatus.USER_UPDATE.name());
                    break;
            }

            log.info("✅ 오늘 알림 이벤트 수정 처리 완료: userScheduleId={}", userSchedule.getUserScheduleId());
        } catch (Exception e) {
            log.error("❌ 오늘 알림 이벤트 수정 처리 중 오류 발생: userScheduleId={}, msg={}", userSchedule.getUserScheduleId(), e.getMessage());
        }
    }
}