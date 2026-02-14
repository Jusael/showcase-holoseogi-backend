package com.standalonejhgl.holoseogiapi.service.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.standalonejhgl.holoseogiapi.entity.NotificationQueue;
import com.standalonejhgl.holoseogiapi.models.UserItemExpirySummary;
import com.standalonejhgl.holoseogiapi.models.UserPlantQueueItem;
import com.standalonejhgl.holoseogiapi.models.UserScheduleQueueItem;
import com.standalonejhgl.holoseogiapi.repository.NotificationQueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationQueueRepository notificationQueueRepository;

    private enum QueueStatus {
        READY,
        FAILED
    }

    /**
     * 🔹 반복유형별 알림 제목 생성
     */
    private static String getTitle(String repeatType, String dayOfWeek, Integer dayOfMonth) {
        switch (repeatType) {
            case "ONCE":
                return "오늘 예정된 일정이 있어요";
            case "WEEKLY":
                return String.format("이번 주 %s 일정이 있어요!", convertDayOfWeek(dayOfWeek));
            case "MONTHLY":
                return String.format("매달 %d일 일정이 있어요!", dayOfMonth);
            default:
                return "예정된 일정 알림이에요";
        }
    }

    private static String convertDayOfWeek(String code) {
        switch (code) {
            case "SUN":
                return "일요일";
            case "MON":
                return "월요일";
            case "TUE":
                return "화요일";
            case "WED":
                return "수요일";
            case "THU":
                return "목요일";
            case "FRI":
                return "금요일";
            case "SAT":
                return "토요일";
            default:
                return "";
        }
    }

    /**
     * 🔹 카테고리 + 반복유형별 알림 메시지 생성
     */

    public static String getBody(String repeatType, String category, String title) {
        switch (category) {
            case "DISPOSAL":
                return repeatType.equals("ONCE") ?
                        String.format("오늘은 %s 분리수거 하는 날이에요.\n잊지 말고 실천해요!", title)
                        : String.format("%s 분리수거 일정이에요.\n이번 %s에도 꼭 실천해요!", title, repeatType.equals("WEEKLY") ? "주" : "달");

            case "RENT":
                return String.format("오늘은 %s 납부일이에요.\n지금 바로 확인해요!", title);

            case "UTILITY":
                return String.format("오늘은 %s 공과금 납부일이에요.\n잊지 말고 처리해요!", title);

            case "SUBSCRIPTION":
                return String.format("오늘은 %s 구독료 결제일이에요.\n해지가 고민되시나요?", title);

            case "ETC":
                return String.format("%s 일정이 예정돼 있어요.\n확인해볼까요?", title);

            case "APPOINTMENT":
                return String.format("%s 약속이 예정돼 있어요.\n잊지 말아요!", title);

            default:
                return String.format("%s 일정이 다가왔어요.", title);
        }
    }

    /**
     * ✅ 하루 일정 (ONCE)
     */
    @Override
    public void insertUserOnceNotification(UserScheduleQueueItem item,String enqueueSourceStatus) {

        this.writeLog(item);

        NotificationQueue queue = new NotificationQueue();
        queue.setUserId(item.getUserId());
        queue.setUserScheduleId(item.getUserScheduleId());
        queue.setStatus(QueueStatus.READY.name());
        queue.setTitle(getTitle(item.getRepeatType(), item.getDayOfWeek(), item.getDayOfMonth()));
        queue.setBody(getBody(item.getRepeatType(), item.getCategory(), item.getTitle()));
        //USER_UPDATE / SCHEDULER / SYSTEM
        queue.setEnqueueSource(enqueueSourceStatus);
        queue.setEnqueueThread(Thread.currentThread().getName());
        queue.setEnqueueTrace(Thread.currentThread().getStackTrace()[2].toString());
        LocalDateTime dateTime = getTargetSendDateTime(item);

        queue.setSendTime(dateTime);

        notificationQueueRepository.save(queue);
    }

    /**
     * ✅ 매주 일정 (WEEKLY)
     */
    @Override
    public void insertUserWeekNotification(UserScheduleQueueItem item ,String enqueueSourceStatus) {

        this.writeLog(item);

        NotificationQueue queue = new NotificationQueue();
        queue.setUserId(item.getUserId());
        queue.setUserScheduleId(item.getUserScheduleId());
        queue.setStatus(QueueStatus.READY.name());
        queue.setTitle(getTitle(item.getRepeatType(), item.getDayOfWeek(), item.getDayOfMonth()));
        queue.setBody(getBody(item.getRepeatType(), item.getCategory(), item.getTitle()));
        queue.setEnqueueSource(enqueueSourceStatus);
        queue.setEnqueueThread(Thread.currentThread().getName());
        queue.setEnqueueTrace(Thread.currentThread().getStackTrace()[2].toString());
        LocalDateTime dateTime = getTargetSendDateTime(item);

        queue.setSendTime(dateTime);

        notificationQueueRepository.save(queue);
    }

    /**
     * ✅ 매월 일정 (MONTHLY)
     */
    @Override
    public void insertUserMonthNotification(UserScheduleQueueItem item,String enqueueSourceStatus) {
        this.writeLog(item);

        NotificationQueue queue = new NotificationQueue();
        queue.setUserId(item.getUserId());
        queue.setUserScheduleId(item.getUserScheduleId());
        queue.setStatus(QueueStatus.READY.name());
        queue.setTitle(getTitle(item.getRepeatType(), item.getDayOfWeek(), item.getDayOfMonth()));
        queue.setBody(getBody(item.getRepeatType(), item.getCategory(), item.getTitle()));
        queue.setEnqueueSource(enqueueSourceStatus);
        queue.setEnqueueThread(Thread.currentThread().getName());
        queue.setEnqueueTrace(Thread.currentThread().getStackTrace()[2].toString());
        LocalDateTime dateTime = getTargetSendDateTime(item);

        queue.setSendTime(dateTime);

        notificationQueueRepository.save(queue);
    }

    /**
     * ✅ 유통기한 임박 알림
     */
    @Override
    public void insertUserItemExpiryNotification(UserItemExpirySummary item) {

        NotificationQueue queue = new NotificationQueue();
        queue.setUserId(item.getUserId());
        queue.setStatus(QueueStatus.READY.name());
        queue.setTitle("유통기한 임박 알림");

        //바디 안내문구
        String body;
        if (item.getItemList().contains(",")) {

            String[] items = item.getItemList().split(",");

            if (items.length > 3) {
                body = String.format("%s 외 %d개의 식재료가 곧 유통기한이 다가와요.\n임박한 재료로 레시피를 만들어 드릴까요?",
                        items[0].trim(), items.length - 1);
            } else {
                body = String.format("다음 식재료의 유통기한이 곧 다가와요. (%s)\n임박한 재료로 레시피를 만들어 드릴까요?", item.getItemList());
            }
        } else {
            body = String.format("%s의 유통기한이 곧 다가와요!\n임박한 재료로 레시피를 만들어 드릴까요?", item.getItemList());
        }
        queue.setBody(body);

        queue.setEnqueueSource(NotificationQueue.EnqueueSourceStatus.SCHEDULER.name());
        queue.setEnqueueThread(Thread.currentThread().getName());
        queue.setEnqueueTrace(Thread.currentThread().getStackTrace()[2].toString());
        //데이터 제이슨
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> json = new HashMap<>();
            json.put("type", "EXPIRY_ALERT");
            json.put("userId", item.getUserId());
            json.put("itemList", item.getItemList());

            queue.setJsonData(mapper.writeValueAsString(json));

        } catch (JsonProcessingException e) {
            e.printStackTrace(); // or log.error("JSON 변환 실패", e);
            queue.setJsonData(null);
        }

        LocalDate today = LocalDate.now();
        LocalTime dinnerTime = LocalTime.parse("19:00");
        LocalDateTime sendTime = LocalDateTime.of(today, dinnerTime);
        queue.setSendTime(sendTime);

        notificationQueueRepository.save(queue);
    }

    /**
     * ✅ 식물 물주기 알람
     */
    @Override
    public void insertUserPlantWaterRemainNotification(UserPlantQueueItem item) {

        NotificationQueue queue = new NotificationQueue();
        queue.setUserId(item.getUserId());
        queue.setStatus(QueueStatus.READY.name());
        queue.setTitle( String.format("%s 물 주는날!", item.getPlantName()));

        String bodyStr;

        if(item.getWaterRemainingDaysValue() == 0)
            bodyStr = String.format("오늘 %s 물주는 날이에요.\n잊지말고 꼭 주세요", item.getPlantName());
        else if (item.getWaterRemainingDaysValue() == 1)
            bodyStr = String.format("어제 %s 물주는 날이었어요.\n오늘은 잊지말고 꼭 주세요!", item.getPlantName());
        else return;

        queue.setBody(bodyStr);

        queue.setEnqueueSource(NotificationQueue.EnqueueSourceStatus.SCHEDULER.name());
        queue.setEnqueueThread(Thread.currentThread().getName());
        queue.setEnqueueTrace(Thread.currentThread().getStackTrace()[2].toString());

        //데이터 제이슨
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> json = new HashMap<>();
            json.put("type", "PLANTS_ALERT");
            json.put("userId", item.getUserId());
            json.put("itemList", item.getPlantName());

            queue.setJsonData(mapper.writeValueAsString(json));

        } catch (JsonProcessingException e) {
            e.printStackTrace(); // or log.error("JSON 변환 실패", e);
            queue.setJsonData(null);
        }

        LocalDate today = LocalDate.now();
        LocalTime dinnerTime = LocalTime.parse("18:00");
        LocalDateTime sendTime = LocalDateTime.of(today, dinnerTime);
        queue.setSendTime(sendTime);

        notificationQueueRepository.save(queue);
    }

    private LocalDateTime getTargetSendDateTime(UserScheduleQueueItem item) {
        LocalDate today = LocalDate.now();
        LocalTime time = item.getTimeOfDay();

        switch (item.getRepeatType()) {
            case "ONCE":
                return LocalDateTime.of(item.getOnceDate(), time);

            case "WEEKLY":
                // 이번 주 요일 찾아서
                DayOfWeek dayOfWeek;
                switch (item.getDayOfWeek()) {
                    case "SUN":
                        dayOfWeek = DayOfWeek.SUNDAY;
                        break;
                    case "MON":
                        dayOfWeek = DayOfWeek.MONDAY;
                        break;
                    case "TUE":
                        dayOfWeek = DayOfWeek.TUESDAY;
                        break;
                    case "WED":
                        dayOfWeek = DayOfWeek.WEDNESDAY;
                        break;
                    case "THU":
                        dayOfWeek = DayOfWeek.THURSDAY;
                        break;
                    case "FRI":
                        dayOfWeek = DayOfWeek.FRIDAY;
                        break;
                    case "SAT":
                        dayOfWeek = DayOfWeek.SATURDAY;
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid dayOfWeek code: " + item.getDayOfWeek());
                }

                LocalDate weekTarget = today.with(TemporalAdjusters.nextOrSame(dayOfWeek));
                return LocalDateTime.of(weekTarget, time);

            case "MONTHLY":
                // 이번 달 해당 일자 (이미 day_of_month 컬럼 있음)
                LocalDate monthTarget = today.withDayOfMonth(item.getDayOfMonth());
                return LocalDateTime.of(monthTarget, time);

            default:
                throw new IllegalArgumentException("잘못된 repeatType: " + item.getRepeatType());
        }
    }

    private void writeLog(UserScheduleQueueItem item) {
        StackTraceElement caller =
                Thread.currentThread().getStackTrace()[3]; // 바로 위 호출자

        log.warn(
                "🔔 [NotificationInsert] type={}, userId={}, scheduleId={}, sendTime={}, now={}, thread={}, caller={}.{}:{}",
                item.getRepeatType(),
                item.getUserId(),
                item.getUserScheduleId(),
                getTargetSendDateTime(item),
                LocalDateTime.now(),
                Thread.currentThread().getName(),
                caller.getClassName(),
                caller.getMethodName(),
                caller.getLineNumber()
        );
    }
}