package com.standalonejhgl.holoseogiapi.controller.app;

import java.util.List;

import com.standalonejhgl.holoseogiapi.dtos.app.DailyIssueResponseDto;
import com.standalonejhgl.holoseogiapi.dtos.app.DailyMotivationResponseDto;
import com.standalonejhgl.holoseogiapi.service.app.HomeService;
import org.slf4j.Logger;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);
    private final HomeService homeService;

    @GetMapping("/get-daily-motivation")
    public ResponseEntity<DailyMotivationResponseDto> generateRecipe(){
        log.info("데일리 글귀 요청");
        return ResponseEntity.ok(homeService.findTodayMotivation());
    }


    @GetMapping("/get-daily-issue")
    public ResponseEntity<List<DailyIssueResponseDto>> generateRecipe(
            @RequestHeader("UserId") String userId){
        log.info(String.format(" 약식 스케쥴 조회요청 UserId: {%s}",userId));
        return ResponseEntity.ok(homeService.findTodayIssue(userId));
    }

}