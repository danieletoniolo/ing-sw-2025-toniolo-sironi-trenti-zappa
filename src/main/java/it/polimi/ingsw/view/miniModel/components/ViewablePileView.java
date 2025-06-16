package it.polimi.ingsw.view.miniModel.components;

import it.polimi.ingsw.view.gui.controllers.components.ViewablePileController;
import it.polimi.ingsw.view.miniModel.MiniModelListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ViewablePileView {
    private final List<ComponentView> viewableComponents = new ArrayList<>();
    private final List<MiniModelListener> listeners = new ArrayList<>();

    public void addComponent(ComponentView component) {
        viewableComponents.add(component);
        notifyListeners();
    }

    public void removeComponent(ComponentView component) {
        viewableComponents.remove(component);
        notifyListeners();
    }

    public ArrayList<ComponentView> getViewableComponents() {
        return new ArrayList<>(viewableComponents);
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

    public Node createGuiNode() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/viewablepile.fxml"));
            Node root = loader.load();

            ViewablePileController controller = loader.getController();
            controller.setComponentPile(this);

            return root;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
