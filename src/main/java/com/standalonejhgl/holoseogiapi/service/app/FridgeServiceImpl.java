package com.standalonejhgl.holoseogiapi.service.app;

import com.standalonejhgl.holoseogiapi.daos.UserItemExpiryDao;
import com.standalonejhgl.holoseogiapi.dtos.app.ItemExpiryResponseDto;
import com.standalonejhgl.holoseogiapi.dtos.app.ItemInsertOrUpdateRequestDto;
import com.standalonejhgl.holoseogiapi.dtos.app.RecipeResponseDto;
import com.standalonejhgl.holoseogiapi.dtos.app.ResultResponseDto;
import com.standalonejhgl.holoseogiapi.entity.UserItemExpiry;
import com.standalonejhgl.holoseogiapi.repository.UserItemExpiryRepository;
import com.standalonejhgl.holoseogiapi.service.common.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FridgeServiceImpl implements FridgeService {

    private static final Logger log = LoggerFactory.getLogger(FridgeServiceImpl.class);
    private final UserItemExpiryDao userItemExpiryDao;
    private final UserItemExpiryRepository userItemExpiryRepository;
    private final OpenAiService openAiService;

    @Override
    public List<ItemExpiryResponseDto> getItemExpiry(String userId){

        return userItemExpiryDao.getUserItemExpiryInfo(userId);

    }

    @Override
    public ResultResponseDto insertItems(String userId, List<ItemInsertOrUpdateRequestDto> dtos){

        dtos.forEach(dto -> {
            UserItemExpiry userItemExpiry = new UserItemExpiry();
            userItemExpiry.setUserId(userId);
            userItemExpiry.setTitle(dto.getTitle());
            userItemExpiry.setCategory(dto.getCategory());
            userItemExpiry.setItemImagesId(dto.getItemImagesId());
            userItemExpiry.setIsActive("Y");

            LocalDate dateValue = null;

            if(dto.getExpiryDate() != null){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                dateValue = LocalDate.parse(dto.getExpiryDate(), formatter);
            }

            userItemExpiry.setExpireDate(dateValue);

            userItemExpiryRepository.save(userItemExpiry);
        });

        return  new ResultResponseDto(true, "품목 정보 신규 생성에 성공하였습니다.");
    }

    @Override
    public ResultResponseDto updateItemInfo(ItemInsertOrUpdateRequestDto dto){

        UserItemExpiry entity = userItemExpiryRepository.findById(dto.getId()).orElseThrow();;

        entity.setCategory(dto.getCategory());
        entity.setTitle(dto.getTitle());

        LocalDate dateValue = null;

        if(dto.getExpiryDate() != null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            dateValue = LocalDate.parse(dto.getExpiryDate(), formatter);
        }

        entity.setExpireDate(dateValue);
        entity.setItemImagesId(dto.getItemImagesId());
        entity.setIsActive("Y");

        userItemExpiryRepository.save(entity);

        return new ResultResponseDto(true, "품목 정보 수정에 성공 하였습니다.");
    }

    @Override
    @Transactional
    public boolean deleteItemInfo(Long id){

        userItemExpiryRepository.deleteById(id);

        return  true;
    }

    @Override
    public RecipeResponseDto getReceipt(String userId, String items){

        return openAiService.generateRecipet(userId, items);
    }
}
