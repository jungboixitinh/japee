package com.identity.service;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.event.dto.NotificationEvent;
import com.identity.dto.response.EmailVerificationResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.identity.constant.PredefinedRole;
import com.identity.dto.request.UserCreationRequest;
import com.identity.dto.request.UserUpdateRequest;
import com.identity.dto.response.UserResponse;
import com.identity.entity.Role;
import com.identity.entity.User;
import com.identity.exception.AppException;
import com.identity.exception.ErrorCode;
import com.identity.mapper.ProfileMapper;
import com.identity.mapper.UserMapper;
import com.identity.repository.RoleRepository;
import com.identity.repository.UserRepository;
import com.identity.repository.httpclient.ProfileClient;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    ProfileMapper profileMapper;
    PasswordEncoder passwordEncoder;
    ProfileClient profileClient;
    KafkaTemplate<String, Object> kafkaTemplate;
    StringRedisTemplate stringRedisTemplate;
    SecureRandom SECURE_RANDOM = new SecureRandom();

    public UserResponse createUser(UserCreationRequest request) {
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        HashSet<Role> roles = new HashSet<>();

        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);

        user.setRoles(roles);
        user.setEmailVerified(false);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException exception){
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        var profileRequest = profileMapper.toProfileCreationRequest(request);
        profileRequest.setUserId(user.getId());

        var profile = profileClient.createProfile(profileRequest);

        String verifyCode = generateVerificationCode(request.getEmail());

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .channel("EMAIL")
                .recipient(request.getEmail())
                .subject("Welcome to Born Hub")
                .body("Hello, " + request.getUsername() + ". Your verification code is: " + verifyCode
                        + ". Please verify your email to continue exploring BornHub!")
                .build();

        kafkaTemplate.send("notification-delivery", notificationEvent);

        var userCreationResponse = userMapper.toUserResponse(user);
        userCreationResponse.setId(profile.getResult().getId());

        return userCreationResponse;
    }

    private String generateVerificationCode(String email) {
        try {
            String code = String.valueOf (100000 + SECURE_RANDOM.nextInt(900000));
            String key = "verify" + email;

            stringRedisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);
            return code;
        } catch (Exception e) {
            throw new AppException(ErrorCode.CANNOT_CREATE_OTP);
        }
    }

    public EmailVerificationResponse verifyCode(String email, String code) {
        String key = "verify" + email;
        String storedCode = stringRedisTemplate.opsForValue().get(key);

        if (storedCode != null && storedCode.equals(code)) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            user.setEmailVerified(true);
            userRepository.save(user);
            stringRedisTemplate.delete(key);

            NotificationEvent notificationEvent = NotificationEvent.builder()
                    .channel("EMAIL")
                    .recipient(user.getEmail())
                    .subject("Email Verified Successfully")
                    .body("Hello, " + user.getUsername() + ". Your email has been verified successfully.")
                    .build();

            kafkaTemplate.send("email-verification-delivery", notificationEvent);

            return EmailVerificationResponse.builder()
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .emailVerified(user.isEmailVerified())
                    .build();
        } else {
            throw new AppException(ErrorCode.INVALID_OTP);
        }
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        log.info("In method get Users");
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUser(String id) {
        return userMapper.toUserResponse(
                userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }
}
