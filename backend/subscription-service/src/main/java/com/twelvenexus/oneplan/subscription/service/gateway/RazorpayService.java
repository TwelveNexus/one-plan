package com.twelvenexus.oneplan.subscription.service.gateway;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.twelvenexus.oneplan.subscription.enums.PaymentGateway;
import com.twelvenexus.oneplan.subscription.enums.PaymentStatus;
import com.twelvenexus.oneplan.subscription.model.Payment;
import com.twelvenexus.oneplan.subscription.model.PaymentMethod;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RazorpayService implements PaymentGatewayService {

  private final RazorpayClient razorpayClient;
  private final String webhookSecret;

  public RazorpayService(
      @Value("${payment.razorpay.key-id}") String keyId,
      @Value("${payment.razorpay.key-secret}") String keySecret,
      @Value("${payment.razorpay.webhook-secret}") String webhookSecret) {
    try {
      this.razorpayClient = new RazorpayClient(keyId, keySecret);
      this.webhookSecret = webhookSecret;
    } catch (RazorpayException e) {
      throw new RuntimeException("Failed to initialize Razorpay client", e);
    }
  }

  @Override
  public Payment createOrder(
      String orderId, BigDecimal amount, String currency, Map<String, String> metadata) {
    try {
      JSONObject orderRequest = new JSONObject();
      orderRequest.put(
          "amount", amount.multiply(BigDecimal.valueOf(100)).intValue()); // Convert to paise
      orderRequest.put("currency", currency);
      orderRequest.put("receipt", orderId);
      orderRequest.put("notes", metadata);

      Order order = razorpayClient.orders.create(orderRequest);

      Payment payment = new Payment();
      payment.setGateway(PaymentGateway.RAZORPAY);
      payment.setAmount(amount);
      payment.setCurrency(currency);
      payment.setGatewayOrderId(order.get("id"));
      payment.setStatus(PaymentStatus.PENDING);
      payment.setMetadata(metadata);

      log.info("Created Razorpay order: {}", (String) order.get("id"));
      return payment;

    } catch (RazorpayException e) {
      log.error("Failed to create Razorpay order", e);
      throw new RuntimeException("Failed to create payment order", e);
    }
  }

  @Override
  public Payment verifyPayment(String orderId, String paymentId, String signature) {
    try {
      JSONObject options = new JSONObject();
      options.put("razorpay_order_id", orderId);
      options.put("razorpay_payment_id", paymentId);
      options.put("razorpay_signature", signature);

      boolean isValid = Utils.verifyPaymentSignature(options, webhookSecret);

      if (isValid) {
        // Fetch payment details
        com.razorpay.Payment razorpayPayment = razorpayClient.payments.fetch(paymentId);

        Payment payment = new Payment();
        payment.setGatewayOrderId(orderId);
        payment.setGatewayPaymentId(paymentId);
        payment.setGatewaySignature(signature);
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaidAt(LocalDateTime.now());

        log.info("Payment verified successfully: {}", paymentId);
        return payment;
      } else {
        throw new RuntimeException("Invalid payment signature");
      }

    } catch (Exception e) {
      log.error("Failed to verify payment", e);
      throw new RuntimeException("Failed to verify payment", e);
    }
  }

  @Override
  public Payment capturePayment(String paymentId, BigDecimal amount) {
    try {
      JSONObject captureRequest = new JSONObject();
      captureRequest.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue());

      com.razorpay.Payment razorpayPayment =
          razorpayClient.payments.capture(paymentId, captureRequest);

      Payment payment = new Payment();
      payment.setGatewayPaymentId(paymentId);
      payment.setStatus(PaymentStatus.COMPLETED);
      payment.setPaidAt(LocalDateTime.now());

      log.info("Payment captured: {}", paymentId);
      return payment;

    } catch (RazorpayException e) {
      log.error("Failed to capture payment", e);
      throw new RuntimeException("Failed to capture payment", e);
    }
  }

  @Override
  public Payment refundPayment(String paymentId, BigDecimal amount, String reason) {
    try {
      JSONObject refundRequest = new JSONObject();
      refundRequest.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue());
      refundRequest.put("notes", new JSONObject().put("reason", reason));

      com.razorpay.Refund refund = razorpayClient.payments.refund(paymentId, refundRequest);

      Payment payment = new Payment();
      payment.setGatewayPaymentId(paymentId);
      payment.setRefundedAmount(amount);
      payment.setRefundedAt(LocalDateTime.now());
      payment.setRefundReason(reason);
      payment.setStatus(PaymentStatus.REFUNDED);

      log.info("Payment refunded: {}", (String) refund.get("id"));
      return payment;

    } catch (RazorpayException e) {
      log.error("Failed to refund payment", e);
      throw new RuntimeException("Failed to refund payment", e);
    }
  }

  @Override
  public PaymentMethod tokenizeCard(Map<String, String> cardDetails) {
    // Implement card tokenization logic
    return new PaymentMethod();
  }

  @Override
  public PaymentMethod saveUpiId(String upiId) {
    PaymentMethod paymentMethod = new PaymentMethod();
    paymentMethod.setGateway(PaymentGateway.RAZORPAY);
    paymentMethod.setType("upi");
    paymentMethod.setUpiId(upiId);
    paymentMethod.setActive(true);
    return paymentMethod;
  }

  @Override
  public void handleWebhook(String webhookBody, String signature) {
    try {
      JSONObject payload = new JSONObject(webhookBody);
      String event = payload.getString("event");

      // Verify webhook signature
      boolean isValid = Utils.verifyWebhookSignature(webhookBody, signature, webhookSecret);

      if (!isValid) {
        throw new RuntimeException("Invalid webhook signature");
      }

      // Process webhook based on event type
      switch (event) {
        case "payment.captured":
          handlePaymentCaptured(payload);
          break;
        case "payment.failed":
          handlePaymentFailed(payload);
          break;
        case "order.paid":
          handleOrderPaid(payload);
          break;
        default:
          log.warn("Unhandled webhook event: {}", event);
      }

    } catch (Exception e) {
      log.error("Failed to process webhook", e);
      throw new RuntimeException("Failed to process webhook", e);
    }
  }

  @Override
  public Map<String, Object> createPaymentLink(
      BigDecimal amount, String currency, String description, Map<String, String> metadata) {
    try {
      JSONObject linkRequest = new JSONObject();
      linkRequest.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue());
      linkRequest.put("currency", currency);
      linkRequest.put("description", description);
      linkRequest.put("notes", metadata);

      // Create payment link (this is a placeholder - actual implementation may vary)
      Map<String, Object> response = new HashMap<>();
      response.put("link", "https://rzp.io/i/sample-link");
      response.put("id", "link_" + System.currentTimeMillis());

      return response;

    } catch (Exception e) {
      log.error("Failed to create payment link", e);
      throw new RuntimeException("Failed to create payment link", e);
    }
  }

  private void handlePaymentCaptured(JSONObject payload) {
    // Handle payment captured event
    log.info("Payment captured: {}", payload.toString());
  }

  private void handlePaymentFailed(JSONObject payload) {
    // Handle payment failed event
    log.info("Payment failed: {}", payload.toString());
  }

  private void handleOrderPaid(JSONObject payload) {
    // Handle order paid event
    log.info("Order paid: {}", payload.toString());
  }
}
