package com.notification.dutynotifier.service.dutyService.userService;

import com.notification.dutynotifier.dto.response.UserResponse;
import com.notification.dutynotifier.dto.userRequest.UserRequest;
import com.notification.dutynotifier.entity.user.User;
import com.notification.dutynotifier.mapper.UserMapper;
import com.notification.dutynotifier.repository.userRepository.UserRepository;
import com.notification.dutynotifier.service.userService.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUser() {

        UserRequest request = new UserRequest();
        request.setName("Alex");
        request.setEmail("alex@gmail.com");

        User user = User.builder()
                .id(1L)
                .name("Alex")
                .email("alex@gmail.com")
                .build();

        UserResponse response = new UserResponse(
                1L,
                "Alex",
                "alex@gmail.com"
        );

        when(userMapper.toEntity(request)).thenReturn(user);

        when(userRepository.save(user)).thenReturn(user);

        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = userService.create(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(userMapper).toEntity(request);
        verify(userRepository).save(user);
        verify(userMapper).toResponse(user);
    }
}