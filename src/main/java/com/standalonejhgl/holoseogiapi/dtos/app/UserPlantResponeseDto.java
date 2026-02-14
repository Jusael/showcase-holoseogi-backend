package com.standalonejhgl.holoseogiapi.dtos.app;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPlantResponeseDto {
    private Long plantId;
    private String plantName;
    private String plantSpecies;
    private String memo;
    private String imageFileName;
    private int waterCycleDays;
    private LocalDate lastWateredDate;
    private int waterRemainingDaysValue;
}
