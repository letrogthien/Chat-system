# Core Services

The **Core Services** module is the central business logic hub of the chat system, providing authentication, message processing, connection management, and event-driven architecture through gRPC services and REST APIs.

## 🏗️ Architecture Overview

Core Services acts as the backbone of the chat system with the following key responsibilities:
- **Authentication & Authorization** - JWT token management and user verification
- **Message Processing** - CRUD operations for chat messages with transaction safety
- **Connection Management** - WebSocket connection lifecycle management  
- **Event Publishing** - Reliable event streaming via Kafka using the Outbox pattern
- **Data Persistence** - MySQL database operations with JPA/Hibernate

```
┌─────────────────────────────────────────────────────────────┐
│                    Core Services (Port 8081)               │
├─────────────────┬─────────────────┬─────────────────────────┤
│   Auth Module   │ Message Module  │ Connection Management   │
│   (JWT/OAuth2)  │ (Chat Messages) │   (WebSocket State)     │
└─────────────────┴─────────────────┴─────────────────────────┘
         │                 │                       │
         ▼                 ▼                       ▼
    ┌─────────┐    ┌──────────────┐        ┌─────────────┐
    │   JWT   │    │   Outbox     │        │   Redis     │
    │ Tokens  │    │  Pattern     │        │ Connection  │
    │         │    │ (Debezium)   │        │   Cache     │
    └─────────┘    └──────────────┘        └─────────────┘
                           │
                           ▼
                    ┌─────────────┐
                    │    Kafka    │
                    │ Event Bus   │
                    └─────────────┘
```

## 🛠️ Technical Stack

### Core Technologies
- **Spring Boot 3.5.7** - Application framework
- **Java 21** - Programming language with virtual threads
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data persistence layer
- **Spring gRPC** - Inter-service communication
- **MySQL 8.0** - Primary database
- **Redis** - Caching and connection state

### Event-Driven Architecture
- **Apache Kafka** - Message streaming
- **Debezium** - Change Data Capture (CDC)
- **Outbox Pattern** - Reliable event publishing
- **Protocol Buffers** - Message serialization

### Additional Tools
- **MapStruct** - Object mapping
- **Lombok** - Code generation
- **Jackson** - JSON processing
- **JWT (JOSE)** - Token handling

## 📁 Module Structure

### 🔐 Auth Module (`auth_module`)
Handles authentication and authorization for the entire system.

**Key Components:**
- `AuthController` - REST endpoints for login/authentication
- `AuthGrpcService` - gRPC service for token verification
- `AuthService` - Business logic for authentication
- `JwtTokenFactory` - JWT token creation and validation
- `User`, `UserBase` - User entities and models

**Features:**
- JWT token generation and validation
- OAuth2 authorization server integration
- User management and authentication
- Token verification for other services

### 💬 Message Module (`message_module`)
Core messaging functionality with comprehensive features.

**Key Components:**
- `MessageService` - gRPC service for message operations
- `MessageServiceHelper` - Business logic helper
- `Message`, `MessageVersion`, `MessageReaction` - Core entities
- `MessageProducerOutbox` - Outbox pattern implementation
- `DebeziumConfig` - Change data capture configuration
- `OutboxFallBackSchedule` - Fallback processing for failed events

**Advanced Features:**
- **Message Versioning** - Edit history tracking
- **Reactions & Pins** - Interactive message features
- **Thread Support** - Nested conversation support
- **Attachment Handling** - File upload support
- **Delivery Tracking** - Read receipts and acknowledgments
- **Reliable Event Publishing** - Outbox pattern with Debezium CDC

### 🔌 Connection Management (`connection_management`)
Manages WebSocket connection lifecycle and state.

**Key Components:**
- `ConnectionManager` - gRPC service for connection operations
- `ConnectionRepository` - Redis-based connection storage
- `Connection` - Connection entity model

**Features:**
- Connection registration/deregistration
- Heartbeat/ping management
- Online user tracking
- Gateway node association
- Connection state persistence in Redis

## 🌐 API Endpoints

### REST APIs

#### Authentication Endpoints
```http
POST /auth/login
Content-Type: application/json

{
  "username": "string",
  "password": "string"
}
```

