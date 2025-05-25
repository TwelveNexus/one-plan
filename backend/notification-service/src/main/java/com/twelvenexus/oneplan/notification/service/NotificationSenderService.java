package com.twelvenexus.oneplan.notification.service;

import com.twelvenexus.oneplan.notification.model.Notification;

public interface NotificationSenderService {

  void sendEmail(Notification notification);

  void sendInApp(Notification notification);

  void sendPush(Notification notification);

  void sendSms(Notification notification);

  void sendWebhook(Notification notification);
}
