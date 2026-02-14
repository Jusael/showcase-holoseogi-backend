package com.standalonejhgl.holoseogiapi.service.app;


import com.standalonejhgl.holoseogiapi.daos.UserScheduleDao;
import com.standalonejhgl.holoseogiapi.dtos.app.DateRequestDto;
import com.standalonejhgl.holoseogiapi.dtos.app.ResultResponseDto;
import com.standalonejhgl.holoseogiapi.dtos.app.ScheduleRequestDto;
import com.standalonejhgl.holoseogiapi.dtos.app.ScheduleResponseDto;
import com.standalonejhgl.holoseogiapi.entity.UserSchedule;
import com.standalonejhgl.holoseogiapi.entity.UserScheduleRule;
import com.standalonejhgl.holoseogiapi.event.UserScheduleTodayInsertEvent;
import com.standalonejhgl.holoseogiapi.event.UserScheduleTodayUpdateEvent;
import com.standalonejhgl.holoseogiapi.repository.UserScheduleRepository;
import com.standalonejhgl.holoseogiapi.repository.UserScheduleRoleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class UserScheduleServiceImpl implements UserScheduleService {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final UserScheduleDao userScheduleDao;
    private final UserScheduleRepository userScheduleRepository;
    private final UserScheduleRoleRepository userScheduleRoleRepository;

    private static final Logger log = LoggerFactory.getLogger(UserScheduleServiceImpl.class);
    /**
     * <p>유저의 하루 알림, 월간 알림, 주간 알림을 모두 조회한다.</p>
     * @return ONCE, MONTHLY, WEEKLY 일정 리스트
     */
    @Override
    public List<ScheduleResponseDto> getUserSchedules(String userId, DateRequestDto dateRequestDto) {

        LocalDate startDate  = LocalDate.parse(dateRequestDto.getStartDate(), DateTimeFormatter.ISO_DATE);
        LocalDate endDate = LocalDate.parse(dateRequestDto.getEndDate(), DateTimeFormatter.ISO_DATE);

        // 하루/월간 일정 조회
        List<ScheduleResponseDto> userSchedules = userScheduleDao.getOnceAndMonthSchedules(userId, dateRequestDto);

        // 주간 일정 조회
        List<ScheduleResponseDto> userWeeklySchedules = userScheduleDao.getOnceAndWeekSchedules(userId, dateRequestDto);

        // 주간 일정 → yyyy-MM-dd 으로 1차 수정
        List<ScheduleResponseDto> expandedList = new ArrayList<>();

        for (ScheduleResponseDto dto : userWeeklySchedules) {
            int targetDay = dto.getDayOfWeekValue(); // MON=1, ..., SUN=7

            //2025-09-01 부터 시작
            LocalDate date = startDate;

            //2025-09-31 까지 반복
            while (!date.isAfter(endDate)) {

                // 월 value : 1 == db value : 1 같은 케이스인 경우
                if (date.getDayOfWeek().getValue() == targetDay) {

                    //copy본 하나 생성
                    ScheduleResponseDto copy = new ScheduleResponseDto();

                    // 값 복사 및 산출한 yyyy-mm-dd형식 삽입
                    copy.setUserScheduleId(dto.getUserScheduleId());
                    copy.setTitle(dto.getTitle());
                    copy.setCategory(dto.getCategory());
                    copy.setCategoryName(dto.getCategoryName());
                    copy.setRepeatType(dto.getRepeatType());
                    copy.setTimeOfDay(dto.getTimeOfDay());
                    copy.setDayOfWeek(dto.getDayOfWeek());
                    copy.setYyyyMmDd(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    copy.setWeekDays(dto.getWeekDays());
                    expandedList.add(copy);
                }

                date = date.plusDays(1);
            }
        }

        // 전체 일정 합치기
        userSchedules.addAll(expandedList);

        // 시간순대로 정렬
        userSchedules.sort(
                Comparator
                        .comparing(ScheduleResponseDto::getYyyyMmDd)
                        .thenComparing(ScheduleResponseDto::getTimeOfDay, Comparator.nullsLast(String::compareTo))
        );

        return userSchedules;
    }

    /**
     * <p>사용자 스케쥴을 삭제한다.</p>
     * @param scheduleId 삭제 대상 스케쥴 ID
     */
    @Override
    @Transactional
    public void deleteUserSchedule(Long scheduleId){

        //하위 role 삭제
        userScheduleRoleRepository.deleteUserScheduleRole(scheduleId);

        //스케쥴 삭제
        userScheduleRepository.deleteById(scheduleId);
    }

    @Override
    @Transactional
    public ResultResponseDto insertUserSchedule(String userId, ScheduleRequestDto scheduleRequestDto){
        UserSchedule userSchedule = new UserSchedule();
        userSchedule.setUserId(userId);
        userSchedule.setTitle(scheduleRequestDto.getTitle());
        userSchedule.setCategory(scheduleRequestDto.getCategory());
        userSchedule.setRepeatType(scheduleRequestDto.getRepeatType());
        userSchedule.setTimeOfDay(scheduleRequestDto.getTimeOfDay());
        userSchedule.setIsActive("Y");
        userSchedule.setCreatedAt(LocalDateTime.now());
        userScheduleRepository.save(userSchedule);

        UserScheduleRule userScheduleRule = new UserScheduleRule();

        switch (scheduleRequestDto.getRepeatType()){

            case"ONCE": {
                //join 컬럼의 identity는 엔티티 자체를 삽입한다.
                userScheduleRule.setUserSchedule(userSchedule);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                userScheduleRule.setOnceDate(LocalDate.parse(scheduleRequestDto.getYyyyMmDd(), formatter));
                userScheduleRoleRepository.save(userScheduleRule);
                break;
            }
            case "WEEKLY":{
                List<String> arr =  scheduleRequestDto.getWeekDays();

                for(String value : arr)
                {
                    userScheduleRule = new UserScheduleRule();
                    userScheduleRule.setUserSchedule(userSchedule);
                    userScheduleRule.setDayOfWeek(value);
                    userScheduleRoleRepository.save(userScheduleRule);
                }
                break;
            }

                case "MONTHLY": {
                    userScheduleRule.setUserSchedule(userSchedule);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                    userScheduleRule.setDayOfMonth(LocalDate.parse(scheduleRequestDto.getYyyyMmDd(), formatter).getDayOfMonth());
                    userScheduleRoleRepository.save(userScheduleRule);
                    break;
                }
        }

        applicationEventPublisher.publishEvent(new UserScheduleTodayInsertEvent(userSchedule));

        return new ResultResponseDto(true,"사용자 알림 저장 성공");
    }

    @Transactional
    @Override
    public ResultResponseDto updateUserSchedule(String userId, ScheduleRequestDto scheduleRequestDto){
        UserSchedule userSchedule = userScheduleRepository.findById(scheduleRequestDto.getScheduleId()).orElse(null);
        if(userSchedule ==null)
            return new ResultResponseDto(false,"사용자 알림 조회실패");

        userSchedule.setTitle(scheduleRequestDto.getTitle());
        userSchedule.setCategory(scheduleRequestDto.getCategory());
        userSchedule.setRepeatType(scheduleRequestDto.getRepeatType());
        userSchedule.setTimeOfDay(scheduleRequestDto.getTimeOfDay());
        userSchedule.setIsActive("Y");
        userSchedule.setCreatedAt(LocalDateTime.now());
        userScheduleRepository.save(userSchedule);

        UserScheduleRule userScheduleRule = new UserScheduleRule();

        //수정은 롤을 삭제 하고 다시등록한다.
        userScheduleRoleRepository.deleteUserScheduleRole(scheduleRequestDto.getScheduleId());
        userScheduleRule = new UserScheduleRule();
        switch (scheduleRequestDto.getRepeatType()){

            case"ONCE": {
                //join 컬럼의 identity는 엔티티 자체를 삽입한다.
                userScheduleRule.setUserSchedule(userSchedule);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                userScheduleRule.setOnceDate(LocalDate.parse(scheduleRequestDto.getYyyyMmDd(), formatter));
                userScheduleRoleRepository.save(userScheduleRule);
                break;
            }
            case "WEEKLY":{
                List<String> arr =  scheduleRequestDto.getWeekDays();

                for(String value : arr)
                {
                    userScheduleRule = new UserScheduleRule();
                    userScheduleRule.setUserSchedule(userSchedule);
                    userScheduleRule.setDayOfWeek(value);
                    userScheduleRoleRepository.save(userScheduleRule);
                }
                break;
            }

            case "MONTHLY": {
                userScheduleRule.setUserSchedule(userSchedule);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                userScheduleRule.setDayOfMonth(LocalDate.parse(scheduleRequestDto.getYyyyMmDd(), formatter).getDayOfMonth());
                userScheduleRoleRepository.save(userScheduleRule);
                break;
            }
        }

        applicationEventPublisher.publishEvent(new UserScheduleTodayUpdateEvent(userSchedule));

        return new ResultResponseDto(true,"사용자 알림 수정 성공");
    }
}