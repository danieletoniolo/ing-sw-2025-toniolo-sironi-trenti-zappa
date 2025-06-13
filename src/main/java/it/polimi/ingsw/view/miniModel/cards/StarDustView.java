package it.polimi.ingsw.view.miniModel.cards;

import javafx.scene.image.Image;

public class StarDustView extends CardView {
    public StarDustView(int ID, boolean covered, int level) {
        super(ID, covered, level);
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
        Image img = new Image(getClass().getResource(path).toExternalForm());
        return img;
    }

    @Override
    public String drawLineTui(int l){
        if(isCovered()) return super.drawLineTui(l);

        return switch(l) {
            case 0 -> Up;
            case 1 -> "│     STARDUST      │";
            case 2,3,4,5,6,7,8 -> Clear;
            case 9 -> Down;
            default -> null;
        };
    }

    @Override
    public CardViewType getCardViewType() {
        return CardViewType.STARDUST;
    }
}
