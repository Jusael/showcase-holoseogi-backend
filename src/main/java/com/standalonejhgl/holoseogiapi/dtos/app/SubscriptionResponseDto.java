package com.standalonejhgl.holoseogiapi.dtos.app;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionResponseDto {
    private boolean result;
    private int userLevel;
}
