
package com.standalonejhgl.holoseogiapi.dtos.app;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@NoArgsConstructor
public class ItemInsertOrUpdateRequestDto {

    Long id;
    String title;
    String expiryDate;
    Integer itemImagesId;
    String category;
}
