package com.standalonejhgl.holoseogiapi.dtos.web;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@NoArgsConstructor
public class LoginAdminRequestDto {

    String userId;
    String password;

}
