
package com.standalonejhgl.holoseogiapi.daos;

import com.standalonejhgl.holoseogiapi.dtos.app.NotificationDispatchDto;
import com.standalonejhgl.holoseogiapi.models.UserScheduleQueueItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NotificationDao {


    List<UserScheduleQueueItem> createOnceNotificationList();
    List<UserScheduleQueueItem> createWeekNotificationList();
    List<UserScheduleQueueItem> createMonthNotificationList();
    List<NotificationDispatchDto> selectReadyNotificationItems();
    List<NotificationDispatchDto> fcmTest();

}
