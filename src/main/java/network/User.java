package network;

import java.util.UUID;

public class User {
    private UUID uuid;
    private boolean RMI;
    private Connection connection;

    public User(UUID uuid, boolean RMI, Connection connection) {
        this.uuid = uuid;
        this.RMI = RMI;
        this.connection = connection;
    }
}
