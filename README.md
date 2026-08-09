# SB-Ecom — Spring Boot E-Commerce API

A RESTful e-commerce backend built with **Java 21** and **Spring Boot**, featuring product/category management, shopping cart, order placement, address book, and JWT-based authentication with role-based access control (User / Seller / Admin).

## ✨ Features

- **Authentication & Authorization** — JWT-based auth (cookie or Bearer header), roles: `ROLE_USER`, `ROLE_SELLER`, `ROLE_ADMIN`
- **Category Management** — CRUD APIs for product categories
- **Product Catalog** — Add, update, delete, search by category/keyword, paginated & sortable listing, product image upload
- **Shopping Cart** — Add/update/remove items, auto price sync when product price changes
- **Address Book** — Manage multiple shipping addresses per user
- **Order Placement** — Convert cart to order, capture payment details, auto-decrement stock
- **API Documentation** — Swagger / OpenAPI UI
- **Database Migrations** — Version-controlled schema via Flyway
- **Environment Profiles** — Separate `dev` and `prod` configurations

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.1.0 |
| Security | Spring Security + JWT (jjwt 0.12.6) |
| Persistence | Spring Data JPA / Hibernate |
| Database | PostgreSQL 16 |
| Migrations | Flyway |
| Mapping | ModelMapper |
| API Docs | springdoc-openapi (Swagger UI) |
| Build Tool | Maven (with Maven Wrapper) |
| Containerization | Docker Compose (Postgres) |

## 📋 Prerequisites

- JDK 21+
- Maven (or use the bundled `./mvnw`)
- Docker & Docker Compose (for local Postgres)

## 🚀 Getting Started

### 1. Clone the repository
```bash
git clone <your-repo-url>
cd sb-ecom
```

### 2. Start the database
```bash
docker-compose up -d
```
This spins up a Postgres 16 container on port `5432` with database `recom` / user `recom` / password `recom`.

### 3. Configure environment variables

The `dev` and `prod` profiles read the following variables:

| Variable | Description |
|---|---|
| `DB_URL` | JDBC base URL, e.g. `jdbc:postgresql://localhost:5432` |
| `DB_NAME` | Database name (e.g. `recom`) |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | Base64-encoded secret key for signing JWTs |
| `JWT_EXPIRATION` | Token expiry in milliseconds |
| `JWT_COOKIENAME` | Name of the JWT cookie |

### 4. Run the application
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```
The app starts on **port 9090** (dev profile) or **port 5000** (default/prod).

### 5. Explore the API
Once running, visit:
- Swagger UI: `http://localhost:9090/swagger-ui/index.html`
- OpenAPI spec: `http://localhost:9090/v3/api-docs`
- Health check: `http://localhost:9090/`

## 🔑 Default Seed Users

On first run, the app seeds these accounts (see `SecurityConfig`):

| Username | Password | Role(s) |
|---|---|---|
| `user1` | `password1` | USER |
| `seller1` | `password2` | SELLER |
| `admin` | `adminPass` | USER, SELLER, ADMIN |

## 📁 Key API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/signup` | Register a new user |
| POST | `/api/auth/signin` | Log in, returns JWT cookie |
| POST | `/api/auth/signout` | Log out |
| GET | `/api/public/catagory` | List categories |
| POST | `/api/admin/catagory` | Create category (admin) |
| GET | `/api/public/products` | List products |
| POST | `/api/admin/categories/{categoryId}/product` | Add product (admin) |
| POST | `/api/carts/products/{productId}/quantity/{quantity}` | Add to cart |
| GET | `/api/carts/users/cart` | Get current user's cart |
| POST | `/api/address` | Add address |
| POST | `/api/order/users/payments/{paymentMethod}` | Place an order |

## 🏗️ Project Structure