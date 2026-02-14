package com.standalonejhgl.holoseogiapi.dtos.app;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
public class ItemInsertListRequestDto {

    private List<ItemInsertOrUpdateRequestDto> items;
}
