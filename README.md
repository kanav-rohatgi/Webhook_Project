# 🪝 Webhooks

> A production-style Spring Boot microservices platform for managing programmable webhook endpoints — secured with Google OAuth2, orchestrated via Eureka, and fully containerised with Docker Compose.

![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.3-6DB33F?logo=springboot&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-2023.0.3-6DB33F?logo=spring&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)
![Auth](https://img.shields.io/badge/Auth-Google_OAuth2-4285F4?logo=google&logoColor=white)

---

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Services](#services)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Google OAuth2 Setup](#google-oauth2-setup)
  - [Run with Docker Compose](#run-with-docker-compose)
  - [Run Locally](#run-locally)
- [API Reference](#api-reference)
- [Frontend Integration](#frontend-integration)

---

## Overview

This project migrates a monolithic webhook inspection tool into a full microservices architecture. Users can create named webhook endpoints, forward any HTTP request to them, and inspect every captured request — all secured behind Google JWT authentication with no additional auth server required.

**Key highlights:**
- Google OAuth2 replaces Keycloak — no external identity server to run
- JWT validation happens directly against Google's public JWK URI
- Services discover each other via Eureka; config is managed centrally
- OpenFeign handles typed HTTP calls between services
- One Docker Compose command spins up the entire stack

---

## Architecture

```
Client
  │
  │   Authorization: Bearer <google_id_token>
  ▼
API Gateway  (port 8080)  ──── Google JWK URI (JWT validation)
  │
  ├── /users/**  ──────────► User Service   (8081) ──► MySQL: webhook_users
  │
  └── /api/**   ──────────► Webhook Service (8082) ──► MySQL: webhook_data
                                  │
                                  └── OpenFeign ──► User Service (internal)

Infrastructure
  ├── Eureka Server  (8761)  — service discovery & health dashboard
  └── Config Server  (8888)  — centralised per-service configuration
```

---

## Services

| Service | Port | Responsibility |
|---|---|---|
| **API Gateway** | 8080 | Single entry point, JWT validation, request routing |
| **User Service** | 8081 | User registration & profile from Google JWT claims |
| **Webhook Service** | 8082 | Endpoint management, request capture & retrieval |
| **Eureka Server** | 8761 | Service registry & health dashboard |
| **Config Server** | 8888 | Centralised YAML config (native classpath) |

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.3.3 |
| Cloud | Spring Cloud 2023.0.3 (Eureka, Config, OpenFeign, Gateway) |
| Security | Spring Security · OAuth2 Resource Server · Google JWT |
| Persistence | Spring Data JPA · MySQL 8 |
| Mapping | ModelMapper · Lombok |
| Containers | Docker · Docker Compose |
| Build | Maven |

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker & Docker Compose (for the containerised path)
- A Google Cloud account (free tier is fine)

---

### Google OAuth2 Setup

**Step 1 — Create OAuth credentials**

1. Go to [Google Cloud Console](https://console.cloud.google.com) and create a project (e.g. `webhook-app`).
2. Navigate to **APIs & Services → OAuth consent screen**
   - User type: External
   - App name: Webhook App
   - Add your email as a test user
3. Navigate to **APIs & Services → Credentials → Create Credentials → OAuth 2.0 Client ID**
   - Application type: Web application
   - Authorised redirect URI: `http://localhost:4200/callback`
4. Copy the generated **Client ID** and **Client Secret**.

**Step 2 — Add your Client ID to the config**

Open `config-server/src/main/resources/configs/user-service.yml` and set:

```yaml
google:
  client-id: YOUR_GOOGLE_CLIENT_ID
```

Repeat in `configs/webhook-service.yml`.

> The services validate incoming Google `id_token` JWTs against Google's public key endpoint (`https://www.googleapis.com/oauth2/v3/certs`) automatically — no Keycloak or custom auth server needed.

---

### Run with Docker Compose

```bash
git clone https://github.com/your-username/webhook-microservices.git
cd webhook-microservices
docker-compose up --build
```

Docker Compose starts services in dependency order:

1. MySQL (webhook_users on 3307, webhook_data on 3308)
2. Eureka Server (8761)
3. Config Server (8888)
4. API Gateway (8080)
5. User Service (8081)
6. Webhook Service (8082)

All health checks are configured — each service waits for its dependencies before starting.

---

### Run Locally

**Create the databases first:**

```sql
CREATE DATABASE webhook_users;
CREATE DATABASE webhook_data;
```

**Start each service in order (separate terminals):**

```bash
# Terminal 1 — Service Discovery
cd eureka-server && mvn spring-boot:run

# Terminal 2 — Centralised Config
cd config-server && mvn spring-boot:run

# Terminal 3 — API Gateway
cd api-gateway && mvn spring-boot:run

# Terminal 4 — User Service
cd user-service && mvn spring-boot:run

# Terminal 5 — Webhook Service
cd webhook-service && mvn spring-boot:run
```

Wait for each service to fully register with Eureka before starting the next.

---

## API Reference

All requests are routed through the **API Gateway on port 8080**.  
Every request must include: `Authorization: Bearer <google_id_token>`

### User Service — `/users`

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/users` | Auto-register user from Google JWT (call once after first login) |
| `GET` | `/users/me` | Get the current user's profile |

### Webhook Service — Endpoints `/api/endpoints`

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/endpoints` | Create a new webhook endpoint |
| `GET` | `/api/endpoints` | List all your endpoints (paginated) |
| `GET` | `/api/endpoints/{id}` | Get endpoint by ID |
| `GET` | `/api/endpoints/name/{name}` | Get endpoint by name |
| `DELETE` | `/api/endpoints/{id}` | Delete endpoint and all its captured requests |

### Webhook Service — Incoming Requests `/api/{endpointName}`

| Method | Endpoint | Description |
|---|---|---|
| `ANY` | `/api/{endpointName}` | Send any HTTP request to capture it |
| `GET` | `/api/{endpointName}/requests` | List all captured requests for the endpoint |
| `GET` | `/api/{endpointName}/requests/{id}` | Get a single captured request |
| `DELETE` | `/api/{endpointName}/requests` | Delete all captured requests |
| `DELETE` | `/api/{endpointName}/requests/{id}` | Delete a single captured request |

---

## Frontend Integration

Your frontend needs the [Google Identity Services](https://developers.google.com/identity/gsi/web) library to obtain a Google `id_token` and send it as a Bearer token on every request.

```javascript
// Angular / any JS framework
google.accounts.id.initialize({
  client_id: 'YOUR_GOOGLE_CLIENT_ID',
  callback: (response) => {
    const idToken = response.credential;
    // Store and attach to every API call:
    // Authorization: Bearer <idToken>
  }
});
```

> **Note:** Google `id_token`s expire after 1 hour. Your frontend must handle silent refresh or re-authentication.

## Eureka Dashboard

Once the stack is running, visit **http://localhost:8761** to see all registered services and their health status.
