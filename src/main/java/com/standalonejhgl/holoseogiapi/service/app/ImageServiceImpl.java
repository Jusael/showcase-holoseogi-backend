package com.standalonejhgl.holoseogiapi.service.app;

import com.standalonejhgl.holoseogiapi.dtos.app.ImagesResponseDto;
import com.standalonejhgl.holoseogiapi.repository.ItemImageRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private static final Logger log = LoggerFactory.getLogger(ImageServiceImpl.class);
private final ItemImageRepository itemImageRepository;

    @Override
    public List<ImagesResponseDto> getIcons() {

        return itemImageRepository.findAll().stream().map(x -> new ImagesResponseDto(
            x.getItemImagesId(),
                x.getCategoryCode(),
                x.getNameKo(),
                x.getNameEn(),
                x.getIconFile()
        )).toList();

    }
}
