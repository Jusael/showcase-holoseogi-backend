package com.standalonejhgl.holoseogiapi.service.app;

import com.standalonejhgl.holoseogiapi.dtos.app.DailyIssueResponseDto;
import com.standalonejhgl.holoseogiapi.dtos.app.DailyMotivationResponseDto;

import java.util.List;

public interface HomeService {


     DailyMotivationResponseDto  findTodayMotivation();

    List<DailyIssueResponseDto> findTodayIssue(String userId);
}
