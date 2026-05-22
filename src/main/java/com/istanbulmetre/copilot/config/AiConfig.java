package com.istanbulmetre.copilot.config;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.List;

@Configuration
public class AiConfig {

    private String getProperty(String key, String defaultValue) {
        String val = System.getProperty(key);
        if (val == null || val.trim().isEmpty()) {
            val = System.getenv(key);
        }
        return (val == null || val.trim().isEmpty()) ? defaultValue : val.trim();
    }

    private List<String> getApiKeys(String keyName) {
        String rawKeys = getProperty(keyName, "");
        if (rawKeys.isEmpty()) {
            throw new IllegalStateException("🚨 " + keyName + " .env dosyasında veya ortam değişkenlerinde bulunamadı!");
        }
        return Arrays.stream(rawKeys.split(","))
                .map(String::trim)
                .filter(k -> !k.isEmpty())
                .toList();
    }

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        String provider = getProperty("LLM_PROVIDER", "gemini");
        String defaultModel = "gemini-2.5-flash";
        if ("openai".equalsIgnoreCase(provider)) {
            defaultModel = "gpt-4o-mini";
        } else if ("ollama".equalsIgnoreCase(provider)) {
            defaultModel = "llama3";
        }
        String modelName = getProperty("LLM_MODEL_NAME", defaultModel);
        String baseUrl = getProperty("OPENAI_BASE_URL", "");
        String ollamaBaseUrl = getProperty("OLLAMA_BASE_URL", "http://localhost:11434");

        List<String> apiKeys = null;
        if (!"ollama".equalsIgnoreCase(provider)) {
            String apiKeyName = "openai".equalsIgnoreCase(provider) ? "OPENAI_API_KEY" : "GEMINI_API_KEY";
            apiKeys = getApiKeys(apiKeyName);
        }

        return new RotatingChatLanguageModel(provider, modelName, baseUrl, ollamaBaseUrl, apiKeys);
    }

    private final Map<Object, ChatMemory> memoryCache = new ConcurrentHashMap<>();

    @Bean
    public ChatMemoryProvider chatMemoryProvider() {
        // Her oturum için son 15 mesaj hafızada tutulur
        return sessionId -> memoryCache.computeIfAbsent(sessionId,
            id -> MessageWindowChatMemory.withMaxMessages(15));
    }
}
