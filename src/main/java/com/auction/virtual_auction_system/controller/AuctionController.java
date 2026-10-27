package com.auction.virtual_auction_system.controller;

import com.auction.virtual_auction_system.controller.dto.AuctionRequest;
import com.auction.virtual_auction_system.controller.dto.AuctionResponse;
import com.auction.virtual_auction_system.service.AuctionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionService auctionService;

    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @PostMapping
    public ResponseEntity<AuctionResponse> createAuction(
            @Valid @RequestBody AuctionRequest request,
            Authentication authentication) {

        AuctionResponse response = auctionService.createAuction(
                request,
                authentication.getName());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<AuctionResponse>> getAllAuctions() {
        return ResponseEntity.ok(auctionService.getAllAuctions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuctionResponse> getAuctionById(
            @PathVariable Long id) {
        return ResponseEntity.ok(auctionService.getAuctionById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuctionResponse> updateAuction(
            @PathVariable Long id,
            @Valid @RequestBody AuctionRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(auctionService.updateAuction(
                id,
                request,
                authentication.getName()));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelAuction(
            @PathVariable Long id,
            Authentication authentication) {

        auctionService.cancelAuction(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