```http
GET /auth/jwt      # Test token generation (user 1)
GET /auth/jwt/1    # Test token generation (user 2)
```

### gRPC Services

#### AuthService (Port 9090)
```protobuf
service AuthService {
  rpc VerifyAccessToken (VerifyRequest) returns (VerifyResponse);
  rpc Hello (HelloRequest) returns (HelloResponse);
}
```

**Usage:**
- Token validation for WebSocket connections
- Inter-service authentication
- User identity verification

#### MessageService (Port 9090) 
```protobuf
service MessageService {
  rpc CreateMessage (CreateMessageRequest) returns (CreateMessageResponse);
  rpc StreamCreateMessages (stream CreateMessageRequest) returns (stream CreateMessageResponse);
  rpc StreamAckOffsets (stream AckOffset) returns (stream AckOffset);
}
```

**Features:**
- Bi-directional message streaming
- Real-time message acknowledgments
- Message persistence with sequencing
- Event publishing to Kafka

#### ConnectionManager (Port 9090)
```protobuf
service ConnectionManager {
  rpc RegisterConnection (RegisterConnectionRequest) returns (RegisterConnectionResponse);
  rpc UnregisterConnection (UnregisterConnectionRequest) returns (UnregisterConnectionResponse);
  rpc UpdateLastPing (UpdateLastPingRequest) returns (UpdateLastPingResponse);
  rpc GetConnectionsByUser (GetConnectionsByUserRequest) returns (GetConnectionsByUserResponse);
  rpc GetOnlineUsers (GetOnlineUsersRequest) returns (GetOnlineUsersResponse);
  rpc GetConnectionsByGatewayNode (GetConnectionsByGatewayNodeRequest) returns (GetConnectionsByGatewayNodeResponse);
  rpc GetAllConnections (GetAllConnectionsRequest) returns (GetAllConnectionsResponse);
}
```

## 🔄 Event-Driven Architecture

### Outbox Pattern Implementation

The system implements the **Transactional Outbox Pattern** for reliable event publishing:

1. **Transaction Safety**: Business operations and outbox entries are saved in the same database transaction
2. **Change Data Capture**: Debezium monitors the `message_producer_outbox` table
3. **Event Publishing**: Changes are automatically published to Kafka
4. **Fallback Mechanism**: Failed events are stored in Redis for retry processing

#### Key Components:
- `MessageProducerOutbox` - Outbox table entity
- `DebeziumConfig` - CDC configuration and event handling
- `OutboxFallBackSchedule` - Redis-based fallback processing
- `SendEventService` - Kafka event publishing service

### Kafka Topics
- `message.all` - Message events for fanout processing
- `outbox.event.raw` - Raw outbox events for processing
- `connection.add` - Connection registration events
- `connection.remove` - Connection cleanup events

### Event Flow
```
[Message Creation] → [Database + Outbox] → [Debezium CDC] → [Kafka] → [Fanout Workers]
                                     ↓
                           [Redis Fallback on Kafka Failure]
```

## 🗄️ Database Schema

### Core Tables
- **`users`** - User accounts and profiles
- **`conversations`** - Chat rooms, DMs, group chats
- **`conversation_members`** - User membership in conversations
- **`messages`** - Chat messages with full metadata
- **`message_versions`** - Edit history for message changes
- **`message_reactions`** - Emoji reactions on messages
- **`message_pins`** - Pinned messages in conversations
- **`message_attachments`** - File attachments metadata
- **`message_acks`** - Delivery and read receipt tracking
- **`message_producer_outbox`** - Outbox pattern for reliable events

### Key Features
- **UUID Primary Keys** - Distributed system friendly
- **Sequence Numbers** - Message ordering per conversation
- **Audit Trails** - Created/updated timestamps
- **Soft Deletes** - Message deletion without data loss
- **Thread Support** - Nested conversation structure

## ⚙️ Configuration

### Application Properties
```properties
spring.application.name=core-services
spring.config.import=jwt.yaml, grpc.yaml, db.yaml, kafka.yaml
server.port=8081

# Outbox Fallback Processing Configuration
outbox.fallback.batch-size=100
outbox.fallback.max-concurrent=10
```

