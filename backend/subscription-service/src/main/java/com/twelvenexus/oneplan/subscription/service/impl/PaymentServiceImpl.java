package com.twelvenexus.oneplan.subscription.service.impl;

import com.twelvenexus.oneplan.subscription.enums.PaymentGateway;
import com.twelvenexus.oneplan.subscription.enums.PaymentStatus;
import com.twelvenexus.oneplan.subscription.model.Payment;
import com.twelvenexus.oneplan.subscription.model.PaymentMethod;
import com.twelvenexus.oneplan.subscription.model.Subscription;
import com.twelvenexus.oneplan.subscription.repository.PaymentMethodRepository;
import com.twelvenexus.oneplan.subscription.repository.PaymentRepository;
import com.twelvenexus.oneplan.subscription.service.PaymentService;
import com.twelvenexus.oneplan.subscription.service.SubscriptionService;
import com.twelvenexus.oneplan.subscription.service.gateway.PaymentGatewayService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

  private final PaymentRepository paymentRepository;
  private final PaymentMethodRepository paymentMethodRepository;
  private final SubscriptionService subscriptionService;
  private final Map<String, PaymentGatewayService> gatewayServices;

  @Override
  public Payment initiatePayment(
      UUID tenantId,
      UUID subscriptionId,
      BigDecimal amount,
      PaymentGateway gateway,
      Map<String, String> metadata) {
    Subscription subscription = subscriptionService.getSubscription(subscriptionId);

    String orderId = generateOrderId();
    metadata.put("tenantId", tenantId.toString());
    metadata.put("subscriptionId", subscriptionId.toString());

    PaymentGatewayService gatewayService = getGatewayService(gateway);
    Payment payment = gatewayService.createOrder(orderId, amount, "INR", metadata);

    payment.setTenantId(tenantId);
    payment.setSubscription(subscription);

    log.info("Initiated payment for tenant: {} amount: {} INR via {}", tenantId, amount, gateway);
    return paymentRepository.save(payment);
  }

  @Override
  public Payment completePayment(
      String gatewayOrderId, String gatewayPaymentId, String signature, PaymentGateway gateway) {
    Payment payment =
        paymentRepository
            .findByGatewayOrderId(gatewayOrderId)
            .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

    PaymentGatewayService gatewayService = getGatewayService(gateway);
    Payment verifiedPayment =
        gatewayService.verifyPayment(gatewayOrderId, gatewayPaymentId, signature);

    payment.setGatewayPaymentId(verifiedPayment.getGatewayPaymentId());
    payment.setGatewaySignature(verifiedPayment.getGatewaySignature());
    payment.setStatus(verifiedPayment.getStatus());
    payment.setPaidAt(verifiedPayment.getPaidAt());

    if (payment.getStatus() == PaymentStatus.COMPLETED) {
      // Update subscription status
      Subscription subscription = payment.getSubscription();
      subscriptionService.updateSubscriptionStatus(
          subscription.getId(),
          com.twelvenexus.oneplan.subscription.enums.SubscriptionStatus.ACTIVE);

      log.info("Payment completed successfully: {}", payment.getId());
    }

    return paymentRepository.save(payment);
  }

  @Override
  public Payment createPaymentFromWebhook(PaymentGateway gateway, Map<String, Object> webhookData) {
    String orderId = (String) webhookData.get("orderId");
    String paymentId = (String) webhookData.get("paymentId");

    Payment payment = paymentRepository.findByGatewayOrderId(orderId).orElse(new Payment());

    payment.setGatewayOrderId(orderId);
    payment.setGatewayPaymentId(paymentId);
    payment.setGateway(gateway);
    payment.setStatus((PaymentStatus) webhookData.get("status"));
    payment.setAmount((BigDecimal) webhookData.get("amount"));
    payment.setPaidAt((LocalDateTime) webhookData.get("paidAt"));

    return paymentRepository.save(payment);
  }

  @Override
  @Transactional(readOnly = true)
  public Payment getPayment(UUID paymentId) {
    return paymentRepository
        .findById(paymentId)
        .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public Payment getPaymentByGatewayOrderId(String gatewayOrderId) {
    return paymentRepository
        .findByGatewayOrderId(gatewayOrderId)
        .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public List<Payment> getTenantPayments(UUID tenantId) {
    return paymentRepository.findByTenantIdAndStatus(tenantId, PaymentStatus.COMPLETED);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Payment> getSubscriptionPayments(UUID subscriptionId) {
    return paymentRepository.findBySubscriptionId(subscriptionId);
  }

  @Override
  public Payment refundPayment(UUID paymentId, BigDecimal amount, String reason) {
    Payment payment = getPayment(paymentId);

    if (payment.getStatus() != PaymentStatus.COMPLETED) {
      throw new IllegalStateException("Can only refund completed payments");
    }

    PaymentGatewayService gatewayService = getGatewayService(payment.getGateway());
    Payment refundedPayment =
        gatewayService.refundPayment(payment.getGatewayPaymentId(), amount, reason);

    payment.setRefundedAmount(refundedPayment.getRefundedAmount());
    payment.setRefundedAt(refundedPayment.getRefundedAt());
    payment.setRefundReason(reason);
    payment.setStatus(PaymentStatus.REFUNDED);

    log.info("Refunded payment: {} amount: {} INR", paymentId, amount);
    return paymentRepository.save(payment);
  }

  @Override
  public void updatePaymentStatus(UUID paymentId, PaymentStatus status, String reason) {
    Payment payment = getPayment(paymentId);
    payment.setStatus(status);

    if (status == PaymentStatus.FAILED) {
      payment.setFailureReason(reason);
    }

    paymentRepository.save(payment);
  }

  @Override
  public void processStalePayments() {
    LocalDateTime cutoffDate = LocalDateTime.now().minusHours(2);
    List<Payment> stalePayments = paymentRepository.findStalePayments(cutoffDate);

    for (Payment payment : stalePayments) {
      payment.setStatus(PaymentStatus.CANCELLED);
      payment.setFailureReason("Payment timeout");
      paymentRepository.save(payment);

      log.info("Cancelled stale payment: {}", payment.getId());
    }
  }

  @Override
  public Map<String, Object> createPaymentLink(
      UUID tenantId, UUID subscriptionId, BigDecimal amount, PaymentGateway gateway) {
    String orderId = generateOrderId();
    Map<String, String> metadata =
        Map.of(
            "tenantId", tenantId.toString(),
            "subscriptionId", subscriptionId.toString(),
            "type", "payment_link");

    PaymentGatewayService gatewayService = getGatewayService(gateway);
    Map<String, Object> linkDetails =
        gatewayService.createPaymentLink(amount, "INR", "Subscription Payment", metadata);

    // Create payment record
    Payment payment = new Payment();
    payment.setTenantId(tenantId);
    payment.setGateway(gateway);
    payment.setAmount(amount);
    payment.setCurrency("INR");
    payment.setStatus(PaymentStatus.PENDING);
    payment.setGatewayOrderId(orderId);
    payment.setMetadata(metadata);

    paymentRepository.save(payment);

    linkDetails.put("paymentId", payment.getId());
    return linkDetails;
  }

  @Override
  public PaymentMethod savePaymentMethod(
      UUID tenantId, PaymentGateway gateway, String type, Map<String, String> details) {
    PaymentMethod paymentMethod = new PaymentMethod();
    paymentMethod.setTenantId(tenantId);
    paymentMethod.setGateway(gateway);
    paymentMethod.setType(type);

    PaymentGatewayService gatewayService = getGatewayService(gateway);

    switch (type) {
      case "card":
        PaymentMethod tokenizedCard = gatewayService.tokenizeCard(details);
        paymentMethod.setTokenId(tokenizedCard.getTokenId());
        paymentMethod.setCardLast4(details.get("last4"));
        paymentMethod.setCardBrand(details.get("brand"));
        break;
      case "upi":
        PaymentMethod upiMethod = gatewayService.saveUpiId(details.get("upiId"));
        paymentMethod.setUpiId(upiMethod.getUpiId());
        break;
      case "netbanking":
        paymentMethod.setBankName(details.get("bankName"));
        break;
    }

    paymentMethod.setActive(true);
    return paymentMethodRepository.save(paymentMethod);
  }

  @Override
  @Transactional(readOnly = true)
  public List<PaymentMethod> getTenantPaymentMethods(UUID tenantId) {
    return paymentMethodRepository.findByTenantIdAndActiveTrue(tenantId);
  }

  @Override
  public void setDefaultPaymentMethod(UUID paymentMethodId) {
    PaymentMethod paymentMethod =
        paymentMethodRepository
            .findById(paymentMethodId)
            .orElseThrow(() -> new IllegalArgumentException("Payment method not found"));

    // Reset current default
    paymentMethodRepository.resetDefaultPaymentMethod(paymentMethod.getTenantId());

    // Set new default
    paymentMethod.setDefault(true);
    paymentMethodRepository.save(paymentMethod);
  }

  @Override
  @Transactional(readOnly = true)
  public Double getTotalRevenue(UUID tenantId, LocalDateTime startDate, LocalDateTime endDate) {
    Double revenue = paymentRepository.getTotalRevenue(tenantId, startDate, endDate);
    return revenue != null ? revenue : 0.0;
  }

  private String generateOrderId() {
    return "ORD_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
  }

  private PaymentGatewayService getGatewayService(PaymentGateway gateway) {
    String serviceName = gateway.name().toLowerCase() + "Service";
    PaymentGatewayService service = gatewayServices.get(serviceName);

    if (service == null) {
      throw new IllegalArgumentException("Unsupported payment gateway: " + gateway);
    }

    return service;
  }
}
