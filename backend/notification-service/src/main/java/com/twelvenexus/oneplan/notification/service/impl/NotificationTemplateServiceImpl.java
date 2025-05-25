package com.twelvenexus.oneplan.notification.service.impl;

import com.twelvenexus.oneplan.notification.enums.NotificationChannel;
import com.twelvenexus.oneplan.notification.enums.NotificationType;
import com.twelvenexus.oneplan.notification.model.NotificationTemplate;
import com.twelvenexus.oneplan.notification.repository.NotificationTemplateRepository;
import com.twelvenexus.oneplan.notification.service.NotificationTemplateService;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationTemplateServiceImpl implements NotificationTemplateService {

  private final NotificationTemplateRepository templateRepository;
  private final TemplateEngine templateEngine;
  private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{([^}]+)\\}\\}");

  @Override
  public NotificationTemplate createTemplate(
      String name,
      NotificationType type,
      NotificationChannel channel,
      String language,
      String subject,
      String bodyTemplate,
      Map<String, String> variables) {
    NotificationTemplate template = new NotificationTemplate();
    template.setName(name);
    template.setType(type);
    template.setChannel(channel);
    template.setLanguage(language);
    template.setSubject(subject);
    template.setBodyTemplate(bodyTemplate);
    template.setVariables(variables);
    template.setActive(true);

    return templateRepository.save(template);
  }

  @Override
  public NotificationTemplate updateTemplate(
      UUID templateId, String subject, String bodyTemplate, Map<String, String> variables) {
    NotificationTemplate template =
        templateRepository
            .findById(templateId)
            .orElseThrow(() -> new IllegalArgumentException("Template not found"));

    template.setSubject(subject);
    template.setBodyTemplate(bodyTemplate);
    template.setVariables(variables);

    return templateRepository.save(template);
  }

  @Override
  @Transactional(readOnly = true)
  public NotificationTemplate getTemplate(
      NotificationType type, NotificationChannel channel, String language) {
    return templateRepository
        .findActiveTemplate(type, channel, language)
        .orElse(templateRepository.findActiveTemplate(type, channel, "en").orElse(null));
  }

  @Override
  public String renderTemplate(NotificationTemplate template, Map<String, String> data) {
    String content = template.getBodyTemplate();

    // If template contains Thymeleaf expressions, use Thymeleaf engine
    if (content.contains("th:") || content.contains("${")) {
      Context context = new Context();
      data.forEach(context::setVariable);
      return templateEngine.process(content, context);
    }

    // Otherwise, use simple variable replacement
    Matcher matcher = VARIABLE_PATTERN.matcher(content);
    StringBuffer result = new StringBuffer();

    while (matcher.find()) {
      String variable = matcher.group(1).trim();
      String value = data.getOrDefault(variable, "");
      matcher.appendReplacement(result, value);
    }
    matcher.appendTail(result);

    return result.toString();
  }

  @Override
  public void activateTemplate(UUID templateId) {
    NotificationTemplate template =
        templateRepository
            .findById(templateId)
            .orElseThrow(() -> new IllegalArgumentException("Template not found"));
    template.setActive(true);
    templateRepository.save(template);
  }

  @Override
  public void deactivateTemplate(UUID templateId) {
    NotificationTemplate template =
        templateRepository
            .findById(templateId)
            .orElseThrow(() -> new IllegalArgumentException("Template not found"));
    template.setActive(false);
    templateRepository.save(template);
  }
}