### Database Configuration (`db.yaml`)
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
  datasource:
    url: jdbc:mysql://localhost:3306/slack_clone
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  data:
    redis:
      host: localhost
      port: 6379
```

### gRPC Configuration (`grpc.yaml`)
```yaml
spring:
  grpc:
    server:
      port: 9090
      security:
        enabled: false
```

### JWT Configuration (`jwt.yaml`)
- RSA key configuration for token signing
- Token expiration settings
- Security parameters

### Kafka Configuration (`kafka.yaml`)
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
    consumer:
      group-id: user-service
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
```

## 🚀 Getting Started

### Prerequisites
- Java 21 or higher
- MySQL 8.0
- Redis 6.0+
- Apache Kafka 2.8+

### Running the Service

1. **Setup Database**
```bash
mysql -u root -p < db.sql
mysql -u root -p < outbox.sql
```

2. **Start Dependencies**
```bash
# Start MySQL, Redis, Kafka (via Docker Compose from project root)
docker-compose up -d mysql redis kafka
```

3. **Run the Application**
```bash
./mvnw spring-boot:run
```

The service will start on:
- **HTTP Server**: `http://localhost:8081`
- **gRPC Server**: `localhost:9090`

### Development Commands

```bash
# Generate Protocol Buffer classes
./mvnw protobuf:generate

# Run tests
./mvnw test

# Build JAR
./mvnw clean package
```

## 🔧 Advanced Features

### Virtual Threads Support
The application leverages Java 21 virtual threads for improved concurrency:
- Debezium event processing uses virtual thread executor
- High-throughput message processing
- Reduced resource consumption

### Connection State Management
- **Redis-based Storage**: Connection state persisted in Redis
- **Gateway Association**: Connections linked to specific gateway nodes
- **Automatic Cleanup**: Stale connection cleanup mechanisms
- **Online Presence**: Real-time user presence tracking

### Message Sequencing
- **Per-Conversation Sequences**: Each conversation maintains its own message sequence
- **Atomic Operations**: Sequence generation and message creation in single transaction
- **Ordering Guarantees**: Messages delivered in correct order

### Security Implementation
- **Spring Security Integration**: Comprehensive security configuration
- **JWT Token Validation**: Secure token-based authentication
- **gRPC Security**: Configurable security for inter-service communication
- **CORS Configuration**: Cross-origin request handling

## 📊 Monitoring & Observability

### Health Checks
- Spring Boot Actuator endpoints
- Database connectivity monitoring
- Kafka producer health
- Redis connection status

### Logging
- Structured logging with SLF4J
- Debezium event processing logs
- gRPC operation logs
- Error handling and fallback logs

## 🧪 Testing

### Test Structure
- Unit tests for business logic
- Integration tests for database operations
- gRPC service tests
- Outbox pattern testing

### Test Configuration
- Testcontainers for integration testing
- Mock configurations for unit tests
- Test profiles for different environments

## 🔍 Troubleshooting

### Common Issues

1. **Debezium Connection Issues**
   - Check MySQL binary log configuration
   - Verify database permissions
   - Monitor offset storage files

2. **Kafka Publishing Failures**
   - Check Kafka broker connectivity
   - Verify topic existence
   - Monitor Redis fallback queue

3. **gRPC Service Errors**
   - Verify port availability (9090)
   - Check service registration
   - Monitor connection timeouts

### Debug Commands
```bash
# Check database connections
./mvnw spring-boot:run --debug

# Monitor Kafka topics
kafka-console-consumer --bootstrap-server localhost:9092 --topic message.all

# Check Redis connections
redis-cli monitor
```

## 📈 Performance Considerations

- **Database Connection Pooling**: Configured for optimal performance
- **Virtual Threads**: Improved concurrency handling
- **Batch Processing**: Outbox fallback processing in batches
- **Caching Strategy**: Redis for frequently accessed data
- **Event Streaming**: Asynchronous processing with Kafka

## 🤝 Contributing

1. Follow the established module structure
2. Add proper tests for new features
3. Update Protocol Buffer definitions when needed
4. Ensure database migrations are included
5. Document gRPC service changes

---

This Core Services module provides the foundation for the entire chat system, ensuring reliable message delivery, secure authentication, and scalable event processing.