package Spring_AdamStore.service.impl;

import Spring_AdamStore.dto.request.PromotionRequest;
import Spring_AdamStore.dto.request.PromotionUpdateRequest;
import Spring_AdamStore.dto.response.PageResponse;
import Spring_AdamStore.dto.response.PromotionResponse;
import Spring_AdamStore.entity.Promotion;
import Spring_AdamStore.exception.AppException;
import Spring_AdamStore.exception.ErrorCode;
import Spring_AdamStore.mapper.PromotionMapper;
import Spring_AdamStore.repository.PromotionRepository;
import Spring_AdamStore.repository.relationship.PromotionUsageRepository;
import Spring_AdamStore.service.PromotionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static Spring_AdamStore.constants.EntityStatus.ACTIVE;


@Service
@Slf4j(topic = "PROMOTION-SERVICE")
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionMapper promotionMapper;
    private final PromotionUsageRepository promotionUsageRepository;


    @Override
    @Transactional
    public PromotionResponse create(PromotionRequest request) {
        log.info("Creating Promotion with data= {}", request);

        if(promotionRepository.countByCode(request.getCode()) > 0){
            throw new AppException(ErrorCode.PROMOTION_EXISTED);
        }

        Promotion promotion = promotionMapper.toPromotion(request);

        return promotionMapper.toPromotionResponse(promotionRepository.save(promotion));
    }

    @Override
    public PromotionResponse fetchById(Long id) {
        log.info("Fetch Promotion By Id: {}", id);

        Promotion promotion = findPromotionById(id);

        return promotionMapper.toPromotionResponse(promotion);
    }

    @Override
    public PageResponse<PromotionResponse> fetchAll(Pageable pageable) {
        log.info("Fetch All Promotion For Admin");

        Page<Promotion> promotionPage = promotionRepository.findAllPromotions(pageable);

        return PageResponse.<PromotionResponse>builder()
                .page(promotionPage.getNumber())
                .size(promotionPage.getSize())
                .totalPages(promotionPage.getTotalPages())
                .totalItems(promotionPage.getTotalElements())
                .items(promotionMapper.toPromotionResponseList(promotionPage.getContent()))
                .build();
    }

    @Override
    @Transactional
    public PromotionResponse update(Long id, PromotionUpdateRequest request) {
        log.info("Updated Promotion with data= {}", request);

        Promotion promotion = findPromotionById(id);

        if(!request.getCode().equals(promotion.getCode()) && promotionRepository.countByCode(request.getCode()) > 0){
            throw new AppException(ErrorCode.PROMOTION_EXISTED);
        }

        promotionMapper.update(promotion, request);

        return promotionMapper.toPromotionResponse(promotionRepository.save(promotion));
    }

    @Transactional
    @Override
    public void delete(Long id) {
        log.info("Delete Promotion By Id: {}", id);

        Promotion promotion = findPromotionById(id);

        if(promotionUsageRepository.existsByPromotionId(promotion.getId())){
            throw new AppException(ErrorCode.PROMOTION_USAGE_CONFLICT);
        }

        promotionRepository.delete(promotion);
    }

    @Override
    public PromotionResponse restore(long id) {
        log.info("Restore Promotion By Id: {}", id);

        Promotion promotion = promotionRepository.findPromotionById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BRANCH_NOT_EXISTED));

        promotion.setStatus(ACTIVE);
        return promotionMapper.toPromotionResponse(promotionRepository.save(promotion));
    }

    private Promotion findPromotionById(Long id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PROMOTION_NOT_EXISTED));
    }
}
