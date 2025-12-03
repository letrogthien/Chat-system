# Fanout Worker

The **Fanout Worker** is a specialized microservice responsible for distributing messages to multiple recipients in real-time. It acts as the message distribution engine of the chat system, consuming events from Kafka and efficiently routing them to appropriate gateway nodes via Redis streams.

## 🏗️ Architecture Overview

The Fanout Worker implements a high-performance message distribution pattern:

```
┌─────────────────────────────────────────────────────────────┐
│                  Fanout Worker (Port 8083)                 │
├─────────────────┬─────────────────┬─────────────────────────┤
│ Kafka Consumers │ Redis Services  │   Connection Manager    │
│   (Message +    │  (Streams +     │   (State Tracking)      │
│  Connection)    │   Cache)        │                         │
└─────────────────┴─────────────────┴─────────────────────────┘
         ▲                 │                       ▲
         │                 ▼                       │
    ┌─────────┐    ┌──────────────┐        ┌─────────────┐
    │  Kafka  │    │ Redis Streams│        │    gRPC     │
    │ Events  │    │ (Per Gateway)│        │Core Services│
    │         │    │              │        │             │
    └─────────┘    └──────────────┘        └─────────────┘
                           │
                           ▼
                 ┌─────────────────┐
                 │ Socket Gateways │
                 │ (Real-time WS)  │
                 └─────────────────┘
```

## 🛠️ Technical Stack

### Core Technologies
- **Spring Boot 3.5.7** - Application framework
- **Java 21** - Programming language with virtual threads
- **Apache Kafka** - Event consumption
- **Redis** - Connection state + message streaming
- **Spring gRPC Client** - Inter-service communication
- **Protocol Buffers** - Message serialization

### Concurrency & Performance
- **Virtual Threads** - Java 21 lightweight threading
- **Partition-based Executors** - Kafka partition isolation
- **Redis Streams** - High-throughput message distribution
- **Connection Pooling** - Redis Lettuce configuration

## 📁 Module Structure

### 🔄 Kafka Consumers (`kafka/consumer`)
Handles real-time event processing from multiple Kafka topics.

**Key Components:**
- `MessageConsumer` - Processes message distribution events
- `ConnectionConsumer` - Manages connection lifecycle events

**Features:**
- **Partition Isolation** - Dedicated virtual thread executors per partition
- **Concurrent Processing** - Configurable concurrency levels
- **Error Handling** - Resilient message processing
- **Auto-acknowledgment** - Kafka offset management

### 🗄️ Redis Services (`redis`)
Manages connection state and message streaming infrastructure.

**Key Components:**
- `RedisConnectionService` - Connection state management
- `RedisEphemeralService` - Message streaming via Redis Streams
- `RedisConfig` - Multi-template Redis configuration

**Advanced Features:**
- **Connection Tracking** - Real-time WebSocket connection state
- **Stream-based Distribution** - Per-gateway message streams
- **TTL Management** - Ephemeral data with automatic expiration
- **Multi-serialization** - JSON, String, and Object serialization

### 🌐 Connection Management (`connection`)
Synchronizes connection state with Core Services.

**Key Components:**
- `ConnectionManager` - Application startup connection sync
- `ConnectionRefresh` - Scheduled connection state refresh
- `ConnectionGrpcClient` - gRPC client for Core Services

**Features:**
- **Startup Sync** - Initial connection state loading
- **Periodic Refresh** - Connection state synchronization (5 minutes)
- **Fault Tolerance** - Graceful handling of connection state drift

### 📋 Event Models (`events`)
Shared event data structures for the entire system.

**Key Components:**
- `MessageEvent` - Complete message data with recipients
- `ConnectionEvent` - WebSocket connection state data
- `EphemeralRedisMessage` - Gateway-specific message wrapper

## 🔄 Message Distribution Flow

### 1. **Message Event Processing**
```java
@KafkaListener(topics = "message.all", groupId = "fanout-worker-group")
public void consumeMessageAll(ConsumerRecord<String, MessageEvent> record) {
    // Partition-based virtual thread processing
    ExecutorService executor = partitionExecutors.computeIfAbsent(
        record.partition(), 
        k -> Executors.newSingleThreadExecutor(
            Thread.ofVirtual().name("vt-chat-" + k, 0).factory()
        )
    );
}
```

