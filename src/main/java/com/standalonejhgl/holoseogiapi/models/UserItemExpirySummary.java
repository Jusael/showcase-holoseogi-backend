package com.standalonejhgl.holoseogiapi.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 유통기한 임박 품목 요약 정보
 * (예: "우유, 계란, 버터 외 2건")
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserItemExpirySummary {

    private String userId;       // 사용자 ID
    private String itemList;     // 그룹합쳐진 품목 문자열 ("우유, 계란, 버터 외 2건")
}