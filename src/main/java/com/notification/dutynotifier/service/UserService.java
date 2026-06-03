package com.notification.dutynotifier.service;

import com.notification.dutynotifier.dto.UserRequest;
import com.notification.dutynotifier.entity.User;
import com.notification.dutynotifier.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User create(UserRequest request) {

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .build();

        return userRepository.save(user);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }
}