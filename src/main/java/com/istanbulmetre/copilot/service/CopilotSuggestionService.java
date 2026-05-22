package com.istanbulmetre.copilot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CopilotSuggestionService {

    private final JdbcTemplate jdbcTemplate;

    public List<String> getCopilotSuggestions() {
        List<String> suggestions = new ArrayList<>();

        Map<String, String> catMap = Map.of(
            "makro_ekonomi", "Makro Ekonomi",
            "ulasim_lojistik", "Ulaşım ve Lojistik",
            "gayrimenkul_insaat", "Gayrimenkul ve İnşaat",
            "ticaret_perakende", "Ticaret ve Perakende",
            "genel", "Genel"
        );

        try {
            // 1. En çok negatif tweet barındıran kategoriyi bul
            String sqlNegCat = "SELECT category, COUNT(*) as cnt FROM tweets WHERE sentiment = 'negatif' GROUP BY category ORDER BY cnt DESC LIMIT 1";
            List<Map<String, Object>> negCatRows = jdbcTemplate.queryForList(sqlNegCat);

            if (!negCatRows.isEmpty()) {
                Map<String, Object> row = negCatRows.get(0);
                String catRaw = (String) row.get("category");
                Long count = ((Number) row.get("cnt")).longValue();

                if (count > 0) {
                    String catTr = catMap.getOrDefault(catRaw, catRaw != null ? catRaw.replace("_", " ") : "Genel");
                    suggestions.add("🔴 Enflasyon ve hayat pahalılığının yoğun olduğu bu dönemde, veritabanımızda en çok negatif bildirim **" + catTr + "** kategorisinde birikmiş görünüyor. Bu kategorideki ana şikayetleri ve çözüm önerilerini analiz eder misin?");
                } else {
                    suggestions.add("📊 Projedeki genel duygu (sentiment) dağılımını bir pasta grafik (pie chart) ile yansıtıp özetler misin?");
                }
            } else {
                suggestions.add("📊 Projedeki genel duygu (sentiment) dağılımını bir pasta grafik (pie chart) ile yansıtıp özetler misin?");
            }

            // 2. İroni/Sarkastik tweet sayısını kontrol et
            String sqlIronic = "SELECT COUNT(*) as cnt FROM tweets WHERE is_ironic = 1 OR is_ironic = '1'";
            List<Map<String, Object>> ironicRows = jdbcTemplate.queryForList(sqlIronic);
            long ironicCount = 0;
            if (!ironicRows.isEmpty()) {
                ironicCount = ((Number) ironicRows.get(0).get("cnt")).longValue();
            }

            if (ironicCount > 0) {
                suggestions.add("😏 Halkın öfke yerine sarkastik/ironik tepki verdiği **" + ironicCount + " adet** tweet tespit ettim. Bu alaycı protestolar en çok hangi konuda yoğunlaşıyor?");
            } else {
                suggestions.add("📈 Son 1 aydaki duygu durumu değişim trendini analiz edip ani bir anomali (spike) olup olmadığını kontrol eder misin?");
            }

            // 3. Karar Destek / Yönetici Aksiyon Planı odaklı
            suggestions.add("🏛️ İstanbul Büyükşehir Belediyesi yönetimi için en kritik kategorilere dair Kısa, Orta ve Uzun vadeli stratejik karar destek planı hazırlar mısın?");

        } catch (Exception e) {
            System.err.println("Dinamik öneriler oluşturulurken hata (Java): " + e.getMessage());
            // Hata durumunda güvenli yedek sorular
            suggestions = List.of(
                "📊 Veritabanındaki duygu (sentiment) dağılımını grafik ile yansıtıp analiz edebilir misin?",
                "🏛️ İstanbul Büyükşehir Belediyesi yönetimi için negatif kategorilere dair 3 aşamalı eylem planı hazırlar mısın?",
                "📈 Son dönemde öne çıkan negatif trendleri ve bunların arkasındaki katalizör nedenleri listeler misin?"
            );
        }

        // Her zaman tam olarak 3 adet benzersiz öneri döndüğümüzden emin olalım
        while (suggestions.size() < 3) {
            suggestions.add("📊 Veritabanındaki tweetleri kategorilerine göre grafik olarak yansıtıp analiz eder misin?");
        }

        return suggestions.subList(0, 3);
    }
}