### 2. **Recipient Resolution**
For each message event:
1. Extract conversation member IDs
2. Query Redis for active connections per user
3. Group connections by gateway node ID
4. Create ephemeral messages per gateway

### 3. **Gateway Distribution**
```java
EphemeralRedisMessage ephemeralMessage = EphemeralRedisMessage.builder()
    .messageEvent(messageEvent)
    .recipientId(memberId.toString())
    .build();

redisEphemeralService.saveEphemeralData(gatewayId, ephemeralMessage);
```

### 4. **Redis Stream Delivery**
Messages are added to gateway-specific Redis streams:
- **Stream Key**: `gatewayId:{gateway-node-id}:stream`
- **Max Length**: 10,000 messages with approximate trimming
- **Auto-acknowledgment**: Consumed by Socket Gateway listeners

## 🔌 Connection State Management

### Connection Tracking
The service maintains real-time connection state in Redis:

```java
// Connection storage pattern
String activeKey = "connection-active" + connectionId;     // Hash storage
String setKey = "connection-set:" + userId;               // Set storage

// Enables fast lookups:
// - Get connection by ID: O(1)
// - Get all user connections: O(N) where N = user's connections
// - Gateway-based routing: Efficient fanout
```

### Event-Driven Updates
```java
@KafkaListener(topics = "connection.add", groupId = "connection-fanoutworker", concurrency = "3")
public void consumeConnectionAddEvent(ConnectionEvent event) {
    redisService.saveConnectionActive(event);
}

@KafkaListener(topics = "connection.remove", groupId = "connection-fanoutworker", concurrency = "3") 
public void consumeConnectionRemoveEvent(ConnectionEvent event) {
    redisService.removeConnectionActive(event);
}
```

### Scheduled Synchronization
```java
@Scheduled(fixedRate = 300000) // Every 5 minutes
public void refreshConnections() {
    List<Connection> connections = connectionGrpcClient.getAllConnections();
    redisService.removeALlConnectionActive();
    connections.stream()
        .map(connectionMapper::toEvent)
        .forEach(redisService::saveConnectionActive);
}
```

## ⚡ Performance Optimizations

### Virtual Thread Architecture
- **Per-Partition Executors** - Kafka partition isolation with virtual threads
- **High Concurrency** - Thousands of lightweight threads
- **Non-blocking I/O** - Efficient Redis and gRPC operations

### Kafka Consumer Optimization
```yaml
spring:
  kafka:
    consumer:
      properties:
        spring.json.trusted.packages: "*"
        spring.json.use.type.headers: true
```

### Redis Configuration
```java
@Bean
RedisTemplate<String, ConnectionEvent> connectionRedisTemplate() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    Jackson2JsonRedisSerializer<ConnectionEvent> serializer = 
        new Jackson2JsonRedisSerializer<>(objectMapper, ConnectionEvent.class);
    
    template.setValueSerializer(serializer);
    return template;
}
```

## 🗄️ Redis Data Structures

### Connection State Storage
```
connection-active{connectionId} → ConnectionEvent (Hash)
connection-set:{userId} → Set<connectionId> (Set)
```

### Message Streams
```
gatewayId:{gateway-node-id}:stream → Stream<EphemeralRedisMessage>
```

**Stream Features:**
- **XADD** operations with max length limits
- **Consumer Groups** for Socket Gateway consumption
- **Auto-trimming** to prevent memory bloat
- **Approximate trimming** for performance

## 🔧 Configuration

### Application Properties
```properties
spring.application.name=fanout_worker
server.port=8083
spring.threads.virtual.enabled=true
spring.config.import=redis.yaml, kafka.yaml, grpc.yaml
```

### Redis Configuration (`redis.yaml`)
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
          max-wait: -1ms
```

### Kafka Configuration (`kafka.yaml`)
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: user-service
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "*"
        spring.json.use.type.headers: true
```

### gRPC Configuration (`grpc.yaml`)
```yaml
spring:
  grpc:
    client:
      channels:
        connection-management-service:
          address: static://localhost:9090
          negotiation-type: plaintext
```

## 📊 Kafka Topics & Events

### Consumed Topics

#### `message.all`
**Purpose**: Message distribution events from Core Services
**Event Type**: `MessageEvent`
**Processing**: 
- Extract conversation members
- Resolve active connections
- Create gateway-specific ephemeral messages
- Stream to appropriate Redis streams

#### `connection.add`
**Purpose**: New WebSocket connection events
**Event Type**: `ConnectionEvent` 
**Processing**: Store connection state in Redis

