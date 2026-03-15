package com.example.jwtauth.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "token_blacklist")
public class TokenBlacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 512)
    private String token;

    @Column(nullable = false)
    private LocalDateTime blacklistedAt;

    public TokenBlacklist() {}

    public TokenBlacklist(Long id, String token, LocalDateTime blacklistedAt) {
        this.id = id;
        this.token = token;
        this.blacklistedAt = blacklistedAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String token;
        private LocalDateTime blacklistedAt;
        public Builder id(Long id)                          { this.id = id; return this; }
        public Builder token(String t)                      { this.token = t; return this; }
        public Builder blacklistedAt(LocalDateTime d)       { this.blacklistedAt = d; return this; }
        public TokenBlacklist build()                       { return new TokenBlacklist(id, token, blacklistedAt); }
    }

    public Long getId()                                     { return id; }
    public String getToken()                                { return token; }
    public void setToken(String token)                      { this.token = token; }
    public LocalDateTime getBlacklistedAt()                 { return blacklistedAt; }
    public void setBlacklistedAt(LocalDateTime d)           { this.blacklistedAt = d; }
}
