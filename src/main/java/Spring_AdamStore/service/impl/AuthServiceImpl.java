package Spring_AdamStore.service.impl;

import Spring_AdamStore.constants.EntityStatus;
import Spring_AdamStore.constants.RoleEnum;
import Spring_AdamStore.constants.TokenType;
import Spring_AdamStore.dto.basic.EntityBasic;
import Spring_AdamStore.dto.request.*;
import Spring_AdamStore.dto.response.TokenResponse;
import Spring_AdamStore.dto.response.UserResponse;
import Spring_AdamStore.dto.response.VerificationCodeResponse;
import Spring_AdamStore.entity.*;
import Spring_AdamStore.exception.AppException;
import Spring_AdamStore.exception.ErrorCode;
import Spring_AdamStore.mapper.AddressMappingHelper;
import Spring_AdamStore.mapper.UserMapper;
import Spring_AdamStore.mapper.UserMappingHelper;
import Spring_AdamStore.repository.RedisPendingUserRepository;
import Spring_AdamStore.repository.RoleRepository;
import Spring_AdamStore.repository.UserRepository;
import Spring_AdamStore.service.*;
import Spring_AdamStore.service.relationship.UserHasRoleService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import static Spring_AdamStore.constants.EntityStatus.ACTIVE;
import static Spring_AdamStore.constants.VerificationType.REGISTER;

@Service
@Slf4j(topic = "AUTH-SERVICE")
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final UserMapper userMapper;
    private final CartService cartService;
    private final UserHasRoleService userHasRoleService;
    private final RedisTokenService redisTokenService;
    private final RedisVerificationCodeService redisVerificationCodeService;
    private final RedisPendingUserRepository redisPendingUserRepository;
    private final CurrentUserService currentUserService;
    private final EmailService emailService;
    private final RoleRepository roleRepository;
    private final UserMappingHelper userMappingHelper;


    @Override
    public TokenResponse login(LoginRequest request) throws JOSEException {
        log.info("Handling login for email: {}", request.getEmail());

        User userDB = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean isAuthenticated = passwordEncoder.matches(request.getPassword(), userDB.getPassword());

        if(!isAuthenticated){
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }
        return generateAndSaveTokenResponse(userDB);
    }

    @Transactional
    @Override
    public VerificationCodeResponse register(RegisterRequest request) {
        log.info("Handling register for email: {}", request.getEmail());

        checkEmailExist(request.getEmail());

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        }

        RedisPendingUser redisPendingUser = userMapper.registerToRedis(request);
        redisPendingUser.setPassword(passwordEncoder.encode(request.getPassword()));

        RedisVerificationCode redisVerificationCode = redisVerificationCodeService
                .saveVerificationCode(redisPendingUser.getEmail(), REGISTER);

        redisPendingUser.setTtl(redisVerificationCode.getTtl());
        redisPendingUserRepository.save(redisPendingUser);

        emailService.sendOtpRegisterEmail(redisPendingUser.getEmail(),
                redisPendingUser.getName(), redisVerificationCode.getVerificationCode());

        return VerificationCodeResponse.builder()
                .email(redisVerificationCode.getEmail())
                .verificationCode(redisVerificationCode.getVerificationCode())
                .ttl(redisVerificationCode.getTtl())
                .build();
    }

    @Transactional
    @Override
    public TokenResponse verifyCodeAndRegister(VerifyCodeRequest request) throws JOSEException {
        log.info("Verifying register code for email: {}", request.getEmail());

        RedisVerificationCode code = redisVerificationCodeService
                .getVerificationCode(request.getEmail(), REGISTER, request.getVerificationCode());

        RedisPendingUser pendingUser = redisPendingUserRepository
                .findById(code.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.PENDING_USER_NOT_FOUND));

        User user = userMapper.redisToUser(pendingUser);

        userRepository.save(user);

        userHasRoleService.saveUserHasRole(user, RoleEnum.USER);

        cartService.createCartForUser(user);

        return generateAndSaveTokenResponse(user);
    }

    @Override
    public UserResponse getMyInfo() {
        log.info("Fetching current user info");

        User user = currentUserService.getCurrentUser();

        return userMapper.toUserResponse(user, userMappingHelper);
    }

    @Override
    public TokenResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        log.info("Refreshing token");

        // verify refresh token (db, expirationTime ...)
        SignedJWT signedJWT = tokenService.verifyToken(request.getRefreshToken(), TokenType.REFRESH_TOKEN);

        String email = signedJWT.getJWTClaimsSet().getSubject();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Set<Role> roleSet = roleRepository.findRolesByUserId(user.getId());

        Set<EntityBasic> roleBasic = roleSet.stream()
                .map(role -> new EntityBasic(role.getId(), role.getName()))
                .collect(Collectors.toSet());

        // new access token
        String accessToken = tokenService.generateToken(user, TokenType.ACCESS_TOKEN);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(request.getRefreshToken())
                .email(email)
                .roles(roleBasic)
                .build();
    }

    @Transactional
    @Override
    public void changePassword(ChangePasswordRequest request) {
        log.info("Changing password for current user");

        User user = currentUserService.getCurrentUser();

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        }

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_OLD_PASSWORD);
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public void logout(TokenRequest request) throws ParseException, JOSEException {
        log.info("Logging out");

        SignedJWT signToken = tokenService.verifyToken(request.getAccessToken(), TokenType.ACCESS_TOKEN);

        String email = signToken.getJWTClaimsSet().getJWTID();
        Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

        RedisRevokedToken redisRevokedToken = RedisRevokedToken.builder()
                .accessToken(request.getAccessToken())
                .email(email)
                .expiryTime(expiryTime)
                .ttl((expiryTime.getTime() - System.currentTimeMillis()) / 1000)
                .build();

        redisTokenService.saveRevokedToken(redisRevokedToken);
    }

    private TokenResponse generateAndSaveTokenResponse(User user) throws JOSEException {
        String accessToken = tokenService.generateToken(user, TokenType.ACCESS_TOKEN);

        String refreshToken = tokenService.generateToken(user, TokenType.REFRESH_TOKEN);

        tokenService.saveRefreshToken(refreshToken);

        Set<Role> roleSet = roleRepository.findRolesByUserId(user.getId());

        Set<EntityBasic> roleBasic = roleSet.stream()
                .map(role -> new EntityBasic(role.getId(), role.getName()))
                .collect(Collectors.toSet());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .authenticated(true)
                .email(user.getEmail())
                .roles(roleBasic)
                .build();
    }

    private void checkEmailExist(String email) {
        if (userRepository.countByEmailAndStatus(email, ACTIVE.name()) > 0) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        if (userRepository.countByEmailAndStatus(email, EntityStatus.INACTIVE.name()) > 0) {
            throw new AppException(ErrorCode.EMAIL_DISABLED);
        }
    }
}
