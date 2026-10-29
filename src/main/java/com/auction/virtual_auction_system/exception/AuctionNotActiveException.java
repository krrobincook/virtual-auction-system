package com.auction.virtual_auction_system.exception;

public class AuctionNotActiveException extends RuntimeException {

    public AuctionNotActiveException(String message) {
        super(message);
    }
}
