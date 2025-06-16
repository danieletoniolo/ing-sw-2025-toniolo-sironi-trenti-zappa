package it.polimi.ingsw.view.miniModel.components;

import it.polimi.ingsw.view.miniModel.MiniModelListener;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public class ConnectorsView extends ComponentView {
    private final List<MiniModelListener> listeners = new ArrayList<>();

    public ConnectorsView(int ID, int[] connectors, int clockWise) {
        super(ID, connectors, clockWise);
    }

    public void addListener(MiniModelListener listener) {
        listeners.add(listener);
    }

    public void removeListener(MiniModelListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (MiniModelListener listener : listeners) {
            listener.onModelChanged();
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
            case 0, 2  -> super.drawLineTui(line);
            case 1 -> super.drawLeft(line) + "  ╬  " + super.drawRight(line);
            default -> throw new IndexOutOfBoundsException("Unexpected value: " + line);
        };
    }

    @Override
    public TilesTypeView getType() {
        return TilesTypeView.CONNECTORS;
    }

    @Override
    public ConnectorsView clone() {
        ConnectorsView copy = new ConnectorsView(this.getID(), this.getConnectors(), this.getClockWise());
        copy.setIsWrong(this.getIsWrong());
        return copy;
    }
}
