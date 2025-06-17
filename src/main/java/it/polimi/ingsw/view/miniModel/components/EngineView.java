package it.polimi.ingsw.view.miniModel.components;

import it.polimi.ingsw.view.gui.controllers.components.EngineController;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EngineView extends ComponentView {
    private final List<MiniModelObserver> listeners = new ArrayList<>();
    private String brown = "\033[38;5;220m";
    private String reset = "\033[0m";
    private boolean doubleEngine;

    public EngineView(int ID, int[] connectors, int clockWise, int power) {
        super(ID, connectors, clockWise);
        this.doubleEngine = power == 2;
    }

    public boolean isDoubleEngine() {
        return doubleEngine;
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/engine.fxml"));
            Node root = loader.load();

            EngineController controller = loader.getController();
            controller.setEngineModel(this);

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
            case 1 -> super.drawLeft(line) + (doubleEngine ? drawDoubleEngine() : drawSingleEngine()) + super.drawRight(line);
            default -> throw new IndexOutOfBoundsException("Unexpected value: " + line);
        };
    }

    private String drawSingleEngine(){
        return switch ((getClockWise() + 2) % getConnectors().length) {
            case 0 -> "  " + brown + ArrowUp + reset + "  ";
            case 1 -> "  " + brown + ArrowRight + reset + "  ";
            case 2 -> "  " + brown + ArrowDown + reset + "  ";
            case 3 -> "  " + brown + ArrowLeft + reset + "  ";
            default -> throw new IllegalStateException("Unexpected value: " + (getClockWise() + 2) % getConnectors().length);
        };
    }

    private String drawDoubleEngine(){
        return switch ((getClockWise() + 2) % getConnectors().length) {
            case 0 -> " " + brown + ArrowUp + reset + " " + brown + ArrowUp + reset + " ";
            case 1 -> " " + brown + ArrowRight + reset + " " + brown + ArrowRight + reset + " ";
            case 2 -> " " + brown + ArrowDown + reset + " " + brown + ArrowDown + reset + " ";
            case 3 -> " " + brown + ArrowLeft + reset + " " + brown + ArrowLeft + reset + " ";
            default -> throw new IllegalStateException("Unexpected value: " + (getClockWise() + 2) % getConnectors().length);
        };
    }

    @Override
    public TilesTypeView getType() {
        return doubleEngine ? TilesTypeView.DOUBLE_ENGINE : TilesTypeView.SINGLE_ENGINE;
    }

    @Override
    public EngineView clone() {
        EngineView copy = new EngineView(this.getID(), this.getConnectors(), this.getClockWise(), this.doubleEngine ? 2 : 1);
        copy.setIsWrong(this.getIsWrong());
        return copy;
    }
}
