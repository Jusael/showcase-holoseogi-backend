package com.standalonejhgl.holoseogiapi.service.app;

import com.standalonejhgl.holoseogiapi.dtos.app.DateRequestDto;
import com.standalonejhgl.holoseogiapi.dtos.app.ResultResponseDto;
import com.standalonejhgl.holoseogiapi.dtos.app.ScheduleRequestDto;
import com.standalonejhgl.holoseogiapi.dtos.app.ScheduleResponseDto;

import java.util.List;

public interface UserScheduleService {

    /**
     * <p>유저의 알림을 조회한다.</p>
     * <p>일시적, 월간 알림에 주마다 반복을 추가하여 조회합니다.</p>
     * @return ONCE, MONTHLY 일정 리스트
     */
    List<ScheduleResponseDto> getUserSchedules(String userId, DateRequestDto dateRequestDto);

    void deleteUserSchedule(Long scheduleId);

    ResultResponseDto insertUserSchedule(String userId, ScheduleRequestDto scheduleRequestDto);

    ResultResponseDto updateUserSchedule(String userId, ScheduleRequestDto scheduleRequestDto);
}
