package network;

import java.util.UUID;

public class User {
    private UUID uuid;
    private boolean RMI;

    public User(UUID uuid, boolean RMI) {
        this.uuid = uuid;
        this.RMI = RMI;
    }
}