#### `connection.remove`
**Purpose**: WebSocket disconnection events
**Event Type**: `ConnectionEvent`
**Processing**: Remove connection state from Redis

### Event Processing Patterns

```java
// Partition-based processing for message ordering
Map<Integer, ExecutorService> partitionExecutors = new ConcurrentHashMap<>();

// Virtual thread executor per partition
ExecutorService executor = partitionExecutors.computeIfAbsent(
    record.partition(),
    k -> Executors.newSingleThreadExecutor(
        Thread.ofVirtual().name("vt-chat-" + k, 0).factory()
    )
);
```

## 🚀 Getting Started

### Prerequisites
- Java 21 or higher
- Redis 6.0+
- Apache Kafka 2.8+
- Core Services running (gRPC on port 9090)

### Running the Service

1. **Start Dependencies**
```bash
# From project root
docker-compose up -d redis kafka
```

2. **Start Core Services**
```bash
cd core-services
./mvnw spring-boot:run
```

3. **Run Fanout Worker**
```bash
./mvnw spring-boot:run
```

The service will start on `http://localhost:8083`

### Development Commands

```bash
# Generate Protocol Buffer classes
./mvnw protobuf:generate

# Run tests
./mvnw test

# Build JAR
./mvnw clean package
```

## 🔍 Monitoring & Observability

### Key Metrics to Monitor

1. **Kafka Consumer Lag** - Message processing delays
2. **Redis Connection Pool** - Connection utilization
3. **Virtual Thread Count** - Concurrency levels
4. **Stream Length** - Redis stream backlog
5. **gRPC Client Health** - Core Services connectivity

### Health Checks

```bash
# Check Redis connectivity
redis-cli ping

# Monitor Kafka consumer groups
kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group fanout-worker-group

# Check Redis streams
redis-cli XLEN gatewayId:gateway-1:stream
```

### Logging
- Structured logging for message processing
- Error logging for failed distributions
- Performance logging for virtual thread usage

## 🔧 Advanced Features

### Virtual Thread Configuration
```java
@Bean("ioExecutor")
Executor ioExecutor() {
    return Executors.newVirtualThreadPerTaskExecutor();
}
```

### Graceful Shutdown
```java
@PreDestroy
public void shutdownExecutors() {
    partitionExecutors.values().forEach(ExecutorService::shutdown);
}
```

### Error Handling
- **Retry Logic** - Built-in Kafka retry mechanisms
- **Dead Letter Queues** - Failed message handling
- **Circuit Breaker** - gRPC client protection
- **Graceful Degradation** - Fallback strategies

## 🔍 Troubleshooting

### Common Issues

1. **High Consumer Lag**
   - Increase Kafka consumer concurrency
   - Monitor virtual thread usage
   - Check Redis performance

2. **Connection State Drift**
   - Verify scheduled refresh is running
   - Check gRPC connectivity to Core Services
   - Monitor Redis connection stability

3. **Memory Issues**
   - Monitor Redis stream lengths
   - Verify stream trimming is working
   - Check for connection leaks

### Debug Commands

```bash
# Check Kafka consumer group status
kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group fanout-worker-group

# Monitor Redis memory usage
redis-cli info memory

# Check active connections in Redis
redis-cli --scan --pattern "connection-active*" | wc -l

# Monitor stream activity
redis-cli monitor
```

## 📈 Scalability Considerations

### Horizontal Scaling
- **Kafka Partitioning** - Scale consumers with topic partitions
- **Redis Clustering** - Distribute connection state
- **Multiple Instances** - Deploy multiple fanout workers

### Performance Tuning
- **Virtual Thread Pools** - Optimize thread count per partition
- **Redis Pipelining** - Batch Redis operations
- **Stream Configuration** - Tune max length and trimming

### Resource Management
- **Memory Usage** - Monitor Redis and JVM heap
- **Network I/O** - Optimize Kafka and Redis connections
- **CPU Usage** - Balance virtual thread concurrency

## 🤝 Contributing

1. Follow the event-driven architecture patterns
2. Maintain virtual thread usage for I/O operations
3. Add proper error handling for distributed failures
4. Update tests for new message distribution logic
5. Document Redis data structure changes

---

The Fanout Worker is a critical component ensuring efficient, real-time message distribution across the chat system, leveraging modern Java concurrency and Redis streaming for optimal performance.