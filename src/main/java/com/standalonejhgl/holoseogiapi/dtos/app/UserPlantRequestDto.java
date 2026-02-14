package com.standalonejhgl.holoseogiapi.dtos.app;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserPlantRequestDto {
    private Long plantId;
    private String plantName;
    private String plantSpecies;
    private String memo;
    private String imageFileName;
    private int waterCycleDays;
    private LocalDate lastWateredDate;
}
