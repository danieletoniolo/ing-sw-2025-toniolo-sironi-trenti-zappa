package it.polimi.ingsw.view.miniModel.components;

import it.polimi.ingsw.view.gui.controllers.components.GenericComponentController;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GenericComponentView extends ComponentView {
    private final List<MiniModelObserver> listeners = new ArrayList<>();

    public GenericComponentView() {
        super(-1, new int[]{0, 0, 0, 0}, 0);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/genericComponent.fxml"));
            Node root = loader.load();

            GenericComponentController controller = loader.getController();
            controller.setGenericComponentModel(this);

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
