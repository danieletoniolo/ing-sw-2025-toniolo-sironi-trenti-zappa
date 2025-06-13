package it.polimi.ingsw.view.miniModel.components.crewmembers;

import it.polimi.ingsw.view.miniModel.good.GoodView;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

public enum CrewMembers {
    HUMAN(0),
    BROWALIEN(1),
    PURPLEALIEN(2);

    private final int value;
    private final String brown = "\033[38;5;220m";
    private final String purple = "\033[35m";
    private final String reset = "\033[0m";

    CrewMembers(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static CrewMembers fromValue(int value) {
        for (CrewMembers crew : values()) {
            if (crew.value == value) {
                return crew;
            }
        }
        throw new IllegalArgumentException("No GoodView with value " + value);
    }

    public BufferedImage drawGui(BufferedImage image, int x, int y, int size) {
        //TODO: Portare ad Image
        Graphics2D g2d = image.createGraphics();

        // Colore di riempimento in base al tipo
        Color fillColor = switch (this) {
            case HUMAN -> Color.WHITE;
            case BROWALIEN -> new Color(95, 75, 25);
            case PURPLEALIEN -> new Color(133, 25, 133);
        };

        Color borderColor = Color.BLACK;
        g2d.setStroke(new BasicStroke(3));

        int radius = size / 2;

        if (this == CrewMembers.HUMAN) {
            int spacing = 5;
            int offset = radius + (spacing / 2);

            Ellipse2D.Double leftCircle = new Ellipse2D.Double(
                    x - offset - radius, y - radius, radius * 2, radius * 2
            );

            Ellipse2D.Double rightCircle = new Ellipse2D.Double(
                    x + offset - radius, y - radius, radius * 2, radius * 2
            );

            g2d.setColor(fillColor);
            g2d.fill(leftCircle);
            g2d.fill(rightCircle);

            g2d.setColor(borderColor);
            g2d.draw(leftCircle);
            g2d.draw(rightCircle);
        } else {
            Ellipse2D.Double circle = new Ellipse2D.Double(
                    x - radius, y - radius, radius * 2, radius * 2
            );

            g2d.setColor(fillColor);
            g2d.fill(circle);

            g2d.setColor(borderColor);
            g2d.draw(circle);
        }

        g2d.dispose();
        return image;
    }

    public String drawTui() {
        return switch (this) {
            case HUMAN -> "☺";
            case BROWALIEN -> brown + "&" + reset;
            case PURPLEALIEN -> purple + "&" + reset;
        };
    }

}
