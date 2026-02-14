package com.standalonejhgl.holoseogiapi.dtos.app;

import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
public class ImagesResponseDto {


    Integer itemImageId;
    String categoryCode ;
    String nameKo;
    String nameEn;
    String iconFile;
}