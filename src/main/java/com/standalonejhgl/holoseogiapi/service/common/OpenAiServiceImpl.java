package com.standalonejhgl.holoseogiapi.service.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.standalonejhgl.holoseogiapi.dtos.app.RecipeResponseDto;
import com.standalonejhgl.holoseogiapi.entity.AiRecipeLog;
import com.standalonejhgl.holoseogiapi.repository.AiRecipeLogRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.util.StopWatch;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAiServiceImpl implements OpenAiService {


    private static final Logger log = LoggerFactory.getLogger(OpenAiServiceImpl.class);
    private final WebClient openAiWebClient;
    private final AiRecipeLogRepository aiRecipeLogRepository;

    private final AiPromptService aiPromptService;
    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.openai.com/v1/chat/completions")
            .defaultHeader("Authorization", "Bearer " + System.getenv("OPENAI_API_KEY"))
            .defaultHeader("Content-Type", "application/json")
            .build();

    public RecipeResponseDto generateRecipet(String userId, String items) {

        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("ingredients", items);

        String prompt = aiPromptService.buildPrompt("select_ingredients", data);

        return callOpenAi(userId, prompt, RecipeResponseDto.class);
    }

    public <T> T callOpenAi(String userId,String prompt, Class<T> responseType) {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            //  OpenAI 요청 JSON 구성
            Map<String, Object> requestBody = Map.of(
                    "model", "gpt-4o-mini",
                    "messages", List.of(
                            Map.of("role", "system", "content", "You are a helpful assistant."),
                            Map.of("role", "user", "content", prompt)
                    )
            );

            // 3요청 보내고 응답 받기
            //openAiWebClient는 컨피그에 정의
            String response = openAiWebClient.post()
                    .uri("")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(25))
                    .block(); // 동기식 호출 (테스트용)

            // 4JSON 파싱 (Jackson)
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            String content = root.path("choices").get(0).path("message").path("content").asText();

            // 5모델의 JSON 응답을 RecipeResponseDto로 역직렬화
            T dto = mapper.readValue(content, responseType);

            log.info("✅ OpenAI Response:\n{}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto));

            stopWatch.stop();

            this.saveRecipeLog(userId, stopWatch,mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto));
            return dto;

        } catch (JsonProcessingException e) {
            log.error("❌ JSON 파싱 오류: {}", e.getMessage());
            throw new RuntimeException("JSON 파싱 중 오류 발생", e);
        } catch (Exception e) {
            log.error("❌ OpenAI API 호출 오류: {}", e.getMessage());
            throw new RuntimeException("OpenAI API 호출 중 오류 발생", e);
        }
}

    public void saveRecipeLog(String userId, StopWatch stopWatch , String recipesJson) {
        AiRecipeLog aiRecipeLog = new AiRecipeLog();
        aiRecipeLog.setUserId(userId);
        aiRecipeLog.setResponseTimeMs((int) stopWatch.getTotalTimeMillis());
        aiRecipeLog.setPromptType("select_ingredients");
        aiRecipeLog.setRecipesJson(recipesJson);

        aiRecipeLogRepository.save(aiRecipeLog);
    }
}
