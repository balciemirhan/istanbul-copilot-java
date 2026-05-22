package com.istanbulmetre.copilot.tools;

import com.istanbulmetre.copilot.context.CopilotRequestContext;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ChartTools {

    private final CopilotRequestContext requestContext;

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

        // Grafik verisini RequestScope context'e kaydederek denetleyici (controller) tarafından alınmasını sağlıyoruz
        requestContext.setChartData(chartPayload);

        return "Grafiği yansıtıyorum";
    }
}
