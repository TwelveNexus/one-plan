package com.twelvenexus.oneplan.subscription.dto;

import com.twelvenexus.oneplan.subscription.enums.InvoiceStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class InvoiceDto {
    private UUID id;
    private String invoiceNumber;
    private InvoiceStatus status;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String currency;
    private List<InvoiceItemDto> items;
    private LocalDateTime paidAt;
}
