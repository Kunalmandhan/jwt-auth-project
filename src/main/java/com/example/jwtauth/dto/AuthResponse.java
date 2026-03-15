package com.example.jwtauth.dto;

public class AuthResponse {
    private String token;
    private String tokenType;
    private String username;
    private String role;
    private long expiresIn;

    public AuthResponse() {}
    public AuthResponse(String token, String tokenType, String username, String role, long expiresIn) {
        this.token = token;
        this.tokenType = tokenType;
        this.username = username;
        this.role = role;
        this.expiresIn = expiresIn;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String token, tokenType, username, role;
        private long expiresIn;
        public Builder token(String t)      { this.token = t; return this; }
        public Builder tokenType(String t)  { this.tokenType = t; return this; }
        public Builder username(String u)   { this.username = u; return this; }
        public Builder role(String r)       { this.role = r; return this; }
        public Builder expiresIn(long e)    { this.expiresIn = e; return this; }
        public AuthResponse build()         { return new AuthResponse(token, tokenType, username, role, expiresIn); }
    }

    public String getToken()                { return token; }
    public String getTokenType()            { return tokenType; }
    public String getUsername()             { return username; }
    public String getRole()                 { return role; }
    public long getExpiresIn()              { return expiresIn; }
}
