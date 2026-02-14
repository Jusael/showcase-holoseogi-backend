package com.standalonejhgl.holoseogiapi.service.web;

import com.standalonejhgl.holoseogiapi.dtos.common.JwtResponeseDto;
import com.standalonejhgl.holoseogiapi.dtos.web.LoginAdminRequestDto;
import com.standalonejhgl.holoseogiapi.entity.AdminUser;
import com.standalonejhgl.holoseogiapi.exception.BusinessException;
import com.standalonejhgl.holoseogiapi.exception.ErrorCode;
import com.standalonejhgl.holoseogiapi.repository.AdminUserRepository;
import com.standalonejhgl.holoseogiapi.service.common.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginWebServiceImpl implements LoginWebService {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    @Override
    public JwtResponeseDto LoginWeb(LoginAdminRequestDto dto) {

        AdminUser adminUser = adminUserRepository.findByLoginIdAndUseYn(dto.getUserId(), "Y").orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        boolean isMatch =
                passwordEncoder.matches(dto.getPassword(), adminUser.getPasswordHash());

        if(!isMatch)
            return new JwtResponeseDto(false, "");

        String jwtToken =  jwtService.issueToken(adminUser.getLoginId(), 600);

        return new JwtResponeseDto(true, jwtToken);
    }
}
