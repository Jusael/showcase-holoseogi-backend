package com.standalonejhgl.holoseogiapi.service.app;

import com.standalonejhgl.holoseogiapi.dtos.app.ResultResponseDto;
import com.standalonejhgl.holoseogiapi.dtos.app.UserPlantRequestDto;
import com.standalonejhgl.holoseogiapi.dtos.app.UserPlantResponeseDto;
import com.standalonejhgl.holoseogiapi.entity.UserPlant;
import com.standalonejhgl.holoseogiapi.exception.BusinessException;
import com.standalonejhgl.holoseogiapi.exception.ErrorCode;
import com.standalonejhgl.holoseogiapi.repository.UserPlantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor

@Service
public class UserPlantServiceImpl implements UserPlantService {

    private final UserPlantRepository userPlantRepository;

    public List<UserPlantResponeseDto> getListPlantInfo(String userId) {
        List<UserPlant> arr = userPlantRepository.findByUserId(userId);

        List<UserPlantResponeseDto> userPlantResponeseDtoList = new ArrayList<>();

        for (UserPlant userPlant : arr) {
            LocalDate nextWateringDate = userPlant.getLastWateredDate().plusDays(userPlant.getWaterCycleDays());
            int waterRemainingDaysValue = (int)ChronoUnit.DAYS.between( nextWateringDate,LocalDate.now());

            userPlantResponeseDtoList.add(
                    UserPlantResponeseDto.builder()
                            .plantId(userPlant.getPlantId())
                            .plantName(userPlant.getPlantName())
                            .plantSpecies(userPlant.getPlantSpecies())
                            .memo(userPlant.getMemo())
                            .imageFileName(userPlant.getImageFileName())
                            .waterCycleDays(userPlant.getWaterCycleDays())
                            .lastWateredDate(userPlant.getLastWateredDate())
                            .waterRemainingDaysValue(waterRemainingDaysValue
                            ).build()
            );
        }

        return userPlantResponeseDtoList;
    }

    public ResultResponseDto insertPlantInfo(String userId, UserPlantRequestDto userPlantRequestDto) {

        UserPlant userPlant = new UserPlant();
        userPlant.applyPlantInfo(userId, userPlantRequestDto);

        userPlantRepository.save(userPlant);
        return new ResultResponseDto(true, "Successfully inserted plant");
    }

    @Transactional
    public ResultResponseDto updatePlantInfo(String userId, UserPlantRequestDto userPlantRequestDto) {

        UserPlant userPlant = userPlantRepository.findByPlantIdAndUserId(userPlantRequestDto.getPlantId(), userId).orElseThrow(() -> new BusinessException(ErrorCode.INFO_NOT_FOUND));
        userPlant.applyPlantInfo(userId, userPlantRequestDto);

        userPlantRepository.save(userPlant);
        return new ResultResponseDto(true, "Successfully Update plant");
    }

    @Transactional
    public ResultResponseDto deletePlantInfo(Long plantId, String userId) {

        UserPlant plant = userPlantRepository.findByPlantIdAndUserId(plantId, userId).orElseThrow(() -> new BusinessException(ErrorCode.INFO_NOT_FOUND));
        userPlantRepository.delete(plant);

        return new ResultResponseDto(true, "Successfully Delete plant");
    }
}
