package view.structures.components;

public abstract class ComponentView {
    public static String Up0 = "╭───────────╮";
    public static String Up1 = "╭──── ↑ ────╮";
    public static String Up2 = "╭ ↑ ───── ↑ ╮";
    public static String Up3 = "╭ ↑ ─ ↑ ─ ↑ ╮";

    public static String Down0 = "╰───────────╯";
    public static String Down1 = "╰──── ↓ ────╯";
    public static String Down2 = "╰ ↓ ───── ↓ ╯";
    public static String Down3 = "╰ ↓ ─ ↓ ─ ↓ ╯";

    public static String ArrowRight = "→";
    public static String ArrowDown = "↓";
    public static String ArrowLeft = "←";
    public static String ArrowUp = "↑";

    public static String clean = "│           │";

    public static String[] Left0 = {
            ".",
            "│",
            "│",
            "│",
            "."
    };

    public static String[] Left1 = {
            ".",
            "│",
            "←",
            "│",
            "."
    };

    public static String[] Left2 = {
            ".",
            "←",
            "│",
            "←",
            "."
    };

    public static String[] Left3 = {
            ".",
            "←",
            "←",
            "←",
            "."
    };

    public static String[] Right0 = {
            ".",
            "│",
            "│",
            "│",
            "."
    };

    public static String[] Right1 = {
            ".",
            "│",
            "→",
            "│",
            "."
    };

    public static String[] Right2 = {
            ".",
            "→",
            "│",
            "→",
            "."
    };

    public static String[] Right3 = {
            ".",
            "→",
            "→",
            "→",
            "."
    };

    private int[] currentConnectors;
    private int ID;
    private int clockwiseRotation;
    private boolean covered;

    public ComponentView(int ID, int[] connectors) {
        this.ID = ID;
        this.currentConnectors = connectors;
        this.clockwiseRotation = 0;
        this.covered = true;
    }

    public void drawComponentGui(){}

    public static int getRowsToDraw() {
        return 5;
    }

    public String drawLineTui(int line) throws IndexOutOfBoundsException{
        return switch (line) {
            case 0 -> isCovered() || currentConnectors[0] == 0 ? Up0 : currentConnectors[0] == 1 ? Up1 : currentConnectors[0] == 2 ? Up2 : Up3;
            case 1, 3 -> drawLeft(line) + "           " + drawRight(line);
            case 2 -> drawLeft(line) + "  Covered  " + drawRight(line);
            case 4 -> isCovered() || currentConnectors[2] == 0 ? Down0 : currentConnectors[2] == 1 ? Down1 : currentConnectors[2] == 2 ? Down2 : Down3;
            default -> throw new IndexOutOfBoundsException("Unexpected value: " + line);
        };
    }

    protected String drawLeft(int line) {
        if (isCovered()) return Left0[line];
        return switch (currentConnectors[1]) {
            case 0 -> Left0[line];
            case 1 -> Left1[line];
            case 2 -> Left2[line];
            case 3 -> Left3[line];
            default -> throw new IndexOutOfBoundsException("Unexpected value: " + currentConnectors[0]);
        };
    }

    protected String drawRight(int line) {
        if (isCovered()) return Right0[line];
        return switch (currentConnectors[3]) {
            case 0 -> Right0[line];
            case 1 -> Right1[line];
            case 2 -> Right2[line];
            case 3 -> Right3[line];
            default -> throw new IndexOutOfBoundsException("Unexpected value: " + currentConnectors[0]);
        };
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }

    public void setClockwiseRotation(int clockwiseRotation) {
        this.clockwiseRotation = clockwiseRotation;
    }

    public int getClockwiseRotation() {
        return clockwiseRotation;
    }

    public void setCovered(boolean covered) {
        this.covered = covered;
    }

    public boolean isCovered() {
        return covered;
    }
}
