package com.twelvenexus.oneplan.subscription.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class InvoiceItemDto {
    private String description;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal amount;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
}
