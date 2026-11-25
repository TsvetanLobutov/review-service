package com.petsale.review_service.controller;

import com.petsale.review_service.entity.Review;
import com.petsale.review_service.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
@ActiveProfiles("test")
class ReviewControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewRepository reviewRepository;

    @Test
    void addReviewReturnsTheStoredReviewAsJson() throws Exception {
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> {
            Review review = invocation.getArgument(0);
            review.setId("generated-id");
            return review;
        });

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productId": 5, "username": "alice", "rating": 4,
                                 "comment": "Great food", "tags": ["fast shipping"]}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("generated-id"))
                .andExpect(jsonPath("$.productId").value(5))
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.rating").value(4))
                .andExpect(jsonPath("$.tags[0]").value("fast shipping"))
                .andExpect(jsonPath("$.createdAt").isString());
    }

    @Test
    void reviewsForAProductAreReturnedAsAnArray() throws Exception {
        when(reviewRepository.findByProductId(5L)).thenReturn(List.of(
                review("a", "alice", 5),
                review("b", "bob", 3)));

        mockMvc.perform(get("/api/reviews/product/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username").value("alice"))
                .andExpect(jsonPath("$[0].rating").value(5))
                .andExpect(jsonPath("$[1].username").value("bob"));
    }

    @Test
    void productWithoutReviewsReturnsAnEmptyArray() throws Exception {
        when(reviewRepository.findByProductId(5L)).thenReturn(List.of());

        mockMvc.perform(get("/api/reviews/product/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void nonNumericProductIdIsRejectedWithBadRequest() throws Exception {
        mockMvc.perform(get("/api/reviews/product/not-a-number"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void malformedJsonIsRejectedWithBadRequest() throws Exception {
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not-json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void acceptsAnOutOfRangeRatingBecauseThePayloadIsNotValidated() throws Exception {
        // Characterization: rating is documented as 1..5 but nothing enforces it.
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productId": 5, "username": "alice", "rating": 9}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(9));
    }

    private Review review(String id, String username, int rating) {
        Review review = new Review();
        review.setId(id);
        review.setProductId(5L);
        review.setUsername(username);
        review.setRating(rating);
        review.setCreatedAt(LocalDateTime.now());
        return review;
    }
}
