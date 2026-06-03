package com.notification.dutynotifier.service.userService;

import com.notification.dutynotifier.dto.response.UserResponse;
import com.notification.dutynotifier.dto.userRequest.UserRequest;
import com.notification.dutynotifier.entity.user.User;
import com.notification.dutynotifier.mapper.UserMapper;
import com.notification.dutynotifier.repository.userRepository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponse create(UserRequest request) {

        User user = userMapper.toEntity(request);

        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    public List<UserResponse> getAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }
}