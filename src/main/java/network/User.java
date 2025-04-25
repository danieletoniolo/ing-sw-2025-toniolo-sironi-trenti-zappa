package network;

import java.util.UUID;

// TODO: should we remove this class and use only IDs in match controller and PlayerData in gameControllers?
public class User {
    private UUID uuid;
    private boolean RMI;

    public User(UUID uuid, boolean RMI) {
        this.uuid = uuid;
        this.RMI = RMI;
    }

    public UUID getUUID() {
        return uuid;
    }
}
