package Spring_AdamStore.repository;

import Spring_AdamStore.entity.RedisPendingUser;
import Spring_AdamStore.entity.RedisRevokedToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisPendingUserRepository extends CrudRepository<RedisPendingUser, String> {


}
