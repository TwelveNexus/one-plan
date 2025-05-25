package com.twelvenexus.oneplan.subscription.controller;

import com.twelvenexus.oneplan.subscription.dto.InvoiceDto;
import com.twelvenexus.oneplan.subscription.dto.InvoiceItemDto;
import com.twelvenexus.oneplan.subscription.enums.InvoiceStatus;
import com.twelvenexus.oneplan.subscription.model.Invoice;
import com.twelvenexus.oneplan.subscription.model.InvoiceItem;
import com.twelvenexus.oneplan.subscription.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
@Tag(name = "Invoices", description = "Invoice management")
public class InvoiceController {

  private final InvoiceService invoiceService;

  @PostMapping("/generate")
  @Operation(summary = "Generate invoice for subscription")
  public ResponseEntity<InvoiceDto> generateInvoice(
      @RequestParam UUID subscriptionId, @RequestParam(required = false) String invoiceDate) {
    LocalDate date = invoiceDate != null ? LocalDate.parse(invoiceDate) : LocalDate.now();

    Invoice invoice = invoiceService.generateInvoice(subscriptionId, date);
    return ResponseEntity.ok(toDto(invoice));
  }

  @GetMapping("/{invoiceId}")
  @Operation(summary = "Get invoice details")
  public ResponseEntity<InvoiceDto> getInvoice(@PathVariable UUID invoiceId) {
    Invoice invoice = invoiceService.getInvoice(invoiceId);
    return ResponseEntity.ok(toDto(invoice));
  }

  @GetMapping("/number/{invoiceNumber}")
  @Operation(summary = "Get invoice by number")
  public ResponseEntity<InvoiceDto> getInvoiceByNumber(@PathVariable String invoiceNumber) {
    Invoice invoice = invoiceService.getInvoiceByNumber(invoiceNumber);
    return ResponseEntity.ok(toDto(invoice));
  }

  @GetMapping
  @Operation(summary = "Get tenant invoices")
  public ResponseEntity<List<InvoiceDto>> getTenantInvoices(
      @RequestHeader("X-Tenant-Id") UUID tenantId) {
    List<Invoice> invoices = invoiceService.getTenantInvoices(tenantId);
    return ResponseEntity.ok(invoices.stream().map(this::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/subscription/{subscriptionId}")
  @Operation(summary = "Get subscription invoices")
  public ResponseEntity<List<InvoiceDto>> getSubscriptionInvoices(
      @PathVariable UUID subscriptionId) {
    List<Invoice> invoices = invoiceService.getSubscriptionInvoices(subscriptionId);
    return ResponseEntity.ok(invoices.stream().map(this::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/{invoiceId}/pdf")
  @Operation(summary = "Download invoice PDF")
  public ResponseEntity<byte[]> downloadInvoicePDF(@PathVariable UUID invoiceId) {
    byte[] pdfData = invoiceService.generateInvoicePDF(invoiceId);
    Invoice invoice = invoiceService.getInvoice(invoiceId);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentDispositionFormData("attachment", invoice.getInvoiceNumber() + ".pdf");

    return ResponseEntity.ok().headers(headers).body(pdfData);
  }

  @PostMapping("/{invoiceId}/send")
  @Operation(summary = "Send invoice email")
  public ResponseEntity<Void> sendInvoiceEmail(
      @PathVariable UUID invoiceId, @RequestParam String email) {
    invoiceService.sendInvoiceEmail(invoiceId, email);
    return ResponseEntity.ok().build();
  }

  @PutMapping("/{invoiceId}/status")
  @Operation(summary = "Update invoice status")
  public ResponseEntity<InvoiceDto> updateInvoiceStatus(
      @PathVariable UUID invoiceId, @RequestParam InvoiceStatus status) {
    Invoice invoice = invoiceService.updateInvoiceStatus(invoiceId, status);
    return ResponseEntity.ok(toDto(invoice));
  }

  @PostMapping("/{invoiceId}/void")
  @Operation(summary = "Void an invoice")
  public ResponseEntity<Void> voidInvoice(
      @PathVariable UUID invoiceId, @RequestParam String reason) {
    invoiceService.voidInvoice(invoiceId, reason);
    return ResponseEntity.ok().build();
  }

  private InvoiceDto toDto(Invoice invoice) {
    InvoiceDto dto = new InvoiceDto();
    dto.setId(invoice.getId());
    dto.setInvoiceNumber(invoice.getInvoiceNumber());
    dto.setStatus(invoice.getStatus());
    dto.setInvoiceDate(invoice.getInvoiceDate());
    dto.setDueDate(invoice.getDueDate());
    dto.setSubtotal(invoice.getSubtotal());
    dto.setTaxAmount(invoice.getTaxAmount());
    dto.setTotalAmount(invoice.getTotalAmount());
    dto.setCurrency(invoice.getCurrency());
    dto.setPaidAt(invoice.getPaidAt());

    if (invoice.getItems() != null) {
      dto.setItems(invoice.getItems().stream().map(this::toItemDto).collect(Collectors.toList()));
    }

    return dto;
  }

  private InvoiceItemDto toItemDto(InvoiceItem item) {
    InvoiceItemDto dto = new InvoiceItemDto();
    dto.setDescription(item.getDescription());
    dto.setQuantity(item.getQuantity());
    dto.setUnitPrice(item.getUnitPrice());
    dto.setAmount(item.getAmount());
    dto.setPeriodStart(item.getPeriodStart());
    dto.setPeriodEnd(item.getPeriodEnd());
    return dto;
  }
}
