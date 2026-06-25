package com.notification.dutynotifier.controller.subscriberController;

import com.notification.dutynotifier.service.subscriberService.SubscriberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/subscribers")
@RequiredArgsConstructor
public class SubscriberController {

    private final SubscriberService subscriberService;

    @GetMapping("/count")
    public long count() {
        return subscriberService.count();
    }
}