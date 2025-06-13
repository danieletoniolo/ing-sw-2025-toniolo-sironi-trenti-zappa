package it.polimi.ingsw.view.miniModel.components;

import javafx.scene.image.Image;

public class LifeSupportPurpleView extends ComponentView {
    private String purple = "\033[35m";
    private String reset = "\033[0m";

    public LifeSupportPurpleView(int ID, int[] connectors, int clockWise) {
        super(ID, connectors, clockWise);
    }

    /**
     * Draws the component GUI.
     * This method is called to draw the component GUI.
     *
     * @return an Image representing the image of the component
     */
    @Override
    public Image drawGui() {
        String path = "/image/tiles/" + this.getID() + ".jpg";
        Image img = new Image(getClass().getResource(path).toExternalForm());
        return img;
    }

    @Override
    public String drawLineTui(int line) {
        if (isCovered()) return super.drawLineTui(line);

        return switch (line) {
            case 0, 2 -> super.drawLineTui(line);
            case 1 -> super.drawLeft(line) + " " + purple + " * " + reset + " " + super.drawRight(line);
            default -> throw new IllegalStateException("Unexpected value: " + line);
        };
    }

    @Override
    public TilesTypeView getType() {
        return TilesTypeView.LIFE_SUPPORT_PURPLE;
    }

    @Override
    public LifeSupportPurpleView clone() {
        LifeSupportPurpleView copy = new LifeSupportPurpleView(this.getID(), this.getConnectors(), this.getClockWise());
        copy.setIsWrong(this.getIsWrong());
        return copy;
    }
}
