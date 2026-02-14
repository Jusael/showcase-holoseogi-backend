package com.standalonejhgl.holoseogiapi.controller.app;

import com.standalonejhgl.holoseogiapi.dtos.app.UserPlantRequestDto;
import com.standalonejhgl.holoseogiapi.dtos.app.UserPlantResponeseDto;
import com.standalonejhgl.holoseogiapi.service.app.UserPlantService;
import org.slf4j.Logger;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plant")
public class PlantController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private final UserPlantService userPlantService;

    @GetMapping("/get-user-plant-info")
    public ResponseEntity<List<UserPlantResponeseDto>> getUserPlantInfo(@RequestHeader("UserId") String userId) {

        return ResponseEntity.ok(userPlantService.getListPlantInfo(userId));
    }

    @GetMapping("/get-user-plant-detail-info")
    public ResponseEntity getUserPlantDetailInfo(@RequestHeader("UserId") String userId, @RequestBody Long plantId) {

        return null;
    }


    @PostMapping("/insert-user-plant-info")
    public ResponseEntity insertUserPlantInfo(@RequestHeader("UserId") String userId, @RequestBody UserPlantRequestDto userPlantRequestDto) {

        log.info(" 식물 등록 요청  post-user-info UserId: {}", userId);

        return ResponseEntity.ok(userPlantService.insertPlantInfo(userId, userPlantRequestDto));
    }

    @PostMapping("/update-user-plant-info")
    public ResponseEntity updateUserPlantInfo(@RequestHeader("UserId") String userId, @RequestBody UserPlantRequestDto userPlantRequestDto) {

        log.info(" 식물 수정 요청  post-user-info UserId: {} PlantId : {}", userId, userPlantRequestDto.getPlantId());

        return ResponseEntity.ok(userPlantService.updatePlantInfo(userId, userPlantRequestDto));
    }

    @DeleteMapping("/delete-user-plant-info/{plantId}")
    public ResponseEntity deleteUserPlantInfo(@RequestHeader("UserId") String userId, @PathVariable Long plantId) {

        log.info(" 식물 삭제 요청  post-user-info UserId: {} PlantId : {}", userId, plantId);

        return ResponseEntity.ok(userPlantService.deletePlantInfo(plantId, userId));
    }

}