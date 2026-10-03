# KalaConnect (कलाकनेक्ट)

**KalaConnect** is an enterprise-grade digital platform engineered to bridge the gap between rural and local Self-Help Groups (SHGs) and Artisans, Non-Governmental Organizations (NGOs) and Administrators, and urban and global Customers.

The platform empowers artisans to build authenticated digital profiles, showcase handcrafted regional products, leverage AI-powered tools for cultural cataloging and promotion, publish products to a curated marketplace, and receive customer enquiries directly.

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Architecture](#2-architecture)
3. [Android Setup](#3-android-setup)
4. [Backend Setup](#4-backend-setup)
5. [Aiven PostgreSQL Setup](#5-aiven-postgresql-setup)
6. [Environment Variables](#6-environment-variables)
7. [Database Migration Instructions](#7-database-migration-instructions)
8. [Gemini Setup](#8-gemini-setup)
9. [API Documentation](#9-api-documentation)
10. [Running Android App](#10-running-android-app)
11. [Running Backend](#11-running-backend)
12. [Troubleshooting](#12-troubleshooting)

---

## 1. Project Overview

KalaConnect is built on the philosophy of **handcrafted digital dignity**:
* **For Artisans & SHGs:** Simple product cataloging with multi-photo support, real-time profile completion indicators, direct customer enquiry tracking, and Google Gemini AI assistance for cultural storytelling and social media promotion.
* **For Customers:** High-quality image browsing, multi-criteria filtering (category, location, price, craft type), artisan cultural story discovery, and direct enquiry submission without intermediary markups.
* **For NGO Admins:** Platform oversight, real-time database-driven analytics, artisan credential verification/approval workflows, product monitoring, and cluster outreach metrics.
* **Multilingual Native Experience:** Full runtime translation support for **English**, **हिंदी (Hindi)**, and **मराठी (Marathi)** with persistent language preferences.
* **Restrained Design System:** Warm minimalist visual identity (`#7C4D3A` Terracotta, `#FAF8F5` Linen, `#C89B7B` Sand) optimized for readability, accessibility, and high visual emphasis on artisan crafts.

---

## 2. Architecture

KalaConnect employs a strictly decoupled, 3-tier client-server architecture:

```
+-------------------------------------------------------------+
|                     Android Application                     |
|                 (Native Java / XML / Jetpack)               |
|                                                             |
|  - Activities: MainActivity, LoginActivity, SignupActivity  |
|  - Jetpack Navigation & Role-based Bottom Navigation        |
|  - MVVM Pattern: ViewModel, LiveData, UI State Handling    |
|  - Centralized AuthManager & OkHttp AuthInterceptor         |
|  - Retrofit HTTP Client & Glide Image Caching Engine        |
|  - LocaleHelper (Runtime Multilingual: en, hi, mr)          |
+------------------------------+------------------------------+
                               |
                               | HTTPS / JSON (REST API)
                               v
+-------------------------------------------------------------+
|                     Spring Boot Backend                     |
|                    (Java 17 / Maven)                        |
|                                                             |
|  - Spring Web REST Controllers with Bean Validation         |
|  - Stateless Spring Security Filter Chain & JWT (HMAC-256) |
|  - Direct Spring JDBC (NamedParameterJdbcTemplate)          |
|  - Centralized Exception Handling (@RestControllerAdvice)   |
|  - Transaction Management (@Transactional)                  |
|  - Google Gemini AI Client (generativelanguage.googleapis)  |
+------------------------------+------------------------------+
                               |
                               | PostgreSQL Wire Protocol / TLS (HikariCP)
                               v
+-------------------------------------------------------------+
|                      Aiven PostgreSQL                       |
|                   (Managed Cloud Database)                  |
|                                                             |
|  - 10 Relational Tables with Foreign Key Constraints        |
|  - Automated DDL Migrations (src/resources/db/migration)    |
|  - Optimized B-Tree Indexes on Foreign Keys & Filters       |
|  - Aggregated SQL Analytics for Real-time Cluster Insights  |
+-------------------------------------------------------------+
```

### Critical Security Isolation:
* **Zero Direct Database Access from Mobile:** The Android application never connects directly to Aiven PostgreSQL. All database operations, validations, and mutations are performed exclusively on the Spring Boot backend service.
* **Backend-Only Secrets:** Aiven database credentials, JWT secrets, and the Google Gemini API key exist strictly on the backend. Mobile clients only receive short-lived JWT bearer tokens.

---

## 3. Android Setup

### Prerequisites
* **Android Studio:** Hedgehog (2023.1.1) or newer.
* **JDK:** Java 17 or Java 11.
* **Android SDK:** API Level 34 (Android 14) or API Level 35 (Android 15).
* **Minimum Supported SDK:** API Level 24 (Android 7.0 Nougat).
* **Build System:** Gradle with Android Gradle Plugin (AGP) 8.3+.

### Project Structure
```
android/
├── app/
│   ├── src/main/
│   │   ├── java/com/kalaconnect/
│   │   │   ├── activities/     # MainActivity, LoginActivity, SignupActivity
│   │   │   ├── auth/           # AuthManager, AuthListener
│   │   │   ├── fragments/      # Role dashboards, product, explore, analytics
│   │   │   ├── models/         # Domain models, Enums (UserRole), DTOs
│   │   │   ├── network/        # Retrofit ApiClient, ApiService, Interceptors
│   │   │   ├── viewmodel/      # AuthViewModel, ProductViewModel, AdminViewModel
│   │   │   └── utils/          # LocaleHelper, Constants, ErrorUtils
│   │   └── res/
│   │       ├── values/         # strings.xml (English default), colors.xml, styles.xml
│   │       ├── values-hi/      # strings.xml (Hindi translations)
│   │       ├── values-mr/      # strings.xml (Marathi translations)
│   │       ├── layout/         # 40+ responsive XML layouts
│   │       └── navigation/     # nav_graph.xml (Jetpack Navigation)
│   └── build.gradle
└── build.gradle
```

---

## 4. Backend Setup

### Prerequisites
* **Java Development Kit (JDK):** JDK 17 (LTS) or higher.
* **Maven:** Apache Maven 3.8+ (wrapper `./mvnw` or `mvnw.cmd` included).
* **Network:** Outbound internet access to connect to Aiven Cloud PostgreSQL and Google Gemini API.

### Technology Stack
* **Framework:** Spring Boot 3.2.5
* **Security:** Spring Security 6 with BCrypt and JJWT (0.12.5)
* **Data Access:** Spring JDBC (`JdbcTemplate`, `NamedParameterJdbcTemplate`)
* **Driver:** PostgreSQL JDBC Driver (`org.postgresql:postgresql:42.7.3`)
* **JSON:** Jackson Databind

---

## 5. Aiven PostgreSQL Setup

1. **Create an Aiven Account:**
   Sign up or log in at [aiven.io](https://aiven.io/).
2. **Create a PostgreSQL Service:**
   - Select **PostgreSQL** as the service type.
   - Choose your preferred cloud provider (AWS, GCP, or Azure) and geographic region closest to your deployment.
   - Select a plan (e.g., Free Tier or Hobbyist plan).
3. **Obtain Connection Details:**
   From the **Service Overview** tab in the Aiven Console, note:
   - **Host:** e.g., `pg-xxxx.l.aivencloud.com`
   - **Port:** e.g., `19420`
   - **Database Name:** e.g., `defaultdb`
   - **User:** e.g., `avnadmin`
   - **Password:** `<your_aiven_password>`
4. **Construct the JDBC URL:**
   Aiven mandates SSL/TLS encryption. Append `sslmode=require` to your connection string:
   ```
   jdbc:postgresql://<HOST>:<PORT>/<DATABASE>?sslmode=require
   ```

---

## 6. Environment Variables

Never commit secrets to version control. The repository includes `.env.example` templates in both the root and `backend/` directories.

### Variable Reference
| Variable | Description | Example / Default | Required |
|---|---|---|---|
| `DB_URL` | JDBC connection string to Aiven PostgreSQL | `jdbc:postgresql://HOST:PORT/DB?sslmode=require` | **Yes** |
| `DB_USERNAME` | Aiven PostgreSQL username | `avnadmin` | **Yes** |
| `DB_PASSWORD` | Aiven PostgreSQL user password | `your_secret_password` | **Yes** |
| `JWT_SECRET` | 256-bit signing key for JWT tokens | `min_32_characters_secret_key_here` | **Yes** |
| `PORT` | HTTP port for the Spring Boot REST API | `8080` | No (default: 8080) |
| `GEMINI_API_KEY` | Google Gemini API key from AI Studio | `AIzaSy...` | Optional (Fallback active if omitted) |
| `GEMINI_MODEL` | Google Gemini model identifier | `gemini-2.5-flash` | No (default: gemini-2.5-flash) |

### Setting Environment Variables

#### Windows (PowerShell)
```powershell
$env:DB_URL="jdbc:postgresql://pg-xxx.l.aivencloud.com:19420/defaultdb?sslmode=require"
$env:DB_USERNAME="avnadmin"
$env:DB_PASSWORD="your_aiven_password"
$env:JWT_SECRET="your_secure_256_bit_secret_key_for_jwt_token_signing_here"
$env:PORT="8080"
$env:GEMINI_API_KEY="your_gemini_api_key"
```

#### Linux / macOS (Bash / Zsh)
```bash
export DB_URL="jdbc:postgresql://pg-xxx.l.aivencloud.com:19420/defaultdb?sslmode=require"
export DB_USERNAME="avnadmin"
export DB_PASSWORD="your_aiven_password"
export JWT_SECRET="your_secure_256_bit_secret_key_for_jwt_token_signing_here"
export PORT="8080"
export GEMINI_API_KEY="your_gemini_api_key"
```

---

## 7. Database Migration Instructions

Database schema migrations are placed in `backend/src/main/resources/db/migration/V1__initial_schema.sql`.

### Tables Created
1. `users`: Core account details (name, email, phone, role, password_hash, language, is_verified).
2. `artisan_profiles`: SHG details, artisan name, bio, location, district, state, experience, contact preference, approval status.
3. `skills`: Master taxonomy of handicraft skills (Pottery, Weaving, Embroidery, Woodcraft, etc.).
4. `artisan_skills`: Many-to-many relationship linking artisans with skills and proficiency.
5. `products`: Handcrafted product listings (price, quantity, status, views, AI description/caption/hashtags).
6. `product_images`: Multi-photo support per product with display ordering.
7. `enquiries`: Customer-to-artisan communication records with resolution statuses.
8. `notifications`: In-app event notifications for enquiries, status updates, and approvals.
9. `admin_actions`: Auditable log of administrative decisions (approvals, rejections).
10. `analytics_events`: Platform usage telemetry.

### Automatic Migration Execution
The Spring Boot backend automatically verifies and applies `V1__initial_schema.sql` at startup via `DatabaseMigrationService`:
```java
@Service
public class DatabaseMigrationService {
    @PostConstruct
    public void runMigrations() { ... }
}
```
If tables already exist, the migration utilizes `CREATE TABLE IF NOT EXISTS`, `ALTER TABLE ... ADD COLUMN IF NOT EXISTS`, and `ON CONFLICT DO NOTHING` statements to guarantee safe, idempotent execution.

---

## 8. Gemini Setup

1. **Obtain an API Key:**
   Visit [Google AI Studio](https://aistudio.google.com/) and generate a free API key.
2. **Configure the Backend:**
   Set the `GEMINI_API_KEY` environment variable.
3. **Architecture & Fault Tolerance:**
   - The key is **strictly stored on the backend** and never delivered to Android.
   - Android requests storytelling assistance via `POST /api/ai/product-content`.
   - The backend communicates with `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent`.
   - **Resilience:** Built-in connection timeouts (10s), read timeouts (25s), automatic retry logic, rate limit handling (HTTP 429 backoff), and an intelligent fallback generator for offline testing.

---

## 9. API Documentation

### Authentication Endpoints
* **`POST /api/auth/register`**
  - **Body:** `{ "name": "...", "email": "...", "phone": "...", "password": "...", "role": "ARTISAN|CUSTOMER", "shgName": "...", "craftType": "..." }`
  - *Note:* Registration as `NGO_ADMIN` is restricted and rejected with HTTP 400.
* **`POST /api/auth/login`**
  - **Body:** `{ "email": "...", "password": "..." }`
  - **Returns:** `{ "token": "...", "userId": 1, "name": "...", "email": "...", "role": "..." }`
* **`GET /api/auth/me`**
  - **Header:** `Authorization: Bearer <token>`
  - **Returns:** Current user profile.

### Artisan & Product Endpoints
* **`GET /api/artisans/profile`**: Get current artisan profile and completion %.
* **`PUT /api/artisans/profile`**: Update artisan bio, SHG name, location, and skills.
* **`GET /api/artisans/skills`**: Get master list of craft skills.
* **`POST /api/products`**: Add a new handcrafted product with photos.
* **`GET /api/products/my`**: List all products belonging to the logged-in artisan.
* **`GET /api/products/{id}`**: Product detail with photos, view count increment, and artisan attribution.
* **`PUT /api/products/{id}`**: Update product details (owner verified).
* **`DELETE /api/products/{id}`**: Delete product (owner verified).
* **`PUT /api/products/{id}/status`**: Toggle product status (`AVAILABLE`, `PUBLISHED`, `DRAFT`, `SOLD_OUT`).

### Customer & Marketplace Endpoints
* **`GET /api/products`**: Paginated exploration with filters: `category`, `craftType`, `state`, `district`, `minPrice`, `maxPrice`, `searchQuery`, `page`, `size`.
* **`GET /api/artisans`**: Public directory of approved artisans with skill filters.
* **`GET /api/artisans/{id}`**: Detailed public artisan profile with listed crafts.

### Enquiry System Endpoints
* **`POST /api/enquiries`**: Submit an enquiry to an artisan. Automatically triggers a notification for the artisan.
* **`GET /api/enquiries`**: Role-authorized enquiry list (Customers view their own, Artisans view their products', Admins view all).
* **`PUT /api/enquiries/{id}/status`**: Update enquiry status (`PENDING`, `CONTACTED`, `RESOLVED`, `CLOSED`). Automatically notifies the customer.

### NGO Admin Endpoints (`hasRole('NGO_ADMIN')`)
* **`GET /api/admin/dashboard`**: High-level platform statistics (total SHGs, artisans, products, enquiries, pending approvals).
* **`GET /api/admin/artisans`**: Full artisan directory with status filtering (`PENDING`, `APPROVED`, `REJECTED`).
* **`PUT /api/admin/artisans/{id}/approve`**: Approve artisan profile and publish catalog.
* **`PUT /api/admin/artisans/{id}/reject`**: Reject artisan profile with verification remark.
* **`GET /api/admin/products`**: Administrative product moderation.
* **`GET /api/admin/enquiries`**: Global enquiry monitoring.

### Analytics Endpoints (`hasRole('NGO_ADMIN')`)
* **`GET /api/admin/analytics/summary`**: Direct PostgreSQL calculations:
  - Products and artisans per district.
  - Products by craft category.
  - Enquiries by product and by artisan.
  - Products with low views (< 5 views).
  - Inactive artisans (0 listed products).
  - Districts with low participation (<= 2 artisans).
  - Skills with low representation.

---

## 10. Running Android App

### Method A: Android Studio
1. Open Android Studio and select **Open** -> choose `c:/Users/admin/Desktop/kalaconnect_new/android`.
2. Allow Gradle sync to complete.
3. Select an Android Virtual Device (AVD) or connect a physical Android device with USB debugging enabled.
4. Click **Run 'app'** (`Shift + F10`).

### Method B: Command Line (Gradle)
```bash
cd android
# Build debug APK
./gradlew assembleDebug

# Install on connected device or running emulator
./gradlew installDebug
```
The compiled APK will be located at:
`android/app/build/outputs/apk/debug/app-debug.apk`

---

## 11. Running Backend

### Method A: Maven Wrapper
1. Navigate to the `backend/` directory:
   ```bash
   cd backend
   ```
2. Export your environment variables (or place them in a `.env` file):
   ```bash
   export DB_URL="jdbc:postgresql://HOST:PORT/DATABASE?sslmode=require"
   export DB_USERNAME="avnadmin"
   export DB_PASSWORD="your_password"
   export JWT_SECRET="your_secure_256_bit_secret_key"
   ```
3. Run the Spring Boot application:
   ```bash
   ./mvnw spring-boot:run
   ```
   *(On Windows use `.\mvnw.cmd spring-boot:run`)*

### Method B: Executable JAR
```bash
cd backend
./mvnw clean package -DskipTests
java -jar target/kalaconnect-backend-0.0.1-SNAPSHOT.jar
```

### Health Verification
Verify that the service is running:
```bash
curl http://localhost:8080/api/health
curl http://localhost:8080/api/health/db
```

---

## 12. Troubleshooting

### 1. Database Connection Refused / SSL Error
* **Symptom:** `org.postgresql.util.PSQLException: Connection refused` or `SSL error: Received fatal alert: handshake_failure`.
* **Fix:**
  - Verify that `sslmode=require` is appended to the `DB_URL` query string.
  - Verify that your Aiven service is in `RUNNING` status in the Aiven Console.
  - Ensure your host network or firewall allows outbound traffic on the Aiven port (e.g., 19420).

### 2. Android Connection Refused (`java.net.ConnectException`)
* **Symptom:** Android app cannot connect to backend running locally.
* **Fix:**
  - When running in the Android Emulator, use `http://10.0.2.2:8080/` instead of `http://localhost:8080/`.
  - When testing on a physical device, connect both the computer and device to the same Wi-Fi network and use your computer's local IP address (e.g., `http://192.168.1.15:8080/`).
  - Verify `android:usesCleartextTraffic="true"` in `AndroidManifest.xml` for local HTTP development.

### 3. JWT Expiration / HTTP 401 Unauthorized
* **Symptom:** Requests fail with `401 Unauthorized`.
* **Behavior:** The `AuthInterceptor` automatically clears expired session tokens and redirects the user to `LoginActivity`.
* **Fix:** Log in again to obtain a fresh token. Token expiration duration is configurable in `application.properties` via `jwt.expiration-ms` (default: 24 hours).

### 4. Language Switching Not Changing Immediately
* **Symptom:** Switching language in dialog does not reflect on the current screen.
* **Behavior:** `LanguageSelectionDialogFragment` invokes `LocaleHelper.setLocale(requireContext(), langCode)` and calls `requireActivity().recreate()`.
* **Fix:** Ensure your custom Application class (`KalaConnectApp`) and each activity override `attachBaseContext(LocaleHelper.onAttach(newBase))`.

### 5. Gemini AI Generation Returns Fallback Content
* **Symptom:** AI content generates standard template text rather than customized output.
* **Fix:** Verify that `GEMINI_API_KEY` is exported in your environment and has quota enabled in Google AI Studio. Check backend logs for HTTP 403 or HTTP 429 status codes.
