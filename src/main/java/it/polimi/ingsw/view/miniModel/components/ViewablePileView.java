package it.polimi.ingsw.view.miniModel.components;

import it.polimi.ingsw.view.gui.controllers.misc.ViewablePileController;
import it.polimi.ingsw.view.miniModel.MiniModelObservable;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.Structure;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ViewablePileView implements Structure, MiniModelObservable {
    private final List<ComponentView> viewableComponents;
    private final List<MiniModelObserver> observers;
    private final int cols = 21;

    public ViewablePileView() {
        this.viewableComponents = new ArrayList<>();
        this.observers = new ArrayList<>();
    }

    @Override
    public void registerObserver(MiniModelObserver observer) {
        synchronized (observers) {
            observers.add(observer);
        }
    }

    @Override
    public void unregisterObserver(MiniModelObserver observer) {
        synchronized (observers) {
            observers.remove(observer);
        }
    }

    @Override
    public void notifyObservers() {
        synchronized (observers) {
            for (MiniModelObserver observer : observers) {
                observer.react();
            }
        }
    }

    public Node getNode() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/misc/viewablePile.fxml"));
            Node node = loader.load();

            ViewablePileController controller = loader.getController();
            controller.setModel(this);

            return node;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void addComponent(ComponentView component) {
        viewableComponents.add(component);
        notifyObservers();
    }

    public void removeComponent(ComponentView component) {
        viewableComponents.remove(component);
        notifyObservers();
    }

    public ArrayList<ComponentView> getViewableComponents() {
        return new ArrayList<>(viewableComponents);
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
