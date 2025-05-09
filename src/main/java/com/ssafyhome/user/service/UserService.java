package com.ssafyhome.user.service;

import java.io.IOException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ssafyhome.security.dto.CustomUserDetails;
import com.ssafyhome.user.dao.UserRepository;
import com.ssafyhome.user.dto.PatchRequest;
import com.ssafyhome.user.dto.RegisterRequest;
import com.ssafyhome.user.dto.User;
import com.ssafyhome.user.dto.UserInfo;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signup(RegisterRequest request) {
        if (isEmailDuplicate(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다: " + request.getEmail());
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_USER");

        MultipartFile profileFile = request.getProfileImage();
        if (profileFile != null && !profileFile.isEmpty()) {
            try {
                byte[] profileBytes = profileFile.getBytes();
                user.setProfile(profileBytes);   // User 엔티티에 byte[] 또는 Blob setter 가 필요
            } catch (IOException e) {
                throw new RuntimeException("프로필 이미지 저장 중 오류 발생", e);
            }
        }

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public boolean isEmailDuplicate(String email) {
        return userRepository.findByEmail(email) != null;
    }

    @Transactional(readOnly = true) // 데이터 조회 작업
    public UserInfo getUserInfoFromDetails(CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new IllegalArgumentException("UserDetails 정보가 없습니다.");
        }

        User user = userDetails.getUser();
        if (user == null) {
            throw new EntityNotFoundException("연결된 사용자 정보를 찾을 수 없습니다.");
        }

        // User 엔티티 정보를 UserInfo DTO로 변환
        return new UserInfo(
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.getProfile()
        );
    }

    @Transactional
    public void deleteUser(CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new IllegalArgumentException("인증 정보가 유효하지 않습니다.");
        }
        String currentUsername = userDetails.getUsername();
        User user = userRepository.findByEmail(currentUsername);

        userRepository.delete(user);
    }

    @Transactional
    public void updateUserInfo(CustomUserDetails userDetails, PatchRequest request) {
        if (userDetails == null) {
            throw new IllegalArgumentException("인증 정보가 유효하지 않습니다.");
        }

        String currentUsername = userDetails.getUsername();
        User user = userRepository.findByEmail(currentUsername);

        if (StringUtils.hasText(request.getName())) {
            user.setName(request.getName());
        }

        String newEmail = request.getEmail();
        if (StringUtils.hasText(newEmail) && !newEmail.equalsIgnoreCase(user.getEmail())) {
            // 변경하려는 새 이메일이 이미 다른 사용자에 의해 사용 중인지 확인
            if (isEmailDuplicate(newEmail)) {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다: " + newEmail);
            }
            user.setEmail(newEmail);
        }

        MultipartFile profileFile = request.getProfile();
        if (profileFile != null && !profileFile.isEmpty()) {
            try {
                byte[] profileBytes = profileFile.getBytes();
                user.setProfile(profileBytes);
            } catch (IOException e) {
                // log.error("프로필 업데이트 중 파일 처리 오류", e); // 로그 남기기
                throw new RuntimeException("프로필 이미지 처리 중 오류가 발생했습니다.", e);
            }
        }

        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
    }



}
