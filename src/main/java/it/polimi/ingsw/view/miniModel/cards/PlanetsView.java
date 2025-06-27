package it.polimi.ingsw.view.miniModel.cards;

import it.polimi.ingsw.view.miniModel.good.GoodView;
import it.polimi.ingsw.view.miniModel.player.MarkerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a view of a planets card in the game.
 * This class extends CardView and contains information about planets that players can visit,
 * including flight days, planet resources, and player positions.
 */
public class PlanetsView extends CardView{
    /** The total number of planets available on this card */
    private final int numberOfPlanets;
    /** The number of days required for flight to reach these planets */
    private final int flightDays;
    /** A list containing the goods available on each planet */
    private final List<List<GoodView>> planets;
    /** Array tracking which players are positioned on each planet */
    private final MarkerView[] playersPosition;
    /** The currently selected planet index */
    private int planetSelected;

    /**
     * Constructs a new PlanetsView with the specified parameters.
     *
     * @param ID the unique identifier for this card
     * @param covered whether the card is currently covered/hidden
     * @param level the level of this card
     * @param flightDays the number of days required for flight
     * @param planets the list of planets with their available goods
     */
    public PlanetsView(int ID, boolean covered, int level, int flightDays, List<List<GoodView>> planets) {
        super(ID, covered, level);
        this.numberOfPlanets = planets.size();
        this.flightDays = flightDays;
        this.planets = planets;
        playersPosition = new MarkerView[5];
    }

    /**
     * Gets the number of flight days required to reach these planets.
     *
     * @return the flight days
     */
    public int getFlightDays() {
        return flightDays;
    }

    /**
     * Gets the goods available on a specific planet.
     *
     * @param n the planet index (0-based)
     * @return a new list containing the goods available on the specified planet
     */
    public List<GoodView> getPlanet(int n) {
        return new ArrayList<>(planets.get(n));
    }

    /**
     * Gets the total number of planets on this card.
     *
     * @return the number of planets
     */
    public int getNumberOfPlanets() {
        return numberOfPlanets;
    }

    /**
     * Sets the currently selected planet.
     *
     * @param planetSelected the index of the planet to select
     */
    public void setPlanetSelected(int planetSelected) {
        this.planetSelected = planetSelected;
    }

    /**
     * Gets the currently selected planet index.
     *
     * @return the selected planet index
     */
    public int getPlanetSelected() {
        return planetSelected;
    }

    /**
     * Draws a specific line of the text-based user interface representation of this planets card.
     * If the card is covered, delegates to the parent class implementation.
     *
     * @param l the line number to draw (0-based)
     * @return the string representation of the specified line
     */
    @Override
    public String drawLineTui(int l){
        if(isCovered()) return super.drawLineTui(l);

        StringBuilder line = new StringBuilder(switch(l) {
            case 0 -> Up;
            case 1 -> "│      PLANETS      │";
            case 2 -> Clear;
            case 3 -> numberOfPlanets >= 1 ? "│ " + drawPlayer(0) + " P1: " + printPlanet(getPlanet(0)) : Clear;
            case 4 -> numberOfPlanets >= 2 ? "│ " + drawPlayer(1) + " P2: " + printPlanet(getPlanet(1)) : Clear;
            case 5 -> numberOfPlanets >= 3 ? "│ " + drawPlayer(2) + " P3: " + printPlanet(getPlanet(2)) : Clear;
            case 6 -> numberOfPlanets >= 4 ? "│ " + drawPlayer(3) + " P4: " + printPlanet(getPlanet(3)) : Clear;
            case 7 -> numberOfPlanets >= 5 ? "│ " + drawPlayer(4) + " P5: " + printPlanet(getPlanet(4)) : Clear;
            case 8 -> "│   FlightDays: " + getFlightDays();
            case 9 -> Down;
            default -> "";
        });

        while (line.toString().replaceAll("\033\\[[0-9;]*m", "").length() < getColsToDraw() - 1) {
            line.append(" ");
        }
        if (line.toString().replaceAll("\033\\[[0-9;]*m", "").length() == getColsToDraw() - 1) {
            line.append("│");
        }
        return line.toString();
    }

    /**
     * Creates a string representation of the goods available on a planet.
     * Each good is represented by its TUI drawing followed by a space.
     *
     * @param planet the list of goods available on the planet
     * @return a string containing all goods separated by spaces
     */
    private String printPlanet(List<GoodView> planet) {
        StringBuilder line = new StringBuilder();
        for (GoodView good : planet) {
            line.append(good.drawTui()).append(" ");
        }
        return line.toString();
    }

    /**
     * Draws the player marker for a specific planet position.
     * Returns a space character if no player is present on the planet.
     *
     * @param planet the planet index to check for player presence
     * @return the TUI representation of the player marker or a space if no player is present
     */
    private String drawPlayer(int planet) {
        return playersPosition[planet] == null ? " " : playersPosition[planet].drawTui();
    }

    /**
     * Sets a player's position on a specific planet.
     *
     * @param planet the planet index where the player is positioned
     * @param player the marker view representing the player
     */
    public void setPlayersPosition(int planet, MarkerView player) {
        playersPosition[planet] = player;
    }

    /**
     * Gets the array containing all player positions on planets.
     *
     * @return an array of MarkerView objects representing player positions
     */
    public MarkerView[] getPlayersPositions() {
        return playersPosition;
    }

    /**
     * Gets the type of this card view.
     *
     * @return the CardViewType enum value for planets cards
     */
    @Override
    public CardViewType getCardViewType() {
        return CardViewType.PLANETS;
    }
}
