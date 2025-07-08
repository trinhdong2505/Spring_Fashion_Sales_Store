package Spring_AdamStore.repository;

import Spring_AdamStore.entity.RedisRevokedToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisRevokedTokenRepository extends CrudRepository<RedisRevokedToken, String> {

    boolean existsById(String accessToken);

}
