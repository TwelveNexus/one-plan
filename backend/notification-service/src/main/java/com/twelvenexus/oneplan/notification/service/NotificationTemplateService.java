package com.twelvenexus.oneplan.notification.service;

import com.twelvenexus.oneplan.notification.model.NotificationTemplate;
import com.twelvenexus.oneplan.notification.enums.NotificationChannel;
import com.twelvenexus.oneplan.notification.enums.NotificationType;

import java.util.Map;
import java.util.UUID;

public interface NotificationTemplateService {
    
    NotificationTemplate createTemplate(String name, NotificationType type,
                                      NotificationChannel channel, String language,
                                      String subject, String bodyTemplate,
                                      Map<String, String> variables);
    
    NotificationTemplate updateTemplate(UUID templateId, String subject, 
                                      String bodyTemplate, Map<String, String> variables);
    
    NotificationTemplate getTemplate(NotificationType type, NotificationChannel channel,
                                   String language);
    
    String renderTemplate(NotificationTemplate template, Map<String, String> data);
    
    void activateTemplate(UUID templateId);
    
    void deactivateTemplate(UUID templateId);
}
