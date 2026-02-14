package com.standalonejhgl.holoseogiapi.security;



import com.standalonejhgl.holoseogiapi.entity.ApiTrafficLog;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final TrafficInterceptor trafficInterceptor;

    public WebConfig(AuthInterceptor authInterceptor,TrafficInterceptor trafficInterceptor) {
        this.authInterceptor = authInterceptor;
        this.trafficInterceptor = trafficInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**"); // 필요한 경로만

        registry.addInterceptor(trafficInterceptor)
                .addPathPatterns("/api/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowedOrigins("*"); // 실제 도메인으로 제한 권장
    }
}
