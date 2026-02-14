package com.standalonejhgl.holoseogiapi.service.app;

import com.standalonejhgl.holoseogiapi.daos.HomeDao;
import com.standalonejhgl.holoseogiapi.dtos.app.DailyIssueResponseDto;
import com.standalonejhgl.holoseogiapi.dtos.app.DailyMotivationResponseDto;
import com.standalonejhgl.holoseogiapi.entity.DailyMotivation;
import com.standalonejhgl.holoseogiapi.repository.MotivationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService{

    private final MotivationRepository motivationRepository;
    private final HomeDao homeDao;
    private static final Logger log = LoggerFactory.getLogger(HomeServiceImpl.class);


    public DailyMotivationResponseDto findTodayMotivation(){
        DailyMotivation entity = motivationRepository.findTodayMotivation();
        return new DailyMotivationResponseDto(entity.getTitle(),entity.getSubtitle1(),entity.getSubtitle2());
    }

    public List<DailyIssueResponseDto> findTodayIssue(String userId){
        List<DailyIssueResponseDto> arr = homeDao.getUserIssue(userId);
        return  arr;
    }
}
