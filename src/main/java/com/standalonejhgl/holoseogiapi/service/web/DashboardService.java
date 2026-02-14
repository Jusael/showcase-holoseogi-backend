package com.standalonejhgl.holoseogiapi.service.web;

import com.standalonejhgl.holoseogiapi.dtos.web.DashboardResponseDto;
import com.standalonejhgl.holoseogiapi.dtos.web.TrafficChartResponseDto;

import java.util.List;

public interface DashboardService {

    DashboardResponseDto getDashboard();

    TrafficChartResponseDto getTrafficChart(String mode);
}
