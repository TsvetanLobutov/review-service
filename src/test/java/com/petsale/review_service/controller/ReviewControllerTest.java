package com.petsale.review_service.controller;

import com.petsale.review_service.entity.Review;
import com.petsale.review_service.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewController reviewController;

    @Test
    void addReviewStampsCreatedAtAndReturnsTheSavedReview() {
        Review incoming = review(null);
        Review stored = review("generated-id");
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        when(reviewRepository.save(any(Review.class))).thenReturn(stored);

        Review result = reviewController.addReview(incoming);

        LocalDateTime after = LocalDateTime.now().plusSeconds(1);
        assertThat(result).isSameAs(stored);
        assertThat(incoming.getCreatedAt()).isBetween(before, after);
        verify(reviewRepository).save(incoming);
    }

    @Test
    void addReviewOverwritesAClientSuppliedCreatedAt() {
        // The client cannot backdate a review: the controller always replaces the timestamp.
        Review incoming = review(null);
        incoming.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        reviewController.addReview(incoming);

        assertThat(incoming.getCreatedAt()).isAfter(LocalDateTime.of(2025, 1, 1, 0, 0));
    }

    @Test
    void addReviewKeepsAClientSuppliedId() {
        // Characterization: a client-supplied _id is forwarded to Mongo, so it can overwrite
        // (upsert) an existing review instead of creating a new one.
        Review incoming = review("client-chosen-id");
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        reviewController.addReview(incoming);

        ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo("client-chosen-id");
    }

    @Test
    void addReviewDoesNotTouchTheProductId() {
        Review incoming = review(null);
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        reviewController.addReview(incoming);

        assertThat(incoming.getProductId()).isEqualTo(5L);
    }

    @Test
    void getReviewsForProductReturnsTheRepositoryResult() {
        List<Review> reviews = List.of(review("a"), review("b"));
        when(reviewRepository.findByProductId(5L)).thenReturn(reviews);

        assertThat(reviewController.getReviewsForProduct(5L)).containsExactlyElementsOf(reviews);
    }

    @Test
    void getReviewsForProductReturnsAnEmptyListWhenThereAreNoReviews() {
        when(reviewRepository.findByProductId(5L)).thenReturn(List.of());

        assertThat(reviewController.getReviewsForProduct(5L)).isEmpty();
    }

    @Test
    void getReviewsForProductQueriesByTheGivenProductId() {
        when(reviewRepository.findByProductId(7L)).thenReturn(List.of());

        reviewController.getReviewsForProduct(7L);

        verify(reviewRepository).findByProductId(7L);
    }

    private Review review(String id) {
        Review review = new Review();
        review.setId(id);
        review.setProductId(5L);
        review.setUsername("alice");
        review.setRating(4);
        review.setComment("Great food");
        review.setTags(List.of("fast shipping"));
        return review;
    }
}
