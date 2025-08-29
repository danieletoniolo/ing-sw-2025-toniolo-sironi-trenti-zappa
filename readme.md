# Galaxy Trucker

This project is a software implementation of the board game "[Galaxy Trucker](https://www.craniocreations.it/prodotto/galaxy-trucker)", developed for the Software Engineering Final Project course at Politecnico di Milano (Academic Year 2024-2025). It includes a client-server architecture, adhering to the game's rules and specified technical requirements.

**Final grade: 30/30**

## Group Components
- [Daniele Toniolo](https://github.com/danieletoniolo)
- [Vittorio Sironi](https://github.com/vittoriosironi)
- [Lorenzo Trenti](https://github.com/LorenzoTrenti)
- [Matteo Zappa](https://github.com/Mz2305)

## Requirements
The project consists of a Java version of the board game **Galaxy Trucker**, made by **Cranio Creations**.

- [Project Requirements](assets/Requisiti%20e%20Tabella%20di%20Valutazione.pdf)

## Advanced Features
The following *advanced features* have been implemented in this project:
- **Trial Flight**: The client of the first player can choose between a normal game (corresponding to Level 2) and a "trial flight" game. The server then implements the two versions of the game rules with differentiated boards.
- **Multiple Games**: The server is designed to manage multiple games concurrently. This allows players to choose which open game to join or to create a new one.

## Launch Instructions
The project consists of a server-side JAR and two client-side JARs.

### Server Launch
The server manages multiple games. Please run the JAR file and follow the instruction on screen to set up your network. Note that only one server is needed for multiple clients to connect.

#### Windows/macOS/Linux
  ``` bash

java -jar deliverables/Server.jar

```

### Client Launch
The client interface can be either textual (TUI) or graphical (GUI), and it can communicate with the server via Socket or RMI.

Only one client instance is needed per player, but multiple instances can be run on the same machine for testing purposes.

#### Windows/macOS/Linux
```bash
java -jar deliverables/Client.jar

```

## Server

You can connect remotely to our server to play Galaxy Trucker. As RMI support is limited by our Provider, only Socket connection type is available.

Run the JAR file of the client and provide the following address when prompted:
- **IP address**: 129.152.14.114

## Deliverables

You can find a UML Sequence Diagram for RMI and Socket connection in some peculiar parts of the game (connection, place component) in deliverables.

[UML class diagrams](deliverables/UML%20High%20Level.pdf) are provided for client and server, please refer to `UML Low Level.pdf` for more details.

A [network architecture documentation file](deliverables/NetworkProtocol.pdf) can also be found for specific description of network classes and design patterns.

## License [![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
This project is licensed under the GPL-v3 License. See the [LICENSE](LICENSE) file for details.

## Copyright
Please note that the game Galaxy Trucker is a copyrighted work by Cranio Creations. This project is an educational implementation and does not intend to infringe on any copyrights. The game rules and assets are used for educational purposes only, and no commercial use is intended.