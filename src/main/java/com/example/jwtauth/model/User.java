package com.example.jwtauth.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public User() {}

    public User(Long id, String username, String password, String email, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String username;
        private String password;
        private String email;
        private Role role;
        public Builder id(Long id)          { this.id = id; return this; }
        public Builder username(String u)   { this.username = u; return this; }
        public Builder password(String p)   { this.password = p; return this; }
        public Builder email(String e)      { this.email = e; return this; }
        public Builder role(Role r)         { this.role = r; return this; }
        public User build()                 { return new User(id, username, password, email, role); }
    }

    public Long getId()                         { return id; }
    public void setId(Long id)                  { this.id = id; }
    public void setUsername(String username)    { this.username = username; }
    public void setPassword(String password)    { this.password = password; }
    public String getEmail()                    { return email; }
    public void setEmail(String email)          { this.email = email; }
    public Role getRole()                       { return role; }
    public void setRole(Role role)              { this.role = role; }

    @Override public String getUsername()       { return username; }
    @Override public String getPassword()       { return password; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return true; }
}
