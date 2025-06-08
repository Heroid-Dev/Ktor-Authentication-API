# 🛡️ Ktor Authentication API

A Kotlin backend project built with [Ktor](https://ktor.io/) providing a secure authentication and user management system using JWT tokens, password hashing with salt, and role-based authorization.

---

## 🚀 Features

- User Registration (`sign-up`)
- User Login (`sign-in`)
- Refresh Token Endpoint
- Secure Password Hashing (Salted SHA-256)
- JWT-based Authentication & Authorization
- Role-based Access (USER & ADMIN)
- Add Todo Tasks (authorized users)
- Admin Endpoints:
  - Get all users
  - Get user by ID
  - Update user password
  - Delete user by ID
  - Delete all users

---

## 🛠️ Technologies Used

- **Ktor** (Routing, Authentication, ContentNegotiation)
- **Koin** for Dependency Injection
- **Kotlinx Serialization**
- **JWT** (Java Web Tokens)
- **Apache Commons Codec** for hashing
- **MongoDB** (via repository pattern - assumed)
- **Gradle** for build automation

---

## 🔐 Endpoints Overview

### ✅ Public Routes

| Method | Route             | Description         |
|--------|------------------|---------------------|
| POST   | `/sign-up`        | Register new user   |
| POST   | `/sign-in`        | Login with username/password |
| POST   | `/sign-in/refresh`| Refresh access token |

### 🔐 Authenticated Routes

| Method | Route             | Description         | Role |
|--------|------------------|---------------------|------|
| PUT    | `/sign-in`        | Update password     | USER |
| POST   | `/sign-in`        | Add TodoTask        | USER |

### 🔒 Admin Only Routes

| Method | Route             | Description              |
|--------|------------------|--------------------------|
| GET    | `/sign-up`        | Get all users            |
| GET    | `/sign-up/by?id=` | Get user by ID           |
| PUT    | `/sign-up`        | Update user password     |
| DELETE | `/sign-up/by?id=` | Delete user by ID        |
| DELETE | `/sign-up/all`    | Delete all users         |

---

## 📦 How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/ktor-auth-api.git
   cd ktor-auth-api
   ```
2. Make sure you have:
   - JDK 17+
   - Gradle
   - MongoDB (or your chosen persistence)
3. Run the application:
  ```bash
   ./gradlew run
  ```

## ⚙️ Configuration

Set the following in your application.conf or environment variables:
```bash
jwt {
    secret = "your-secret-key"
    issuer = "your-app-name"
    audience = "your-app-users"
    realm = "access"
}
```

## 📌 Notes
- Only users with username = "Admin" and a specific hardcoded password will be assigned the ADMIN role.
- Tokens are signed using HMAC256 and expire in:
- Access Token: 1 hour
- Refresh Token: 24 hours
