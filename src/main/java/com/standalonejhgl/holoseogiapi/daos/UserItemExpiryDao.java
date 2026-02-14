
package com.standalonejhgl.holoseogiapi.daos;

import com.standalonejhgl.holoseogiapi.dtos.app.ItemExpiryResponseDto;
import com.standalonejhgl.holoseogiapi.models.UserItemExpirySummary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserItemExpiryDao {

    List<ItemExpiryResponseDto> getUserItemExpiryInfo(@Param("userId") String userId);

    List<UserItemExpirySummary> createFridgeNotificationList();

   }
