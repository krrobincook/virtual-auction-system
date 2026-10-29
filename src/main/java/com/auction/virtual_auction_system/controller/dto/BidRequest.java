package com.auction.virtual_auction_system.controller.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class BidRequest {

    @NotNull(message = "Bid amount is required")
    @DecimalMin(value = "0.00", inclusive = false,
            message = "Bid amount must be greater than zero")
    @Digits(integer = 13, fraction = 2,
            message = "Bid amount must have at most 2 decimal places")
    private BigDecimal amount;

    public BidRequest() {
    }

    public BidRequest(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
