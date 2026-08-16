package com.notification.dutynotifier.mapper;

import com.notification.dutynotifier.dto.notificationTemplate.NotificationTemplateDto;
import com.notification.dutynotifier.entity.notificationTemplate.NotificationTemplate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationTemplateMapper {

    NotificationTemplateDto toResponse(NotificationTemplate template);

    @Mapping(target = "id", ignore = true)
    NotificationTemplate toEntity(NotificationTemplateDto dto);
}