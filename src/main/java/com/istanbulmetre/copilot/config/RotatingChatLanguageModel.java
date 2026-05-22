package com.istanbulmetre.copilot.config;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.output.Response;

import java.util.List;
import java.util.function.Function;

/**
 * Concrete implementation of ChatLanguageModel that manages provider fallback,
 * baseUrl overrides, and API key rotation under a single OOP abstraction.
 */
public class RotatingChatLanguageModel implements ChatLanguageModel {

    private final String provider;
    private final String modelName;
    private final String baseUrl;
    private final String ollamaBaseUrl;
    private final List<String> apiKeys;

    public RotatingChatLanguageModel(String provider, String modelName, String baseUrl, String ollamaBaseUrl, List<String> apiKeys) {
        this.provider = provider;
        this.modelName = modelName;
        this.baseUrl = baseUrl;
        this.ollamaBaseUrl = ollamaBaseUrl;
        this.apiKeys = apiKeys;
    }

    private ChatLanguageModel buildModelForProvider(String provider, String key, String modelName) {
        if ("openai".equalsIgnoreCase(provider)) {
            var builder = OpenAiChatModel.builder()
                    .apiKey(key)
                    .modelName(modelName)
                    .temperature(0.2);
            if (baseUrl != null && !baseUrl.isEmpty()) {
                builder.baseUrl(baseUrl);
            }
            return builder.build();
        } else if ("ollama".equalsIgnoreCase(provider)) {
            String url = (ollamaBaseUrl != null && !ollamaBaseUrl.isEmpty()) ? ollamaBaseUrl : "http://localhost:11434";
            return OllamaChatModel.builder()
                    .baseUrl(url)
                    .modelName(modelName)
                    .temperature(0.2)
                    .build();
        } else {
            // Default is Gemini
            return GoogleAiGeminiChatModel.builder()
                    .apiKey(key)
                    .modelName(modelName)
                    .temperature(0.2)
                    .build();
        }
    }

    private Response<AiMessage> executeWithFallback(Function<ChatLanguageModel, Response<AiMessage>> modelAction) {
        // Ollama does not require API keys
        if ("ollama".equalsIgnoreCase(provider)) {
            try {
                ChatLanguageModel model = buildModelForProvider(provider, null, modelName);
                return modelAction.apply(model);
            } catch (Exception e) {
                throw new RuntimeException("🚨 Ollama modeli çağrılırken hata oluştu: " + e.getMessage(), e);
            }
        }

        if (apiKeys == null || apiKeys.isEmpty()) {
            throw new IllegalStateException("🚨 API anahtarları bulunamadı!");
        }

        Exception lastException = null;
        for (int i = 0; i < apiKeys.size(); i++) {
            String key = apiKeys.get(i);
            try {
                ChatLanguageModel model = buildModelForProvider(provider, key, modelName);
                return modelAction.apply(model);
            } catch (Exception e) {
                System.err.println("🚨 " + provider.toUpperCase() + " API Anahtarı " + (i + 1) + "/" + apiKeys.size() + " hata verdi: " + e.getMessage() + ". Sonrakine geçiliyor...");
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
            if (m instanceof AiMessage aiM) {
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
}
