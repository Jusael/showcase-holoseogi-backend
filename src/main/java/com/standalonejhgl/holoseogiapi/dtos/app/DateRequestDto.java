package com.standalonejhgl.holoseogiapi.dtos.app;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@NoArgsConstructor
public class DateRequestDto {

    @Schema(description = "시작일", example = "2020-01-01")
    String startDate;
    String endDate;
    String userId;
}
