package com.auction.virtual_auction_system.service;

import com.auction.virtual_auction_system.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService {
    User register(User user);

    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    User findByEmail(String email);
}