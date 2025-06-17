package it.polimi.ingsw.view.miniModel.components;

import it.polimi.ingsw.view.gui.controllers.components.LifeSupportPurpleController;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LifeSupportPurpleView extends ComponentView {
    private final List<MiniModelObserver> listeners = new ArrayList<>();
    private String purple = "\033[35m";
    private String reset = "\033[0m";

    public LifeSupportPurpleView(int ID, int[] connectors, int clockWise) {
        super(ID, connectors, clockWise);
    }

    public void addListener(MiniModelObserver listener) {
        listeners.add(listener);
    }

    public void removeListener(MiniModelObserver listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (MiniModelObserver listener : listeners) {
            listener.onModelChanged();
        }
    }

    public Node createGuiNode() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/lifeSupportPurple.fxml"));
            Node root = loader.load();

            LifeSupportPurpleController controller = loader.getController();
            controller.setLifeSupportPurpleModel(this);

            return root;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
