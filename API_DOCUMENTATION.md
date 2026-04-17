# Sarvasya LMS Backend API Documentation

This document provides details on the available API endpoints, their expected payloads, and testing examples using `curl`.

## Base URL
All API requests should be prefixed with: `http://localhost:8080/api`

---

## 1. Authentication Endpoints

### 1.1 Signup
Registers a new user in the system. The password is securely hashed using BCrypt before storing.

- **URL:** `/auth/signup`
- **Method:** `POST`
- **Content-Type:** `application/json`

#### Request Body
```json
{
  "name": "John Doe",
  "email": "johndoe@example.com",
  "password": "securepassword123",
  "role": "professor"
}
```
*(Note: `role` is optional. Allowed values: `sarvasya-admin`, `admin`, `professor`, `user`. Defaults to `user` if not provided).*

#### Expected Response (200 OK)
```text
User registered successfully!
```

#### `curl` Example
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"John Doe\", \"email\": \"johndoe@example.com\", \"password\": \"securepassword123\", \"role\": \"professor\"}"
```

---

### 1.2 Login
Authenticates a user and returns a JSON Web Token (JWT) that must be used for subsequent authenticated requests.

- **URL:** `/auth/login`
- **Method:** `POST`
- **Content-Type:** `application/json`

#### Request Body
```json
{
  "email": "johndoe@example.com",
  "password": "securepassword123"
}
```

#### Expected Response (200 OK)
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huZG9lQGV4...",
  "email": "johndoe@example.com",
  "role": "ROLE_USER"
}
```

#### `curl` Example
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\": \"johndoe@example.com\", \"password\": \"securepassword123\"}"
```

---

### 1.3 Logout
Logs out the user. Since JWTs are stateless, this endpoint clears the server-side SecurityContext. The client should also discard the token locally.

- **URL:** `/auth/logout`
- **Method:** `POST`
- **Authorization:** `Bearer <Your-JWT-Token>`

#### Expected Response (200 OK)
```text
Logged out successfully.
```

#### `curl` Example
```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

---

## Testing Flow (Quick Start)
1. **Create a User:** Run the Signup `curl` command.
2. **Log In:** Run the Login `curl` command. Copy the `token` string from the JSON response.
3. **Make Authenticated Requests:** Add `-H "Authorization: Bearer <token>"` to any secured API endpoint you build in the future.
