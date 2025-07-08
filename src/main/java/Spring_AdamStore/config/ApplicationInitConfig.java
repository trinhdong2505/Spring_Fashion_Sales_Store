package Spring_AdamStore.config;

import Spring_AdamStore.constants.AdminProperties;
import Spring_AdamStore.constants.Gender;
import Spring_AdamStore.constants.RoleEnum;
import Spring_AdamStore.constants.SizeEnum;
import Spring_AdamStore.entity.*;
import Spring_AdamStore.repository.*;
import Spring_AdamStore.service.*;
import Spring_AdamStore.service.relationship.UserHasRoleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j(topic = "APPLICATION-INITIALIZATION")
@RequiredArgsConstructor
@Configuration
public class ApplicationInitConfig {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ProvinceRepository provinceRepository;
    private final InitService initService;
    private final SizeRepository sizeRepository;
    private final AdminProperties adminProperties;


    @Bean
    ApplicationRunner applicationRunner() {
        log.info("INIT APPLICATION STARTING....");

        return args -> {

            if (roleRepository.count() == 0) {
                log.info("Initializing roles...");

                initService.initRoles();
            }

            if (userRepository.countByEmail(adminProperties.getEmail()) == 0) {
                log.info("Creating default admin account...");

                initService.initAdmin();
            }

            if(sizeRepository.count() == 0){
                log.info("Initializing Sizes...");

                initService.initSizes();
            }

            if(provinceRepository.count() == 0){
                log.info("Initializing Provinces and Districts...");

                initService.initProvinces();
            }

        };
    }



}
