package com.standalonejhgl.holoseogiapi.service.web;

import com.standalonejhgl.holoseogiapi.dtos.common.JwtResponeseDto;
import com.standalonejhgl.holoseogiapi.dtos.web.LoginAdminRequestDto;

public interface LoginWebService {
    JwtResponeseDto LoginWeb(LoginAdminRequestDto dto);
}
