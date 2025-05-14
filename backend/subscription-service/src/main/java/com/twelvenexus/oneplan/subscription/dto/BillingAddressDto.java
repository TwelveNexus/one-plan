package com.twelvenexus.oneplan.subscription.dto;

import lombok.Data;

@Data
public class BillingAddressDto {
    private String companyName;
    private String contactName;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String gstNumber;
    private String phone;
    private String email;
}
