# Sarvasya LMS Backend API Documentation

This document provides details on the available API endpoints, their expected payloads, and the distinction between Tenant-Specific and Global Management flows.

---

## 1. Authentication & Platform Entry
Authentication paths vary depending on your role. Platform Managers use the global path, while school staff/students use their specific tenant path.

### 1.1 Global Manager Signup
Registers a new **Tenant Manager** (Platform Owner) in the central database.
- **URL:** `/api/auth/signup`
- **Method:** `POST`
- **Permissions:** Public (requires `signupKey`)
- **Payload Example:**
```json
{
  "name": "Platform Admin",
  "email": "manager@sarvasya.com",
  "password": "securepassword123",
  "role": "tenant-manager",
  "signupKey": "sarvasya-secret-key-2024"
}
```

### 1.2 Login (Dual-Path)
Authenticates a user and returns a JWT + user metadata.
- **Global Path (Manager):** `/api/auth/login`
- **Tenant Path (Staff/Student):** `/api/{tenantName}/auth/login`
- **Method:** `POST`
- **Permissions:** Public
- **Payload Example:**
```json
{
  "email": "manager@sarvasya.com",
  "password": "securepassword123"
}
```

---

## 2. Tenant-Specific Operations (Staff & Students)
These endpoints use the `{tenantName}` path variable to activate a specific school's database schema.

### 2.1 Theme & Branding
- **Get Theme:** `GET /api/v1/tenants/{tenantName}/theme`
- **Update Theme:** `PUT /api/v1/tenants/{tenantName}/theme`
- **Permissions:** 
    - `GET`: Public
    - `PUT`: `sarvasya-admin` only
- **Payload Example:**
```json
{
  "primary": {
    "seedColor": "#3F51B5",
    "gradientStart": "#3F51B5",
    "gradientEnd": "#303F9F",
    "gradientDir": 1,
    "useGradient": true,
    "textColor": "#FFFFFF"
  },
  "secondary": {
    "backgroundColor": "#F5F5F5",
    "gradientStart": "#F5F5F5",
    "gradientEnd": "#E0E0E0",
    "gradientDir": 1,
    "useGradient": false,
    "textColor": "#000000"
  },
  "sidebar": {
    "seedColor": "#212121",
    "gradientStart": "#212121",
    "gradientEnd": "#000000",
    "gradientDir": 1,
    "useGradient": true,
    "textColor": "#FFFFFF"
  },
  "widgets": {
    "cardBackgroundColor": "#FFFFFF",
    "cardElevation": 2.0,
    "buttonBackgroundColor": "#3F51B5",
    "buttonTextColor": "#FFFFFF",
    "inputBackgroundColor": "#FAFAFA",
    "inputBorderColor": "#BDBDBD"
  },
  "logoUrl": "https://example.com/logo.png"
}
```

### 2.2 User Management (School Level)
- **Create User:** `POST /api/{tenantName}/users`
- **Permissions:** `sarvasya-admin`, `admin`, `professor` (Role-restricted)
- **Payload Example:**
```json
{
  "name": "Pratham Sharma",
  "email": "pratham@harvard.com",
  "role": "professor"
}
```

---

## 3. Platform & Global Management (V1 APIs)
These endpoints are used for cross-tenant administration. **Tenant-Managers** have full CRUD (Create, Read, Update, Delete) permissions. Other authenticated roles have **Read-Only** access to verify quotas and features.

### 3.1 Global Theme Management
- **URL:** `/api/v1/tenants/theme`
- **Method:** `PUT`
- **Permissions:** `tenant-manager` only
- **Payload Example:**
```json
{
  "tenantId": "harvard",
  "primary": {
    "seedColor": "#009688",
    "gradientStart": "#009688",
    "gradientEnd": "#00796B",
    "gradientDir": 1,
    "useGradient": true,
    "textColor": "#FFFFFF"
  },
  "secondary": {
    "backgroundColor": "#F5F5F5",
    "textColor": "#000000"
  },
  "sidebar": {
    "seedColor": "#263238",
    "useGradient": true
  },
  "widgets": {
    "cardElevation": 4.0,
    "buttonBackgroundColor": "#009688"
  }
}
```

### 3.2 Resource Quotas & Limits
- **View Limits:** `GET /api/v1/limits?tenantId=harvard`
- **Modify Limits:** `PUT /api/v1/limits`
- **Permissions:**
    - `GET`: All authenticated roles
    - `PUT`: `tenant-manager` only
- **Payload Example:**
```json
{
  "tenantId": "harvard",
  "userLimit": 5000,
  "professorLimit": 300,
  "adminLimit": 20
}
```

### 3.3 Tenant Configuration & Features
- **List All Tenants:** `GET /api/v1/tenants`
- **Get Single Config:** `GET /api/v1/tenants/{tenantId}`
- **Update Config:** `PUT /api/v1/tenants`
- **Permissions:**
    - `GET`: All authenticated roles
    - `PUT`: `tenant-manager` only
- **Payload Example:**
```json
{
  "tenantId": "harvard",
  "features": { "basicLms": { "enabled": true } },
  "license": { "type": "ENTERPRISE", "expiryDate": "2025-12-31" }
}
```

### 3.4 Global User Provisioning
Creating a school administrator globally automatically triggers the creation of the tenant schema and default configuration.
- **URL:** `/api/v1/users`
- **Method:** `POST`
- **Permissions:** `tenant-manager` only
- **Payload Example:**
```json
{
  "name": "School Admin",
  "email": "admin@harvard.com",
  "role": "sarvasya-admin",
  "tenantId": "harvard"
}
```

