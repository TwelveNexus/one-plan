package com.twelvenexus.oneplan.subscription.model;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class BillingAddress {

    private String companyName;
    private String contactName;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country = "India";
    private String gstNumber;
    private String panNumber;
    private String phone;
    private String email;
}
