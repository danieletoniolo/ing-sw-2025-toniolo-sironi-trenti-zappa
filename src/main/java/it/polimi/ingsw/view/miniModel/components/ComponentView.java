package it.polimi.ingsw.view.miniModel.components;

import it.polimi.ingsw.view.miniModel.Structure;

import java.io.Serializable;

public abstract class ComponentView implements Structure {
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

    public static String clean = "│     │";

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
    private String red = "\033[31m";
    private String reset = "\033[0m";

    public ComponentView(int ID, int[] connectors) {
        this.ID = ID;
        this.connectors = connectors;
        this.covered = false;
    }

    @Override
    public void drawGui(){}

    public static int getRowsToDraw() {
        return 3;
    }

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
        this.row = row;
    }

    public int getRow() {
        return row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getCol() {
        return col;
    }

    public abstract ComponentView clone();
}
