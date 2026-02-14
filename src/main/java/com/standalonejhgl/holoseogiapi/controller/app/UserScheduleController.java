

package com.standalonejhgl.holoseogiapi.controller.app;


import com.standalonejhgl.holoseogiapi.dtos.app.DateRequestDto;
import com.standalonejhgl.holoseogiapi.dtos.app.ScheduleRequestDto;
import com.standalonejhgl.holoseogiapi.dtos.app.ScheduleResponseDto;
import com.standalonejhgl.holoseogiapi.dtos.app.ResultResponseDto;

import com.standalonejhgl.holoseogiapi.service.app.UserScheduleService;
import org.slf4j.Logger;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedule")
public class UserScheduleController {

    private static final Logger log = LoggerFactory.getLogger(UserScheduleController.class);
    private final UserScheduleService userScheduleService;

    @GetMapping("/get-user-schedule")
    public ResponseEntity<List<ScheduleResponseDto>> getUserSchedule(
            @RequestHeader("UserId") String userId,
            @ModelAttribute DateRequestDto dto) {

        log.info("사용자 스케줄 조회 요청 userId: {}", userId);
        return ResponseEntity.ok(userScheduleService.getUserSchedules(userId, dto));
    }

    @DeleteMapping("/delete-user-schedule/{scheduleId}")
    public ResponseEntity<Void> deleteUserSchedule(
            @RequestHeader("UserId") String userId,
            @PathVariable Long scheduleId) {

        log.info("사용자 스케줄 삭제 요청 userId: {}, scheduleId: {}", userId, scheduleId);
        userScheduleService.deleteUserSchedule(scheduleId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/insert-user-schedule")
    public ResponseEntity<ResultResponseDto> insertUserSchedule(
            @RequestHeader("UserId") String userId,
            @RequestBody ScheduleRequestDto dto) {

        log.info("스케줄 등록 요청 userId: {}", userId);
        return ResponseEntity.ok(userScheduleService.insertUserSchedule(userId, dto));
    }

    @PostMapping("/update-user-schedule")
    public ResponseEntity<ResultResponseDto> updateUserSchedule(
            @RequestHeader("UserId") String userId,
            @RequestBody ScheduleRequestDto dto) {

        log.info("스케줄 수정 요청 userId: {}", userId);
        return ResponseEntity.ok(userScheduleService.updateUserSchedule(userId, dto));
    }
}