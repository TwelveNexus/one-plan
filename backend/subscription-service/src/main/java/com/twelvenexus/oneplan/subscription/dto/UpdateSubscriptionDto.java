package com.twelvenexus.oneplan.subscription.dto;

import java.util.UUID;

import com.twelvenexus.oneplan.subscription.enums.BillingCycle;

import lombok.Data;

@Data
public class UpdateSubscriptionDto {
    private UUID planId;
    private BillingCycle billingCycle;
    private Integer quantity;
}
