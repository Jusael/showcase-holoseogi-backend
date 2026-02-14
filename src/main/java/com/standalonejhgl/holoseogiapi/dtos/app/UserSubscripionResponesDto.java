package com.standalonejhgl.holoseogiapi.dtos.app;

import com.standalonejhgl.holoseogiapi.entity.UserSubscription;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSubscripionResponesDto {
    private UserSubscription.Plan plan;
    private UserSubscription.Status status;
    private LocalDateTime expireTime;
}
