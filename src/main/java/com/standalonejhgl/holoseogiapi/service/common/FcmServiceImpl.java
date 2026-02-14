package com.standalonejhgl.holoseogiapi.service.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.standalonejhgl.holoseogiapi.dtos.app.NotificationDispatchDto;
import com.standalonejhgl.holoseogiapi.entity.NotificationQueue;
import com.standalonejhgl.holoseogiapi.entity.NotificationQueueLog;
import com.standalonejhgl.holoseogiapi.repository.NotificationQueueLogRepository;
import com.standalonejhgl.holoseogiapi.repository.NotificationQueueRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FcmServiceImpl implements FcmService {

    private static final Logger log = LoggerFactory.getLogger(FcmServiceImpl.class);
    private final ObjectMapper objectMapper= new ObjectMapper();
    private final NotificationQueueRepository notificationQueueRepository;
    private final NotificationQueueLogRepository notificationQueueLogRepository;
    private final FirebaseMessaging firebaseMessaging;

    private  enum SendStatus
    {
        READY,
        SEND_SUCCESS,
        FAILED
    }

    SendStatus status = SendStatus.READY;

    public void sendMessage(NotificationDispatchDto dispatchDto) {

        try {
            Map<String, Object> jsonMap = new HashMap<>();
            try {
                if (dispatchDto.getJsonData() != null && !dispatchDto.getJsonData().isBlank()) {
                    jsonMap = objectMapper.readValue(dispatchDto.getJsonData(), new TypeReference<>() {});
                }
            } catch (Exception e) {
                log.warn("⚠️ JSON 파싱 실패, 기본값 사용: {}", e.getMessage());
            }

            String type = (String) jsonMap.getOrDefault("type", "USER_SCHEDULED");
            String itemList = (String) jsonMap.getOrDefault("itemList", "");

            Message message = Message.builder()
                    .setToken(dispatchDto.getFcmToken())
                    .setNotification(Notification.builder()
                            .setTitle(dispatchDto.getTitle())
                            .setBody(dispatchDto.getBody())
                            .build())
                    .putData("type", type)
                    .putData("itemList", itemList)
                    .build();

            ApiFuture<String> future = firebaseMessaging.sendAsync(message);

            future.addListener(() -> {
                try {
                    String response = future.get();

                    log.info("✅ FCM 전송 성공: {}", response);

                    // 전송이 성공 처리 되었으나 디바이스에 알람이 오지 않는 경우존재
                    if(response != null) {
                        updateStatusMessage(dispatchDto, status.SEND_SUCCESS);
                    }
                    else {
                        updateStatusMessage(dispatchDto, status.FAILED);
                    }
                } catch (Exception e) {
                    failSendMessage(dispatchDto, e);
                    updateStatusMessage(dispatchDto, status.FAILED);
                }
            }, Runnable::run);


        } catch (Exception e) {
            updateStatusMessage(dispatchDto, status.FAILED);
        }
    }

    private  void updateStatusMessage(NotificationDispatchDto dispatchDto, SendStatus sendStatus) {
        NotificationQueue queue = notificationQueueRepository.findById(dispatchDto.getNotificationQueueId()).orElse(null);

        if(queue == null)
            return;
        queue.setStatus(sendStatus.name());
        notificationQueueRepository.save(queue);
    }

    private  void failSendMessage(NotificationDispatchDto dispatchDto, Exception e) {

        NotificationQueueLog queueLog = new NotificationQueueLog();
        queueLog.setNotificationQueueId(dispatchDto.getNotificationQueueId());
        queueLog.setUserId(dispatchDto.getUserId());
        queueLog.setStatus("FAILED");
        queueLog.setTitle(dispatchDto.getTitle());
        queueLog.setBody(dispatchDto.getBody());
        queueLog.setJsonData(dispatchDto.getJsonData());

        String errorMsg;

        Throwable cause = e.getCause();
        if (cause instanceof FirebaseMessagingException fe) {
            String error = fe.getErrorCode().toString();
            switch (error) {
                case "unregistered":
                case "invalid-registration-token":
                    errorMsg = String.format("❌ 유효하지 않은 토큰 → DB에서 제거필요: %s", dispatchDto.getFcmToken());
                    break;
                case "unavailable":
                case "internal":
                    errorMsg = "⚠️ Firebase 서버 일시적 오류 → 재시도 큐 등록 예정";
                    break;
                case "invalid-argument":
                    errorMsg = String.format("🚨 잘못된 요청: %s", e.getMessage());
                    break;
                default:
                    errorMsg = String.format("🚨 알 수 없는 오류 발생: %s", e.getMessage());
            }
        } else {
            errorMsg = String.format("🔥 비예상 예외 발생: %s", cause.getMessage());
        }

        queueLog.setErrorMessage(errorMsg);
        queueLog.setSentAt(LocalDateTime.now());
        notificationQueueLogRepository.save(queueLog);
    }
}