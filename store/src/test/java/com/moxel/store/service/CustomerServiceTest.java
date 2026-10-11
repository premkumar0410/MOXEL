package com.moxel.store.service;

import com.moxel.store.model.Customer;
import com.moxel.store.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @Test
    void rejectsInvalidPhone() {
        assertThrows(IllegalArgumentException.class,
                () -> service.register("Priya", "priya@example.com",
                        "904223850055", "correct horse battery"));

        verify(customerRepository, never()).saveAndFlush(any());
    }

    @Mock
    CustomerRepository customerRepository;

    PasswordEncoder encoder = new BCryptPasswordEncoder(4);
    CustomerService service;

    @BeforeEach
    void setUp() {
        service = new CustomerService(customerRepository, encoder);
    }

    @Test
    void registersWithNormalizedEmailAndHashedPassword() {
        when(customerRepository.existsByEmail("priya@example.com")).thenReturn(false);
        when(customerRepository.saveAndFlush(any(Customer.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Customer saved = service.register("  Priya  ", "  Priya@Example.COM ",
                " ", "correct horse battery");

        assertEquals("priya@example.com", saved.getEmail());
        assertEquals("Priya", saved.getFullName());
        assertNull(saved.getPhone());
        assertNotEquals("correct horse battery", saved.getPasswordHash());
        assertTrue(encoder.matches("correct horse battery", saved.getPasswordHash()));
    }

    @Test
    void rejectsDuplicateEmail() {
        when(customerRepository.existsByEmail("priya@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyRegisteredException.class,
                () -> service.register("Priya", "PRIYA@example.com", null, "correct horse battery"));
        verify(customerRepository, never()).saveAndFlush(any());
    }

    @Test
    void translatesDatabaseUniqueViolationIntoDuplicateEmail() {
        when(customerRepository.existsByEmail("priya@example.com")).thenReturn(false);
        when(customerRepository.saveAndFlush(any(Customer.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate"));

        assertThrows(EmailAlreadyRegisteredException.class,
                () -> service.register("Priya", "priya@example.com", null, "correct horse battery"));
    }
}