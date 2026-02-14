package com.standalonejhgl.holoseogiapi.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSubscriptionVerifyList {
    private String userId;
    private String transactionId;
    private String originalTransactionId;
}

