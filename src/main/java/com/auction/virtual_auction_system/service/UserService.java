package com.auction.virtual_auction_system.service;

import com.auction.virtual_auction_system.entity.User;

public interface UserService {
    User register(User user);
    User findByEmail(String email);
}