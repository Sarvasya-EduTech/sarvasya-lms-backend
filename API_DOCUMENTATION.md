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
- **Rules:**
    - `STUDENT`/`USER`: Must provide `classId`. Optionally provide `degreeId`.
    - `ADMIN`: Must provide `departmentId`.
    - `PROFESSOR`: Linked to classes via join table (can leave `classId` null).
    - `sarvasya-admin`: No class/department assignment required.
- **Payload Example:**
```json
{
  "name": "Pratham Sharma",
  "email": "pratham@harvard.com",
  "role": "user",
  "classId": "018f6c42-2b8e-7111-a83d-e21b7643a5f2",
  "degreeId": "018f6c50-1a2b-7333-b44c-d5e6f7890abc"
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
  "busId": "uuid-of-bus",
  "routeName": "Route A - Downtown",
  "startTime": "08:00:00",
  "endTime": "18:00:00",
  "stops": [
    { "stopName": "Main Gate", "arrivalTime": "08:00:00" },
    { "stopName": "Library", "arrivalTime": "08:15:00" },
    { "stopName": "Hostel Block A", "arrivalTime": "08:30:00" }
  ]
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
  "stops": [
    { "stopName": "Main Gate", "arrivalTime": "08:00:00" },
    { "stopName": "Library", "arrivalTime": "08:15:00" },
    { "stopName": "Hostel Block A", "arrivalTime": "08:30:00" }
  ],
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
  "busId": "uuid-of-bus",
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
  "stopName": "Library",
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
  "stopName": "Library",
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
Manages minimalist class entities linked to calendar items and courses.
- **Get Classes:** `GET /api/{tenantName}/classes`
- **Create Class:** `POST /api/{tenantName}/classes`
- **Permissions:** `sarvasya-admin`, `admin`
- **Payload Example:**
```json
{
  "courseId": "018f6c46-32a1-77b3-90ea-f2ab8790b1c1",
  "batchId": "018f6c42-2b8e-7111-a83d-e21b7643a5f2",
  "teacherId": "018f6c43-1d4e-761a-b33c-f4ab9801d3b4"
}
```

### 5.3 Exam Management
Manages minimalist exam entities linked to calendar items and courses.
- **Get Exams:** `GET /api/{tenantName}/exams`
- **Create Exam:** `POST /api/{tenantName}/exams`
- **Permissions:** `sarvasya-admin`, `admin`
- **Payload Example:**
```json
{
  "courseId": "018f6c46-32a1-77b3-90ea-f2ab8790b1c1",
  "batchId": "018f6c42-2b8e-7111-a83d-e21b7643a5f2",
  "totalMarks": 100
}
```

### 5.4 Department Management
- **URL:** `/api/{tenantName}/departments`
- **Methods:** `GET`, `POST`, `DELETE`
- **Permissions:** `sarvasya-admin`, `admin` (Create/Delete)
- **Payload Example:**
```json
{
  "name": "Computer Science & Engineering"
}
```

### 5.5 Course Management
- **URL:** `/api/{tenantName}/courses`
- **Methods:** `GET`, `POST`, `DELETE`
- **Filtering:** `GET /api/{tenantName}/courses/department/{departmentId}`
- **Permissions:** `sarvasya-admin`, `admin` (Create/Delete)
- **Payload Example:**
```json
{
  "name": "Data Structures & Algorithms",
  "departmentId": "018f6c45-1d4e-761a-b33c-f4ab9801d3b4",
  "collegeId": "COL-12345"
}
```

### 5.6 Degree Management
Manages degree programs (e.g., B.Tech, M.Sc) with their duration and semester structure.

#### Get All Degrees
- **URL:** `/api/{tenantName}/degrees`
- **Method:** `GET`
- **Permissions:** All authenticated users

#### Create Degree
- **URL:** `/api/{tenantName}/degrees`
- **Method:** `POST`
- **Permissions:** `sarvasya-admin`, `admin`
- **Payload Example:**
```json
{
  "degreeName": "B.Tech Computer Science",
  "numberOfYears": 4,
  "numberOfSemesters": 8
}
```

#### Update Degree
- **URL:** `/api/{tenantName}/degrees/{id}`
- **Method:** `PUT`
- **Permissions:** `sarvasya-admin`, `admin`
- **Payload Example:**
```json
{
  "degreeName": "B.Tech Computer Science & Engineering",
  "numberOfYears": 4,
  "numberOfSemesters": 8
}
```

#### Delete Degree
- **URL:** `/api/{tenantName}/degrees/{id}`
- **Method:** `DELETE`
- **Permissions:** `sarvasya-admin`, `admin`
- **Note:** Deleting a degree sets `degree_id` to `NULL` on all associated student records (`ON DELETE SET NULL`).

---

## 6. Global Course Management (Sarvasya LMS APIs)
These endpoints manage the core LMS content (courses, modules, exams, questions) and student records (enrollments, attempts, payments). All data is stored centrally in the global `tenant` schema.

All endpoints support standard REST CRUD operations (`GET`, `POST`, `PUT`, `DELETE`).

### 6.1 Content Management (Global Path)
These APIs do not require a tenant context as they manage shared, global content.

**Base Path:** `/api/sarvasya/{resource}`

- **Courses**: `/api/sarvasya/courses`
- **Modules**: `/api/sarvasya/modules`
- **Lessons**: `/api/sarvasya/lessons`
- **Study Materials**: `/api/sarvasya/studymaterials`
- **Quizzes**: `/api/sarvasya/quizs`
- **Exams**: `/api/sarvasya/exams`
- **Questions**: `/api/sarvasya/questions`
- **Options**: `/api/sarvasya/options`
- **Quiz Questions Mapping**: `/api/sarvasya/quizquestions`
- **Exam Questions Mapping**: `/api/sarvasya/examquestions`

### 6.2 Student Records (Tenant-Scoped Path)
Because students belong to specific tenants, these APIs require `{tenantid}` in the path to establish the tenant context. However, the data is still persisted centrally in the global `tenant` schema.

**Base Path:** `/api/sarvasya/{tenantid}/{resource}`

- **Assessment Attempts**: `/api/sarvasya/{tenantid}/attempts`
- **Student Answers**: `/api/sarvasya/{tenantid}/studentanswers`
- **Enrollments**: `/api/sarvasya/{tenantid}/enrollments`
- **Payments**: `/api/sarvasya/{tenantid}/payments`
- **Invoices**: `/api/sarvasya/{tenantid}/invoices`
- **Certificates**: `/api/sarvasya/{tenantid}/certificates`

### Example: Course Management
- **Get All Courses**: `GET /api/sarvasya/courses`
- **Get Course by ID**: `GET /api/sarvasya/courses/{id}`
- **Create Course**: `POST /api/sarvasya/courses`
- **Update Course**: `PUT /api/sarvasya/courses/{id}`
- **Delete Course**: `DELETE /api/sarvasya/courses/{id}`

**Payload Example (Course Create/Update):**
```json
{
  "title": "Advanced Java Programming",
  "description": "Deep dive into multithreading, JVM performance, and memory management.",
  "price": 199.99,
  "courseCode": "CS-JAVA-400",
  "isActive": true
}
```

### Example: Enrollment
- **Create Enrollment**: `POST /api/sarvasya/{tenantid}/enrollments`

**Payload Example (Enrollment):**
```json
{
  "studentId": "018f6c42-2b8e-7111-a83d-e21b7643a5f2",
  "courseId": "uuid-of-course",
  "enrollmentNumber": "ENR-2024-001",
  "sequenceNumber": 1,
  "isPaid": true
}
```

### Example: Module Management
- **Create Module**: `POST /api/sarvasya/modules`

**Payload Example:**
```json
{
  "courseId": "uuid-of-course",
  "title": "Getting Started with Java",
  "orderIndex": 1
}
```

### Example: Lesson Management
- **Create Lesson**: `POST /api/sarvasya/lessons`

**Payload Example:**
```json
{
  "moduleId": "uuid-of-module",
  "title": "Variables and Data Types",
  "videoUrl": "https://video.example.com/java-variables.mp4",
  "orderIndex": 1,
  "isPreview": true
}
```

### Example: Study Material Management
- **Create Study Material**: `POST /api/sarvasya/studymaterials`

**Payload Example:**
```json
{
  "moduleId": "uuid-of-module",
  "title": "Java Cheat Sheet",
  "content": "### Java Basics\n...",
  "fileUrl": "https://files.example.com/cheat-sheet.pdf",
  "type": "FILE",
  "orderIndex": 2,
  "isDownloadable": true
}
```

### Example: Quiz Management
- **Create Quiz**: `POST /api/sarvasya/quizs`

**Payload Example:**
```json
{
  "moduleId": "uuid-of-module",
  "passingScore": 75
}
```

### Example: Exam Management
- **Create Exam**: `POST /api/sarvasya/exams`

**Payload Example:**
```json
{
  "courseId": "uuid-of-course",
  "passingScore": 80
}
```

### Example: Question Management
- **Create Question**: `POST /api/sarvasya/questions`

**Payload Example:**
```json
{
  "type": "MCQ",
  "questionText": "What is the size of int in Java?",
  "explanation": "In Java, int is a 32-bit signed integer.",
  "marks": 2.5,
  "negativeMarks": 0.5,
  "topic": "Data Types",
  "difficulty": "EASY"
}
```

### Example: Option Management
- **Create Option**: `POST /api/sarvasya/options`

**Payload Example:**
```json
{
  "questionId": "uuid-of-question",
  "text": "32-bit",
  "isCorrect": true
}
```

### Example: Quiz / Exam Question Mapping
- **Create Quiz Question**: `POST /api/sarvasya/quizquestions`

**Payload Example:**
```json
{
  "quizId": "uuid-of-quiz",
  "questionId": "uuid-of-question",
  "orderIndex": 1
}
```

### Example: Attempt Management
- **Create Attempt**: `POST /api/sarvasya/{tenantid}/attempts`

**Payload Example:**
```json
{
  "studentId": "uuid-of-student",
  "assessmentId": "uuid-of-quiz-or-exam",
  "type": "QUIZ",
  "score": 85.0,
  "maxScore": 100.0,
  "percentage": 85.0,
  "isPassed": true,
  "totalQuestions": 20,
  "correctAnswers": 17,
  "submittedAt": "2026-04-21T10:00:00"
}
```

### Example: Student Answer
- **Create Student Answer**: `POST /api/sarvasya/{tenantid}/studentanswers`

**Payload Example:**
```json
{
  "attemptId": "uuid-of-attempt",
  "questionId": "uuid-of-question",
  "selectedOptionIds": "uuid-of-option-1,uuid-of-option-2",
  "isCorrect": true,
  "marksAwarded": 2.5
}
```

### Example: Payment Management
- **Create Payment**: `POST /api/sarvasya/{tenantid}/payments`

**Payload Example:**
```json
{
  "studentId": "uuid-of-student",
  "courseId": "uuid-of-course",
  "amount": 199.99,
  "paymentGateway": "STRIPE",
  "paymentId": "pi_1234567890",
  "status": "SUCCESS",
  "paidAt": "2026-04-21T10:05:00"
}
```

### Example: Invoice Management
- **Create Invoice**: `POST /api/sarvasya/{tenantid}/invoices`

**Payload Example:**
```json
{
  "paymentId": "uuid-of-payment",
  "invoiceNumber": "INV-2026-001",
  "invoiceUrl": "https://invoices.example.com/INV-2026-001.pdf"
}
```

### Example: Certificate Management
- **Create Certificate**: `POST /api/sarvasya/{tenantid}/certificates`

**Payload Example:**
```json
{
  "studentId": "uuid-of-student",
  "courseId": "uuid-of-course",
  "certificateUrl": "https://certs.example.com/CERT-123.pdf",
  "issuedAt": "2026-04-21T10:10:00"
}
```
