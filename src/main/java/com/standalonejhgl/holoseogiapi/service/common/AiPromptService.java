package com.standalonejhgl.holoseogiapi.service.common;

import java.util.Map;

public interface AiPromptService {

    String buildPrompt(String templateName, Map<String, Object> data);
}
