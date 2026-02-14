package com.standalonejhgl.holoseogiapi.event;

import com.standalonejhgl.holoseogiapi.entity.UserSchedule;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserScheduleTodayUpdateEvent {
    private final UserSchedule UserSchedule;
}