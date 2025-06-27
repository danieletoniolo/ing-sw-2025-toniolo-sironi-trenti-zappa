package it.polimi.ingsw.view.miniModel.cards;

import it.polimi.ingsw.view.miniModel.good.GoodView;

import java.util.List;

/**
 * Represents a view of an Abandoned Station card in the game.
 * This card type requires crew members and provides goods after a certain number of flight days.
 * @author Matteo Zappa
 */
public class AbandonedStationView extends CardView {
    /** The number of crew members required to activate this station */
    private final int crewRequired;
    /** The list of goods provided by this abandoned station */
    private final List<GoodView> goods;
    /** The number of flight days required to reach this station */
    private final int flightDays;

    /**
     * Constructs a new AbandonedStationView with the specified parameters.
     *
     * @param ID the unique identifier of the card
     * @param covered whether the card is currently covered
     * @param level the level of the card
     * @param crewRequired the number of crew members required to activate this station
     * @param flightDays the number of flight days required to reach this station
     * @param goods the list of goods provided by this abandoned station
     */
    public AbandonedStationView(int ID, boolean covered, int level, int crewRequired, int flightDays, List<GoodView> goods) {
        super(ID, covered, level);
        this.crewRequired = crewRequired;
        this.goods = goods;
        this.flightDays = flightDays;
    }

    /**
     * Draws a specific line of the TUI representation for this abandoned station card.
     * If the card is covered, delegates to the parent class method.
     * Otherwise, renders the card with abandoned station specific information including
     * crew requirements, goods, and flight days.
     *
     * @param l the line number to draw (0-9)
     * @return the string representation of the specified line
     */
    @Override
    public String drawLineTui(int l){
        if(isCovered()) return super.drawLineTui(l);

        StringBuilder line = new StringBuilder(switch(l) {
            case 0 -> Up;
            case 1 -> "│ ABANDONEDSTATION  │";
            case 2,5,6,7 -> Clear;
            case 3 -> "│  CrewRequired: " + crewRequired;
            case 4 -> "│  Goods: " + printGoods();
            case 8 -> "│   FlightDays: " + flightDays;
            case 9 -> Down;
            default -> null;
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
     * Returns the list of goods provided by this abandoned station.
     *
     * @return the list of goods provided by this abandoned station
     */
    public List<GoodView> getGoods() {
        return goods;
    }

    /**
     * Creates a string representation of all goods provided by this abandoned station.
     * Each good is rendered using its TUI representation followed by a space.
     *
     * @return a string containing the TUI representation of all goods
     */
    private String printGoods() {
        StringBuilder goodsString = new StringBuilder();
        for (GoodView good : goods) {
            goodsString.append(good.drawTui()).append(" ");
        }
        return goodsString.toString();
    }

    /**
     * Returns the type of this card view.
     * This method is used to identify the specific type of card in the view hierarchy.
     *
     * @return the card view type for abandoned station cards
     */
    @Override
    public CardViewType getCardViewType() {
        return CardViewType.ABANDONEDSTATION;
    }
}
