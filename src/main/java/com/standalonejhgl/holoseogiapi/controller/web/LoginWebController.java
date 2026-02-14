package com.standalonejhgl.holoseogiapi.controller.web;


import com.standalonejhgl.holoseogiapi.dtos.common.JwtResponeseDto;
import com.standalonejhgl.holoseogiapi.dtos.web.LoginAdminRequestDto;
import com.standalonejhgl.holoseogiapi.service.web.LoginWebService;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/auth")
public class LoginWebController {

    private final LoginWebService loginWebService;


    @PostMapping("/web-login")
    public ResponseEntity<JwtResponeseDto> loginAdminPage(@RequestBody LoginAdminRequestDto dto) {

        return ResponseEntity.ok(loginWebService.LoginWeb(dto));
    }

}