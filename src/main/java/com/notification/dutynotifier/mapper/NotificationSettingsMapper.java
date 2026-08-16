package com.notification.dutynotifier.mapper;

import com.notification.dutynotifier.dto.notificationSettings.NotificationSettingsDto;
import com.notification.dutynotifier.entity.notificationSettings.NotificationSettings;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationSettingsMapper {

    NotificationSettingsDto toResponse(NotificationSettings settings);
    @Mapping(target = "id", ignore = true)
    NotificationSettings toEntity(NotificationSettingsDto request);
}