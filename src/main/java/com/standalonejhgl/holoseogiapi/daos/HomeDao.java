
package com.standalonejhgl.holoseogiapi.daos;

import com.standalonejhgl.holoseogiapi.dtos.app.DailyIssueResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HomeDao {


    List<DailyIssueResponseDto> getUserIssue(@Param("userId") String userId
    );


}
