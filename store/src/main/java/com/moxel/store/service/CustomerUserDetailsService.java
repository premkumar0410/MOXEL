package com.moxel.store.service;

import com.moxel.store.model.Customer;
import com.moxel.store.repository.CustomerRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomerUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    public CustomerUserDetailsService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerRepository
                .findByEmail(CustomerService.normalizeEmail(username))
                .orElseThrow(() -> new UsernameNotFoundException("Unknown user"));

        return User.withUsername(customer.getEmail())
                .password(customer.getPasswordHash())
                .authorities("ROLE_CUSTOMER")
                .disabled(!customer.isEnabled())
                .build();
    }
}