package view.structures.player;

import view.structures.Structure;
import view.structures.components.ComponentView;
import view.structures.spaceship.SpaceShipView;

import java.util.UUID;

public class PlayerDataView implements Structure {
    private String username;
    private ColorView color;
    private int step;
    private int coins;
    private SpaceShipView ship;
    private ComponentView hand;

    public PlayerDataView(String username, ColorView color, SpaceShipView ship) {
        this.username = username;
        this.color = color;
        this.ship = ship;
    }

    @Override
    public void drawGui() {
        //TODO: Implements player data gui
    }

    public static int getRowsToDraw() {
        return 4;
    }

    @Override
    public String drawLineTui(int line) {
        return switch (line) {
            case 0 -> color.drawTui();
            case 1 -> username;
            case 2 -> "Step: " + String.valueOf(step);
            case 3 -> "Coins: " + String.valueOf(coins);
            default -> throw new IllegalStateException("Unexpected value: " + line);
        };
    }

    public String getUsername() {
        return username;
    }

    public ColorView getColor() {
        return color;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getStep() {
        return step;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getCoins() {
        return coins;
    }

    public SpaceShipView getShip() {
        return ship;
    }

    public void setHand(ComponentView hand) {
        this.hand = hand;
    }

    public ComponentView getHand() {
        return hand;
    }
}
