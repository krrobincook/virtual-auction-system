package com.auction.virtual_auction_system.service.Impl;

import com.auction.virtual_auction_system.controller.dto.AuctionRequest;
import com.auction.virtual_auction_system.controller.dto.AuctionResponse;
import com.auction.virtual_auction_system.entity.Auction;
import com.auction.virtual_auction_system.entity.AuctionStatus;
import com.auction.virtual_auction_system.entity.User;
import com.auction.virtual_auction_system.exception.ResourceNotFoundException;
import com.auction.virtual_auction_system.repository.AuctionRepository;
import com.auction.virtual_auction_system.service.AuctionService;
import com.auction.virtual_auction_system.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuctionServiceImpl implements AuctionService {

    private final AuctionRepository auctionRepository;
    private final UserService userService;

    public AuctionServiceImpl(
            AuctionRepository auctionRepository,
            UserService userService) {
        this.auctionRepository = auctionRepository;
        this.userService = userService;
    }

    @Override
    @Transactional
    public AuctionResponse createAuction(
            AuctionRequest request,
            String sellerEmail) {

        validateAuctionTimes(request);

        User seller = userService.findByEmail(sellerEmail);
        Auction auction = new Auction();
        auction.setTitle(request.getTitle());
        auction.setDescription(request.getDescription());
        auction.setStartingPrice(request.getStartingPrice());
        auction.setCurrentPrice(request.getStartingPrice());
        auction.setStartTime(request.getStartTime());
        auction.setEndTime(request.getEndTime());
        auction.setStatus(determineInitialStatus(
                request.getStartTime(),
                request.getEndTime()));
        auction.setSeller(seller);

        return mapToResponse(auctionRepository.save(auction));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuctionResponse> getAllAuctions() {
        return auctionRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AuctionResponse getAuctionById(Long id) {
        return mapToResponse(findAuction(id));
    }

    @Override
    @Transactional
    public AuctionResponse updateAuction(
            Long id,
            AuctionRequest request,
            String sellerEmail) {

        validateAuctionTimes(request);

        Auction auction = findAuction(id);
        ensureSeller(auction, sellerEmail);

        if (auction.getStatus() != AuctionStatus.PENDING) {
            throw new IllegalStateException(
                    "Only upcoming auctions can be updated");
        }

        auction.setTitle(request.getTitle());
        auction.setDescription(request.getDescription());
        auction.setStartingPrice(request.getStartingPrice());
        auction.setCurrentPrice(request.getStartingPrice());
        auction.setStartTime(request.getStartTime());
        auction.setEndTime(request.getEndTime());
        auction.setStatus(determineInitialStatus(
                request.getStartTime(),
                request.getEndTime()));

        return mapToResponse(auctionRepository.save(auction));
    }

    @Override
    @Transactional
    public void cancelAuction(Long id, String sellerEmail) {
        Auction auction = findAuction(id);
        ensureSeller(auction, sellerEmail);

        if (auction.getStatus() == AuctionStatus.ENDED
                || auction.getStatus() == AuctionStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Only upcoming or active auctions can be cancelled");
        }

        auction.setStatus(AuctionStatus.CANCELLED);
        auctionRepository.save(auction);
    }

    private Auction findAuction(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Auction id is required");
        }

        return auctionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Auction not found with id: " + id));
    }

    private void ensureSeller(Auction auction, String sellerEmail) {
        if (sellerEmail == null
                || auction.getSeller() == null
                || !sellerEmail.equalsIgnoreCase(
                auction.getSeller().getEmail())) {
            throw new IllegalStateException(
                    "You are not allowed to modify this auction");
        }
    }

    private void validateAuctionTimes(AuctionRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Auction request is required");
        }

        if (request.getStartTime() == null
                || request.getEndTime() == null) {
            throw new IllegalArgumentException(
                    "Start time and end time are required");
        }

        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new IllegalArgumentException(
                    "End time must be after start time");
        }
    }

    private AuctionStatus determineInitialStatus(
            LocalDateTime startTime,
            LocalDateTime endTime) {
        LocalDateTime now = LocalDateTime.now();

        if (!now.isBefore(startTime)) {
            return now.isBefore(endTime)
                    ? AuctionStatus.ACTIVE
                    : AuctionStatus.ENDED;
        }

        return AuctionStatus.PENDING;
    }

    private AuctionResponse mapToResponse(Auction auction) {
        AuctionResponse response = new AuctionResponse();
        response.setId(auction.getId());
        response.setTitle(auction.getTitle());
        response.setDescription(auction.getDescription());
        response.setStartingPrice(auction.getStartingPrice());
        response.setCurrentPrice(auction.getCurrentPrice());
        response.setStartTime(auction.getStartTime());
        response.setEndTime(auction.getEndTime());
        response.setStatus(auction.getStatus());
        response.setSellerId(auction.getSeller().getId());
        response.setSellerName(auction.getSeller().getName());
        response.setCreatedAt(auction.getCreatedAt());
        return response;
    }
}
