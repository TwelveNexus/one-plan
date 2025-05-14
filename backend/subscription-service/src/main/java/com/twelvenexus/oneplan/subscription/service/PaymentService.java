package com.twelvenexus.oneplan.subscription.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.twelvenexus.oneplan.subscription.enums.PaymentGateway;
import com.twelvenexus.oneplan.subscription.enums.PaymentStatus;
import com.twelvenexus.oneplan.subscription.model.Payment;
import com.twelvenexus.oneplan.subscription.model.PaymentMethod;

public interface PaymentService {

    Payment initiatePayment(UUID tenantId, UUID subscriptionId, BigDecimal amount,
                           PaymentGateway gateway, Map<String, String> metadata);

    Payment completePayment(String gatewayOrderId, String gatewayPaymentId,
                           String signature, PaymentGateway gateway);

    Payment createPaymentFromWebhook(PaymentGateway gateway, Map<String, Object> webhookData);

    Payment getPayment(UUID paymentId);

    Payment getPaymentByGatewayOrderId(String gatewayOrderId);

    List<Payment> getTenantPayments(UUID tenantId);

    List<Payment> getSubscriptionPayments(UUID subscriptionId);

    Payment refundPayment(UUID paymentId, BigDecimal amount, String reason);

    void updatePaymentStatus(UUID paymentId, PaymentStatus status, String reason);

    void processStalePayments();

    Map<String, Object> createPaymentLink(UUID tenantId, UUID subscriptionId,
                                         BigDecimal amount, PaymentGateway gateway);

    PaymentMethod savePaymentMethod(UUID tenantId, PaymentGateway gateway,
                                   String type, Map<String, String> details);

    List<PaymentMethod> getTenantPaymentMethods(UUID tenantId);

    void setDefaultPaymentMethod(UUID paymentMethodId);

    Double getTotalRevenue(UUID tenantId, LocalDateTime startDate, LocalDateTime endDate);
}
