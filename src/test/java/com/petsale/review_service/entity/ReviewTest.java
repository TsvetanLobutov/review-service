package com.petsale.review_service.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewTest {

    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 11, 25, 19, 0);

    @Test
    void storesEveryFieldThroughAccessors() {
        Review review = new Review();
        review.setId("abc123");
        review.setProductId(5L);
        review.setUsername("alice");
        review.setRating(4);
        review.setComment("Great food");
        review.setTags(List.of("fast shipping", "good quality"));
        review.setCreatedAt(CREATED_AT);

        assertThat(review.getId()).isEqualTo("abc123");
        assertThat(review.getProductId()).isEqualTo(5L);
        assertThat(review.getUsername()).isEqualTo("alice");
        assertThat(review.getRating()).isEqualTo(4);
        assertThat(review.getComment()).isEqualTo("Great food");
        assertThat(review.getTags()).containsExactly("fast shipping", "good quality");
        assertThat(review.getCreatedAt()).isEqualTo(CREATED_AT);
    }

    @Test
    void newReviewHasOnlyEmptyFields() {
        Review review = new Review();

        assertThat(review.getId()).isNull();
        assertThat(review.getProductId()).isNull();
        assertThat(review.getUsername()).isNull();
        assertThat(review.getRating()).isNull();
        assertThat(review.getComment()).isNull();
        assertThat(review.getTags()).isNull();
        assertThat(review.getCreatedAt()).isNull();
    }

    @Test
    void reviewsWithSameFieldValuesAreEqual() {
        Review first = review("abc123");
        Review second = review("abc123");
        Review different = review("xyz789");

        assertThat(first).isEqualTo(second);
        assertThat(first).hasSameHashCodeAs(second);
        assertThat(first).isNotEqualTo(different);
    }

    private Review review(String id) {
        Review review = new Review();
        review.setId(id);
        review.setProductId(5L);
        review.setUsername("alice");
        review.setRating(4);
        review.setComment("Great food");
        review.setTags(List.of("fast shipping"));
        review.setCreatedAt(CREATED_AT);
        return review;
    }
}
