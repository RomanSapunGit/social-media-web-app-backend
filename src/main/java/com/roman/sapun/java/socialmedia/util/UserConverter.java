package com.roman.sapun.java.socialmedia.util;

import com.roman.sapun.java.socialmedia.dto.SignUpDTO;
import com.roman.sapun.java.socialmedia.entity.UserEntity;

public interface UserConverter {
    UserEntity convertToUserEntity(SignUpDTO signUpDTO, UserEntity entity);
}
