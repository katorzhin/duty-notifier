package com.notification.dutynotifier.mapper;

import com.notification.dutynotifier.dto.response.UserResponse;
import com.notification.dutynotifier.dto.userRequest.UserRequest;
import com.notification.dutynotifier.entity.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserRequest request);
    UserResponse toResponse(User user);
}