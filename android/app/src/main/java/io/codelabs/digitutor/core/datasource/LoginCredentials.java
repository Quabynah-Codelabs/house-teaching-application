package io.codelabs.digitutor.core.datasource;

import android.util.Patterns;

/**
 * Login credentials class
 */
public class LoginCredentials {
    private String email, password;

    public LoginCredentials(String email, String password) {
        this.email = email;
        this.password = password;
    }

    String getEmail() {
        return email;
    }

    void setEmail(String email) {
        this.email = email;
    }

    String getPassword() {
        return password;
    }

    void setPassword(String password) {
        this.password = password;
    }

    boolean validate() {
        if (email == null || password == null) return false;
        return Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length() > 5;
    }
}
