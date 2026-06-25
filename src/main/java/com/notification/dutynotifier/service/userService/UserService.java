package com.notification.dutynotifier.service.userService;

import com.notification.dutynotifier.dto.response.UserResponse;
import com.notification.dutynotifier.dto.userRequest.UserRequest;
import com.notification.dutynotifier.entity.user.User;
import com.notification.dutynotifier.exception.UserNotFoundException;
import com.notification.dutynotifier.mapper.UserMapper;
import com.notification.dutynotifier.repository.dutyRepository.DutyRepository;
import com.notification.dutynotifier.repository.userRepository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final DutyRepository dutyRepository;

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

    public UserResponse update(Long id, UserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        return userMapper.toResponse(
                userRepository.save(user)
        );
    }

    public void delete(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        if (dutyRepository.existsByUsers_Id(id)) {
            throw new IllegalStateException("Cannot delete user because they are assigned to duties.");
        }

        userRepository.delete(user);
    }
}