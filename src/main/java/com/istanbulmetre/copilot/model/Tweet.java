package com.istanbulmetre.copilot.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) representing a single Tweet record in the SQLite database.
 * Promotes high-level type-safety and OOP best practices.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tweet {
    private Long id;
    private String tweetId;
    private String text;
    private String sentiment;
    private String category;
    private LocalDateTime createdAt;
    private Boolean isIronic;
    private Double score;
    private Integer likes;
    private Integer retweets;
    private Integer views;
    private String authorUsername;
}
