package com.standalonejhgl.holoseogiapi.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationQueueItem {
    private String userId;     // FCM 보낼 대상 사용자
    private String title;      // 알림 제목
    private String body;       // 알림 내용
    private String jsonData;   // 추가 데이터(JSON 문자열)
    private LocalTime sendTime;
}