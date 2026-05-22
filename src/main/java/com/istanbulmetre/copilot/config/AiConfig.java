package com.istanbulmetre.copilot.config;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.output.Response;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

@Configuration
public class AiConfig {

    private static void loadDotEnv() {
        // Hem mevcut dizini hem de üst dizini kontrol ederek .env dosyasını yükler
        File envFile = new File("../.env");
        if (!envFile.exists()) {
            envFile = new File(".env");
        }
        if (envFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }
                    int eqIdx = line.indexOf('=');
                    if (eqIdx > 0) {
                        String key = line.substring(0, eqIdx).trim();
                        String value = line.substring(eqIdx + 1).trim();
                        // Tırnak işaretlerini temizle
                        if (value.startsWith("\"") && value.endsWith("\"")) {
                            value = value.substring(1, value.length() - 1);
                        } else if (value.startsWith("'") && value.endsWith("'")) {
                            value = value.substring(1, value.length() - 1);
                        }
                        System.setProperty(key, value);
                    }
                }
            } catch (Exception e) {
                System.err.println("🚨 .env yüklenirken hata oluştu: " + e.getMessage());
            }
        }
    }

    private String getProperty(String key, String defaultValue) {
        loadDotEnv();
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

    private ChatLanguageModel buildModelForProvider(String provider, String key, String modelName) {
        if ("openai".equalsIgnoreCase(provider)) {
            String baseUrl = getProperty("OPENAI_BASE_URL", "");
            var builder = OpenAiChatModel.builder()
                    .apiKey(key)
                    .modelName(modelName)
                    .temperature(0.2);
            if (!baseUrl.isEmpty()) {
                builder.baseUrl(baseUrl);
            }
            return builder.build();
        } else if ("ollama".equalsIgnoreCase(provider)) {
            String baseUrl = getProperty("OLLAMA_BASE_URL", "http://localhost:11434");
            return OllamaChatModel.builder()
                    .baseUrl(baseUrl)
                    .modelName(modelName)
                    .temperature(0.2)
                    .build();
        } else {
            // Varsayılan: Gemini
            return GoogleAiGeminiChatModel.builder()
                    .apiKey(key)
                    .modelName(modelName)
                    .temperature(0.2)
                    .build();
        }
    }

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return new ChatLanguageModel() {
            private Response<AiMessage> executeWithFallback(java.util.function.Function<ChatLanguageModel, Response<AiMessage>> modelAction) {
                String provider = getProperty("LLM_PROVIDER", "gemini");
                String defaultModel = "gemini-2.5-flash";
                if ("openai".equalsIgnoreCase(provider)) {
                    defaultModel = "gpt-4o-mini";
                } else if ("ollama".equalsIgnoreCase(provider)) {
                    defaultModel = "llama3";
                }
                String modelName = getProperty("LLM_MODEL_NAME", defaultModel);

                // Ollama API anahtarı gerektirmez
                if ("ollama".equalsIgnoreCase(provider)) {
                    try {
                        ChatLanguageModel model = buildModelForProvider(provider, null, modelName);
                        return modelAction.apply(model);
                    } catch (Exception e) {
                        throw new RuntimeException("🚨 Ollama modeli çağrılırken hata oluştu: " + e.getMessage(), e);
                    }
                }

                // Gemini veya OpenAI için API anahtarları listesini al (rotasyon desteği)
                String apiKeyName = "openai".equalsIgnoreCase(provider) ? "OPENAI_API_KEY" : "GEMINI_API_KEY";
                List<String> keys = getApiKeys(apiKeyName);
                Exception lastException = null;

                for (int i = 0; i < keys.size(); i++) {
                    String key = keys.get(i);
                    try {
                        ChatLanguageModel model = buildModelForProvider(provider, key, modelName);
                        return modelAction.apply(model);
                    } catch (Exception e) {
                        System.err.println("🚨 " + provider.toUpperCase() + " API Anahtarı " + (i + 1) + "/" + keys.size() + " hata verdi: " + e.getMessage() + ". Sonrakine geçiliyor...");
                        lastException = e;
                    }
                }
                throw new RuntimeException("🚨 Havuzdaki tüm " + provider.toUpperCase() + " API anahtarları tükendi ya da geçersiz!", lastException);
            }

            @Override
            public Response<AiMessage> generate(List<ChatMessage> messages) {
                return executeWithFallback(model -> model.generate(messages));
            }

            @Override
            public Response<AiMessage> generate(List<ChatMessage> messages, List<ToolSpecification> toolSpecifications) {
                System.out.println("=== CO-PILOT GENERATE (with tools) MSG LIST ===");
                for (int idx = 0; idx < messages.size(); idx++) {
                    ChatMessage m = messages.get(idx);
                    System.out.println("  [" + idx + "] Class: " + m.getClass().getSimpleName() + ", Type: " + m.type() + ", Content: " + m.text());
                    if (m instanceof dev.langchain4j.data.message.AiMessage aiM) {
                        if (aiM.hasToolExecutionRequests()) {
                            System.out.println("    Tool Requests: " + aiM.toolExecutionRequests());
                        }
                    }
                }
                return executeWithFallback(model -> model.generate(messages, toolSpecifications));
            }

            @Override
            public Response<AiMessage> generate(List<ChatMessage> messages, ToolSpecification toolSpecification) {
                return executeWithFallback(model -> model.generate(messages, toolSpecification));
            }
        };
    }

    private final Map<Object, ChatMemory> memoryCache = new ConcurrentHashMap<>();

    @Bean
    public ChatMemoryProvider chatMemoryProvider() {
        // Her oturum için son 15 mesaj hafızada tutulur
        return sessionId -> memoryCache.computeIfAbsent(sessionId,
            id -> MessageWindowChatMemory.withMaxMessages(15));
    }
}
