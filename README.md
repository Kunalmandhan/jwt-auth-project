# 🔐 JWT Authentication – Spring Boot

A complete JWT (JSON Web Token) authentication system built with **Spring Boot 3**, **Spring Security**, and **H2 in-memory database**.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Project Structure](#project-structure)
- [How It Works](#how-it-works)
- [Setup & Run](#setup--run)
- [API Endpoints](#api-endpoints)
- [Postman Testing Guide](#postman-testing-guide)
- [Screenshots](#screenshots)
- [Security Design](#security-design)

---

## Overview

This project implements a stateless JWT authentication system with:

- **User Registration** – create an account, receive a JWT immediately
- **User Login** – authenticate with username & password, receive a JWT
- **Protected Routes** – secured endpoints that require a valid Bearer token
- **Role-Based Access** – `USER` and `ADMIN` roles with different permissions
- **Logout / Token Invalidation** – token blacklisting via the database

---

## Project Structure

```
src/
├── main/
│   ├── java/com/example/jwtauth/
│   │   ├── JwtAuthApplication.java          # Entry point + demo user seeding
│   │   │
│   │   ├── config/
│   │   │   └── SecurityConfig.java          # Spring Security rules, filter chain
│   │   │
│   │   ├── controller/
│   │   │   ├── AuthController.java          # /api/auth/** (register, login, logout)
│   │   │   └── ProtectedController.java     # /api/protected/** and /api/admin/**
│   │   │
│   │   ├── dto/
│   │   │   ├── LoginRequest.java            # { username, password }
│   │   │   ├── RegisterRequest.java         # { username, password, email }
│   │   │   ├── AuthResponse.java            # { token, tokenType, username, role, expiresIn }
│   │   │   └── ApiResponse.java             # Generic wrapper { success, message, data }
│   │   │
│   │   ├── model/
│   │   │   ├── User.java                    # JPA entity implementing UserDetails
│   │   │   ├── Role.java                    # Enum: USER | ADMIN
│   │   │   └── TokenBlacklist.java          # Stores invalidated tokens
│   │   │
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   └── TokenBlacklistRepository.java
│   │   │
│   │   ├── security/
│   │   │   ├── JwtService.java              # Token generation, validation, claims
│   │   │   └── JwtAuthenticationFilter.java # Per-request JWT middleware
│   │   │
│   │   └── service/
│   │       ├── AuthService.java             # Business logic: register, login, logout
│   │       └── UserDetailsServiceImpl.java  # Loads user from DB for Spring Security
│   │
│   └── resources/
│       └── application.properties           # Server, DB, JWT config
│
postman/
│   └── JWT_Auth_Collection.json             # Import into Postman to test all routes
│
screenshots/
│   └── (add your Postman screenshots here)
│
pom.xml
README.md
```

---

## How It Works

### Authentication Flow

```
Client                          Server
  │                               │
  │── POST /api/auth/login ──────►│
  │   { username, password }      │
  │                               │── Validate credentials
  │                               │── Generate JWT (signed with secret)
  │◄─ { token: "eyJ..." } ───────│
  │                               │
  │── GET /api/protected/profile  │
  │   Authorization: Bearer eyJ..│
  │                               │── JwtAuthenticationFilter intercepts
  │                               │── Verifies token signature
  │                               │── Checks token not blacklisted
  │                               │── Sets SecurityContext
  │◄─ { profile data } ──────────│
  │                               │
  │── POST /api/auth/logout ──────│
  │   Authorization: Bearer eyJ..│
  │                               │── Saves token to blacklist table
  │◄─ { "Logged out" } ──────────│
  │                               │
  │── GET /api/protected/profile  │
  │   Authorization: Bearer eyJ..│  (same token as before)
  │                               │── Token found in blacklist → REJECT
  │◄─ 401 Unauthorized ──────────│
```

### JWT Token Structure

```
Header.Payload.Signature

eyJhbGciOiJIUzI1NiJ9          ← Header (algorithm: HS256)
.eyJzdWIiOiJhZG1pbiIsInJvbG   ← Payload (username, roles, issued-at, expiry)
.SflKxwRJSMeKKF2QT4fwpMeJf36  ← Signature (HMAC-SHA256 with secret key)
```

### Key Components

| Component | Role |
|-----------|------|
| `JwtService` | Creates and validates JWT tokens |
| `JwtAuthenticationFilter` | Intercepts every HTTP request, extracts and validates the token |
| `SecurityConfig` | Defines which routes are public vs. protected |
| `AuthService` | Orchestrates registration, login, and logout |
| `TokenBlacklist` | Database table for invalidated tokens (logout support) |

---

## Setup & Run

### Prerequisites

- Java 17+
- Maven 3.6+

### Steps

```bash
# 1. Clone / download the project
cd jwt-auth-project

# 2. Build
mvn clean package -DskipTests

# 3. Run
mvn spring-boot:run
```

The server starts on **http://localhost:8080**

On startup, two demo users are created automatically:

| Username | Password  | Role  |
|----------|-----------|-------|
| `admin`  | `admin123`| ADMIN |
| `user`   | `user123` | USER  |

> **H2 Console**: http://localhost:8080/h2-console  
> JDBC URL: `jdbc:h2:mem:jwtdb` | Username: `sa` | Password: *(empty)*

---

## API Endpoints

### Public (no token required)

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/public/info` | Health check |
| POST | `/api/auth/register` | Create account, returns JWT |
| POST | `/api/auth/login` | Login, returns JWT |

### Protected (requires `Authorization: Bearer <token>`)

| Method | URL | Role Required | Description |
|--------|-----|---------------|-------------|
| GET | `/api/protected/profile` | Any | Current user's profile |
| GET | `/api/protected/dashboard` | Any | Dashboard message |
| GET | `/api/admin/users` | ADMIN only | Admin panel |
| POST | `/api/auth/logout` | Any | Invalidate token |

### Request / Response Examples

**Login**
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "username": "admin",
    "role": "ADMIN",
    "expiresIn": 86400000
  }
}
```

**Access Protected Route**
```http
GET /api/protected/profile
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

```json
{
  "success": true,
  "message": "Profile retrieved successfully",
  "data": {
    "id": 1,
    "username": "admin",
    "email": "admin@example.com",
    "role": "ADMIN"
  }
}
```

**Logout**
```http
POST /api/auth/logout
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

```json
{
  "success": true,
  "message": "Logged out successfully. Token has been invalidated."
}
```

---

## Postman Testing Guide

### Import the Collection

1. Open Postman
2. Click **Import** → select `postman/JWT_Auth_Collection.json`
3. The collection includes all requests pre-configured

### Testing Steps (Screenshots Required)

#### Screenshot 1 – Successful Login

1. Open **2. Auth → POST /api/auth/login (admin)**
2. Click **Send**
3. ✅ You should see `200 OK` with a JWT token in the response
4. The token is **auto-saved** to the `{{token}}` collection variable

#### Screenshot 2 – Access Protected Route

1. Open **3. Protected Routes → GET /api/protected/profile**
2. The `Authorization: Bearer {{token}}` header is pre-filled
3. Click **Send**
4. ✅ You should see `200 OK` with your profile data

#### Screenshot 3 – Logout + Token Invalidation

1. Open **5. Logout → POST /api/auth/logout**
2. Click **Send** → ✅ `200 OK` "Logged out successfully"
3. Open **5. Logout → GET /api/protected/profile (AFTER LOGOUT)**
4. Click **Send**
5. ✅ You should see `401 Unauthorized` – token is now blacklisted

#### Bonus – Wrong Password (401)

1. Open **2. Auth → POST /api/auth/login (WRONG PASSWORD)**
2. Click **Send**
3. ✅ Returns `401` – "Invalid username or password"

#### Bonus – Admin-Only Route

1. First login as **admin** (saves admin token)
2. Open **4. Admin Routes → GET /api/admin/users**
3. ✅ Returns `200` with admin message
4. Now login as **user** (saves user token)
5. Run the same request → ✅ Returns `403 Forbidden`

---

## Screenshots

Store your Postman screenshots in the `screenshots/` folder.

Suggested filenames:

```
screenshots/
├── 01_login_success.png          # POST /login → 200 with JWT token
├── 02_protected_profile.png      # GET /protected/profile → 200 with data
├── 03_logout.png                 # POST /logout → 200 confirmed
├── 04_after_logout_401.png       # GET /protected/profile → 401 blacklisted
├── 05_wrong_password_401.png     # POST /login wrong pass → 401
└── 06_admin_only.png             # GET /admin/users → 200 (admin) or 403 (user)
```

---

## Security Design

| Feature | Implementation |
|---------|----------------|
| Password storage | BCrypt hashing (never stored in plaintext) |
| Token signing | HMAC-SHA256 with a 256-bit secret key |
| Token expiry | 24 hours (configurable in `application.properties`) |
| Session management | Stateless – no server-side sessions |
| Logout | Token blacklist in database |
| Route protection | Spring Security filter chain + `@PreAuthorize` |

### Configuration (`application.properties`)

```properties
jwt.secret=5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437
jwt.expiration=86400000   # 24 hours in milliseconds
```

> ⚠️ In production: use a strong random secret (32+ bytes) stored in an environment variable, not in the properties file. Use a real database (PostgreSQL/MySQL) instead of H2.
