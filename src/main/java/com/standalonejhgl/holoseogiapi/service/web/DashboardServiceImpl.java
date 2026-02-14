package com.standalonejhgl.holoseogiapi.service.web;

import com.standalonejhgl.holoseogiapi.dtos.web.DashboardResponseDto;
import com.standalonejhgl.holoseogiapi.dtos.web.TrafficChartResponseDto;
import com.standalonejhgl.holoseogiapi.exception.BusinessException;
import com.standalonejhgl.holoseogiapi.exception.ErrorCode;
import com.standalonejhgl.holoseogiapi.projection.NotificationSummaryProjection;
import com.standalonejhgl.holoseogiapi.projection.TodayTrafficProjection;
import com.standalonejhgl.holoseogiapi.projection.TrafficSummaryProjection;
import com.standalonejhgl.holoseogiapi.projection.TrafficChartProjection;
import com.standalonejhgl.holoseogiapi.repository.ApiTrafficLogRepository;
import com.standalonejhgl.holoseogiapi.repository.NotificationQueueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService{

    private final NotificationQueueRepository notificationQueueRepository;
    private final ApiTrafficLogRepository apiTrafficLogRepository;

    @Override
    @Cacheable(value="dashboard", key="'summary'")
    public DashboardResponseDto getDashboard() {

        //Web Dashboard 용 정보 병렬 조회
        CompletableFuture<TodayTrafficProjection> todayTraffic=
                CompletableFuture.supplyAsync(() -> apiTrafficLogRepository.getTodayTrafficInfo());

        CompletableFuture<TrafficSummaryProjection> trafficSummary=
                CompletableFuture.supplyAsync(() -> apiTrafficLogRepository.getTrafficSummary());

        CompletableFuture<NotificationSummaryProjection> notificationSummary =
                CompletableFuture.supplyAsync(() -> notificationQueueRepository.getNotificationSummary());


        //첫 대쉬보드 진입시에는 기본값이 주간
        CompletableFuture<List<TrafficChartProjection>> weeklyTraffic =
                CompletableFuture.supplyAsync(() -> apiTrafficLogRepository.getWeeklyTrafficInfo());


        //특정 wait점 삽입을 위해 작성
        CompletableFuture.allOf(todayTraffic, trafficSummary, notificationSummary,weeklyTraffic).join();

        DashboardResponseDto dashboardResponseDto = new DashboardResponseDto(todayTraffic.join(),
                trafficSummary.join(),
                notificationSummary.join(),
                weeklyTraffic.join());

        return dashboardResponseDto;
    }

    @Override
    @Cacheable(value="traffic", key="#mode")
    public TrafficChartResponseDto getTrafficChart(String mode) {

        List<TrafficChartProjection> trafficChart;

        switch (mode) {
            case "week":
                trafficChart = apiTrafficLogRepository.getWeeklyTrafficInfo();
                break;

            case "month":
             trafficChart = apiTrafficLogRepository.getMonthlyTrafficInfo();
                    break;

            case "year":
                trafficChart = apiTrafficLogRepository.getYearTrafficInfo();
                break;

             default:
                 throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        return new TrafficChartResponseDto(trafficChart);
    }
}
