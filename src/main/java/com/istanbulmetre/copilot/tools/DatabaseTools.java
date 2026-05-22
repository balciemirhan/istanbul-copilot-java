package com.istanbulmetre.copilot.tools;

import com.istanbulmetre.copilot.context.CopilotRequestContext;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DatabaseTools {

    private final JdbcTemplate jdbcTemplate;
    private final CopilotRequestContext requestContext;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Tool("query_sqlite_db: istanbul_ekonomi.db veritabanındaki tweets tablosunu sorgulamak için kullanılır. " +
          "Sadece SELECT sorguları çalıştırılabilir. " +
          "Sorgulanabilecek Gerçek Kolonlar: " +
          "- id (INTEGER): Benzersiz ID. " +
          "- tweet_id (VARCHAR): Orijinal X API ID'si. " +
          "- text (VARCHAR): Tweet metni (Sorguda 'tweet_text' kullanılırsa otomatik haritalanır). " +
          "- sentiment (VARCHAR): Duygu durumu ('pozitif', 'negatif', 'notr' değerlerini alır, mutlaka küçük harfli değerlerle sorgulanmalıdır). " +
          "- category (VARCHAR): Kategori ('makro_ekonomi', 'ulasim_lojistik', 'gayrimenkul_insaat', 'ticaret_perakende', 'genel' değerlerini alır, alt tireli ve küçük harfli kullanılmalıdır). " +
          "- created_at (DATETIME): Tweet oluşturulma tarihi. " +
          "- is_ironic (BOOLEAN): İroni içerip içermediği (Sorguda 'irony' kullanılırsa otomatik haritalanır). " +
          "- score (FLOAT): Duygu analizi güven skoru. " +
          "- likes (INTEGER), retweets (INTEGER), views (INTEGER): Etkileşim istatistikleri. " +
          "- author_username (VARCHAR): Kullanıcı adı.")
    public String querySqliteDb(String query) {
        try {
            // Dinamik Kolon Haritalaması (tweet_text -> text, irony -> is_ironic)
            String sanitizedQuery = query;
            sanitizedQuery = sanitizedQuery.replaceAll("(?i)\\btweet_text\\b", "text");
            sanitizedQuery = sanitizedQuery.replaceAll("(?i)\\birony\\b", "is_ironic");

            // Güvenlik Kontrolü: Sadece SELECT sorgularına izin ver
            if (!sanitizedQuery.trim().toUpperCase().startsWith("SELECT")) {
                return "{\"error\": \"Güvenlik kısıtlaması: Sadece SELECT sorguları çalıştırılabilir.\"}";
            }

            System.out.println("Çalıştırılan SQL Sorgusu (Java): " + sanitizedQuery);

            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sanitizedQuery);
            if (rows.isEmpty()) {
                return "{\"message\": \"Sorgu başarıyla çalıştırıldı ancak eşleşen veri bulunamadı.\"}";
            }

            // Sorgu sonuçlarını RequestScope context'e kaydediyoruz
            requestContext.setDbResults(rows);

            return objectMapper.writeValueAsString(rows);
        } catch (Exception e) {
            System.err.println("SQL sorgu hatası: " + e.getMessage());
            return "{\"error\": \"Sorgu çalıştırılırken hata oluştu: " + e.getMessage() + "\"}";
        }
    }
}
