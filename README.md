# Japee Microservices E-Commerce Platform

**Japee** is an **E-Commerce system** built with a **Microservices architecture**.  
Each service is responsible for a specific business domain and communicates with others via **REST API** and **Kafka (event-driven)**.

**Project goals**:
- Manage users, products, carts, orders, reviews, and notifications in independent services.
- Scalable and maintainable design, easy to deploy in a distributed environment.
- Demonstrate a real-world microservices architecture using **Spring Boot**, **Spring Security**, **Kafka**, **MongoDB/MySQL**, and **Docker**.

## Table of Contents

- [TechStack](#techstack)
- [Features](#features)
- [Installation](#installation)
  
## TechStack
- **Spring Boot 3.x**
- **Spring Cloud** (Gateway, OpenFeign)
- **Spring Security + JWT**
- **MongoDB / MySQL** 
- **Kafka** (event-driven communication)
- **Docker & Docker Compose**
- **Lombok**, **MapStruct**, **Feign Client**

## Features
### 1. API Gateway
- Routes requests to appropriate services.
- Handles security (JWT filter).

### 2. Identity Service
- User registration & login.
- Role-based access control with JWT.

### 3. Product Service
- Product CRUD operations.
- Stock management with optimistic/pessimistic locking.
- Atomic stock updates to prevent overselling.

### 4. Order Service
- Create and manage orders.
- Transaction boundary: save order + reserve product stock.
- Saga pattern: rollback stock if order creation fails.
- Publishes events to Kafka for notifications.

### 5. Cart Service
- Manage shopping cart per user.
- Add/remove/update product quantities in cart.

### 6. Profile Service
- Store and manage user profile data.
- Works alongside Identity Service.

### 7. Review Service
- Users can review and rate products.
- Calculates average ratings.

### 8. Notification Service
- Consumes Kafka events.
- Sends email notifications (order placed, canceled, etc.).
 

## Installation
### Prerequisites
- **Java 17+**
- **Maven**
- **Docker & Docker Compose**
- **Kafka + Zookeeper**
- **MongoDB / MySQL**
### Build & Run
1. Clone the repository:
```
    git clone https://github.com/jungboixitinh/japee.git

    cd japee
```
2. Compile the source code:
```bash
javac src/*.java
```
3. Start infrastructure + services using Docker Compose:
```
docker-compose up -d
```
