package com.standalonejhgl.holoseogiapi.service.common;

import com.standalonejhgl.holoseogiapi.dtos.app.NotificationDispatchDto;

public interface FcmService {
    void sendMessage(NotificationDispatchDto dispatchDto);
}
