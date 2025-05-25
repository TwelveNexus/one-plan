package com.twelvenexus.oneplan.subscription.service.impl;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.twelvenexus.oneplan.subscription.enums.InvoiceStatus;
import com.twelvenexus.oneplan.subscription.model.*;
import com.twelvenexus.oneplan.subscription.repository.InvoiceRepository;
import com.twelvenexus.oneplan.subscription.service.InvoiceService;
import com.twelvenexus.oneplan.subscription.service.SubscriptionService;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

  private final InvoiceRepository invoiceRepository;
  private final SubscriptionService subscriptionService;

  @Value("${subscription.invoice.tax-rate}")
  private BigDecimal taxRate;

  @Value("${subscription.invoice.company-name}")
  private String companyName;

  @Value("${subscription.invoice.company-address}")
  private String companyAddress;

  @Value("${subscription.invoice.gst-number}")
  private String gstNumber;

  @Override
  public Invoice generateInvoice(UUID subscriptionId, LocalDate invoiceDate) {
    Subscription subscription = subscriptionService.getSubscription(subscriptionId);

    Invoice invoice = new Invoice();
    invoice.setInvoiceNumber(getNextInvoiceNumber());
    invoice.setTenantId(subscription.getTenantId());
    invoice.setSubscription(subscription);
    invoice.setInvoiceDate(invoiceDate);
    invoice.setDueDate(invoiceDate.plusDays(7));
    invoice.setStatus(InvoiceStatus.DRAFT);
    invoice.setCurrency("INR");

    // Create invoice items
    List<InvoiceItem> items = new ArrayList<>();
    InvoiceItem item = new InvoiceItem();
    item.setInvoice(invoice);
    item.setDescription(subscription.getPlan().getName() + " - " + subscription.getBillingCycle());
    item.setQuantity(subscription.getQuantity());
    item.setUnitPrice(subscription.getAmount());
    item.setAmount(
        subscription.getAmount().multiply(BigDecimal.valueOf(subscription.getQuantity())));
    item.setPeriodStart(subscription.getCurrentPeriodStart());
    item.setPeriodEnd(subscription.getCurrentPeriodEnd());
    items.add(item);

    invoice.setItems(items);

    // Calculate totals
    BigDecimal subtotal =
        items.stream().map(InvoiceItem::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal taxAmount =
        subtotal.multiply(taxRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

    invoice.setSubtotal(subtotal);
    invoice.setTaxRate(taxRate);
    invoice.setTaxAmount(taxAmount);
    invoice.setTotalAmount(subtotal.add(taxAmount));

    log.info(
        "Generated invoice {} for subscription {}", invoice.getInvoiceNumber(), subscriptionId);
    return invoiceRepository.save(invoice);
  }

  @Override
  public Invoice createInvoiceFromPayment(Payment payment) {
    Invoice invoice = generateInvoice(payment.getSubscription().getId(), LocalDate.now());
    invoice.setPayment(payment);
    invoice.setStatus(InvoiceStatus.PAID);
    invoice.setPaidAt(payment.getPaidAt());

    return invoiceRepository.save(invoice);
  }

  @Override
  public Invoice updateInvoiceStatus(UUID invoiceId, InvoiceStatus status) {
    Invoice invoice = getInvoice(invoiceId);
    invoice.setStatus(status);

    if (status == InvoiceStatus.ISSUED) {
      invoice.setInvoiceDate(LocalDate.now());
    }

    return invoiceRepository.save(invoice);
  }

  @Override
  @Transactional(readOnly = true)
  public Invoice getInvoice(UUID invoiceId) {
    return invoiceRepository
        .findById(invoiceId)
        .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public Invoice getInvoiceByNumber(String invoiceNumber) {
    return invoiceRepository
        .findByInvoiceNumber(invoiceNumber)
        .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public List<Invoice> getTenantInvoices(UUID tenantId) {
    return invoiceRepository.findByTenantIdAndStatus(tenantId, InvoiceStatus.PAID);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Invoice> getSubscriptionInvoices(UUID subscriptionId) {
    return invoiceRepository.findBySubscriptionId(subscriptionId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Invoice> getOverdueInvoices() {
    return invoiceRepository.findOverdueInvoices(LocalDate.now());
  }

  @Override
  public byte[] generateInvoicePDF(UUID invoiceId) {
    Invoice invoice = getInvoice(invoiceId);

    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      PdfWriter writer = new PdfWriter(baos);
      PdfDocument pdf = new PdfDocument(writer);
      Document document = new Document(pdf);

      // Add company header
      document.add(
          new Paragraph(companyName).setFontSize(20).setTextAlignment(TextAlignment.CENTER));
      document.add(
          new Paragraph(companyAddress).setFontSize(12).setTextAlignment(TextAlignment.CENTER));
      document.add(
          new Paragraph("GSTIN: " + gstNumber)
              .setFontSize(10)
              .setTextAlignment(TextAlignment.CENTER));

      document.add(new Paragraph("\n"));

      // Add invoice header
      document.add(new Paragraph("INVOICE").setFontSize(16).setTextAlignment(TextAlignment.CENTER));

      // Add invoice details
      Table detailsTable = new Table(2);
      detailsTable.addCell("Invoice Number:");
      detailsTable.addCell(invoice.getInvoiceNumber());
      detailsTable.addCell("Invoice Date:");
      detailsTable.addCell(invoice.getInvoiceDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
      detailsTable.addCell("Due Date:");
      detailsTable.addCell(invoice.getDueDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
      document.add(detailsTable);

      document.add(new Paragraph("\n"));

      // Add billing address
      if (invoice.getBillingAddress() != null) {
        BillingAddress addr = invoice.getBillingAddress();
        document.add(new Paragraph("Bill To:").setBold());
        document.add(new Paragraph(addr.getCompanyName()));
        document.add(new Paragraph(addr.getAddressLine1()));
        if (addr.getAddressLine2() != null) {
          document.add(new Paragraph(addr.getAddressLine2()));
        }
        document.add(
            new Paragraph(addr.getCity() + ", " + addr.getState() + " " + addr.getPostalCode()));
        if (addr.getGstNumber() != null) {
          document.add(new Paragraph("GSTIN: " + addr.getGstNumber()));
        }
      }

      document.add(new Paragraph("\n"));

      // Add items table
      Table itemsTable = new Table(5);
      itemsTable.addCell("Description");
      itemsTable.addCell("Quantity");
      itemsTable.addCell("Unit Price");
      itemsTable.addCell("Amount");
      itemsTable.addCell("Total");

      for (InvoiceItem item : invoice.getItems()) {
        itemsTable.addCell(item.getDescription());
        itemsTable.addCell(item.getQuantity().toString());
        itemsTable.addCell("₹" + item.getUnitPrice().toString());
        itemsTable.addCell("₹" + item.getAmount().toString());
        itemsTable.addCell("₹" + item.getAmount().toString());
      }

      document.add(itemsTable);

      document.add(new Paragraph("\n"));

      // Add totals
      Table totalsTable = new Table(2);
      totalsTable.addCell("Subtotal:");
      totalsTable.addCell("₹" + invoice.getSubtotal().toString());
      totalsTable.addCell("Tax (" + invoice.getTaxRate() + "%):");
      totalsTable.addCell("₹" + invoice.getTaxAmount().toString());
      totalsTable.addCell("Total:");
      totalsTable.addCell("₹" + invoice.getTotalAmount().toString());
      document.add(totalsTable);

      document.close();

      byte[] pdfBytes = baos.toByteArray();
      log.info("Generated PDF for invoice: {}", invoice.getInvoiceNumber());
      return pdfBytes;

    } catch (Exception e) {
      log.error("Failed to generate PDF for invoice: {}", invoiceId, e);
      throw new RuntimeException("Failed to generate invoice PDF", e);
    }
  }

  @Override
  public String getNextInvoiceNumber() {
    String prefix = "INV-" + LocalDate.now().getYear() + "-";
    String lastNumber = invoiceRepository.findLastInvoiceNumber(prefix);

    if (lastNumber == null) {
      return prefix + "00001";
    }

    String[] parts = lastNumber.split("-");
    int nextNumber = Integer.parseInt(parts[parts.length - 1]) + 1;
    return prefix + String.format("%05d", nextNumber);
  }

  @Override
  public void sendInvoiceEmail(UUID invoiceId, String recipientEmail) {
    Invoice invoice = getInvoice(invoiceId);
    byte[] pdfData = generateInvoicePDF(invoiceId);

    // Email sending logic would go here
    log.info("Sending invoice {} to {}", invoice.getInvoiceNumber(), recipientEmail);
  }

  @Override
  public void markInvoiceAsPaid(UUID invoiceId, Payment payment) {
    Invoice invoice = getInvoice(invoiceId);
    invoice.setStatus(InvoiceStatus.PAID);
    invoice.setPayment(payment);
    invoice.setPaidAt(payment.getPaidAt());
    invoiceRepository.save(invoice);

    log.info("Marked invoice {} as paid", invoice.getInvoiceNumber());
  }

  @Override
  public void voidInvoice(UUID invoiceId, String reason) {
    Invoice invoice = getInvoice(invoiceId);
    invoice.setStatus(InvoiceStatus.VOID);
    invoice.setNotes("Voided: " + reason);
    invoiceRepository.save(invoice);

    log.info("Voided invoice {}: {}", invoice.getInvoiceNumber(), reason);
  }

  @Override
  public void processOverdueInvoices() {
    List<Invoice> overdueInvoices = getOverdueInvoices();

    for (Invoice invoice : overdueInvoices) {
      invoice.setStatus(InvoiceStatus.OVERDUE);
      invoiceRepository.save(invoice);

      // Send overdue notification
      log.info("Invoice {} is overdue", invoice.getInvoiceNumber());
    }
  }
}
