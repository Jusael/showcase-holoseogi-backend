package com.standalonejhgl.holoseogiapi.dtos.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JwtResponeseDto {
    private boolean result;
    private String jwtToken;
}