### 3.5 Tenant Impersonation
Allows tenant-managers to impersonate a tenant and get an impersonation token to access tenant-specific resources.
- **URL:** `/api/v1/tenants/{tenantId}/impersonate`
- **Method:** `POST`
- **Permissions:** `tenant-manager` only
- **Response Example:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "impersonationToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tenantId": "harvard"
}
```

---

## 4. Bus Management APIs (Tenant-Specific)
These endpoints manage buses, bus schedules, and bus passes for transportation within a tenant.

### 4.1 Bus Management
#### Create Bus
- **URL:** `/api/v1/buses`
- **Method:** `POST`
- **Permissions:** `ADMIN` only
- **Payload Example:**
```json
{
  "busNumber": "BUS-001",
  "capacity": 50
}
```

#### Get All Buses
- **URL:** `/api/v1/buses`
- **Method:** `GET`
- **Permissions:** All authenticated users (`ADMIN`, `PROFESSOR`, `USER`)

#### Get Bus by ID
- **URL:** `/api/v1/buses/{id}`
- **Method:** `GET`
- **Permissions:** All authenticated users

#### Update Bus
- **URL:** `/api/v1/buses/{id}`
- **Method:** `PUT`
- **Permissions:** `ADMIN` only
- **Payload Example:**
```json
{
  "busNumber": "BUS-001",
  "capacity": 60
}
```

#### Delete Bus
- **URL:** `/api/v1/buses/{id}`
- **Method:** `DELETE`
- **Permissions:** `ADMIN` only

### 4.2 Bus Schedule Management
#### Create Bus Schedule
- **URL:** `/api/v1/bus-schedules`
- **Method:** `POST`
- **Permissions:** `ADMIN` only
- **Payload Example:**
```json
{
  "bus": { "id": "uuid-of-bus" },
  "routeName": "Route A - Downtown",
  "startTime": "08:00:00",
  "endTime": "18:00:00"
}
```

#### Response Example (DTO)
```json
{
  "id": "uuid-of-schedule",
  "bus": {
    "id": "uuid-of-bus",
    "busNumber": "BUS-001"
  },
  "routeName": "Route A - Downtown",
  "startTime": "08:00:00",
  "endTime": "18:00:00",
  "createdAt": "2024-01-01T12:00:00"
}
```

#### Get All Bus Schedules
- **URL:** `/api/v1/bus-schedules`
- **Method:** `GET`
- **Permissions:** `ADMIN`, `PROFESSOR`, `USER`

#### Get Bus Schedule by ID
- **URL:** `/api/v1/bus-schedules/{id}`
- **Method:** `GET`
- **Permissions:** `ADMIN`, `PROFESSOR`, `USER`

#### Get Bus Schedules by Bus ID
- **URL:** `/api/v1/bus-schedules/bus/{busId}`
- **Method:** `GET`
- **Permissions:** `ADMIN`, `PROFESSOR`, `USER`

#### Update Bus Schedule
- **URL:** `/api/v1/bus-schedules/{id}`
- **Method:** `PUT`
- **Permissions:** `ADMIN` only
- **Payload Example:**
```json
{
  "bus": { "id": "uuid-of-bus" },
  "routeName": "Route A - Downtown",
  "startTime": "07:30:00",
  "endTime": "18:30:00"
}
```

#### Delete Bus Schedule
- **URL:** `/api/v1/bus-schedules/{id}`
- **Method:** `DELETE`
- **Permissions:** `ADMIN` only

### 4.3 Bus Pass Management
#### Create Bus Pass
- **URL:** `/api/v1/bus-passes`
- **Method:** `POST`
- **Permissions:** `ADMIN` only
- **Payload Example:**
```json
{
  "userName": "John Doe",
  "busId": "uuid-of-bus",
  "validFrom": "2024-01-01",
  "validTo": "2024-12-31"
}
```

#### Response Example (DTO)
```json
{
  "id": "uuid-of-pass",
  "user": {
    "id": "uuid-of-user",
    "name": "John Doe",
    "email": "john@example.com"
  },
  "bus": {
    "id": "uuid-of-bus",
    "busNumber": "BUS-001"
  },
  "validFrom": "2024-01-01",
  "validTo": "2024-12-31",
  "status": "ACTIVE",
  "createdAt": "2024-01-01T12:00:00"
}
```

#### Get All Bus Passes
- **URL:** `/api/v1/bus-passes`
- **Method:** `GET`
- **Permissions:** `ADMIN`, `PROFESSOR`, `USER`

#### Get Bus Pass by ID
- **URL:** `/api/v1/bus-passes/{id}`
- **Method:** `GET`
- **Permissions:** `ADMIN`, `PROFESSOR`, `USER`

#### Get Bus Passes by User ID
- **URL:** `/api/v1/bus-passes/user/{userId}`
- **Method:** `GET`
- **Permissions:** `ADMIN`, `PROFESSOR`, `USER`

#### Get Bus Passes by Bus ID
- **URL:** `/api/v1/bus-passes/bus/{busId}`
- **Method:** `GET`
- **Permissions:** `ADMIN`, `PROFESSOR`, `USER`

#### Update Bus Pass
- **URL:** `/api/v1/bus-passes/{id}`
- **Method:** `PUT`
- **Permissions:** `ADMIN` only
- **Payload Example:**
```json
{
  "user": { "id": "uuid-of-user" },
  "bus": { "id": "uuid-of-bus" },
  "validFrom": "2024-01-01",
  "validTo": "2025-12-31",
  "status": "EXPIRED"
}
```

#### Delete Bus Pass
- **URL:** `/api/v1/bus-passes/{id}`
- **Method:** `DELETE`
- **Permissions:** `ADMIN` only

---

## 5. Error Handling: Quota Exceeded
The system enforces strict role-based quotas. Returns `400 Bad Request` when limits are hit.
```json
{
  "message": "Role Creation Quota limit exceeded. Please purchase more quota limit for it."
}
```
