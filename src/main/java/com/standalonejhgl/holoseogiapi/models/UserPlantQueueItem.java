package com.standalonejhgl.holoseogiapi.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


/**
 * 식물 물주기 fcm메세지 정보
 * 꽃 다발, 물주기 오늘
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPlantQueueItem {
    private String userId;
    private long plantId;
    private String plantName;
    private String plantSpecies;
    private int waterRemainingDaysValue;
}
