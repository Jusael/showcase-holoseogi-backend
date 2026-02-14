package com.standalonejhgl.holoseogiapi.daos;

import com.standalonejhgl.holoseogiapi.models.UserSubscriptionVerifyList;

import org.springframework.data.jpa.repository.Modifying;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface AppleSubscriptionVerifyDao {

    /**
     * <p>하루 한번 새벽 3시에 사용자 구독 정보를 최신화 한다.</p>
     *
     */
    List<UserSubscriptionVerifyList> getListVerifyUserSubscriptions();
}
