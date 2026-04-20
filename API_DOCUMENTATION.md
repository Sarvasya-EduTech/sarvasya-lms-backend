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

---

## 4. Error Handling: Quota Exceeded
The system enforces strict role-based quotas. Returns `400 Bad Request` when limits are hit.
```json
{
  "message": "Role Creation Quota limit exceeded. Please purchase more quota limit for it."
}
```

---

## 5. Scheduling & Calendar Operations
These endpoints are tenant-specific and handle all calendar events, classes, and exams.

### 5.1 Calendar Management
Manages the unified calendar which handles classes, exams, assignments, events, and holidays.
- **Get Calendar Items:** `GET /api/{tenantName}/calendar?start={startDate}&end={endDate}`
- **Create Calendar Item:** `POST /api/{tenantName}/calendar`
- **Permissions:** 
    - `GET`: All authenticated roles
    - `POST`: `sarvasya-admin`, `admin`, `professor`
- **Payload Example (Event):**
```json
{
  "title": "Annual Sports Meet",
  "description": "Inter-school sports competition.",
  "type": "EVENT",
  "startDateTime": "2026-10-15T09:00:00",
  "endDateTime": "2026-10-17T17:00:00",
  "allDay": true,
  "colorCode": "#FF5722"
}
```
- **Payload Example (Class - requires referenceId):**
```json
{
  "title": "Mathematics 101",
  "type": "CLASS",
  "startDateTime": "2026-05-10T10:00:00",
  "endDateTime": "2026-05-10T11:00:00",
  "allDay": false,
  "referenceId": "018f6c44-32a1-77b3-90ea-f2ab8790b1c1",
  "referenceType": "CLASS",
  "colorCode": "#2196F3"
}
```

### 5.2 Classes Management
Manages minimalist class entities linked to calendar items. Used primarily for attendance mapping.
- **Get Classes:** `GET /api/{tenantName}/classes`
- **Create Class:** `POST /api/{tenantName}/classes`
- **Permissions:** `sarvasya-admin`, `admin`
- **Payload Example:**
```json
{
  "subject": "Mathematics",
  "batchId": "018f6c42-2b8e-7111-a83d-e21b7643a5f2",
  "teacherId": "018f6c43-1d4e-761a-b33c-f4ab9801d3b4"
}
```

### 5.3 Exam Management
Manages minimalist exam entities linked to calendar items. Used primarily for admit card generation and grading.
- **Get Exams:** `GET /api/{tenantName}/exams`
- **Create Exam:** `POST /api/{tenantName}/exams`
- **Permissions:** `sarvasya-admin`, `admin`
- **Payload Example:**
```json
{
  "subject": "Physics Midterm",
  "batchId": "018f6c42-2b8e-7111-a83d-e21b7643a5f2",
  "totalMarks": 100
}
```
