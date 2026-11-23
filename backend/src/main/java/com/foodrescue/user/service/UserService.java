package com.foodrescue.user.service;

import com.foodrescue.common.exception.ApiException;
import com.foodrescue.user.dto.AuthDtos.UpdateProfileRequest;
import com.foodrescue.user.dto.UserDto;
import com.foodrescue.user.entity.User;
import com.foodrescue.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository users;

    @Transactional(readOnly = true)
    public UserDto getProfile(Long userId) {
        return UserDto.from(find(userId));
    }

    @Transactional
    public UserDto updateProfile(Long userId, UpdateProfileRequest req) {
        User user = find(userId);
        user.setFullName(req.fullName().trim());
        user.setPhone(req.phone());
        user.setAddress(req.address());
        user.setLat(req.lat());
        user.setLng(req.lng());
        return UserDto.from(users.save(user));
    }

    private User find(Long id) {
        return users.findById(id).orElseThrow(() -> ApiException.notFound("User"));
    }
}
