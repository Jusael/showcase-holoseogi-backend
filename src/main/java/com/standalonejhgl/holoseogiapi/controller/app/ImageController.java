package com.standalonejhgl.holoseogiapi.controller.app;


import java.util.List;

import com.standalonejhgl.holoseogiapi.dtos.app.ImagesResponseDto;
import com.standalonejhgl.holoseogiapi.service.app.ImageService;
import org.slf4j.Logger;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {

    private static final Logger log = LoggerFactory.getLogger(ImageController.class);
    private final ImageService imageService;

    @GetMapping("/get-icons")
    public ResponseEntity<List<ImagesResponseDto>> getIcon() {

        log.info(String.format("사용자 아이콘 다운로드 요청"));

        return ResponseEntity.ok(imageService.getIcons());
    }


}