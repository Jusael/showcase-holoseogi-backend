package com.standalonejhgl.holoseogiapi.dtos.app;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleResponseDto {

    private String userScheduleId;
    private String title;
    private String category;
    private String categoryName;
    private String repeatType;
    private String timeOfDay;
    private String yyyyMmDd;
    private String dayOfWeek; // DB에서 "MON", "TUE" 등으로 들어옴
    private String weekDays;// DB에서 MON, TUE 반복 요일 직열화로 들어옴

    // "MON" → 1, "SUN" → 7
    @JsonIgnore
    public int getDayOfWeekValue() {
        return switch (dayOfWeek.toUpperCase()) {
            case "MON" -> 1;
            case "TUE" -> 2;
            case "WED" -> 3;
            case "THU" -> 4;
            case "FRI" -> 5;
            case "SAT" -> 6;
            case "SUN" -> 7;
            default -> throw new IllegalArgumentException("Invalid day: " + dayOfWeek);
        };
    }
}