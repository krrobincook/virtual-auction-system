package com.auction.virtual_auction_system.exception;

public class UnauthorizedAuctionActionException extends RuntimeException {

    public UnauthorizedAuctionActionException(String message) {
        super(message);
    }
}
