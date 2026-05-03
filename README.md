# Webhook Microservices — Google OAuth2

Monolith → Microservices migration. Keycloak removed. Google OAuth2 replaces it entirely.

---

## Architecture

```
Client
  │
  ▼
API Gateway (8080)  ──── Google Auth Server (JWT verification)
  │
  ├──── /users/**  ──────► User Service (8081) ──► MySQL: webhook_users
  │
  └──── /api/**   ──────► Webhook Service (8082) ──► MySQL: webhook_data
                               │
                               └── OpenFeign ──► User Service (internal)

Infrastructure:
  Eureka Server (8761) — service discovery
  Config Server (8888) — centralised config
```

---

## Step 1 — Google Cloud Console Setup

1. Go to https://console.cloud.google.com
2. Create a new project (e.g. `webhook-app`)
3. Navigate to **APIs & Services → OAuth consent screen**
   - User type: External
   - App name: Webhook App
   - Add your email as test user
4. Navigate to **APIs & Services → Credentials → Create Credentials → OAuth 2.0 Client ID**
   - Application type: Web application
   - Authorised redirect URIs: `http://localhost:4200/callback` (your frontend)
5. Copy your **Client ID** and **Client Secret**

---

## Step 2 — Configure Your Client ID

Open `config-server/src/main/resources/configs/user-service.yml` and replace:
```yaml
google:
  client-id: YOUR_GOOGLE_CLIENT_ID   # ← paste here
```

Do the same in `configs/webhook-service.yml`.

---

## Step 3 — How Google OAuth2 Works (No Keycloak!)

**Before (Keycloak):**
```
Client → Keycloak (port 8180) → get JWT → call backend
Backend → validates JWT against Keycloak's JWK URI
```

**After (Google):**
```
Client → Google (accounts.google.com) → get JWT (id_token)
Client → sends JWT in Authorization: Bearer <token> header
Backend → validates JWT against Google's JWK URI automatically:
          https://www.googleapis.com/oauth2/v3/certs
```

Your frontend needs to use **Google Identity Services** library to get the `id_token`:
```javascript
// In your Angular frontend
google.accounts.id.initialize({
  client_id: 'YOUR_GOOGLE_CLIENT_ID',
  callback: (response) => {
    const idToken = response.credential; // ← send this as Bearer token
    // Store and use in every API call:
    // Authorization: Bearer <idToken>
  }
});
```

---

## Step 4 — Run With Docker Compose (Recommended)

```bash
cd webhook-microservices
docker-compose up --build
```

Services start in this order automatically:
1. MySQL DBs (webhook_users + webhook_data)
2. Eureka Server (8761)
3. Config Server (8888)
4. API Gateway (8080)
5. User Service (8081)
6. Webhook Service (8082)

---

## Step 5 — Run Locally Without Docker

Start in this exact order (wait for each to fully start):

```bash
# Terminal 1 — Eureka
cd eureka-server && mvn spring-boot:run

# Terminal 2 — Config Server
cd config-server && mvn spring-boot:run

# Terminal 3 — API Gateway
cd api-gateway && mvn spring-boot:run

# Terminal 4 — User Service
cd user-service && mvn spring-boot:run

# Terminal 5 — Webhook Service
cd webhook-service && mvn spring-boot:run
```

Create these two MySQL databases first:
```sql
CREATE DATABASE webhook_users;
CREATE DATABASE webhook_data;
```

---

## API Reference

All requests go through the **API Gateway on port 8080**.
Every request needs: `Authorization: Bearer <google_id_token>`

### User Service

| Method | URL | Description |
|--------|-----|-------------|
| POST | `/users` | Auto-register from Google JWT (call on first login) |
| GET | `/users/me` | Get current user profile |

### Webhook Service — Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/endpoints` | Create a new webhook endpoint |
| GET | `/api/endpoints` | List all your endpoints (paginated) |
| GET | `/api/endpoints/{id}` | Get endpoint by ID |
| GET | `/api/endpoints/name/{name}` | Get endpoint by name |
| DELETE | `/api/endpoints/{id}` | Delete endpoint + all its requests |

### Webhook Service — Incoming Requests

| Method | URL | Description |
|--------|-----|-------------|
| ANY | `/api/{endpointName}` | Send any HTTP request to your webhook |
| GET | `/api/{endpointName}/requests` | List all captured requests |
| GET | `/api/{endpointName}/requests/{id}` | Get single captured request |
| DELETE | `/api/{endpointName}/requests` | Delete all requests for endpoint |
| DELETE | `/api/{endpointName}/requests/{id}` | Delete single request |

---

## Key Changes From Monolith

| What changed | Old (Monolith) | New (Microservices) |
|---|---|---|
| Auth | Keycloak on port 8180 | Google OAuth2 (no server needed) |
| Role extraction | `KeyCloakRoleConverter` (realm_access.roles) | Google JWT email_verified claim |
| JWK URI | `localhost:8180/realms/.../certs` | `googleapis.com/oauth2/v3/certs` |
| User FK in Endpoint | `@ManyToOne UserEntity` | `String userEmail` column |
| Database | Single `webhook` DB | `webhook_users` + `webhook_data` |
| Service discovery | None | Eureka Server |
| Config | Single application.yml | Config Server (per-service YMLs) |
| Inter-service calls | Direct method calls | OpenFeign (User Service client) |

---

## Eureka Dashboard

Visit http://localhost:8761 to see all registered services.

---

## Troubleshooting

**Google JWT expired?** — Google id_tokens expire in 1 hour. Your frontend must refresh them.

**401 Unauthorized?** — Make sure you're sending `Authorization: Bearer <id_token>` not `access_token`.

**User not found in webhook-service?** — Call `POST /users` first after Google login to register.

**Feign connection refused?** — Make sure user-service is running and registered with Eureka before webhook-service starts.
