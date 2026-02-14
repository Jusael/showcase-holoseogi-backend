package com.standalonejhgl.holoseogiapi.service.common;

import org.springframework.stereotype.Service;

@Service
public interface JwtService {

     String parseSubject(String jwt) ;
    String issueToken(String uid, long expireSeconds);


}