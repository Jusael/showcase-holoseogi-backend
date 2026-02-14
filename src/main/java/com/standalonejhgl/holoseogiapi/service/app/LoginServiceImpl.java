package com.standalonejhgl.holoseogiapi.service.app;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.standalonejhgl.holoseogiapi.dtos.app.ResultResponseDto;
import com.standalonejhgl.holoseogiapi.event.UserDeletedEvent;
import com.standalonejhgl.holoseogiapi.exception.BusinessException;
import com.standalonejhgl.holoseogiapi.exception.ErrorCode;
import com.standalonejhgl.holoseogiapi.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import com.standalonejhgl.holoseogiapi.entity.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginServiceImpl.class);

    private final ApplicationEventPublisher applicationEventPublisher;

    private final UserRepository userRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final UserScheduleRepository userScheduleRepository;
    private final UserItemExpiryRepository userItemExpiryRepository;
    private final UserSubscriptionAuditRepository userSubscriptionAuditRepository;
    private final  UserScheduleAuditRepository userScheduleAuditRepository;
    private final UserItemExpiryAuditRepository userItemExpiryAuditRepository;

    @Transactional
    @Override
    public ResultResponseDto IosSignIn(String fireBaseToken)
    {
        try {
            String[] userInfo = getUserInfo(fireBaseToken);

            if (userInfo == null) {
                throw new BusinessException(ErrorCode.INVALID_FIREBASE_TOKEN);
            }

            String userId = userInfo[0];

            User user = new User();
            user = userRepository.findById(userId).orElse(null);

            if (user == null) {
                user = new User();
                user.setUserId(userInfo[0]);
                user.setStatus("ACTIVE");
                user.setUserLevel(1);
                user.setPlatform("IOS");

                userRepository.save(user);

                UserSubscription userSubscription = new UserSubscription();
                userSubscription.setUserId(userId);
                userSubscription.setPlan(UserSubscription.Plan.FREE);
                userSubscription.setStatus(UserSubscription.Status.ACTIVE);
                userSubscription.setStartTime(LocalDateTime.now());

                userSubscriptionRepository.save(userSubscription);
            }

            return new ResultResponseDto(true, "Sign In Successful");
        }catch (DuplicateKeyException e) {
            throw new BusinessException(ErrorCode.DUPLICATE_USER);
        }

    }

    @Transactional
    @Override
    public ResultResponseDto updateFcmToken(String fireBaseToken, String userId)
    {
        try {
            FcmToken fcmToken =  fcmTokenRepository.findById(userId).orElse(null);

            if(fcmToken == null) {
                fcmToken = new FcmToken();
                fcmToken.setUserId(userId);
                fcmToken.setFcmToken(fireBaseToken);
                fcmToken.setActive(true);
                fcmToken.setCreateTime(LocalDateTime.now());

                fcmTokenRepository.save(fcmToken);
            }

            fcmToken.setFcmToken(fireBaseToken);
            fcmToken.setLastRefreshTime(LocalDateTime.now());
            fcmTokenRepository.save(fcmToken);

            return new ResultResponseDto(true, "FCM Token Updated Successfully");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessException(ErrorCode.INVALID_FIREBASE_TOKEN);
        }
    }

    /// FirebaseToken 유효성 검사 후, uId반환
    public String[] getUserInfo(String idToken) {
        try {
            // revoke 검사까지 하려면 두 번째 인자 true
            FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(idToken);

            // 필요정보 꺼내기
            String uid = decoded.getUid();
            String email = decoded.getEmail();                 // null일 수 있음
            Boolean emailVerified = decoded.isEmailVerified();  // 이메일 로그인일 때만 의미
            // Map<String, Object> customClaims = decoded.getClaims();

            log.info("✅ Firebase 토큰 검증 성공 uid={}, email={}, emailVerified={}", uid, email, emailVerified);

            return new String[]{uid, email};
        } catch (FirebaseAuthException e) {
            log.warn("❌ Firebase 토큰 검증 실패: {}", e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            log.warn("❌ 잘못된 토큰 형식: {}", e.getMessage());
            return null;
        }
    }


    @Override
    @Transactional
    public ResultResponseDto deleteAccount(String userId)
    {
        userRepository.deleteById(userId);

        //사용자 fcm토큰 삭제
        fcmTokenRepository.deleteByFcmToken(userId);

        //사용자 구독 정보 삭제
        userSubscriptionRepository.deleteByUserSubscription(userId);

        //UserDeletedEventListener 비동기 호출로 속도 향상
        applicationEventPublisher.publishEvent(new UserDeletedEvent(userId));

        return new ResultResponseDto(true, "Account Deleted Successfully");
    }
}
