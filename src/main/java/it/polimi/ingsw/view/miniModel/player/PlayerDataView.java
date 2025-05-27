package it.polimi.ingsw.view.miniModel.player;

import it.polimi.ingsw.view.miniModel.Structure;
import it.polimi.ingsw.view.miniModel.components.ComponentView;
import it.polimi.ingsw.view.miniModel.spaceship.SpaceShipView;

public class PlayerDataView implements Structure {
    private String username;
    private String color;
    private MarkerView markerView;
    private int step;
    private int coins;
    private SpaceShipView ship;
    private ComponentView hand;
    private final String blue =   "\033[34m";
    private final String green =  "\033[32m";
    private final String yellow = "\033[33m";
    private final String red =    "\033[31m";
    private final String reset =  "\033[0m";

    public PlayerDataView(String username, MarkerView color, SpaceShipView ship) {
        this.username = username;
        this.markerView = color;
        this.color = switch (color) {
            case BLUE -> blue;
            case GREEN -> green;
            case YELLOW -> yellow;
            case RED -> red;
        };
        this.ship = ship;
    }

    @Override
    public void drawGui() {
        //TODO: Implements player data gui
    }

    public int getRowsToDraw() {
        return 3;
    }

    @Override
    public String drawLineTui(int line) {
        return switch (line) {
            case 0 -> color + username + reset;
            case 1 -> "Step: " + String.valueOf(step);
            case 2 -> "Coins: " + String.valueOf(coins);
            default -> throw new IllegalStateException("Unexpected value: " + line);
        };
    }

    public String getUsername() {
        return username;
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

    public MarkerView getMarkerView() {
        return markerView;
    }
}
