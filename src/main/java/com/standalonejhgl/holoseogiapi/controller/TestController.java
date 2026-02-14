package com.standalonejhgl.holoseogiapi.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.standalonejhgl.holoseogiapi.daos.NotificationDao;
import com.standalonejhgl.holoseogiapi.dtos.app.NotificationDispatchDto;
import com.standalonejhgl.holoseogiapi.entity.AdminUser;
import com.standalonejhgl.holoseogiapi.repository.AdminUserRepository;
import com.standalonejhgl.holoseogiapi.repository.NotificationQueueLogRepository;
import com.standalonejhgl.holoseogiapi.repository.NotificationQueueRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController
{
    private static final Logger log = LoggerFactory.getLogger(TestController.class);
    private final ObjectMapper objectMapper= new ObjectMapper();
    private final NotificationQueueRepository notificationQueueRepository;
    private final NotificationQueueLogRepository notificationQueueLogRepository;
    private final NotificationDao notificationDao;
    private final FirebaseMessaging firebaseMessaging;
    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/fcm-test")
    public ResponseEntity test () {

        List<NotificationDispatchDto> arr =  notificationDao.fcmTest();

        arr.forEach(item -> {
            sendMessage(item);
        });

        return ResponseEntity.ok("되랏");
    }

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

            try {
                String response = firebaseMessaging.send(message);
                log.info("✅ FCM 전송 성공: {}", response);
            } catch (FirebaseMessagingException e) {
                log.error("🚨 FCM 전송 실패: {}", e.getMessage(), e);
            }


        } catch (Exception e) {
            log.info("??");
        }
    }


    @PostMapping("/create-admin")
    public ResponseEntity createAdmin () {

        AdminUser adminUser = AdminUser.builder()
                .loginId("")
                .passwordHash(passwordEncoder.encode(""))
                .role("")
                .useYn("")
                .build();

        adminUserRepository.save(adminUser);


        return ResponseEntity.ok("되랏");
    }

}
