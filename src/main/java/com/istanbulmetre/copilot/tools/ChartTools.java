package com.istanbulmetre.copilot.tools;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class ChartTools {

    private static final ThreadLocal<Map<String, Object>> currentChartData = new ThreadLocal<>();

    public static Map<String, Object> getAndClearCurrentChartData() {
        Map<String, Object> data = currentChartData.get();
        currentChartData.remove();
        return data;
    }

    public static void clearCurrentChartData() {
        currentChartData.remove();
    }

    @Tool("generate_chart_json: Kullanıcı grafik çizilmesini istediğinde çalışır. Chart.js formatında grafik verisi üretir.")
    public String generateChartJson(String chartType, String labelsCsv, String dataCsv, String title) {
        if (labelsCsv == null || dataCsv == null) {
            throw new IllegalArgumentException("Etiketler (labelsCsv) ve veriler (dataCsv) boş olamaz.");
        }

        List<String> labels = Arrays.stream(labelsCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        List<Integer> data = Arrays.stream(dataCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .toList();

        if (labels.size() != data.size()) {
            throw new IllegalArgumentException("Etiketler ve veriler eşit sayıda eleman içermelidir.");
        }

        Map<String, Object> chartPayload = Map.of(
            "type", chartType,
            "labels", labels,
            "data", data,
            "title", title != null ? title : "Sentiment Analizi Dağılımı"
        );

        // Grafik verisini ThreadLocal'e kaydederek denetleyici (controller) tarafından alınmasını sağlıyoruz
        currentChartData.set(chartPayload);

        return "Grafiği yansıtıyorum";
    }
}
