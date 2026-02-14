package com.standalonejhgl.holoseogiapi.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserDeletedEvent {
    private final String userId;
}