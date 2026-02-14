package com.standalonejhgl.holoseogiapi.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.standalonejhgl.holoseogiapi.models.AppleReceipt;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;

public class AppleReceiptParser {

    private static final ObjectMapper mapper = new ObjectMapper();

    private AppleReceiptParser() {}

    public static AppleReceipt parse(String receiptData) {
        try {
            String[] parts = receiptData.split("\\.");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid receipt format");
            }

            String payloadJson = new String(
                    Base64.getUrlDecoder().decode(parts[1]),
                    StandardCharsets.UTF_8
            );

            JsonNode payload = mapper.readTree(payloadJson);

            String productId = payload.get("productId").asText();
            long expiresDateMs = payload.get("expiresDate").asLong();

            String transactionId =
                    payload.path("transactionId").asText(null);

            String originalTransactionId =
                    payload.path("originalTransactionId").asText(null);

            LocalDateTime expiresAt =
                    Instant.ofEpochMilli(expiresDateMs)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();

            return new AppleReceipt(
                    productId,
                    expiresAt,
                    transactionId,
                    originalTransactionId
            );

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse Apple receipt", e);
        }
    }
}