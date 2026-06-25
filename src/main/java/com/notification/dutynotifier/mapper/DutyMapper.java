package com.notification.dutynotifier.mapper;

import com.notification.dutynotifier.dto.response.DutyResponse;
import com.notification.dutynotifier.entity.duty.Duty;
import com.notification.dutynotifier.entity.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DutyMapper {

    @Mapping(target = "users", expression = "java(mapUsers(duty))")
    @Mapping(target = "userIds", expression = "java(mapUserIds(duty))")

    DutyResponse toResponse(Duty duty);

    default List<String> mapUsers(Duty duty) {
        return duty.getUsers()
                .stream()
                .map(User::getName)
                .toList();
    }

    default List<Long> mapUserIds(Duty duty) {
        return duty.getUsers()
                .stream()
                .map(User::getId)
                .toList();
    }
}