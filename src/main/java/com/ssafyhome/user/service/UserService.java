package com.ssafyhome.user.service;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ssafyhome.user.dao.AddressRepository;
import com.ssafyhome.user.dto.*;
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

    @Transactional
    public void signup(UserRegisterRequest request) {
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

    @Transactional(readOnly = true) // 데이터 조회 작업
    public UserInfo getUserInfoFromDetails(CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new IllegalArgumentException("인증 정보가 유효하지 않습니다.");
        }

        User user = userDetails.getUser();
        if (user == null) {
            throw new EntityNotFoundException("사용자 정보를 찾을 수 없습니다.");
        }

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
    public void updateUserInfo(CustomUserDetails userDetails, UserPatchRequest request) {
        if (userDetails == null) {
            throw new IllegalArgumentException("인증 정보가 유효하지 않습니다.");
        }

        String currentUsername = userDetails.getUsername();
        User user = userRepository.findByEmail(currentUsername);

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

    public String findSchoolOrWorkAddress(String username) {
        User user = userRepository.findByEmail(username);

        // 우선순위: 직장 > 학교
        return addressRepository.findByUserAndTitle(user, TitleType.COMPANY)
                .or(() -> addressRepository.findByUserAndTitle(user, TitleType.SCHOOL))
                .map(Address::getAddress)
                .orElse("");
    }

}
