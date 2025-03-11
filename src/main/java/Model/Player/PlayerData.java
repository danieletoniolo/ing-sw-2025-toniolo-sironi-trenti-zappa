package Model.Player;

public class PlayerData {
    private final String username;
    private final PlayerColor color;
    private int steps;

    private int coins;
    private final SpaceShip ship;
    private boolean leader;
    private boolean gaveUp;

    private boolean disconnected;

    PlayerData(String username, PlayerColor color, SpaceShip ship) {
        this.username = username;
        this.color = color;
        this.ship = ship;
        this.steps = 0;
        this.coins = 0;
        this.leader = false;
        this.gaveUp = false;
        this.disconnected = false;
    }

    public String getUsername() {
        return this.username;
    }

    public PlayerColor getColor() {
        return this.color;
    }

    public int getSteps() {
        return this.steps;
    }

    public int getCoins() {
        return this.coins;
    }

    public int getNumberOfLaps(int numberOfCells) {
        return (int) (this.steps % numberOfCells);
    }

    public boolean isLeader() {
        return this.leader;
    }

    public boolean isDisconnected() {
        return this.disconnected;
    }

    public boolean hasGivenUp() {
        return this.gaveUp;
    }

    public void addCoins(int coins) {
        this.coins += coins;
    }

    public void addSteps(int x) {
        this.steps += x;
    }

    public void setLeader(boolean leader) {
        this.leader = leader;
    }

    public void setGaveUp(boolean gaveUp) {
        this.gaveUp = gaveUp;
    }

    public void setDisconnected(boolean disconnected) {
        this.disconnected = disconnected;
    }
}
