# 🚀 Galaxy Trucker - Prova Finale di Ingegneria del Software (AA 2024/2025)

## 📚 Descrizione Generale

Questo progetto rappresenta la nostra prova finale per il corso di **Ingegneria del Software** dell’Anno Accademico **2024/2025**, e consiste nella realizzazione distribuita del gioco da tavolo **Galaxy Trucker**.

## 🧩 Funzionalità Implementate

| Categoria                  | Funzionalità Specifica                                         | Stato |
|---------------------------|----------------------------------------------------------------|-------|
| 🎮 Regole di gioco         | Regole Complete (livello II, no trasvolata intergalattica)     | ✅    |
| 🔄 Interfacce              | TUI + GUI (JavaFX)                                             | ✅    |
| 🌐 Comunicazione           | Supporto a Socket e RMI                                        | ✅    |
| 🚀 Modalità di gioco       | Volo Standard + Volo di Prova                                  | ✅    |
| 🔁 Partite multiple        | Server multi-partita, selezione o creazione dinamica           | ✅    |

Quindi riferendoci alla tabella dei requisiti abbiamo implementato requisiti per un voto massimo di 30.

---

## 🏗️ Architettura del Progetto

Il progetto è basato sul pattern **MVC** per entrambi i lati **client** e **server**, con una chiara separazione dei moduli. Di seguito i principali pacchetti:

it.polimi.ingsw/
├── Client.java                    # Entry point client
├── Server.java                    # Entry point server
└── app/
    ├── ApplicationClient.java      # Gestione applicazione client
    └── ApplicationServer.java      # Gestione applicazione server
    controller/
    ├── DatabaseController.java        # Gestione database
    ├── EventCallback.java             # Callback eventi
    ├── GameController.java            # Controller principale gioco
    ├── MatchController.java           # Gestione partite
    ├── ServerEventManager.java        # Manager eventi server
    └── StateTransitionHandler.java    # Gestione transizioni stato
    event/
    ├── EventListener.java             # Listener eventi
    ├── EventTransceiver.java          # Trasmissione eventi
    ├── NetworkTransceiver.java        # Trasmissione di rete
    ├── Requester.java                 # Richieste eventi
    ├── Responder.java                 # Risposte eventi
    ├── TransmitterEventWrapper.java   # Wrapper trasmissione
    ├── game/                          # Eventi di gioco
    │   ├── HeartBeat.java
    │   ├── clientToServer/            # Eventi client → server
    │   └── serverToClient/            # Eventi server → client
    ├── internal/                      # Eventi interni
    │   ├── ConnectionLost.java
    │   └── EndGame.java
    ├── lobby/                         # Eventi lobby
    │   ├── clientToServer/
    │   └── serverToClient/
    ├── receiver/                      # Ricevitori eventi
    │   ├── CastEventReceiver.java
    │   └── EventReceiver.java
    ├── trasmitter/                    # Trasmettitori eventi
    │   └── EventTransmitter.java
    └── type/                          # Tipi eventi
        ├── Event.java
        └── StatusEvent.java
        model/
        ├── cards/                     # Carte del gioco
        │   └── hits/                      # Danni alle carte
        ├── game/
        │   ├── board/                     # Gestione board
        │   └── lobby/                     # Gestione lobby
        ├── good/                          # Beni/merci
        ├── player/                        # Giocatori
        ├── spaceship/                     # Navicella spaziale
        └── state/                         # Stati del gioco

---

## 🚀 Project Execution

### 📋 Prerequisites

- **Java 21** or higher
- Operating System: Windows, macOS, or Linux

### � Compilation

To compile the project and generate executable JARs:

```bash
# Complete project compilation
mvn clean package

# JARs will be generated in target/libs/
# - Server.jar (for the server)
# - Client.jar (for the client)
```

### 🖥️ Command Line Execution

#### 🌐 Server

The server must be started before any client. It supports both Socket and RMI connections.

```bash
# Navigate to the project directory
cd /path/to/ing-sw-2025-toniolo-sironi-trenti-zappa

# Launch server with default parameters
java -jar target/libs/Server.jar

# Launch with custom parameters
java -jar target/libs/Server.jar [SOCKET_PORT] [RMI_PORT]
```

**Server Parameters:**
- `SOCKET_PORT` (optional): Port for Socket connections (default: 1234)
- `RMI_PORT` (optional): Port for RMI connections (default: 1235)

**Examples:**
```bash
# Server with default ports (Socket: 1234, RMI: 1235)
java -jar target/libs/Server.jar

# Server with custom ports
java -jar target/libs/Server.jar 8080 8081

# Server with custom Socket port only
java -jar target/libs/Server.jar 9000
```

#### 📱 Client

The client can be started in **GUI** (graphical interface) or **TUI** (text interface) mode.

```bash
# Navigate to the project directory
cd /path/to/ing-sw-2025-toniolo-sironi-trenti-zappa

# Launch GUI client (default mode)
java -jar target/libs/Client.jar

# Launch with text interface (TUI)
java -jar target/libs/Client.jar --tui

# Launch with specific server
java -jar target/libs/Client.jar [SERVER_IP] [MODE]
```

**Client Parameters:**
- `SERVER_IP` (optional): Server IP address (default: localhost)
- `MODE` (optional): 
  - `--gui` or no parameter: Graphical interface (default)
  - `--tui`: Text interface

**Examples:**
```bash
# GUI client connected to local server
java -jar target/libs/Client.jar

# TUI client connected to local server
java -jar target/libs/Client.jar --tui

# GUI client connected to remote server
java -jar target/libs/Client.jar 192.168.1.100

# TUI client connected to remote server
java -jar target/libs/Client.jar 192.168.1.100 --tui
```

### 🔄 Complete Startup Sequence

1. **Start the Server:**
   ```bash
   java -jar target/libs/Server.jar
   ```
   
2. **Start one or more Clients:**
   ```bash
   # Terminal 1 - GUI Client
   java -jar target/libs/Client.jar
   
   # Terminal 2 - TUI Client (optional)
   java -jar target/libs/Client.jar --tui
   ```

3. **In the client:** 
   - Choose connection type (Socket or RMI)
   - Enter username
   - Create or join a game

### ⚠️ Troubleshooting

**Server won't start:**
- Verify that ports are not already in use
- Check firewall permissions
- Ensure you have Java 21+

**Client can't connect:**
- Verify that the server is running
- Check IP address and ports
- Verify network connection

**GUI interface issues:**
- On Linux you might need: `export DISPLAY=:0`
- On macOS ensure JavaFX is supported
