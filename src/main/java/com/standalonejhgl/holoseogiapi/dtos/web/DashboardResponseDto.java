package com.standalonejhgl.holoseogiapi.dtos.web;


import com.standalonejhgl.holoseogiapi.projection.NotificationSummaryProjection;
import com.standalonejhgl.holoseogiapi.projection.TodayTrafficProjection;
import com.standalonejhgl.holoseogiapi.projection.TrafficSummaryProjection;
import com.standalonejhgl.holoseogiapi.projection.TrafficChartProjection;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponseDto {

    private TodayTrafficProjection todayTraffic;
    private TrafficSummaryProjection trafficSummary;
    private NotificationSummaryProjection notificationSummary;
    private List<TrafficChartProjection> listTrafficChart;


}
