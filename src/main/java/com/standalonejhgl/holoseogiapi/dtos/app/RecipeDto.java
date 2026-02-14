package com.standalonejhgl.holoseogiapi.dtos.app;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class RecipeDto {
    private String id; // e.g. "recipe001"
    private String name; // 요리 이름
    private String timeMinutes; // 조리 시간 (분 단위)
    private List<IngredientDto> ingredients; // 재료 리스트
    private List<String> steps; // 단계별 설명
    private String notes; // 추가 팁
    private String tasteNote;
    private List<String> missingIngredients;
    private List<String> alternativeIngredients;
}