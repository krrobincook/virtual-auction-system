package com.auction.virtual_auction_system.controller;

import com.auction.virtual_auction_system.controller.dto.BidRequest;
import com.auction.virtual_auction_system.controller.dto.BidResponse;
import com.auction.virtual_auction_system.service.BidService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auctions/{auctionId}")
public class BidController {

    private final BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    @PostMapping("/bids")
    public ResponseEntity<BidResponse> placeBid(
            @PathVariable Long auctionId,
            @Valid @RequestBody BidRequest request,
            Authentication authentication) {

        BidResponse response = bidService.placeBid(
                auctionId,
                request,
                authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/bids")
    public ResponseEntity<List<BidResponse>> getBiddingHistory(
            @PathVariable Long auctionId) {
        return ResponseEntity.ok(
                bidService.getBiddingHistory(auctionId));
    }

    @GetMapping({"/highest-bid", "/bids/highest-bid"})
    public ResponseEntity<BidResponse> getHighestBid(
            @PathVariable Long auctionId) {
        return bidService.getHighestBid(auctionId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}
