package com.twelvenexus.oneplan.notification.service.impl;

import com.twelvenexus.oneplan.notification.model.Notification;
import com.twelvenexus.oneplan.notification.model.NotificationTemplate;
import com.twelvenexus.oneplan.notification.service.NotificationSenderService;
import com.twelvenexus.oneplan.notification.service.NotificationTemplateService;
import jakarta.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSenderServiceImpl implements NotificationSenderService {

  private final JavaMailSender mailSender;
  private final RabbitTemplate rabbitTemplate;
  private final SimpMessagingTemplate messagingTemplate;
  private final NotificationTemplateService templateService;
  private final RestTemplate restTemplate = new RestTemplate();

  @Override
  public void sendEmail(Notification notification) {
    try {
      // Get template if available
      NotificationTemplate template =
          templateService.getTemplate(notification.getType(), notification.getChannel(), "en");

      String subject = notification.getTitle();
      String body = notification.getContent();

      if (template != null) {
        Map<String, String> data = new HashMap<>(notification.getMetadata());
        data.put("title", notification.getTitle());
        data.put("userId", notification.getUserId().toString());

        subject = templateService.renderTemplate(template, data);
        body = templateService.renderTemplate(template, data);
      }

      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true);

      helper.setTo(notification.getRecipient());
      helper.setSubject(subject);
      helper.setText(body, true);

      mailSender.send(message);

      notification.setStatus(
          com.twelvenexus.oneplan.notification.enums.NotificationStatus.DELIVERED);
      notification.setDeliveredAt(java.time.LocalDateTime.now());

      log.info("Email sent successfully to {}", notification.getRecipient());
    } catch (Exception e) {
      log.error("Failed to send email notification", e);
      throw new RuntimeException("Failed to send email", e);
    }
  }

  @Override
  public void sendInApp(Notification notification) {
    try {
      // Send via WebSocket to user
      String destination = "/user/" + notification.getUserId() + "/notifications";

      Map<String, Object> payload = new HashMap<>();
      payload.put("id", notification.getId());
      payload.put("type", notification.getType());
      payload.put("title", notification.getTitle());
      payload.put("content", notification.getContent());
      payload.put("metadata", notification.getMetadata());
      payload.put("createdAt", notification.getCreatedAt());

      messagingTemplate.convertAndSend(destination, payload);

      notification.setStatus(
          com.twelvenexus.oneplan.notification.enums.NotificationStatus.DELIVERED);
      notification.setDeliveredAt(java.time.LocalDateTime.now());

      log.info("In-app notification sent to user {}", notification.getUserId());
    } catch (Exception e) {
      log.error("Failed to send in-app notification", e);
      throw new RuntimeException("Failed to send in-app notification", e);
    }
  }

  @Override
  public void sendPush(Notification notification) {
    try {
      // This would integrate with a push notification service like FCM
      Map<String, Object> pushPayload = new HashMap<>();
      pushPayload.put("to", notification.getMetadata().get("deviceToken"));
      pushPayload.put(
          "notification",
          Map.of(
              "title", notification.getTitle(),
              "body", notification.getContent()));

      // Send to push service (example with FCM)
      String fcmUrl = "https://fcm.googleapis.com/fcm/send";
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("Authorization", "key=" + notification.getMetadata().get("fcmKey"));

      HttpEntity<Map<String, Object>> request = new HttpEntity<>(pushPayload, headers);
      restTemplate.postForObject(fcmUrl, request, String.class);

      notification.setStatus(
          com.twelvenexus.oneplan.notification.enums.NotificationStatus.DELIVERED);
      notification.setDeliveredAt(java.time.LocalDateTime.now());

      log.info("Push notification sent to device");
    } catch (Exception e) {
      log.error("Failed to send push notification", e);
      throw new RuntimeException("Failed to send push notification", e);
    }
  }

  @Override
  public void sendSms(Notification notification) {
    try {
      // This would integrate with an SMS service like Twilio
      Map<String, Object> smsPayload = new HashMap<>();
      smsPayload.put("to", notification.getRecipient());
      smsPayload.put("body", notification.getContent());

      // Send to SMS service (example with Twilio)
      String twilioUrl = "https://api.twilio.com/2010-04-01/Accounts/{AccountSid}/Messages.json";
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
      headers.setBasicAuth(
          notification.getMetadata().get("twilioAccount"),
          notification.getMetadata().get("twilioAuth"));

      HttpEntity<Map<String, Object>> request = new HttpEntity<>(smsPayload, headers);
      restTemplate.postForObject(twilioUrl, request, String.class);

      notification.setStatus(
          com.twelvenexus.oneplan.notification.enums.NotificationStatus.DELIVERED);
      notification.setDeliveredAt(java.time.LocalDateTime.now());

      log.info("SMS sent to {}", notification.getRecipient());
    } catch (Exception e) {
      log.error("Failed to send SMS notification", e);
      throw new RuntimeException("Failed to send SMS", e);
    }
  }

  @Override
  public void sendWebhook(Notification notification) {
    try {
      String webhookUrl = notification.getMetadata().get("webhookUrl");

      Map<String, Object> webhookPayload = new HashMap<>();
      webhookPayload.put("notificationId", notification.getId());
      webhookPayload.put("type", notification.getType());
      webhookPayload.put("title", notification.getTitle());
      webhookPayload.put("content", notification.getContent());
      webhookPayload.put("metadata", notification.getMetadata());
      webhookPayload.put("timestamp", notification.getCreatedAt());

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      // Add any custom headers from metadata
      notification.getMetadata().entrySet().stream()
          .filter(e -> e.getKey().startsWith("header_"))
          .forEach(e -> headers.add(e.getKey().substring(7), e.getValue()));

      HttpEntity<Map<String, Object>> request = new HttpEntity<>(webhookPayload, headers);
      restTemplate.postForObject(webhookUrl, request, String.class);

      notification.setStatus(
          com.twelvenexus.oneplan.notification.enums.NotificationStatus.DELIVERED);
      notification.setDeliveredAt(java.time.LocalDateTime.now());

      log.info("Webhook sent to {}", webhookUrl);
    } catch (Exception e) {
      log.error("Failed to send webhook notification", e);
      throw new RuntimeException("Failed to send webhook", e);
    }
  }
}
