# Chat System

A modern, scalable chat application built with microservices architecture, featuring real-time messaging, WebSocket connections, and event-driven communication.

## 🏗️ Architecture Overview

This system is designed as a microservices architecture with three main components:

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  Socket Gateway │    │  Core Services  │    │ Fanout Worker   │
│   (WebSocket)   │◄──►│ (Business Logic)│◄──►│ (Event Handler) │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 ▼
    ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐
    │  MySQL  │  │  Redis  │  │  Kafka  │  │  MinIO  │
    │   DB    │  │ Cache   │  │ Events  │  │ Storage │
    └─────────┘  └─────────┘  └─────────┘  └─────────┘
```

### Components

#### 🌐 Socket Gateway (`socket_gateway`)
- **Port**: 8082 (WebSocket Server)
- **Technology**: Spring Boot 3.5.7 + WebSocket + STOMP
- **Purpose**: Real-time WebSocket gateway for client connections with Redis stream integration
- **Key Features**:
  - WebSocket + STOMP messaging with SockJS fallback
  - JWT authentication with anti-spoofing protection
  - Redis stream consumption for message delivery
  - Gateway node coordination for horizontal scaling
  - Virtual threads for efficient connection handling

📖 **[Detailed Documentation](socket_gateway/README.md)** - Comprehensive guide covering WebSocket configuration, authentication pipeline, Redis streams, and real-time message delivery.

#### ⚙️ Core Services (`core-services`)
- **Port**: 8081 (gRPC Server + REST API)
- **Technology**: Spring Boot 3.5.7 + JPA + gRPC + Debezium CDC
- **Purpose**: Central business logic hub with authentication, messaging, and connection management
- **Key Features**:
  - JWT authentication with RSA signing and Redis caching
  - Message processing with Outbox pattern and Debezium CDC
  - Connection management with Redis state tracking
  - gRPC services for inter-service communication
  - Virtual thread executors for performance optimization

📖 **[Detailed Documentation](core-services/README.md)** - In-depth coverage of authentication modules, message processing, Outbox pattern, Debezium configuration, and gRPC services.

#### 📡 Fanout Worker (`fanout_worker`)
- **Port**: 8083 (Kafka Consumer Service)
- **Technology**: Spring Boot 3.5.7 + Kafka + Redis Streams + Virtual Threads
- **Purpose**: Message distribution engine with sophisticated routing and connection state management
- **Key Features**:
  - Kafka consumers with partition-based virtual thread executors
  - Redis stream publishing with gateway-specific routing
  - Connection state management with ephemeral data handling
  - High-performance message distribution architecture
  - Advanced error handling and monitoring capabilities

📖 **[Detailed Documentation](fanout_worker/README.md)** - Complete guide to message distribution, virtual thread architecture, Redis streaming, and connection state management.

## 🛠️ Technology Stack

### Backend
- **Java 21** - Latest LTS version
- **Spring Boot 3.5.7** - Framework
- **Spring Security** - Authentication & Authorization
- **Spring Data JPA** - Data persistence
- **gRPC** - Inter-service communication
- **Protocol Buffers** - Message serialization
- **MapStruct** - Object mapping
- **Lombok** - Code generation

### Infrastructure
- **MySQL 8.0** - Primary database
- **Redis** - Caching and session storage
- **Apache Kafka** - Event streaming
- **MinIO** - Object storage (S3-compatible)
- **Elasticsearch** - Search and analytics
- **Nginx** - Load balancing and reverse proxy
- **Docker & Docker Compose** - Containerization

### Build & Tools
- **Maven** - Dependency management
- **Protobuf Maven Plugin** - Protocol buffer compilation
- **Debezium** - Change data capture

## 📊 Database Schema

### Core Tables
- **users** - User information
- **conversations** - Chat channels, DMs, group chats
- **conversation_members** - User membership in conversations
- **messages** - Chat messages with threading support
- **message_versions** - Edit history
- **message_reactions** - Emoji reactions
- **message_pins** - Pinned messages
- **message_attachments** - File attachments
- **message_acks** - Delivery/read receipts
- **message_producer_outbox** - Outbox pattern for reliable event publishing

## 🚀 Quick Start

### Prerequisites
- **Docker & Docker Compose**
- **Java 21** (for local development)
- **Maven** (for building)

### Running the Application

1. **Clone the repository**
```bash
git clone <repository-url>
cd chat-sys
```

2. **Start infrastructure services**
```bash
docker-compose up -d mysql redis kafka minio elasticsearch nginx
```

3. **Build and run services**

**Core Services:**
```bash
cd core-services
./mvnw spring-boot:run
```

**Socket Gateway:**
```bash
cd socket_gateway
./mvnw spring-boot:run
```

**Fanout Worker:**
```bash
cd fanout_worker
./mvnw spring-boot:run
```

### Environment Configuration

The application uses YAML configuration files:
- `db.yaml` - Database and Redis configuration
- `grpc.yaml` - gRPC service configuration  
- `kafka.yaml` - Kafka configuration
- `jwt.yaml` - JWT authentication settings

## 📡 API Endpoints

### gRPC Services

#### AuthService
- `VerifyAccessToken` - Token validation
- `Hello` - Health check

#### MessageService  
- `CreateMessage` - Send new messages
- `StreamCreateMessages` - Bi-directional message streaming
- `StreamAckOffsets` - Message acknowledgment streaming

#### ConnectionManager
- `RegisterConnection` - Register WebSocket connections
- `UnregisterConnection` - Clean up connections
- `UpdateLastPing` - Update connection heartbeat
- `GetConnectionsByUser` - Get user's active connections
- `GetOnlineUsers` - Get online users in workspace

## 🔄 Event-Driven Architecture

### Kafka Topics
The system uses Apache Kafka for event-driven communication:
- **Message Events** - New messages, edits, deletions
- **User Events** - User joins, leaves, status changes
- **Connection Events** - Connection state changes

### Outbox Pattern
Core Services implements the outbox pattern using Debezium for reliable event publishing:
1. Business operations and outbox entries are saved in the same transaction
2. Debezium captures database changes
3. Events are reliably published to Kafka
4. Workers process events asynchronously

## 🏃‍♂️ Development

### Building the Project
```bash
# Build all services
./mvnw clean package

