package com.auction.virtual_auction_system.controller.dto;

import com.auction.virtual_auction_system.entity.AuctionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AuctionResponse {

    private Long id;
    private String title;
    private String description;
    private BigDecimal startingPrice;
    private BigDecimal currentPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AuctionStatus status;
    private Long sellerId;
    private String sellerName;
    private LocalDateTime createdAt;

    public AuctionResponse() {
    }

    public AuctionResponse(
            Long id,
            String title,
            String description,
            BigDecimal startingPrice,
            BigDecimal currentPrice,
            LocalDateTime startTime,
            LocalDateTime endTime,
            AuctionStatus status,
            Long sellerId,
            String sellerName,
            LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startingPrice = startingPrice;
        this.currentPrice = currentPrice;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(BigDecimal startingPrice) {
        this.startingPrice = startingPrice;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public AuctionStatus getStatus() {
        return status;
    }

    public void setStatus(AuctionStatus status) {
        this.status = status;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
