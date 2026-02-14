package com.standalonejhgl.holoseogiapi.service.app;

import com.standalonejhgl.holoseogiapi.dtos.app.ResultResponseDto;
import com.standalonejhgl.holoseogiapi.dtos.app.UserPlantRequestDto;
import com.standalonejhgl.holoseogiapi.dtos.app.UserPlantResponeseDto;
import com.standalonejhgl.holoseogiapi.dtos.app.UserSubscripionResponesDto;
import com.standalonejhgl.holoseogiapi.entity.User;
import com.standalonejhgl.holoseogiapi.entity.UserSerialKey;
import com.standalonejhgl.holoseogiapi.entity.UserSubscription;
import com.standalonejhgl.holoseogiapi.repository.UserRepository;
import com.standalonejhgl.holoseogiapi.repository.UserSerialKeyRepository;
import com.standalonejhgl.holoseogiapi.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface UserPlantService {

    List<UserPlantResponeseDto> getListPlantInfo(String userId);

    ResultResponseDto insertPlantInfo(String userId, UserPlantRequestDto userPlantRequestDto);

    ResultResponseDto updatePlantInfo(String userId, UserPlantRequestDto userPlantRequestDto);

    ResultResponseDto deletePlantInfo(Long plantId, String userId);

    interface SubscriptionService {

         UserSubscripionResponesDto getUserSubscripion(String userId);

         ResultResponseDto registerSerialKey(String userId, String serialKey);
    }

    @Service
    @RequiredArgsConstructor
    class SubscriptionServiceImpl implements SubscriptionService {

        private static final Logger log = LoggerFactory.getLogger(SubscriptionServiceImpl.class);
        private  final UserSubscriptionRepository userSubscriptionRepository;
        private final UserSerialKeyRepository userSerialKeyRepository;
        private final UserRepository userRepository;

        public UserSubscripionResponesDto getUserSubscripion(String userId) {
            UserSubscription sub = userSubscriptionRepository.findByUserId(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User subscription not found"));

            checkAndExpireIfNeeded(sub);

            return new UserSubscripionResponesDto(sub.getPlan(), sub.getStatus(), sub.getExpireTime());
        }

        private void checkAndExpireIfNeeded(UserSubscription sub) {
            if (sub.getPlan() == UserSubscription.Plan.FREE &&
                    sub.getExpireTime().isBefore(LocalDateTime.now())) {

                sub.setStatus(UserSubscription.Status.EXPIRED);
                sub.setUpdateTime(LocalDateTime.now());
                userSubscriptionRepository.save(sub);

            }
        }

        //시리얼 키등록 함수
        @Transactional
        public ResultResponseDto registerSerialKey(String userId, String serialKey){
            UserSerialKey userSerialKey  = userSerialKeyRepository.findBySerialCode(serialKey.trim()).orElse(null);



            if(userSerialKey == null )
                return new ResultResponseDto(false, "등록된 시리얼 키가 없습니다.");

            if(!userSerialKey.getStatus().equals("ready"))
                return new ResultResponseDto(false, "이미 사용된 시리얼 키입니다.");

            userSerialKey.setUserId(userId);
            userSerialKey.setStatus("used");
            userSerialKey.setActivatedAt(LocalDateTime.now());
            userSerialKey.setExpiresAt(LocalDateTime.now().plusYears(10));
            userSerialKey.setUpdatedAt(LocalDateTime.now());

            userSerialKeyRepository.save(userSerialKey);

            User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
            user.setUserLevel(5);

            userRepository.save(user);

            return new ResultResponseDto(true, "시리얼키 등록에 성공하였습니다. \n 앱을 종료 후 재실행 하면 적용됩니다.");
        }
    }
}
