package com.standalonejhgl.holoseogiapi.dtos.app;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDispatchDto {
    private Long notificationQueueId;
    private String userId;
    private String fcmToken;
    private String title;
    private String body;
    private String jsonData;
}