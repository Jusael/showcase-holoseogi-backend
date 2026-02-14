package com.standalonejhgl.holoseogiapi.controller.web;
import com.standalonejhgl.holoseogiapi.dtos.common.JwtResponeseDto;
import com.standalonejhgl.holoseogiapi.dtos.web.DashboardResponseDto;
import com.standalonejhgl.holoseogiapi.dtos.web.LoginAdminRequestDto;
import com.standalonejhgl.holoseogiapi.dtos.web.TrafficChartResponseDto;
import com.standalonejhgl.holoseogiapi.service.web.DashboardService;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/web-dashboard-info")
    public ResponseEntity<DashboardResponseDto> getDashboard() {

        return ResponseEntity.ok(dashboardService.getDashboard());
    }

    @GetMapping("/web-traffic-chart")
    public ResponseEntity<TrafficChartResponseDto> getTrafficChart(String mode) {
        return ResponseEntity.ok(dashboardService.getTrafficChart(mode));
    }

}