package it.polimi.ingsw.view.miniModel.components;

import it.polimi.ingsw.view.gui.controllers.components.ViewablePileController;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.Structure;
import it.polimi.ingsw.view.tui.TerminalUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ViewablePileView implements Structure {
    private final List<ComponentView> viewableComponents = new ArrayList<>();
    private final List<MiniModelObserver> listeners = new ArrayList<>();
    private final int cols = 22;

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

    public void addListener(MiniModelObserver listener) {
        listeners.add(listener);
    }

    public void removeListener(MiniModelObserver listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (MiniModelObserver listener : listeners) {
            listener.react();
        }
    }

    public Node createGuiNode() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/viewablepile.fxml"));
            Node root = loader.load();

            ViewablePileController controller = loader.getController();
            controller.setComponentPile(this);

            return root;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getCols() {
        return cols;
    }

    public int getRowsToDraw() {
        int rows = 1 + (viewableComponents.size() / cols) * ComponentView.getRowsToDraw();
        if (viewableComponents.size() % cols != 0 || viewableComponents.isEmpty()) {
            rows += ComponentView.getRowsToDraw();
        }
        return rows;
    }

    @Override
    public String drawLineTui(int l) {
        StringBuilder line = new StringBuilder();

        if (l == 0) {
            line.append("   ");
            for (int i = 0; i < cols; i++) {
                line.append("  ").append((i + 1) / 10 == 0 ? " " + (i + 1) : (i + 1)).append("   ");
            }
            return line.toString();
        }


        l -= 1; // Adjust for header line
        int h = l / ComponentView.getRowsToDraw();
        int i = l % ComponentView.getRowsToDraw();

        if (h < viewableComponents.size() / cols) {
            if (i == 1) {
                line.append(((h + 1) / 10 == 0 ? ((h + 1) + "  ") : (h + 1) + " "));
            } else {
                line.append("   ");
            }
            for (int k = 0; k < cols; k++) {
                line.append(viewableComponents.get(h * cols + k).drawLineTui(i));
            }
            return line.toString();
        }

        if (viewableComponents.size() % cols != 0 || viewableComponents.isEmpty()) {
            if (i == 1) {
                line.append(((viewableComponents.size() / cols + 1) / 10 == 0 ? ((viewableComponents.size() / cols + 1) + "  ") : ((viewableComponents.size() / cols + 1) + " ")));
            } else {
                line.append("   ");
            }
            for (int k = 0; k < viewableComponents.size() % cols; k++) {
                line.append(viewableComponents.get((viewableComponents.size() / cols) * cols + k).drawLineTui(i));
            }
            return line.toString();
        }

        return line.toString();
    }
}
