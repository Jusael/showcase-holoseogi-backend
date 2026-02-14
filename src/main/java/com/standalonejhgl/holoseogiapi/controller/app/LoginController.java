package com.standalonejhgl.holoseogiapi.controller.app;


import com.standalonejhgl.holoseogiapi.dtos.app.LoginRequestDto;
import com.standalonejhgl.holoseogiapi.dtos.app.ResultResponseDto;
import com.standalonejhgl.holoseogiapi.dtos.common.JwtResponeseDto;
import com.standalonejhgl.holoseogiapi.service.common.JwtService;
import com.standalonejhgl.holoseogiapi.service.app.LoginService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/login")
public class LoginController {
	
	private static final Logger log = LoggerFactory.getLogger(LoginController.class);
	private final LoginService loginService;
    private final JwtService jwtService;

    @GetMapping("/get-login")
	public ResponseEntity login (@ModelAttribute LoginRequestDto loginRequestDto) {
		
		log.info(String.format(" 로그인 요청  postuserinfo UserId: {%s}",loginRequestDto.getUserId()));
		
		return ResponseEntity.ok("userId = " + loginRequestDto.getUserId());
	}

    @PostMapping("/apple-sign-in")
    public ResponseEntity<ResultResponseDto> signIn(@RequestHeader("Authorization") String authorization) {

        log.info("✅ Flutter에서 sign-in 요청 도착: {}", authorization);

        String firebaseToken = authorization.replace("Bearer ", "").trim();

        return ResponseEntity.ok(loginService.IosSignIn(firebaseToken));
    }

    @PostMapping("/post-jwt-token")
    public ResponseEntity<JwtResponeseDto> getJwtToken(@RequestHeader("Authorization") String authorization)
    {
        log.info("✅ Flutter에서 jwt 발급 요청 도착: {}", authorization);

        String idToken = authorization.replace("Bearer ", "").trim();

        String jwtToken =  jwtService.issueToken(idToken, 3600);

        return ResponseEntity.ok(new JwtResponeseDto(true, jwtToken));
    }

    @PostMapping("/post-fcm-token")
    public ResponseEntity<ResultResponseDto> postFcmToken(@RequestHeader("Authorization") String authorization, @RequestHeader("UserId") String userId)
    {
        log.info("✅ Flutter에서 FCM 토큰 발급 요청 도착: {}", authorization);
        String fcmToken = authorization.replace("Bearer ", "").trim();
        log.info("✅ 사용자 {} 신규 FCM토큰 : {} ", userId, fcmToken);

        return ResponseEntity.ok(loginService.updateFcmToken(fcmToken, userId));
    }

    @DeleteMapping("/delete-user-data")
    public  ResponseEntity<ResultResponseDto> deleteUserData(@RequestHeader("UserId") String userId)
    {
        log.info("사용자 회원탈퇴 요청: {}", userId);
        return  ResponseEntity.ok( loginService.deleteAccount(userId));
    }
}