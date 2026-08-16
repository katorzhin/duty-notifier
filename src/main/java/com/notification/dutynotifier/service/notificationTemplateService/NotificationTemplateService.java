package com.notification.dutynotifier.service.notificationTemplateService;

import com.notification.dutynotifier.entity.notificationTemplate.NotificationTemplate;
import com.notification.dutynotifier.repository.notificationTemplateRepository.NotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationTemplateService {

    private static final Long TEMPLATE_ID = 1L;

    private final NotificationTemplateRepository repository;

    public NotificationTemplate getTemplate() {

        return repository.findById(TEMPLATE_ID)
                .orElseGet(this::createDefaultTemplate);
    }

    public NotificationTemplate updateTemplate(NotificationTemplate template) {

        template.setId(TEMPLATE_ID);

        return repository.save(template);
    }

    private NotificationTemplate createDefaultTemplate() {

        NotificationTemplate template = NotificationTemplate.builder()
                .id(TEMPLATE_ID)
                .todayTemplate("""
                        🔔 Сьогодні чергують:
                        
                        {{TODAY}}
                        """)
                .scheduleTemplate("""
                        📅 Наступні чергування:
                        
                        {{NEXT_DUTIES}}
                        """)
                .build();

        return repository.save(template);
    }
}