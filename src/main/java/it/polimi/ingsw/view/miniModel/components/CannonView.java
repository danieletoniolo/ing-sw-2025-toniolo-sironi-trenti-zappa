package it.polimi.ingsw.view.miniModel.components;

import it.polimi.ingsw.view.gui.controllers.components.CannonController;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CannonView extends ComponentView {
    private final List<MiniModelObserver> listeners = new ArrayList<>();
    private float power;
    private final String purple = "\033[35m";
    private final String reset = "\033[0m";
    private final boolean doubleCannon;

    public CannonView(int ID, int[] connectors, int clockWise, float power) {
        super(ID, connectors, clockWise);
        this.power = power;
        this.doubleCannon = (getClockWise() == 0 && power == 2) || (getClockWise() != 0 && power == 1);
    }

    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        this.power = power;
    }

    public boolean isDoubleCannon() {
        return doubleCannon;
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/cannon.fxml"));
            Node root = loader.load();

            CannonController controller = loader.getController();
            controller.setCannonModel(this);

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
            case 1 -> super.drawLeft(line) + " " + (doubleCannon ? drawDoubleCannon() : drawCannon()) + " " + super.drawRight(line);
            default -> throw new IndexOutOfBoundsException("Unexpected value: " + line);
        };
    }

    private String drawCannon(){
        return switch (getClockWise()) {
            case 0 -> " " + purple + ArrowUp + reset + " ";
            case 1 -> " " + purple + ArrowRight + reset + " ";
            case 2 -> " " + purple + ArrowDown + reset + " ";
            case 3 -> " " + purple + ArrowLeft + reset + " ";
            default -> throw new IllegalStateException("Unexpected value: " + getClockWise());
        };
    }

    private String drawDoubleCannon(){
        return switch (getClockWise()) {
            case 0 -> purple + ArrowUp + reset + " " + purple + ArrowUp + reset;
            case 1 -> purple + ArrowRight + reset + " " + purple + ArrowRight + reset;
            case 2 -> purple + ArrowDown + reset + " " + purple + ArrowDown + reset;
            case 3 -> purple + ArrowLeft + reset + " " + purple + ArrowLeft + reset;
            default -> throw new IllegalStateException("Unexpected value: " + getClockWise());
        };
    }

    @Override
    public TilesTypeView getType() {
        return doubleCannon ? TilesTypeView.DOUBLE_CANNON : TilesTypeView.SINGLE_CANNON;
    }

    @Override
    public CannonView clone() {
        CannonView copy = new CannonView(this.getID(), this.getConnectors(), this.getClockWise(), this.power);
        copy.setIsWrong(this.getIsWrong());
        return copy;
    }
}
