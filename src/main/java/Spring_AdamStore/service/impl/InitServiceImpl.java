package Spring_AdamStore.service.impl;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j(topic = "INIT-SERVICE")
@RequiredArgsConstructor
public class InitServiceImpl implements InitService {

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserHasRoleService userHasRoleService;
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final ProvinceService provinceService;
    private final DistrictService districtService;
    private final SizeRepository sizeRepository;
    private final CartService cartService;
    private final WardService wardService;
    private final WardRepository wardRepository;
    private final AdminProperties adminProperties;



    @Transactional
    public void initRoles() {
        Role userRole = roleRepository.save(Role.builder()
                .name(RoleEnum.USER.name())
                .description("ROLE_USER")
                .build());

        Role adminRole = roleRepository.save(Role.builder()
                .name(RoleEnum.ADMIN.name())
                .description("ROLE_ADMIN")
                .build());

        List<Role> roleList = List.of(userRole, adminRole);
        roleRepository.saveAllAndFlush(roleList);
    }

    @Transactional
    public void initAdmin() {
        User admin = userRepository.save(User.builder()
                .name("Admin")
                .email(adminProperties.getEmail())
                .password(passwordEncoder.encode(adminProperties.getPassword()))
                .dob(LocalDate.now())
                .gender(Gender.MALE)
                .build());

        userHasRoleService.saveUserHasRole(admin, RoleEnum.ADMIN);

        cartService.createCartForUser(admin);
    }

    @Transactional
    public void initSizes() {
        List<Size> sizeList = SizeEnum.getAllSizes();

        sizeRepository.saveAllAndFlush(sizeList);
    }

    @Transactional
    public void initProvinces(){
        List<Province> provinceList = provinceService.loadProvincesFromGhn();

        provinceRepository.saveAllAndFlush(provinceList);

        provinceList.forEach(this::saveAllDistrictsByProvince);
    }


    private void saveAllDistrictsByProvince(Province province){
        List<District> districtList = districtService.loadDistrictsFromGhn(province.getId());
        districtList.forEach(district -> district.setProvinceId(province.getId()));

        districtRepository.saveAllAndFlush(districtList);

        districtList.forEach(this::saveAllWardsByDistrict);
    }

    private void saveAllWardsByDistrict(District district){
        List<Ward> wardList = wardService.loadWardsFromGhn(district.getId());
        wardList.forEach(ward -> ward.setDistrictId(district.getId()));

        wardRepository.saveAllAndFlush(wardList);
    }


}
