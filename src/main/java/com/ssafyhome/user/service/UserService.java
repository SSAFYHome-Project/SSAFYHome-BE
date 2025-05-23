package com.ssafyhome.user.service;

import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ssafyhome.common.exception.BusinessException;
import com.ssafyhome.common.exception.ErrorCode;
import com.ssafyhome.common.util.UserUtils;
import com.ssafyhome.user.dao.AddressRepository;
import com.ssafyhome.user.dto.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ssafyhome.security.dto.CustomUserDetails;
import com.ssafyhome.user.dao.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Transactional
    public void signup(UserRegisterRequest request) {
        if (isEmailDuplicate(request.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS, "이미 사용 중인 이메일입니다: " + request.getEmail());
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
                user.setProfile(profileBytes);
            } catch (IOException e) {
                throw new RuntimeException("프로필 이미지 저장 중 오류가 발생했습니다.", e);
            }
        }

        userRepository.save(user);

        if (request.getAddresses() != null) {
            for (AddressDto addrReq : request.getAddresses()) {
                Address address = new Address();
                address.setUser(user);
                address.setTitle(addrReq.getTitle());
                address.setAddress(addrReq.getAddress());
                address.setDetailAddress(addrReq.getDetailAddress());
                address.setX(addrReq.getX());
                address.setY(addrReq.getY());

                addressRepository.save(address);
            }
        }

    }

    @Transactional(readOnly = true)
    public boolean isEmailDuplicate(String email) {

        return userRepository.findByEmail(email) != null;
    }

    @Transactional
    public void changePassword(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new EntityNotFoundException("사용자를 찾을 수 없습니다: " + email);
        }

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD, "현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 유효성 검사
        validatePassword(newPassword);

        // 비밀번호 변경
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetRequired(false); // 비밀번호 변경 완료
        userRepository.save(user);
    }

    private void validatePassword(String password) {
        if (password == null || password.trim().isEmpty() ) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "비밀번호를 입력해주세요.");
        }
    }

    @Transactional(readOnly = true) // 데이터 조회 작업
    public UserInfo getUserInfoFromDetails(CustomUserDetails userDetails) {
        User user = UserUtils.getUserFromUserDetails(userDetails);

        List<AddressDto> addressDtos = addressRepository.findByUser(user).stream()
                .map(addr -> {
                    AddressDto dto = new AddressDto();
                    dto.setTitle(addr.getTitle());
                    dto.setAddress(addr.getAddress());
                    dto.setDetailAddress(addr.getDetailAddress());
                    dto.setX(addr.getX());
                    dto.setY(addr.getY());
                    return dto;
                })
                .collect(Collectors.toList());

        return new UserInfo(
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                addressDtos,
                user.getProfile(),
                user.isPasswordResetRequired()
        );
    }

    @Transactional
    public void deleteUser(CustomUserDetails userDetails) {
        User user = UserUtils.getUserFromUserDetails(userDetails);

        userRepository.delete(user);
    }

    @Transactional
    public void updateUserInfo(CustomUserDetails userDetails, UserPatchRequest request) {
        User user = UserUtils.getUserFromUserDetails(userDetails);

        if (StringUtils.hasText(request.getName())) {
            user.setName(request.getName());
        }

        MultipartFile profileFile = request.getProfile();
        if (profileFile != null && !profileFile.isEmpty()) {
            try {
                byte[] profileBytes = profileFile.getBytes();
                user.setProfile(profileBytes);
            } catch (IOException e) {
                throw new RuntimeException("프로필 이미지 처리 중 오류가 발생했습니다.", e);
            }
        }

        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        List<AddressDto> addressDtos = request.getAddresses();
        if (addressDtos != null && !addressDtos.isEmpty()) {

            // 기존 주소를 Map<TitleType, Address>으로 변환
            Map<TitleType, Address> addressMap = addressRepository.findByUser(user).stream()
                    .collect(Collectors.toMap(Address::getTitle, a -> a));

            for (AddressDto addrReq : addressDtos) {
                TitleType title = addrReq.getTitle();
                Address existing = addressMap.get(title);

                boolean isAllEmpty = !StringUtils.hasText(addrReq.getAddress()) &&
                        !StringUtils.hasText(addrReq.getDetailAddress()) &&
                        !StringUtils.hasText(addrReq.getX()) &&
                        !StringUtils.hasText(addrReq.getY());

                if (isAllEmpty) {
                    // 모든 필드가 비어있으면 → 해당 title 주소 삭제
                    if (existing != null) {
                        addressRepository.delete(existing);
                    }
                    continue;
                }

                if (existing != null) {
                    // 수정
                    existing.setAddress(addrReq.getAddress());
                    existing.setDetailAddress(addrReq.getDetailAddress());
                    existing.setX(addrReq.getX());
                    existing.setY(addrReq.getY());
                    addressRepository.save(existing);
                } else {
                    // 새로 추가
                    Address newAddr = new Address();
                    newAddr.setUser(user);
                    newAddr.setTitle(title);
                    newAddr.setAddress(addrReq.getAddress());
                    newAddr.setDetailAddress(addrReq.getDetailAddress());
                    newAddr.setX(addrReq.getX());
                    newAddr.setY(addrReq.getY());
                    addressRepository.save(newAddr);
                }
            }
        }
    }

    public SchoolWorkAddress findSchoolAndWorkAddress(String username) {
        User user = userRepository.findByEmail(username);

        if (user == null) {
            throw new EntityNotFoundException("사용자를 찾을 수 없습니다: " + username);
        }

        String workAddress = addressRepository.findByUserAndTitle(user, TitleType.COMPANY)
                .map(Address::getAddress)
                .orElse("");

        String schoolAddress = addressRepository.findByUserAndTitle(user, TitleType.SCHOOL)
                .map(Address::getAddress)
                .orElse("");

        return new SchoolWorkAddress(workAddress, schoolAddress);
    }

    public List<UserInfo> searchUserInfo(String keyword) {
        List<User> users = userRepository.searchByKeyword(keyword);

        return users.stream().map(user -> {
            List<AddressDto> addressDtos = addressRepository.findByUser(user).stream()
                    .map(addr -> {
                        AddressDto dto = new AddressDto();
                        dto.setTitle(addr.getTitle());
                        dto.setAddress(addr.getAddress());
                        dto.setDetailAddress(addr.getDetailAddress());
                        dto.setX(addr.getX());
                        dto.setY(addr.getY());
                        return dto;
                    }).collect(Collectors.toList());

            return new UserInfo(user.getName(), user.getEmail(), user.getPassword(), user.getRole(), addressDtos, user.getProfile(), user.isPasswordResetRequired());
        }).collect(Collectors.toList());
    }

    @Transactional
    public void resetPassword(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "해당 이메일로 등록된 사용자가 없습니다: " + email);
        }

        // 임시 비밀번호 생성 (8자리)
        String tempPassword = generateRandomPassword(8);

        // 비밀번호 암호화 및 저장
        user.setPassword(passwordEncoder.encode(tempPassword));
        user.setPasswordResetRequired(true);
        userRepository.save(user);

        // 이메일 전송
        emailService.sendPasswordResetEmail(email, tempPassword);
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(chars.length());
            sb.append(chars.charAt(randomIndex));
        }

        return sb.toString();
    }

}
