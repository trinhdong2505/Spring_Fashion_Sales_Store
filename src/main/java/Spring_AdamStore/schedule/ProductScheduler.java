package Spring_AdamStore.schedule;

import Spring_AdamStore.constants.EntityStatus;
import Spring_AdamStore.entity.Product;
import Spring_AdamStore.entity.ProductVariant;
import Spring_AdamStore.repository.ProductRepository;
import Spring_AdamStore.repository.ProductVariantRepository;
import Spring_AdamStore.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j(topic = "PRODUCT-SCHEDULER")
@Component
@RequiredArgsConstructor
public class ProductScheduler {

    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final ProductVariantRepository productVariantRepository;


    @Scheduled(cron = "0 0 0 */7 * ?")
    public void updateProductRatings() {
        log.info("Update Products Ratings");

        List<Product> productList = productRepository.findAll();
        for (Product product : productList) {
            double avgRating = reviewRepository.getAverageRatingByProductId(product.getId());
            int totalReviews = reviewRepository.getTotalReviewsByProductId(product.getId());
            product.setAverageRating(avgRating);
            product.setTotalReviews(totalReviews);
            productRepository.save(product);
        }
    }

    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void checkProductsAvailability() {
        log.info("The product availability check...");

        Iterable<ProductVariant> productVariants = productVariantRepository.findAll();
        for (ProductVariant variant : productVariants) {
            if (variant.getQuantity() == 0) {
                variant.setIsAvailable(false);
                productVariantRepository.save(variant);
            }
        }

        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            List<ProductVariant> productVariantList = getListProductVariant(product);

            boolean allVariantsUnavailable = productVariantList.stream()
                    .filter(variant -> variant.getStatus() == EntityStatus.ACTIVE)
                    .noneMatch(ProductVariant::getIsAvailable);

            if (allVariantsUnavailable) {
                product.setIsAvailable(false);
                productRepository.save(product);
            }

        }
    }

    private List<ProductVariant> getListProductVariant(Product product){
        return productVariantRepository.findAllByProductId(product.getId());
    }
}
