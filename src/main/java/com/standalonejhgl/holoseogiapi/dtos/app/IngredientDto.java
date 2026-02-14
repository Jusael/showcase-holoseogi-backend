package com.standalonejhgl.holoseogiapi.dtos.app;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IngredientDto {
    private String name;   // 재료명
    private String amount; // 분량 (컵, 큰술 등)
}
