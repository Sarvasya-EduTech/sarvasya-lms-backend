# Sarvasya LMS Backend API Documentation

This document provides details on the available API endpoints, their expected payloads, and testing examples using `curl`.

## Base URL & Multi-Tenancy
All API requests must be prefixed with the `tenantName` (the specific university or college you are interacting with):
`http://localhost:8080/api/{tenantName}`

*(Example: `http://localhost:8080/api/harvard`)*

When an API request is made to a new tenant for the very first time, the system will automatically provision a new, isolated database schema for that tenant.

---

## 1. Authentication Endpoints

### 1.1 Signup
Registers a new user in the tenant's specific database schema. The password is securely hashed using BCrypt before storing.

- **URL:** `/{tenantName}/auth/signup`
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
curl -X POST http://localhost:8080/api/harvard/auth/signup \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"John Doe\", \"email\": \"johndoe@example.com\", \"password\": \"securepassword123\", \"role\": \"professor\"}"
```

---

### 1.2 Login
Authenticates a user from the specified tenant's database and returns a JSON Web Token (JWT) that must be used for subsequent authenticated requests.

- **URL:** `/{tenantName}/auth/login`
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
curl -X POST http://localhost:8080/api/harvard/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\": \"johndoe@example.com\", \"password\": \"securepassword123\"}"
```

---

### 1.3 Logout
Logs out the user. Since JWTs are stateless, this endpoint clears the server-side SecurityContext. The client should also discard the token locally.

- **URL:** `/{tenantName}/auth/logout`
- **Method:** `POST`
- **Authorization:** `Bearer <Your-JWT-Token>`

#### Expected Response (200 OK)
```text
Logged out successfully.
```

