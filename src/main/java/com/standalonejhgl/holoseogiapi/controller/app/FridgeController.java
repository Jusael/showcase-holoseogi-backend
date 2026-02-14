

package com.standalonejhgl.holoseogiapi.controller.app;


import java.util.List;

import com.standalonejhgl.holoseogiapi.dtos.app.ItemExpiryResponseDto;
import com.standalonejhgl.holoseogiapi.dtos.app.ItemInsertOrUpdateRequestDto;
import com.standalonejhgl.holoseogiapi.dtos.app.RecipeResponseDto;
import com.standalonejhgl.holoseogiapi.dtos.app.ResultResponseDto;
import com.standalonejhgl.holoseogiapi.service.app.FridgeService;
import org.slf4j.Logger;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fridge")
public class FridgeController {

    private static final Logger log = LoggerFactory.getLogger(FridgeController.class);
    private final FridgeService fridgeService;

    @GetMapping("/get-item-list")
    public ResponseEntity<List<ItemExpiryResponseDto>> getUserItemList(
            @RequestHeader("UserId") String userId) {

        log.info(String.format(" 냉장고 리스트 조회 요청 UserId: {%s}",userId));
        return ResponseEntity.ok(fridgeService.getItemExpiry(userId));
    }

    @PostMapping("/insert-items")
    public ResponseEntity<ResultResponseDto> insertItems(
            @RequestHeader("UserId") String userId,
            @RequestBody List<ItemInsertOrUpdateRequestDto> dtos) {

        log.info(String.format("사용자 {%s} 냉장고 삽입 요청 {%s} 건",userId, dtos.toArray().length));
        return ResponseEntity.ok(fridgeService.insertItems(userId,dtos));

    }

    @PostMapping("/update-item")
    public ResponseEntity<ResultResponseDto> updateItemInfo(
            @RequestBody ItemInsertOrUpdateRequestDto dto) {

        log.info(String.format(" 냉장고 수정 요청 요청 Id: {%s}",dto.getId()));
        return ResponseEntity.ok(fridgeService.updateItemInfo(dto));
    }

    @DeleteMapping("/delete-item/{itemId}")
    public ResponseEntity<Void> deleteItemInfo(
            @RequestHeader("UserId") String userId,
            @PathVariable Long itemId) {

        log.info("품목 삭제 요청 userId: {}, scheduleId: {}", userId, itemId);
        fridgeService.deleteItemInfo(itemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/get-select-item-recipe")
    public ResponseEntity<RecipeResponseDto> generateRecipe(
            @RequestHeader("UserId") String userId,
            @RequestParam("items") String items) {

        log.info(String.format("레시피 추천 요청 UserId: {%s}",userId));
        return ResponseEntity.ok(fridgeService.getReceipt(userId,items));
    }

}