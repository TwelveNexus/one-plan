package com.twelvenexus.oneplan.subscription.service.gateway;

import com.twelvenexus.oneplan.subscription.model.Payment;
import com.twelvenexus.oneplan.subscription.model.PaymentMethod;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentGatewayService {

    Payment createOrder(String orderId, BigDecimal amount, String currency,
                       Map<String, String> metadata);

    Payment verifyPayment(String orderId, String paymentId, String signature);

    Payment capturePayment(String paymentId, BigDecimal amount);

    Payment refundPayment(String paymentId, BigDecimal amount, String reason);

    PaymentMethod tokenizeCard(Map<String, String> cardDetails);

    PaymentMethod saveUpiId(String upiId);

    void handleWebhook(String webhookBody, String signature);

    Map<String, Object> createPaymentLink(BigDecimal amount, String currency,
                                         String description, Map<String, String> metadata);
}
