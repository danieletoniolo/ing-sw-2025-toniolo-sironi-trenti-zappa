package it.polimi.ingsw.view.miniModel.components.crewmembers;

import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public enum CrewView {
    HUMAN(0),
    BROWALIEN(1),
    PURPLEALIEN(2);

    private final List<MiniModelObserver> listeners = new ArrayList<>();
    private final int value;
    private final String brown = "\033[38;5;220m";
    private final String purple = "\033[35m";
    private final String reset = "\033[0m";

    CrewView(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
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


    public static CrewView fromValue(int value) {
        for (CrewView crew : values()) {
            if (crew.value == value) {
                return crew;
            }
        }
        throw new IllegalArgumentException("No GoodView with value " + value);
    }

    public Image drawGui(Image image, int x, int y, int size, int numberOfCrewMembers) {
        Canvas canvas = new Canvas(image.getWidth(), image.getHeight());
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(image, 0, 0);

        Color fillColor = switch (this) {
            case HUMAN -> Color.WHITE;
            case BROWALIEN -> Color.rgb(95, 75, 25);
            case PURPLEALIEN -> Color.rgb(133, 25, 133);
        };

        Color borderColor = Color.BLACK;
        double radius = size / 8;

        gc.setLineWidth(2);
        gc.setStroke(borderColor);
        gc.setFill(fillColor);

        if (this == CrewView.HUMAN && numberOfCrewMembers == 2) {
            double spacing = 5;
            double offset = radius + (spacing / 2);

            gc.fillOval(x - offset - radius, y - radius, radius * 2, radius * 2);
            gc.strokeOval(x - offset - radius, y - radius, radius * 2, radius * 2);

            gc.fillOval(x + offset - radius, y - radius, radius * 2, radius * 2);
            gc.strokeOval(x + offset - radius, y - radius, radius * 2, radius * 2);
        } else {
            gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
            gc.strokeOval(x - radius, y - radius, radius * 2, radius * 2);
        }

        // Scrivi il disegno finale in una nuova WritableImage
        WritableImage result = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        canvas.snapshot(null, result);
        return result;
    }

    public String drawTui() {
        return switch (this) {
            case HUMAN -> "☺";
            case BROWALIEN -> brown + "&" + reset;
            case PURPLEALIEN -> purple + "&" + reset;
        };
    }
}
