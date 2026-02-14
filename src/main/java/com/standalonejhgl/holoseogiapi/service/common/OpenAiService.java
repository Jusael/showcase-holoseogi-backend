package com.standalonejhgl.holoseogiapi.service.common;


import com.standalonejhgl.holoseogiapi.dtos.app.RecipeResponseDto;

public interface OpenAiService {

    RecipeResponseDto generateRecipet(String userId, String items);
}
