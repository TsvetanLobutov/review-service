package com.petsale.review_service.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "reviews")
@Data
public class Review {
    @Id
    private String id; // MongoDB uses String IDs (ObjectId) by default
    private Long productId; // Links back to our Postgres product
    private String username;
    private Integer rating; // 1 to 5
    private String comment;
    private List<String> tags; // e.g., "fast shipping", "good quality"
    private LocalDateTime createdAt;
}