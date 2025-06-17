package it.polimi.ingsw.view.miniModel.cards;

import it.polimi.ingsw.view.miniModel.good.GoodView;
import javafx.scene.image.Image;

import java.util.List;
import java.util.Objects;

public class AbandonedStationView extends CardView {
    private final int crewRequired;
    private final List<GoodView> goods;
    private final int flightDays;

    public AbandonedStationView(int ID, boolean covered, int level, int crewRequired, int flightDays, List<GoodView> goods) {
        super(ID, covered, level);
        this.crewRequired = crewRequired;
        this.goods = goods;
        this.flightDays = flightDays;
    }

    /**
     * Draws the card GUI.
     * This method is called to draw the card GUI.
     *
     * @return an Image representing the image of the card
     */
    @Override
    public Image drawGui() {
        String path = "/image/card/" + this.getID() + ".jpg";
        return new Image(Objects.requireNonNull(getClass().getResource(path)).toExternalForm());
    }

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

    public List<GoodView> getGoods() {
        return goods;
    }

    private String printGoods() {
        StringBuilder goodsString = new StringBuilder();
        for (GoodView good : goods) {
            goodsString.append(good.drawTui()).append(" ");
        }
        return goodsString.toString();
    }

    @Override
    public CardViewType getCardViewType() {
        return CardViewType.ABANDONEDSTATION;
    }
}