#### `curl` Example
```bash
curl -X POST http://localhost:8080/api/harvard/auth/logout \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

---

## Testing Flow (Quick Start)
1. **Create a User:** Run the Signup `curl` command using a specific tenant (e.g., `harvard`). The system will automatically create a new database schema named `harvard` if it doesn't exist.
2. **Log In:** Run the Login `curl` command for the same tenant. Copy the `token` string from the JSON response.
3. **Make Authenticated Requests:** Add `-H "Authorization: Bearer <token>"` to any secured API endpoint you build in the future.
4. **Test Data Isolation:** Try to log in with the same credentials but change the URL to a different tenant (e.g., `stanford`). It should fail because the databases are completely isolated!

---

## 2. Theme Settings Endpoints

Theme configuration for the platform is stored per tenant.

### 2.1 Get Theme Settings
Fetches the current theme configuration for the specific tenant. Unauthenticated users (e.g., the login page) can also access this to load tenant branding.

- **URL:** `/api/v1/tenants/{tenantId}/theme`
- **Method:** `GET`

#### Expected Response (200 OK)
```json
{
  "primary": {
    "seedColor": "#009688",
    "gradientStart": "#009688",
    "gradientEnd": "#B2DFDB",
    "gradientDir": 0,
    "useGradient": false,
    "textColor": "#FFFFFF"
  },
  "secondary": {
    "backgroundColor": "#FFFFFF",
    "gradientStart": "#FFFFFF",
    "gradientEnd": "#FFFFFF",
    "gradientDir": 1,
    "useGradient": false,
    "textColor": "#1A1A1A"
  },
  "sidebar": {
    "seedColor": "#1E1E2C",
    "gradientStart": "#1E1E2C",
    "gradientEnd": "#2D2D44",
    "gradientDir": 1,
    "useGradient": true,
    "textColor": "#FFFFFF"
  },
  "widgets": {
    "cardBackgroundColor": "#FFFFFF",
    "cardElevation": 2.0,
    "buttonBackgroundColor": "#009688",
    "buttonTextColor": "#FFFFFF",
    "inputBackgroundColor": "#FFFFFF",
    "inputBorderColor": "#E0E0E0"
  }
}
```

---

### 2.2 Update Theme Settings
Updates the theme configuration. Restricted to `admin` and `sarvasya-admin` roles.

- **URL:** `/api/v1/tenants/{tenantId}/theme`
- **Method:** `PUT`
- **Authorization:** `Bearer <Your-JWT-Token>`
- **Content-Type:** `application/json`

#### Request Body
Same as the `GET` response body.

#### Expected Response (200 OK)
Returns the updated theme settings in the same JSON format.

#### Field Definitions
| Section | Field | Type | Description |
| :--- | :--- | :--- | :--- |
| **primary** | seedColor | Hex String | Primary branding color (AppBar, Nav) |
| **widgets** | cardBackgroundColor | Hex String | Default background for all Cards |
| **widgets** | cardElevation | Double | Depth of shadows (0.0 to 12.0) |
| **widgets** | buttonBackgroundColor | Hex String | Fill color for primary buttons |
| **widgets** | buttonTextColor | Hex String | Text color for primary buttons |
| **widgets** | inputBackgroundColor | Hex String | Background for text inputs |
| **widgets** | inputBorderColor | Hex String | Border color for text inputs |

---

## 3. Bulk User Management & Forced Login Flow

### 3.1 Initial Login & Forced Password Change
When users (especially those created via bulk import) log in for the first time, their response will contain `"requiresPasswordChange": true` inside the nested user object.

- **URL:** `/{tenantName}/auth/change-password`
- **Method:** `POST`
- **Authorization:** `Bearer <Your-JWT-Token>`

#### Request Body
```json
{
  "currentPassword": "USER_EMAIL", 
  "newPassword": "SECURE_NEW_PASSWORD"
}
```
*(Note: Bulk-created accounts have their `email` set as their initial temporary password.)*

---

### 3.2 Bulk Create Users
Creates multiple users at once. Restricted to `sarvasya-admin` and `admin` roles. Automatically enforces tenant limits.

- **URL:** `/api/{tenantName}/users/bulk`
- **Method:** `POST`
- **Authorization:** `Bearer <Your-JWT-Token>`

#### Request Body (JSON Array)
```json
[
  {
    "name": "Student A",
    "email": "studenta@example.com",
    "role": "user"
  },
  {
    "name": "Professor B",
    "email": "profb@example.com",
    "role": "professor"
  }
]
```

### 3.3 Get Bulk CSV Template
Downloads a CSV template for bulk user imports.

- **URL:** `/api/{tenantName}/users/bulk/template`
- **Method:** `GET`
- **Authorization:** `Bearer <Your-JWT-Token>`

### 3.4 Bulk Delete Users
Deletes multiple users by ID.

- **URL:** `/api/{tenantName}/users/bulk`
- **Method:** `DELETE`
- **Authorization:** `Bearer <Your-JWT-Token>`

#### Request Body
```json
{
  "ids": ["UUID-1", "UUID-2"]
}
```

---

## 4. Tenant Specific Limits Configuration

Each tenant has isolated row limits.

### 4.1 Get Tenant Limits
Fetches the active row limits configured for the current tenant.

- **URL:** `/api/{tenantName}/limits`
- **Method:** `GET`
- **Authorization:** `Bearer <Your-JWT-Token>` (Requires `admin` or `sarvasya-admin`)

#### Expected Response
```json
{
  "id": 1,
  "userLimit": 1000,
  "professorLimit": 150,
  "adminLimit": 10
}
```

### 4.2 Update Tenant Limits
Overrides the active limits for the tenant. Restricted specifically to the platform owner (`sarvasya-admin`).

- **URL:** `/api/{tenantName}/limits`
- **Method:** `PUT`
- **Authorization:** `Bearer <Your-JWT-Token>` (Requires `sarvasya-admin`)

#### Request Body
```json
{
  "userLimit": 5000,
  "professorLimit": 300,
  "adminLimit": 20
}
```
