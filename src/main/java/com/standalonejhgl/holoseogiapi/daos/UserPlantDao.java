
package com.standalonejhgl.holoseogiapi.daos;

import com.standalonejhgl.holoseogiapi.models.UserPlantQueueItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserPlantDao {

    List<UserPlantQueueItem> createUserPlantNotificationList();
}
