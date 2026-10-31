package com.auction.virtual_auction_system.event;

import com.auction.virtual_auction_system.controller.dto.AuctionUpdateMessage;

public record AuctionUpdateEvent(AuctionUpdateMessage message) {
}
