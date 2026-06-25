package com.notification.dutynotifier.service.subscriberService;

import com.notification.dutynotifier.repository.subscriberRepository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;

    public long count() {
        return subscriberRepository.count();
    }
}