package Spring_AdamStore.repository;

import Spring_AdamStore.entity.RedisForgotPasswordToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RedisForgotPwdTokenRepository extends CrudRepository<RedisForgotPasswordToken, String> {


}
