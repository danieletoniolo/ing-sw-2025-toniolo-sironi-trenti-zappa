package it.polimi.ingsw.view.miniModel.components;

public class EngineView extends ComponentView {
    private static final String brown = "\033[38;5;220m";
    private static final String reset = "\033[0m";
    private final boolean doubleEngine;

    public EngineView(int ID, int[] connectors, int clockWise, int power) {
        super(ID, connectors, clockWise);
        this.doubleEngine = power == 2;
    }

    public boolean isDoubleEngine() {
        return doubleEngine;
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
        return switch ((getClockWise() + 2) % getConnectors().length) {
            case 0 -> "  " + brown + ArrowUp + reset + "  ";
            case 1 -> "  " + brown + ArrowRight + reset + "  ";
            case 2 -> "  " + brown + ArrowDown + reset + "  ";
            case 3 -> "  " + brown + ArrowLeft + reset + "  ";
            default -> throw new IllegalStateException("Unexpected value: " + (getClockWise() + 2) % getConnectors().length);
        };
    }

    private String drawDoubleEngine(){
        return switch ((getClockWise() + 2) % getConnectors().length) {
            case 0 -> " " + brown + ArrowUp + reset + " " + brown + ArrowUp + reset + " ";
            case 1 -> " " + brown + ArrowRight + reset + " " + brown + ArrowRight + reset + " ";
            case 2 -> " " + brown + ArrowDown + reset + " " + brown + ArrowDown + reset + " ";
            case 3 -> " " + brown + ArrowLeft + reset + " " + brown + ArrowLeft + reset + " ";
            default -> throw new IllegalStateException("Unexpected value: " + (getClockWise() + 2) % getConnectors().length);
        };
    }

    @Override
    public ComponentTypeView getType() {
        return doubleEngine ? ComponentTypeView.DOUBLE_ENGINE : ComponentTypeView.SINGLE_ENGINE;
    }

    @Override
    public EngineView clone() {
        EngineView copy = new EngineView(this.getID(), this.getConnectors(), this.getClockWise(), this.doubleEngine ? 2 : 1);
        copy.setIsWrong(this.getIsWrong());
        return copy;
    }
}
