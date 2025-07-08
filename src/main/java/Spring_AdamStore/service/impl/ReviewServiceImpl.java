package Spring_AdamStore.service.impl;

import Spring_AdamStore.dto.request.ReviewRequest;
import Spring_AdamStore.dto.request.ReviewUpdateRequest;
import Spring_AdamStore.dto.response.ReviewResponse;
import Spring_AdamStore.entity.Product;
import Spring_AdamStore.entity.Review;
import Spring_AdamStore.entity.User;
import Spring_AdamStore.exception.AppException;
import Spring_AdamStore.exception.ErrorCode;
import Spring_AdamStore.mapper.ReviewMapper;
import Spring_AdamStore.mapper.ReviewMappingHelper;
import Spring_AdamStore.repository.ProductRepository;
import Spring_AdamStore.repository.ReviewRepository;
import Spring_AdamStore.service.CurrentUserService;
import Spring_AdamStore.service.ReviewService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "REVIEW-SERVICE")
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final CurrentUserService currentUserService;
    private final ReviewMapper reviewMapper;
    private final ProductRepository productRepository;
    private final ReviewMappingHelper reviewMappingHelper;

    @Override
    @Transactional
    public ReviewResponse create(ReviewRequest request) {
        log.info("Creating review with data= {}", request);

        Review review = reviewMapper.toReview(request, reviewMappingHelper);

        User user = currentUserService.getCurrentUser();
        review.setUserId(user.getId());

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
        review.setProductId(product.getId());

        return reviewMapper.toReviewResponse(reviewRepository.save(review));
    }

    @Override
    @Transactional
    public ReviewResponse update(Long id, ReviewUpdateRequest request) {
        log.info("Updated review with data= {}", request);

        Review review = findReviewById(id);

        reviewMapper.update(review, request, reviewMappingHelper);

        return reviewMapper.toReviewResponse(reviewRepository.save(review));
    }

    @Override
    public void delete(Long id) {
        log.info("Delete Review By Id: {}", id);

        Review review = findReviewById(id);
        reviewRepository.delete(review);
    }

    private Review findReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_EXISTED));
    }
}
