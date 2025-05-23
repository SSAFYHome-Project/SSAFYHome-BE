package com.ssafyhome.common.util;

import com.ssafyhome.security.dto.CustomUserDetails;
import com.ssafyhome.user.dto.User;
import com.ssafyhome.common.exception.AuthenticationException;
import com.ssafyhome.common.exception.ResourceNotFoundException;

public class UserUtils {

    public static User getUserFromUserDetails(CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new AuthenticationException("인증 정보가 유효하지 않습니다.");
        }

        User user = userDetails.getUser();
        if (user == null) {
            throw new ResourceNotFoundException("사용자 정보를 찾을 수 없습니다.");
        }

        return user;
    }

    public static String getEmailFromUserDetails(CustomUserDetails userDetails) {
        return getUserFromUserDetails(userDetails).getEmail();
    }
}
