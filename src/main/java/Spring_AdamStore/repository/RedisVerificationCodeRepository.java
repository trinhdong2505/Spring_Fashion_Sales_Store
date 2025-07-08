package Spring_AdamStore.repository;

import Spring_AdamStore.entity.RedisVerificationCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RedisVerificationCodeRepository extends CrudRepository<RedisVerificationCode, String> {

}



