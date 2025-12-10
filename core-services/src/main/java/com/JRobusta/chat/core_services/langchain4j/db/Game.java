package com.JRobusta.chat.core_services.langchain4j.db;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "games", indexes = {
        @Index(name = "idx_games_title", columnList = "title"),
        @Index(name = "idx_games_is_vectorized", columnList = "is_vectorized")
})
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Game {

    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "price", precision = 15, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "currency", length = 10)
    private String currency = "VND";

    @Column(name = "developer")
    private String developer;

    @Column(name = "publisher")
    private String publisher;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "genre_tags", columnDefinition = "TEXT")
    private String genreTags;

    @Column(name = "review_summary")
    private String reviewSummary;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "system_requirements", columnDefinition = "TEXT")
    private String systemRequirements;

    @Column(name = "image_url", length = 2083)
    private String imageUrl;

    @Column(name = "source_url", length = 2083)
    private String sourceUrl;

    @Column(name = "is_vectorized")
    private Boolean isVectorized = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


}