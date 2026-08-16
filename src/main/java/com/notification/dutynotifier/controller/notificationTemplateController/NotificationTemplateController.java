package com.notification.dutynotifier.controller.notificationTemplateController;

import com.notification.dutynotifier.dto.notificationTemplate.NotificationTemplateDto;
import com.notification.dutynotifier.mapper.NotificationTemplateMapper;
import com.notification.dutynotifier.service.notificationTemplateService.NotificationTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification-template")
@RequiredArgsConstructor
public class NotificationTemplateController {

    private final NotificationTemplateService service;
    private final NotificationTemplateMapper mapper;

    @GetMapping
    public NotificationTemplateDto getTemplate() {

        return mapper.toResponse(service.getTemplate());
    }

    @PutMapping
    public NotificationTemplateDto updateTemplate(
            @RequestBody @Valid NotificationTemplateDto request) {

        return mapper.toResponse(service.updateTemplate(mapper.toEntity(request)));
    }
}