package com.istanbulmetre.copilot.controller;

import com.istanbulmetre.copilot.service.IstanbulCopilotAgent;
import com.istanbulmetre.copilot.service.CopilotSuggestionService;
import com.istanbulmetre.copilot.tools.ChartTools;
import com.istanbulmetre.copilot.tools.DatabaseTools;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.message.AiMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class CopilotController {

    private final IstanbulCopilotAgent copilotAgent;
    private final CopilotSuggestionService suggestionService;
    private final ChatMemoryProvider chatMemoryProvider;

    @PostMapping("/api/copilot")
    public ResponseEntity<?> copilotChat(@RequestBody Map<String, Object> payload) {
        String userMsg = (String) payload.get("message");
        if (userMsg == null || userMsg.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Mesaj boş olamaz"));
        }

        String sessionId = (String) payload.getOrDefault("sessionId", "default-session");
        List<Map<String, String>> historyRaw = (List<Map<String, String>>) payload.get("history");

        try {
            // Frontend'den gelen geçmişi LangChain4j ChatMemory ile senkronize et
            ChatMemory memory = chatMemoryProvider.get(sessionId);
            memory.clear();

            if (historyRaw != null) {
                for (Map<String, String> msg : historyRaw) {
                    String role = msg.get("role");
                    String content = msg.get("content");
                    if ("user".equalsIgnoreCase(role)) {
                        memory.add(new UserMessage(content));
                    } else if ("assistant".equalsIgnoreCase(role) || "ai".equalsIgnoreCase(role)) {
                        memory.add(new AiMessage(content));
                    }
                }
            }

            // Model çağrısını gerçekleştir
            String responseText = copilotAgent.chat(sessionId, userMsg);

            // ThreadLocal üzerinden araç çıktılarını (Chart & DB) topla
            Map<String, Object> chartData = ChartTools.getAndClearCurrentChartData();
            List<Map<String, Object>> dbResults = DatabaseTools.getAndClearLastQueryResults();

            // API yanıtını Flask formatında döndür
            Map<String, Object> responseBody = Map.of(
                "response", responseText,
                "chart_data", chartData != null ? chartData : Map.of(),
                "db_results", dbResults != null ? dbResults : List.of()
            );

            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Co-Pilot analizi sırasında bir hata oluştu: " + e.getMessage()));
        } finally {
            // ThreadLocal sızıntılarını ve veri çakışmalarını önlemek için temizle
            ChartTools.clearCurrentChartData();
            DatabaseTools.clearLastQueryResults();
        }
    }

    @GetMapping("/api/copilot/suggestions")
    public ResponseEntity<?> copilotSuggestions() {
        try {
            List<String> suggestions = suggestionService.getCopilotSuggestions();
            return ResponseEntity.ok(Map.of("suggestions", suggestions));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Co-Pilot önerileri üretilirken bir hata oluştu: " + e.getMessage()));
        }
    }
}
