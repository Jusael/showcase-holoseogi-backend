package com.standalonejhgl.holoseogiapi.service.app;

import com.standalonejhgl.holoseogiapi.dtos.app.ItemExpiryResponseDto;
import com.standalonejhgl.holoseogiapi.dtos.app.ItemInsertOrUpdateRequestDto;
import com.standalonejhgl.holoseogiapi.dtos.app.ResultResponseDto;
import com.standalonejhgl.holoseogiapi.dtos.app.RecipeResponseDto;
import java.util.List;

public interface FridgeService {

    List<ItemExpiryResponseDto> getItemExpiry(String userId);

    ResultResponseDto insertItems(String userId, List<ItemInsertOrUpdateRequestDto> dtos);

    ResultResponseDto updateItemInfo(ItemInsertOrUpdateRequestDto dto);

    boolean deleteItemInfo(Long id);

    RecipeResponseDto getReceipt(String userId, String items);
}
