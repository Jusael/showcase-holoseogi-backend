package com.standalonejhgl.holoseogiapi.security;

import com.standalonejhgl.holoseogiapi.entity.ApiTrafficLog;
import com.standalonejhgl.holoseogiapi.repository.ApiTrafficLogRepository;
import com.standalonejhgl.holoseogiapi.service.common.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TrafficInterceptor implements HandlerInterceptor {

    private final ApiTrafficLogRepository apiTrafficLogRepository;

    public TrafficInterceptor(ApiTrafficLogRepository apiTrafficLogRepository) {
        this.apiTrafficLogRepository = apiTrafficLogRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        // JWT 통과 여부 확인
        Object uid = req.getAttribute("uid");
        if (uid == null) {
            // 비인증 요청 (봇, 스캔, preflight 등)
            return true;
        }

        // 시작 시간 저장
        req.setAttribute("trafficStartTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest req,
            HttpServletResponse res,
            Object handler,
            Exception ex
    ) {
        Object uid = req.getAttribute("uid");
        Object startTimeObj = req.getAttribute("trafficStartTime");

        if (uid == null || startTimeObj == null) return;

        long elapsedMs = System.currentTimeMillis() - (long) startTimeObj;

        String method = req.getMethod();
        String uri = req.getRequestURI();
        int status = res.getStatus();
        String ip = req.getRemoteAddr();
        String clientType = req.getHeader("X-Client-Type");

        if(clientType == null){
            clientType = "UNKNOWN";
        }

        ApiTrafficLog apiTrafficLog =  ApiTrafficLog.builder()
                .method(method)
                .uri(uri)
                .status(status)
                .ip(ip)
                .elapsedMs(elapsedMs)
                .clientType(clientType)
                .build();

        apiTrafficLogRepository.save(apiTrafficLog);
    }
}