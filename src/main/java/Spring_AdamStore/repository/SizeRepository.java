package Spring_AdamStore.repository;

import Spring_AdamStore.entity.Color;
import Spring_AdamStore.entity.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface SizeRepository extends JpaRepository<Size, Long> {

}
