package com.standalonejhgl.holoseogiapi.daos;

import com.standalonejhgl.holoseogiapi.dtos.app.DateRequestDto;
import com.standalonejhgl.holoseogiapi.dtos.app.ScheduleResponseDto;
import com.standalonejhgl.holoseogiapi.models.UserScheduleQueueItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface UserScheduleDao {

    /**
     * <p>유저의 하루 알림 또는 월간 알림을 조회한다.</p>
     * @return ONCE, MONTHLY 일정 리스트
     */
    List<ScheduleResponseDto> getOnceAndMonthSchedules(@Param("userId") String userId,@Param("dto") DateRequestDto dateRequestDto);


    /**
     * <p>유저의 주 마다 알림을 조회한다.</p>
     * @return Weekly 일정 리스트
     */
    List<ScheduleResponseDto> getOnceAndWeekSchedules(@Param("userId") String userId,@Param("dto") DateRequestDto dateRequestDto);


    /**
     * <p>사용자가 금일 알람 생성 시 사용</p>
     * @return 금일 알람에대한 노티피케이션큐 생성용
     */
    UserScheduleQueueItem createUserNotificationItem(@Param("userScheduleId") Long userScheduleId);
}
