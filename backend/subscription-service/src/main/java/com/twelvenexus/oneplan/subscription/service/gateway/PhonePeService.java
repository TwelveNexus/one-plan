package com.twelvenexus.oneplan.subscription.service.gateway;

import com.twelvenexus.oneplan.subscription.enums.PaymentGateway;
import com.twelvenexus.oneplan.subscription.enums.PaymentStatus;
import com.twelvenexus.oneplan.subscription.model.Payment;
import com.twelvenexus.oneplan.subscription.model.PaymentMethod;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class PhonePeService implements PaymentGatewayService {

  private final String merchantId;
  private final String saltKey;
  private final String saltIndex;
  private final String apiUrl;
  private final RestTemplate restTemplate;

  public PhonePeService(
      @Value("${payment.phonepe.merchant-id}") String merchantId,
      @Value("${payment.phonepe.salt-key}") String saltKey,
      @Value("${payment.phonepe.salt-index}") String saltIndex,
      @Value("${payment.phonepe.environment}") String environment) {
    this.merchantId = merchantId;
    this.saltKey = saltKey;
    this.saltIndex = saltIndex;
    this.apiUrl =
        environment.equals("production")
            ? "https://api.phonepe.com/apis/hermes"
            : "https://api-preprod.phonepe.com/apis/hermes";
    this.restTemplate = new RestTemplate();
  }

  @Override
  public Payment createOrder(
      String orderId, BigDecimal amount, String currency, Map<String, String> metadata) {
    try {
      String transactionId = "TXN_" + UUID.randomUUID().toString();

      JSONObject request = new JSONObject();
      request.put("merchantId", merchantId);
      request.put("merchantTransactionId", transactionId);
      request.put("merchantUserId", metadata.get("userId"));
      request.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue());
      request.put("redirectUrl", metadata.get("redirectUrl"));
      request.put("redirectMode", "POST");
      request.put("callbackUrl", metadata.get("callbackUrl"));
      request.put("mobileNumber", metadata.get("mobileNumber"));

      JSONObject paymentInstrument = new JSONObject();
      paymentInstrument.put("type", "PAY_PAGE");
      request.put("paymentInstrument", paymentInstrument);

      String base64Body = Base64.getEncoder().encodeToString(request.toString().getBytes());
      String checksum = calculateChecksum(base64Body + "/pg/v1/pay" + saltKey) + "###" + saltIndex;

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("X-VERIFY", checksum);

      JSONObject payload = new JSONObject();
      payload.put("request", base64Body);

      HttpEntity<String> entity = new HttpEntity<>(payload.toString(), headers);

      ResponseEntity<String> response =
          restTemplate.exchange(apiUrl + "/pg/v1/pay", HttpMethod.POST, entity, String.class);

      JSONObject responseBody = new JSONObject(response.getBody());

      Payment payment = new Payment();
      payment.setGateway(PaymentGateway.PHONEPE);
      payment.setAmount(amount);
      payment.setCurrency(currency);
      payment.setGatewayOrderId(transactionId);
      payment.setStatus(PaymentStatus.PENDING);
      payment.setMetadata(metadata);

      log.info("Created PhonePe order: {}", transactionId);
      return payment;

    } catch (Exception e) {
      log.error("Failed to create PhonePe order", e);
      throw new RuntimeException("Failed to create payment order", e);
    }
  }

  @Override
  public Payment verifyPayment(String orderId, String paymentId, String signature) {
    try {
      String checksum = calculateChecksum(merchantId + "/" + orderId + saltKey) + "###" + saltIndex;

      HttpHeaders headers = new HttpHeaders();
      headers.set("X-VERIFY", checksum);
      headers.set("X-MERCHANT-ID", merchantId);

      HttpEntity<String> entity = new HttpEntity<>(headers);

      ResponseEntity<String> response =
          restTemplate.exchange(
              apiUrl + "/pg/v1/status/" + merchantId + "/" + orderId,
              HttpMethod.GET,
              entity,
              String.class);

      JSONObject responseBody = new JSONObject(response.getBody());
      String code = responseBody.getString("code");

      Payment payment = new Payment();
      payment.setGatewayOrderId(orderId);
      payment.setGatewayPaymentId(responseBody.getJSONObject("data").getString("transactionId"));

      if ("PAYMENT_SUCCESS".equals(code)) {
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaidAt(java.time.LocalDateTime.now());
        log.info("Payment verified successfully: {}", orderId);
      } else {
        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(code);
      }

      return payment;

    } catch (Exception e) {
      log.error("Failed to verify payment", e);
      throw new RuntimeException("Failed to verify payment", e);
    }
  }

  @Override
  public Payment capturePayment(String paymentId, BigDecimal amount) {
    // PhonePe captures payment automatically
    Payment payment = new Payment();
    payment.setGatewayPaymentId(paymentId);
    payment.setStatus(PaymentStatus.COMPLETED);
    return payment;
  }

  @Override
  public Payment refundPayment(String paymentId, BigDecimal amount, String reason) {
    try {
      String refundId = "REFUND_" + UUID.randomUUID().toString();

      JSONObject request = new JSONObject();
      request.put("merchantId", merchantId);
      request.put("merchantTransactionId", paymentId);
      request.put("originalTransactionId", paymentId);
      request.put("merchantRefundId", refundId);
      request.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue());

      String base64Body = Base64.getEncoder().encodeToString(request.toString().getBytes());
      String checksum =
          calculateChecksum(base64Body + "/pg/v1/refund" + saltKey) + "###" + saltIndex;

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("X-VERIFY", checksum);

      JSONObject payload = new JSONObject();
      payload.put("request", base64Body);

      HttpEntity<String> entity = new HttpEntity<>(payload.toString(), headers);

      ResponseEntity<String> response =
          restTemplate.exchange(apiUrl + "/pg/v1/refund", HttpMethod.POST, entity, String.class);

      Payment payment = new Payment();
      payment.setGatewayPaymentId(paymentId);
      payment.setRefundedAmount(amount);
      payment.setRefundedAt(java.time.LocalDateTime.now());
      payment.setRefundReason(reason);
      payment.setStatus(PaymentStatus.REFUNDED);

      log.info("Payment refunded: {}", refundId);
      return payment;

    } catch (Exception e) {
      log.error("Failed to refund payment", e);
      throw new RuntimeException("Failed to refund payment", e);
    }
  }

  @Override
  public PaymentMethod tokenizeCard(Map<String, String> cardDetails) {
    // PhonePe doesn't support direct card tokenization in the same way
    return new PaymentMethod();
  }

  @Override
  public PaymentMethod saveUpiId(String upiId) {
    PaymentMethod paymentMethod = new PaymentMethod();
    paymentMethod.setGateway(PaymentGateway.PHONEPE);
    paymentMethod.setType("upi");
    paymentMethod.setUpiId(upiId);
    paymentMethod.setActive(true);
    return paymentMethod;
  }

  @Override
  public void handleWebhook(String webhookBody, String signature) {
    try {
      JSONObject payload = new JSONObject(webhookBody);
      String responseSignature = payload.getString("signature");

      // Verify webhook signature
      String data = payload.getString("data");
      String calculatedSignature = calculateChecksum(data + saltKey);

      if (!responseSignature.startsWith(calculatedSignature)) {
        throw new RuntimeException("Invalid webhook signature");
      }

      // Decode the data
      String decodedData = new String(Base64.getDecoder().decode(data));
      JSONObject eventData = new JSONObject(decodedData);

      String eventType = eventData.getString("code");

      switch (eventType) {
        case "PAYMENT_SUCCESS":
          handlePaymentSuccess(eventData);
          break;
        case "PAYMENT_FAILED":
          handlePaymentFailed(eventData);
          break;
        default:
          log.warn("Unhandled webhook event: {}", eventType);
      }

    } catch (Exception e) {
      log.error("Failed to process webhook", e);
      throw new RuntimeException("Failed to process webhook", e);
    }
  }

  @Override
  public Map<String, Object> createPaymentLink(
      BigDecimal amount, String currency, String description, Map<String, String> metadata) {
    // Create payment link logic for PhonePe
    Map<String, Object> response = new HashMap<>();
    response.put("link", "https://phonepe.com/pay/sample-link");
    response.put("id", "link_" + System.currentTimeMillis());
    return response;
  }

  private String calculateChecksum(String data) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] hashBytes = md.digest(data.getBytes(StandardCharsets.UTF_8));
      return bytesToHex(hashBytes);
    } catch (Exception e) {
      throw new RuntimeException("Failed to calculate checksum", e);
    }
  }

  private String bytesToHex(byte[] bytes) {
    StringBuilder result = new StringBuilder();
    for (byte b : bytes) {
      result.append(String.format("%02x", b));
    }
    return result.toString();
  }

  private void handlePaymentSuccess(JSONObject eventData) {
    log.info("Payment successful: {}", eventData.toString());
  }

  private void handlePaymentFailed(JSONObject eventData) {
    log.info("Payment failed: {}", eventData.toString());
  }
}
