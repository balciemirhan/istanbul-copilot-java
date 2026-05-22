package com.istanbulmetre.copilot.repository;

import java.util.List;
import java.util.Map;

/**
 * Repository interface to abstract database operations away from business service layers.
 * Adheres to standard Data Access Object / Repository patterns.
 */
public interface SuggestionRepository {
    
    /**
     * Finds the category with the highest amount of negative sentiment tweets.
     *
     * @return List containing query result rows as a map.
     */
    List<Map<String, Object>> findTopNegativeCategory();

    /**
     * Counts the total number of ironic/sarcastic tweets.
     *
     * @return Count of ironic tweets.
     */
    long countIronicTweets();
}
