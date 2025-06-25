package it.polimi.ingsw.view.miniModel.components;

import it.polimi.ingsw.view.gui.controllers.components.ComponentController;
import it.polimi.ingsw.view.miniModel.MiniModelObservable;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.Structure;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import org.javatuples.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class ComponentView implements Structure, MiniModelObservable {
    public static String Up0 =   "╭─────╮";
    public static String Up1 =   "╭──|──╮";
    public static String Up2 =   "╭─|─|─╮";
    public static String Up3 =   "╭─|||─╮";

    public static String Down0 = "╰─────╯";
    public static String Down1 = "╰──|──╯";
    public static String Down2 = "╰─|─|─╯";
    public static String Down3 = "╰─|||─╯";

    public static String ArrowRight = "→";
    public static String ArrowDown = "↓";
    public static String ArrowLeft = "←";
    public static String ArrowUp = "↑";

    public static String[] Side0 = {
            ".",
            "│",
            "."
    };

    public static String[] Side1 = {
            ".",
            "─",
            "."
    };

    public static String[] Side2 = {
            ".",
            "═",
            "."
    };

    public static String[] Side3 = {
            ".",
            "≣",
            "."
    };

    private int[] connectors;
    private int ID;
    private boolean covered;
    private int row;
    private int col;
    private boolean isWrong;
    private final String red = "\033[31m";
    private final String reset = "\033[0m";
    private int clockWise;
    private final List<MiniModelObserver> observers;
    private Pair<Node, ComponentController> componentPair;

    public ComponentView(int ID, int[] connectors, int clockWise) {
        this.ID = ID;
        this.connectors = connectors;
        this.covered = false;
        this.clockWise = clockWise;
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

    public Pair<Node, ComponentController> getNode() {
        try {
            if (componentPair != null) return componentPair;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/component.fxml"));
            Parent root = loader.load();

            ComponentController controller = loader.getController();
            controller.setModel(this);

            componentPair = new Pair<>(root, controller);
            return componentPair;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Image rotateImage(Image inputImage) {
        int width = (int) inputImage.getWidth();
        int height = (int) inputImage.getHeight();

        WritableImage outputImage = new WritableImage(height, width);
        PixelReader reader = inputImage.getPixelReader();
        PixelWriter writer = outputImage.getPixelWriter();

        // Ruota in senso orario
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                writer.setArgb(height - y - 1, x, reader.getArgb(x, y));
            }
        }
        return outputImage;
    }

    // TUI methods
    @Override
    public String drawLineTui(int line) throws IndexOutOfBoundsException{
        String str = switch (line) {
            case 0 -> isCovered() || connectors[0] == 0 ? Up0 : connectors[0] == 1 ? Up1 : connectors[0] == 2 ? Up2 : Up3;
            case 1 -> drawLeft(line) + "  ?  " + drawRight(line);
            case 2 -> isCovered() || connectors[2] == 0 ? Down0 : connectors[2] == 1 ? Down1 : connectors[2] == 2 ? Down2 : Down3;
            default -> throw new IndexOutOfBoundsException("Unexpected value: " + line);
        };

        return isWrong ? red + str + reset : str;
    }

    public static int getRowsToDraw() {
        return 3;
    }

    protected String drawLeft(int line) {
        if (isCovered()) return Side0[line];
        String str = switch (connectors[1]) {
            case 0 -> Side0[line];
            case 1 -> Side1[line];
            case 2 -> Side2[line];
            case 3 -> Side3[line];
            default -> throw new IndexOutOfBoundsException("Unexpected value: " + connectors[0]);
        };

        return isWrong ? red + str + reset : str;
    }

    protected String drawRight(int line) {
        if (isCovered()) return Side0[line];
        String str = switch (connectors[3]) {
            case 0 -> Side0[line];
            case 1 -> Side1[line];
            case 2 -> Side2[line];
            case 3 -> Side3[line];
            default -> throw new IndexOutOfBoundsException("Unexpected value: " + connectors[0]);
        };

        return isWrong ? red + str + reset : str;
    }

    public void setIsWrong(boolean isWrong) {
        this.isWrong = isWrong;
    }

    public boolean getIsWrong() {
        return isWrong;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }

    public void setConnectors(int[] connectors) {
        this.connectors = connectors;
    }

    public int[] getConnectors() {
        return connectors;
    }

    public void setCovered(boolean covered) {
        this.covered = covered;
    }

    public boolean isCovered() {
        return covered;
    }

    public abstract TilesTypeView getType();

    public void setRow(int row) {
        this.row = row + 1;
    }

    public int getRow() {
        return row;
    }

    public void setCol(int col) {
        this.col = col + 1;
    }

    public int getCol() {
        return col;
    }

    public abstract ComponentView clone();

    public void rotate() {
        this.clockWise++;
        this.clockWise = this.clockWise % this.connectors.length;
    }

    public int getClockWise() {
        return clockWise;
    }
}
