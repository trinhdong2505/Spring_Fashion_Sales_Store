package Spring_AdamStore.service.relationship;

import Spring_AdamStore.constants.EntityStatus;
import Spring_AdamStore.constants.RoleEnum;
import Spring_AdamStore.entity.Role;
import Spring_AdamStore.entity.User;
import Spring_AdamStore.entity.relationship.UserHasRole;
import Spring_AdamStore.entity.relationship.UserHasRoleId;
import Spring_AdamStore.exception.AppException;
import Spring_AdamStore.exception.ErrorCode;
import Spring_AdamStore.repository.RoleRepository;
import Spring_AdamStore.repository.relationship.UserHasRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j(topic = "USER-HAS-ROLE-SERVICE")
@Service
@RequiredArgsConstructor
public class UserHasRoleService {

    private final UserHasRoleRepository userHasRoleRepository;
    private final RoleRepository roleRepository;

    public UserHasRole saveUserHasRole(User user, RoleEnum roleEnum){
        Role role = roleRepository.findByName(roleEnum.name())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        UserHasRole userHasRole = UserHasRole.builder()
                .id(new UserHasRoleId(user.getId(), role.getId()))
                .build();

        return userHasRoleRepository.save(userHasRole);
    }


    public boolean checkRoleForUser(User user, RoleEnum roleEnum){
       Role role = roleRepository.findByName(roleEnum.name())
            .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

       return userHasRoleRepository.findById(UserHasRoleId.builder()
               .userId(user.getId())
               .roleId(role.getId())
               .build())
               .isPresent();
    }

}
