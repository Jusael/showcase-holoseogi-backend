package com.standalonejhgl.holoseogiapi.dtos.app;


import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class RecipeResponseDto {
    private List<RecipeDto> recipes;
}