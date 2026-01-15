<p align="center">
  <img src="https://github.com/user-attachments/assets/ff658978-1866-4c2c-a5bd-f419b873f997" 
       alt="Khichdi Logo" 
       style="max-width: 150px; width: 20%; height: auto;">
</p>

if i were to write a description regarding why i made this? i would end up writing a 10 page note about my frustrated self. this is a work in progress. as the name suggests its messed up and chaotic just like [khichdi](https://en.wikipedia.org/wiki/Khichdi_(dish)). bye.

# Khichdi

A lightweight, functional Redis server implementation built from scratch in Kotlin, featuring support for strings, lists, and streams data structures with RESP protocol compliance.

## Features

### Data Types
- **Strings**: Basic key-value storage with TTL support
- **Lists**: Doubly-linked list operations with blocking capabilities
- **Streams**: Append-only log data structure with auto-generated IDs

### Commands Implemented

#### String Commands
- `SET key value [EX seconds] [PX milliseconds]` - Set key to value with optional expiration
- `GET key` - Get value of key

#### List Commands
- `LPUSH key element [element ...]` - Insert elements at the head
- `RPUSH key element [element ...]` - Insert elements at the tail
- `LPOP key` - Remove and return first element
- `BLPOP key timeout` - Blocking left pop with timeout
- `LRANGE key start stop` - Get range of elements
- `LLEN key` - Get list length

#### Stream Commands
- `XADD key ID field value [field value ...]` - Append entry to stream

#### Utility Commands
- `PING` - Test server connectivity
- `ECHO message` - Echo the given message
- `TYPE key` - Determine the type stored at key

### Advanced Features
- **RESP Protocol**: Full Redis Serialization Protocol support
- **Concurrent Connections**: Multi-threaded client handling
- **Blocking Operations**: FIFO-ordered blocking list operations with timeout support
- **Expiration**: Automatic key expiration with millisecond precision
- **Type Safety**: Kotlin sealed classes for type-safe data structures

## Architecture

```
src/main/kotlin/
├── Main.kt                    # Application entry point
├── commands/                  # Command implementations
│   ├── Command.kt            # Command interface
│   ├── SetCommand.kt         # String operations
│   ├── BlpopCommand.kt       # Blocking operations
│   └── ...                   # Other command implementations
├── protocol/                  # RESP protocol handling
│   ├── RespParser.kt         # Parse incoming RESP messages
│   └── RespEncoder.kt        # Encode responses to RESP format
├── server/                    # Server core
│   ├── RedisServer.kt        # TCP server & connection handling
│   ├── CommandHandler.kt     # Command routing & execution
│   └── BlockedClientsManager.kt # Manages blocking operations
└── storage/                   # Data layer
    ├── InMemoryStore.kt      # Unified keyspace storage
    ├── RedisValue.kt         # Data type definitions
    ├── StringOperations.kt   # String-specific logic
    ├── ListOperations.kt     # List-specific logic
    └── StreamOperations.kt   # Stream-specific logic
```

## Requirements

- **Kotlin**: 2.0 or higher
- **Java**: JDK 21
- **Maven**: For dependency management and build

## Getting Started

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd khichdi
```

2. Ensure you have Kotlin 2.0+ and JDK 21 installed:
```bash
kotlin -version
java -version
```

### Running the Server

Execute the startup script:
```bash
./your_program.sh
```

The server will start on port `6379` by default.

### Building Manually

```bash
mvn clean package
java -jar target/build-your-own-redis.jar
```

### Testing with Redis CLI

Connect to the server using the official Redis CLI:
```bash
redis-cli -p 6379

# Try some commands
127.0.0.1:6379> PING
PONG

127.0.0.1:6379> SET mykey "Hello"
OK

127.0.0.1:6379> GET mykey
"Hello"

127.0.0.1:6379> RPUSH mylist "one" "two" "three"
(integer) 3

127.0.0.1:6379> LRANGE mylist 0 -1
1) "one"
2) "two"
3) "three"
```

## Design Decisions

### Unified Keyspace
All data types (strings, lists, streams) share a single namespace using Kotlin sealed classes, ensuring type safety while preventing key collisions across different types.

### Blocking Operations
The `BlockedClientsManager` implements FIFO-ordered blocking with timeout support using `ConcurrentHashMap` and scheduled executors, ensuring fairness and proper resource cleanup.

### Protocol Compliance
Full RESP (REdis Serialization Protocol) implementation enables compatibility with standard Redis clients and tools.

### Thread Safety
Concurrent client connections are handled using thread-per-connection model with thread-safe data structures for shared state.

## Project Status

This is a learning project built as part of the [CodeCrafters](https://codecrafters.io) Redis challenge. It implements core Redis functionality with clean architecture and idiomatic Kotlin code.

## License

This project is developed for educational purposes.
