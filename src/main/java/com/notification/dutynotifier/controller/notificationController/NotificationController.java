package com.notification.dutynotifier.controller.notificationController;

import com.notification.dutynotifier.dto.notificationRequest.NotificationRequest;
import com.notification.dutynotifier.service.notificationService.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/send")
    public void send(@RequestBody @Valid NotificationRequest request) {
        notificationService.sendNotification(request);
    }
}