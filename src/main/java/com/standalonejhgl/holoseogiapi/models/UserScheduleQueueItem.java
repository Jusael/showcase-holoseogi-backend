package com.standalonejhgl.holoseogiapi.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserScheduleQueueItem {

    private String repeatType;       // ONCE / WEEKLY / MONTHLY
    private String userId;           // 사용자 ID
    private String title;            // 일정 제목
    private String category;         // 일정 카테고리 코드
    private String categoryName;     // 카테고리 이름 (공과금, 구독료 등)
    private LocalTime timeOfDay;     // 발송 시간 (유저가 지정)
    private LocalDate onceDate;      // 1회성 일정 날짜
    private String dayOfWeek;        // 요일 (WEEKLY)
    private Integer dayOfMonth;      // 날짜 (MONTHLY)
    private Long userScheduleId;
}