package com.standalonejhgl.holoseogiapi.dtos.app;


import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleRequestDto {

    private Long scheduleId;
    private String title;
    private String category;
    private String repeatType;
    private LocalTime timeOfDay;
    private List<String> weekDays;
    private String yyyyMmDd;
}

