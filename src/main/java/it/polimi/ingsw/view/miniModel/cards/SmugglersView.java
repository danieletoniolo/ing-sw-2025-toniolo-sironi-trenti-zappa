package it.polimi.ingsw.view.miniModel.cards;

import it.polimi.ingsw.view.miniModel.good.GoodView;

import java.util.List;

/**
 * Represents a view of a Smugglers card in the game.
 * This card type requires cannons to be played and causes goods loss and flight days when activated.
 * @author Matteo Zappa
 */
public class SmugglersView extends CardView{
    /** The number of cannons required to play this card */
    private final int cannonRequired;
    /** The number of goods lost when this card is played */
    private final int goodsLoss;
    /** The number of flight days incurred when this card is played */
    private final int flightDays;
    /** The list of goods associated with this card */
    private final List<GoodView> goods;

    /**
     * Constructs a new SmugglersView with the specified parameters.
     *
     * @param ID the unique identifier of the card
     * @param covered whether the card is covered (face down)
     * @param level the level of the card
     * @param cannonRequired the number of cannons required to play this card
     * @param goodsLoss the number of goods lost when playing this card
     * @param flightDays the number of flight days incurred when playing this card
     * @param goods the list of goods associated with this card
     */
    public SmugglersView(int ID, boolean covered, int level, int cannonRequired, int goodsLoss, int flightDays, List<GoodView> goods) {
        super(ID, covered, level);
        this.cannonRequired = cannonRequired;
        this.goodsLoss = goodsLoss;
        this.flightDays = flightDays;
        this.goods = goods;
    }

    /**
     * Draws a specific line of the TUI representation for this Smugglers card.
     * If the card is covered, delegates to the parent implementation.
     * Otherwise, renders the card details including cannons required, goods lost,
     * associated goods, and flight days across multiple lines.
     *
     * @param l the line number to draw (0-based indexing)
     * @return the string representation of the specified line with proper formatting and padding
     */
    @Override
    public String drawLineTui(int l){
        if(isCovered()) return super.drawLineTui(l);

        StringBuilder line = new StringBuilder(switch(l) {
            case 0 -> Up;
            case 1 -> "│     SMUGGLERS     │";
            case 2,6,7 -> Clear;
            case 3 -> "│   StrengthReq: " + getCannonRequired();
            case 4 -> "│   GoodLost: " + getGoodsLoss();
            case 5 -> "│   Good: " + printGoods();
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
     * Gets the number of cannons required to play this card.
     *
     * @return the number of cannons required
     */
    public int getCannonRequired() {
        return cannonRequired;
    }

    /**
     * Gets the number of goods lost when this card is played.
     *
     * @return the number of goods lost
     */
    public int getGoodsLoss() {
        return goodsLoss;
    }

    /**
     * Gets the number of flight days incurred when this card is played.
     *
     * @return the number of flight days
     */
    public int getFlightDays() {
        return flightDays;
    }

    /**
     * Gets the list of goods associated with this card.
     *
     * @return the list of goods
     */
    public List<GoodView> getGoods() {
        return goods;
    }

    /**
     * Creates a string representation of the goods associated with this card
     * by concatenating their TUI representations.
     *
     * @return a string containing the TUI representation of all goods
     */
    private String printGoods() {
        StringBuilder sb = new StringBuilder();
        for (GoodView good : goods) {
            sb.append(good.drawTui()).append(" ");
        }
        return sb.toString();
    }

    /**
     * Gets the card view type for this Smugglers card.
     *
     * @return the CardViewType enum value representing a SMUGGLERS card
     */
    @Override
    public CardViewType getCardViewType() {
        return CardViewType.SMUGGLERS;
    }
}
