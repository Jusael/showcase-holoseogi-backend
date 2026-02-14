package com.standalonejhgl.holoseogiapi.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.standalonejhgl.holoseogiapi.service.common.JwtService;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtService jwtService;

    public AuthInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        // OPTIONS (CORS preflight) 통과
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) return true;

        // 공개 엔드포인트 화이트리스트
        String path = req.getRequestURI();
        if (path.startsWith("/api/login/apple-sign-in")
        || path.startsWith("/api/login/post-jwt-token")
                || path.startsWith("/api/login/post-fcm-token")
                || path.startsWith("/api/login/get-login")
                || path.startsWith("/test/fcm-test")
                || path.startsWith("/test/create-admin")
                || path.startsWith("/api/admin/auth/web-login") )
            return true;


        // Authorization 헤더 검사
        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            res.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        String token = auth.substring("Bearer ".length()).trim();
        try {
            String uid = jwtService.parseSubject(token); // 토큰 서명/만료 검증 + uid 추출
            // 컨트롤러에서 쓰도록 request attribute로 uid 전달
            req.setAttribute("uid", uid);
            return true;
        } catch (Exception e) {
            res.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
    }
}