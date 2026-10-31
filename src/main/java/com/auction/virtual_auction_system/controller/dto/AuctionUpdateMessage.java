package com.auction.virtual_auction_system.controller.dto;

import com.auction.virtual_auction_system.entity.AuctionEventType;
import com.auction.virtual_auction_system.entity.AuctionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Payload sent to subscribers of one auction topic. */
public class AuctionUpdateMessage {

    private AuctionEventType eventType;
    private Long auctionId;
    private BigDecimal currentPrice;
    private Long bidId;
    private Long bidderId;
    private String bidderName;
    private BigDecimal bidAmount;
    private LocalDateTime bidTime;
    private AuctionStatus auctionStatus;
    private LocalDateTime timestamp;

    public AuctionUpdateMessage() {
    }

    public AuctionUpdateMessage(
            AuctionEventType eventType,
            Long auctionId,
            BigDecimal currentPrice,
            Long bidId,
            Long bidderId,
            String bidderName,
            BigDecimal bidAmount,
            LocalDateTime bidTime,
            AuctionStatus auctionStatus,
            LocalDateTime timestamp) {
        this.eventType = eventType;
        this.auctionId = auctionId;
        this.currentPrice = currentPrice;
        this.bidId = bidId;
        this.bidderId = bidderId;
        this.bidderName = bidderName;
        this.bidAmount = bidAmount;
        this.bidTime = bidTime;
        this.auctionStatus = auctionStatus;
        this.timestamp = timestamp;
    }

    public AuctionEventType getEventType() {
        return eventType;
    }

    public void setEventType(AuctionEventType eventType) {
        this.eventType = eventType;
    }

    public Long getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Long auctionId) {
        this.auctionId = auctionId;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Long getBidId() {
        return bidId;
    }

    public void setBidId(Long bidId) {
        this.bidId = bidId;
    }

    public Long getBidderId() {
        return bidderId;
    }

    public void setBidderId(Long bidderId) {
        this.bidderId = bidderId;
    }

    public String getBidderName() {
        return bidderName;
    }

    public void setBidderName(String bidderName) {
        this.bidderName = bidderName;
    }

    public BigDecimal getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(BigDecimal bidAmount) {
        this.bidAmount = bidAmount;
    }

    public LocalDateTime getBidTime() {
        return bidTime;
    }

    public void setBidTime(LocalDateTime bidTime) {
        this.bidTime = bidTime;
    }

    public AuctionStatus getAuctionStatus() {
        return auctionStatus;
    }

    public void setAuctionStatus(AuctionStatus auctionStatus) {
        this.auctionStatus = auctionStatus;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
