package Spring_AdamStore.mapper;

import Spring_AdamStore.dto.basic.EntityBasic;
import Spring_AdamStore.entity.Role;
import Spring_AdamStore.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserMappingHelper {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public Set<EntityBasic> getRoles(Long userId) {
        Set<Role> roleSet = roleRepository.findRolesByUserId(userId);

        return roleMapper.toEntityBasicSet(roleSet);
    }
}
