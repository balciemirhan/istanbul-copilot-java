package com.istanbulmetre.copilot.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Spring JDBC Template implementation of SuggestionRepository.
 * Strictly uses constructor-based dependency injection.
 */
@Repository
@RequiredArgsConstructor
public class JdbcSuggestionRepository implements SuggestionRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Map<String, Object>> findTopNegativeCategory() {
        String sql = "SELECT category, COUNT(*) as cnt FROM tweets WHERE sentiment = 'negatif' GROUP BY category ORDER BY cnt DESC LIMIT 1";
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public long countIronicTweets() {
        String sql = "SELECT COUNT(*) as cnt FROM tweets WHERE is_ironic = 1 OR is_ironic = '1'";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        if (!rows.isEmpty() && rows.get(0).get("cnt") != null) {
            return ((Number) rows.get(0).get("cnt")).longValue();
        }
        return 0;
    }
}
