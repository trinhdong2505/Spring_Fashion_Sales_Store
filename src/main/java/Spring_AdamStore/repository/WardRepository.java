package Spring_AdamStore.repository;

import Spring_AdamStore.entity.District;
import Spring_AdamStore.entity.Ward;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WardRepository extends JpaRepository<Ward, String> {

    Page<Ward> findByDistrictId(Integer districtId, Pageable pageable);


    Optional<Ward> findByCode(String code);
}
