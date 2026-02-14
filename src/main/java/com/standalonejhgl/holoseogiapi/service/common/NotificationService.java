package com.standalonejhgl.holoseogiapi.service.common;

import com.standalonejhgl.holoseogiapi.models.UserItemExpirySummary;
import com.standalonejhgl.holoseogiapi.models.UserPlantQueueItem;
import com.standalonejhgl.holoseogiapi.models.UserScheduleQueueItem;

public interface NotificationService {

    void insertUserOnceNotification(UserScheduleQueueItem userScheduleQueueItem,String enqueueSourceStatus);

    void insertUserWeekNotification(UserScheduleQueueItem userScheduleQueueItem,String enqueueSourceStatus);

    void insertUserMonthNotification(UserScheduleQueueItem userScheduleQueueItem,String enqueueSourceStatus);

    void insertUserItemExpiryNotification(UserItemExpirySummary userItemExpirySummary);

    void insertUserPlantWaterRemainNotification(UserPlantQueueItem item);
}
