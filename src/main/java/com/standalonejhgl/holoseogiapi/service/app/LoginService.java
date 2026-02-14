package com.standalonejhgl.holoseogiapi.service.app;


import com.standalonejhgl.holoseogiapi.dtos.app.ResultResponseDto;

public interface LoginService {

    ResultResponseDto IosSignIn(String firebaseToken);

    ResultResponseDto updateFcmToken(String fireBaseToken, String userId);

    ResultResponseDto deleteAccount(String userId);
}
