package com.standalonejhgl.holoseogiapi.dtos.web;
import com.standalonejhgl.holoseogiapi.projection.TrafficChartProjection;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrafficChartResponseDto {

    private List<TrafficChartProjection> listTrafficChart;


}
