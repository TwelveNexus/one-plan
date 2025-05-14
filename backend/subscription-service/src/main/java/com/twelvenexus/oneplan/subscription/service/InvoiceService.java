package com.twelvenexus.oneplan.subscription.service;

import com.twelvenexus.oneplan.subscription.model.Invoice;
import com.twelvenexus.oneplan.subscription.model.Subscription;
import com.twelvenexus.oneplan.subscription.model.Payment;
import com.twelvenexus.oneplan.subscription.enums.InvoiceStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface InvoiceService {

    Invoice generateInvoice(UUID subscriptionId, LocalDate invoiceDate);

    Invoice createInvoiceFromPayment(Payment payment);

    Invoice updateInvoiceStatus(UUID invoiceId, InvoiceStatus status);

    Invoice getInvoice(UUID invoiceId);

    Invoice getInvoiceByNumber(String invoiceNumber);

    List<Invoice> getTenantInvoices(UUID tenantId);

    List<Invoice> getSubscriptionInvoices(UUID subscriptionId);

    List<Invoice> getOverdueInvoices();

    byte[] generateInvoicePDF(UUID invoiceId);

    String getNextInvoiceNumber();

    void sendInvoiceEmail(UUID invoiceId, String recipientEmail);

    void markInvoiceAsPaid(UUID invoiceId, Payment payment);

    void voidInvoice(UUID invoiceId, String reason);

    void processOverdueInvoices();
}
