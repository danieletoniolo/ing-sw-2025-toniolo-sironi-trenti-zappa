package view.structures.components;

public class EngineView extends ComponentView {
    private String brown = "\033[38;5;220m";
    private String reset = "\033[0m";
    private boolean doubleEngine;
    private int engineRotation;

    public EngineView(int ID, int[] connectors, int power, int engineRotation) {
        super(ID, connectors);
        this.doubleEngine = power == 2;
        this.engineRotation = engineRotation;
    }

    public void setEngineRotation(int engineRotation) {
        this.engineRotation = engineRotation;
    }

    @Override
    public void drawGui() {
        //TODO: Implement the GUI drawing logic for the Engine component here
    }

    @Override
    public String drawLineTui(int line) {
        if (isCovered()) return super.drawLineTui(line);

        return switch (line) {
            case 0, 2 -> super.drawLineTui(line);
            case 1 -> super.drawLeft(line) + (doubleEngine ? drawDoubleEngine() : drawSingleEngine()) + super.drawRight(line);
            default -> throw new IndexOutOfBoundsException("Unexpected value: " + line);
        };
    }

    private String drawSingleEngine(){
        return switch (engineRotation) {
            case 0 -> "  " + brown + ArrowUp + reset + "  ";
            case 1 -> "  " + brown + ArrowRight + reset + "  ";
            case 2 -> "  " + brown + ArrowDown + reset + "  ";
            case 3 -> "  " + brown + ArrowLeft + reset + "  ";
            default -> throw new IllegalStateException("Unexpected value: " + engineRotation);
        };
    }

    private String drawDoubleEngine(){
        return switch (engineRotation) {
            case 0 -> " " + brown + ArrowUp + reset + " " + brown + ArrowUp + reset + " ";
            case 1 -> " " + brown + ArrowRight + reset + " " + brown + ArrowRight + reset + " ";
            case 2 -> " " + brown + ArrowDown + reset + " " + brown + ArrowDown + reset + " ";
            case 3 -> " " + brown + ArrowLeft + reset + " " + brown + ArrowLeft + reset + " ";
            default -> throw new IllegalStateException("Unexpected value: " + engineRotation);
        };
    }
}
