# 🚀 Galaxy Trucker - Software Engineering Final Project (AY 2024/2025)

## 📚 General Description

This project represents our final exam for the **Software Engineering** course of Academic Year **2024/2025**, and consists of implementing a distributed version of the **Galaxy Trucker** board game.

## 🧩 Implemented Features

| Category                   | Specific Feature                                               | Status |
|---------------------------|----------------------------------------------------------------|--------|
| 🎮 Game Rules              | Complete Rules (level II, no intergalactic flight)            | ✅     |
| 🔄 Interfaces              | TUI + GUI (JavaFX)                                           | ✅     |
| 🌐 Communication           | Socket and RMI support                                       | ✅     |
| 🚀 Game Modes              | Standard Flight + Test Flight                                 | ✅     |
| 🔁 Multiple Matches        | Multi-match server, dynamic selection or creation            | ✅     |

Based on the requirements table, we have implemented features for a maximum grade of 30.

## 🚀 Project Execution

### 🖥️ Command Line Execution

#### 🌐 Server

The server must be started before any client. It supports both Socket and RMI connections.

```bash
# Navigate to the project directory
cd /path/to/ing-sw-2025-toniolo-sironi-trenti-zappa

# Launch server with default parameters
java -jar target/libs/Server.jar
```

#### 📱 Client

The client can be started in **GUI** (graphical interface) or **TUI** (text interface) mode.

```bash
# Navigate to the project directory
cd /path/to/ing-sw-2025-toniolo-sironi-trenti-zappa

# Launch GUI client (default mode)
java -jar target/libs/Client.jar

```

### 🔄 Complete Startup Sequence

1. **Start the Server:**
   ```bash
   java -jar target/libs/Server.jar
   ```
   
2. **Start one or more Clients:**
   ```bash
   # Terminal 1 - Client
   java -jar target/libs/Client.jar
   
   # Terminal 2 - Client
   java -jar target/libs/Client.jar
   ```

3. **In the client:** 
   - Choose connection type (Socket or RMI)
   - Choose IP Adress
   - Enter username
   - Create or join a game
