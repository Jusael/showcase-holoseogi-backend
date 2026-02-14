package com.standalonejhgl.holoseogiapi.service.common;

import com.standalonejhgl.holoseogiapi.entity.AiPromptTemplate;
import com.standalonejhgl.holoseogiapi.entity.UserItemExpiry;
import com.standalonejhgl.holoseogiapi.repository.AiPromptTemplateRepository;
import com.standalonejhgl.holoseogiapi.repository.UserItemExpiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiPromptServiceImpl implements AiPromptService {

    private final AiPromptTemplateRepository aiPromptTemplateRepository;
    private final UserItemExpiryRepository userItemExpiryRepository;

    @Transactional(readOnly = true)
    public String buildPrompt(String templateName, Map<String, Object> data){

        switch (templateName) {

            // [선택 재료: {{SELECTED}}] [보유 재료: {{OWNED}}]
            case "select_ingredients" : {

                //템플릿 정보 조회
                AiPromptTemplate aiPromptTemplate = aiPromptTemplateRepository.findByTemplateName(templateName).orElseThrow(() ->new IllegalArgumentException("Invalid templateName: " + templateName));
                String template =  aiPromptTemplate.getTemplateText();

                //요청받은 데이터
                String selected =  data.get("ingredients").toString();
                String userId = data.get("userId").toString();

                StringBuilder expiryItemBuilder = new StringBuilder();
                List<UserItemExpiry> expiryItemList = userItemExpiryRepository.findValidItems(userId, LocalDate.now());

                 expiryItemList.forEach(item -> {
                     expiryItemBuilder.append(item.getTitle()).append(", ");
                 });

                String prompt = template.replace("{{SELECTED}}", selected).replace("{{OWNED}}", expiryItemBuilder.toString());;

                return prompt;
            }

            case "near_expiry_ingredients" : {
                break;
            }

            default:{
                throw new IllegalArgumentException("Invalid templateName: " + templateName);
            }
        }
        return null;
    }
}
