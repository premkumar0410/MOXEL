package com.moxel.store.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegistrationForm {

    @NotBlank(message = "Please enter your name")
    @Size(max = 100, message = "Name is too long")
    private String fullName;

    @NotBlank(message = "Please enter your email")
    @Email(message = "Please enter a valid email")
    @Size(max = 254, message = "Email is too long")
    private String email;

    @Pattern(regexp = "^$|^[6-9][0-9]{9}$",
            message = "Enter a 10-digit mobile number (without +91) or leave it blank")
    private String phone;

    @NotBlank(message = "Please choose a password")
    @Size(min = 10, max = 64, message = "Password must be 10 to 64 characters")
    private String password;

    @NotBlank(message = "Please repeat your password")
    private String confirmPassword;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}