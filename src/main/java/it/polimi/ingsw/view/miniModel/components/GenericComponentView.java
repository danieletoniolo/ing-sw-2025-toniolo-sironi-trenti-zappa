package it.polimi.ingsw.view.miniModel.components;

import javafx.scene.image.Image;

public class GenericComponentView extends ComponentView {
    public GenericComponentView() {
        super(-1, new int[]{0, 0, 0, 0}, 0);
    }

    /**
     * Draws the component GUI.
     * This method is called to draw the component GUI.
     *
     * @return an Image representing the image of the component
     */
    //TODO: Capire cosa stampare
    @Override
    public Image drawGui() {
        String path = "/image/tiles/covered.jpg";
        Image img = new Image(getClass().getResource(path).toExternalForm());
        return img;
    }

    @Override
    public String drawLineTui(int line) {
        if (isCovered()) return super.drawLineTui(line);

        return switch (line) {
            case 0, 2 -> super.drawLineTui(line);
            case 1 -> super.drawLeft(line) + "     " + super.drawRight(line);
            default -> throw new IndexOutOfBoundsException("Unexpected value: " + line);
        };
    }

    @Override
    public TilesTypeView getType() {
        return TilesTypeView.GENERIC;
    }

    @Override
    public GenericComponentView clone() {
        GenericComponentView copy = new GenericComponentView();
        copy.setIsWrong(this.getIsWrong());
        return copy;
    }
}
