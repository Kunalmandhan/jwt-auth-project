package com.example.jwtauth.dto;

public class RegisterRequest {
    private String username;
    private String password;
    private String email;

    public RegisterRequest() {}
    public RegisterRequest(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername()             { return username; }
    public void setUsername(String u)       { this.username = u; }
    public String getPassword()             { return password; }
    public void setPassword(String p)       { this.password = p; }
    public String getEmail()                { return email; }
    public void setEmail(String e)          { this.email = e; }
}
