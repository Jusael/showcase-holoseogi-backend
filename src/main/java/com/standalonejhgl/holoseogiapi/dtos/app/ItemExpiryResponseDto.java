package com.standalonejhgl.holoseogiapi.dtos.app;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ItemExpiryResponseDto {

    Long id;
    String title;
    String category;
    String categoryNm;
    @JsonProperty("dDayStr")
    String dDayStr;
    @JsonProperty("dDayValue")
    Integer dDayValue;
    String expiryDate;
    Integer itemImagesId;

}


