package com.petsale.review_service.repository;

import com.petsale.review_service.entity.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, String> {
    // Spring Data automatically writes the MongoDB query for this!
    List<Review> findByProductId(Long productId);
}