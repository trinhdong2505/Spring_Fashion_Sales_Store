package Spring_AdamStore.repository;

import Spring_AdamStore.entity.Branch;
import Spring_AdamStore.entity.Product;
import Spring_AdamStore.entity.ProductVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    List<ProductVariant> findAllByProductId(Long productId);

    Optional<ProductVariant> findByProductIdAndColorIdAndSizeId(Long productId, Long colorId, Long sizeId);

    @Query(value = "SELECT COUNT(*) FROM product_variants WHERE color_id = :colorId", nativeQuery = true)
    long countByColorId(@Param("colorId") Long colorId);

    @Query(value = "SELECT * FROM product_variants pv WHERE pv.id = :id", nativeQuery = true)
    Optional<ProductVariant> findProductVariantById(@Param("id") Long id);

    @Query(value = "SELECT pv.* FROM product_variants pv WHERE pv.product_id = :productId", nativeQuery = true)
    Page<ProductVariant> findAllVariantsByProductId(Long productId, Pageable pageable);

    @Query( value = "SELECT * FROM product_variants pv WHERE pv.product_id = :productId", nativeQuery = true)
   List<ProductVariant> getAllByProductId(@Param("productId") Long productId);
}
