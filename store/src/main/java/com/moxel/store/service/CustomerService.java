package com.moxel.store.service;

import com.moxel.store.dto.AccountView;
import com.moxel.store.model.Customer;
import com.moxel.store.repository.CustomerRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class CustomerService {

    private static final Pattern MOBILE = Pattern.compile("^[6-9][0-9]{9}$");

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CustomerRepository customerRepository,
                           PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Customer register(String fullName, String email, String phone, String rawPassword) {
        String phoneValue = blankToNull(phone);
        if (phoneValue != null && !MOBILE.matcher(phoneValue).matches()) {
            throw new IllegalArgumentException("Invalid phone number");
        }
        String normalizedEmail = normalizeEmail(email);
        if (customerRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyRegisteredException();
        }
        Customer customer = new Customer(
                fullName.trim(),
                normalizedEmail,
                passwordEncoder.encode(rawPassword),
                phoneValue);
        try {
            return customerRepository.saveAndFlush(customer);
        } catch (DataIntegrityViolationException e) {
            // two sign-ups with the same email at the same moment
            throw new EmailAlreadyRegisteredException();
        }
    }

    @Transactional(readOnly = true)
    public Optional<AccountView> findAccount(String email) {
        return customerRepository.findByEmail(normalizeEmail(email))
                .map(c -> new AccountView(c.getFullName(), c.getEmail(), c.getPhone()));
    }

    public static String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    private static String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}