package it.polimi.ingsw.view.miniModel.good;

import it.polimi.ingsw.view.miniModel.board.LevelView;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Enum representing the different types of goods in the view.
 * Each good has an associated integer value and color codes for TUI representation.
 */
public enum GoodView {
    BLUE(1), GREEN(2), YELLOW(3), RED(4);

    private final int value;
    // ANSI color codes for TUI representation
    private final String blue =   "\033[34m";
    private final String green =  "\033[32m";
    private final String yellow = "\033[33m";
    private final String red =    "\033[31m";
    private final String reset =  "\033[0m";
    private final String Cell = "■";

    /**
     * Constructor for GoodView.
     * @param value the integer value associated with the good
     */
    GoodView(int value) {
        this.value = value;
    }

    /**
     * Gets the integer value associated with the good.
     * @return the value of the good
     */
    public int getValue() {
        return value;
    }

    /**
     * Draws the good on a GUI image at the specified position.
     * @param inputImage the input image to draw on
     * @param centerX the x-coordinate of the center
     * @param centerY the y-coordinate of the center
     * @param size the size of the square to draw
     * @param numberOfGoods the total number of goods to display
     * @param position the position index of this good
     * @return the modified image with the good drawn
     */
    public Image drawGui(Image inputImage, int centerX, int centerY, int size, int numberOfGoods, int position) {
        int width = (int) inputImage.getWidth();
        int height = (int) inputImage.getHeight();

        WritableImage outputImage = new WritableImage(inputImage.getPixelReader(), width, height);
        PixelWriter writer = outputImage.getPixelWriter();

        Color color = switch (this) {
            case RED -> Color.RED;
            case YELLOW -> Color.GREEN;
            case GREEN -> Color.YELLOW;
            case BLUE -> Color.BLUE;
        };

        int spacing = 22;

        if(numberOfGoods == 2){
            int offsetY = (size + spacing) / 2;

            int squareCenterY;
            if (position == 1) { // sopra
                squareCenterY = centerY - offsetY;
            } else if (position == 2) { // sotto
                squareCenterY = centerY + offsetY;
            } else {
                squareCenterY = centerY;
            }

            drawSquare(writer, width, height, centerX, squareCenterY, size, color);
        } else if (numberOfGoods == 3) {
            int offsetY = (size + spacing) / 2;
            int offsetX = (size + spacing) / 2;

            int squareCenterY;
            int squareCenterX;
            if (position == 1) {
                squareCenterY = centerY - offsetY;
                squareCenterX = centerX + offsetX + 2;
            } else if (position == 2) {
                squareCenterY = centerY + offsetY;
                squareCenterX = centerX + offsetX + 2;
            } else {
                squareCenterY = centerY;
                squareCenterX = centerX - offsetX + 2;
            }

            drawSquare(writer, width, height, squareCenterX, squareCenterY, size, color);
        } else {
            drawSquare(writer, width, height, centerX, centerY, size, color);
        }


        return outputImage;
    }

    /**
     * Draws a filled square with a black border on the given PixelWriter.
     * The square is centered at (centerX, centerY) with the specified size and color.
     *
     * @param writer   the PixelWriter to draw on
     * @param imgWidth the width of the image
     * @param imgHeight the height of the image
     * @param centerX  the x-coordinate of the square's center
     * @param centerY  the y-coordinate of the square's center
     * @param size     the size (width and height) of the square
     * @param color    the fill color of the square
     */
    private static void drawSquare(PixelWriter writer, int imgWidth, int imgHeight, int centerX, int centerY, int size, Color color) {
        int halfSize = size / 2;
        int startX = centerX - halfSize;
        int startY = centerY - halfSize;

        for (int y = startY; y < startY + size; y++) {
            for (int x = startX; x < startX + size; x++) {
                if (x >= 0 && y >= 0 && x < imgWidth && y < imgHeight) {
                    if (x == startX || x == startX + size - 1 || y == startY || y == startY + size - 1) {
                        writer.setColor(x, y, Color.BLACK);
                    } else {
                        writer.setColor(x, y, color);
                    }
                }
            }
        }
    }

    /**
     * Returns the GoodView enum constant corresponding to the given integer value.
     *
     * @param value the integer value associated with the good
     * @return the GoodView constant matching the value
     * @throws IllegalArgumentException if no GoodView with the specified value exists
     */
    public static GoodView fromValue(int value) {
        for (GoodView good : values()) {
            if (good.value == value) {
                return good;
            }
        }
        throw new IllegalArgumentException("No GoodView with value " + value);
    }

    /**
     * Returns a string representation of the good for TUI display,
     * including the appropriate ANSI color codes.
     *
     * @return the colored string representation of the good for TUI
     */
    public String drawTui() {
        return switch (this) {
            case BLUE -> blue + Cell + reset;
            case GREEN -> green + Cell + reset;
            case YELLOW -> yellow + Cell + reset;
            case RED -> red + Cell + reset;
        };
    }
}
