package Spring_AdamStore.service;

import Spring_AdamStore.dto.request.PromotionRequest;
import Spring_AdamStore.dto.request.ReviewRequest;
import Spring_AdamStore.dto.request.ReviewUpdateRequest;
import Spring_AdamStore.dto.response.PageResponse;
import Spring_AdamStore.dto.response.PromotionResponse;
import Spring_AdamStore.dto.response.ReviewResponse;
import org.springframework.data.domain.Pageable;

public interface ReviewService {

    ReviewResponse create(ReviewRequest request);

    ReviewResponse update(Long id, ReviewUpdateRequest request);

    void delete(Long id);
}