# Generate Protocol Buffer classes
./mvnw protobuf:generate
```

### Running Tests
```bash
./mvnw test
```

### Database Setup
Execute the SQL scripts:
```bash
# Main database schema
mysql -u root -p < core-services/db.sql

# Outbox tables
mysql -u root -p < core-services/outbox.sql
```

## 🔧 Configuration

### Application Ports
- **Core Services**: 8081 (gRPC + REST)
- **Socket Gateway**: 8082 (WebSocket)
- **Fanout Worker**: 8083 (Kafka Consumer)

### Infrastructure Ports
- **MySQL**: 3306
- **Redis**: 6379
- **Kafka**: 9092
- **Kafka UI**: 8080
- **MinIO Console**: 9001
- **Elasticsearch**: 9200
- **Nginx**: 80

### Environment Variables
Key configuration can be overridden via environment variables:
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_DATA_REDIS_HOST`
- `SPRING_KAFKA_BOOTSTRAP_SERVERS`

## 🔒 Security

- **JWT Authentication** - Token-based authentication
- **Spring Security** - Authorization and access control
- **OAuth2 Authorization Server** - Centralized authentication
- **Secure WebSocket connections** - Token-based WebSocket authentication

## 📈 Monitoring

### Available Dashboards
- **Kafka UI** - http://localhost:8080 (Kafka management)
- **MinIO Console** - http://localhost:9001 (Object storage)
- **Elasticsearch** - http://localhost:9200 (Search queries)

### Health Checks
Each service exposes Spring Boot Actuator endpoints for health monitoring.

## 🧪 Message Types

The system supports various message types:
- `MESSAGE` - Regular text messages
- `TYPING` - Typing indicators
- `REACTION_ADDED` - Emoji reactions
- `USER_JOINED` - User join notifications
- `USER_LEFT` - User leave notifications

## 🔧 Troubleshooting

### Common Issues

1. **Port conflicts**: Ensure ports 3306, 6379, 9092, etc. are available
2. **Database connection**: Check MySQL credentials and connectivity
3. **Kafka startup**: Allow time for Kafka cluster formation
4. **gRPC connection issues**: Verify service discovery and network connectivity

### Logs
Check application logs for detailed error information:
```bash
# Core Services logs
cd core-services && ./mvnw spring-boot:run

# Socket Gateway logs  
cd socket_gateway && ./mvnw spring-boot:run

# Fanout Worker logs
cd fanout_worker && ./mvnw spring-boot:run
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [gRPC Java](https://grpc.io/docs/languages/java/)
- [Apache Kafka](https://kafka.apache.org/documentation/)
- [Protocol Buffers](https://developers.google.com/protocol-buffers)
- [Docker Compose](https://docs.docker.com/compose/)