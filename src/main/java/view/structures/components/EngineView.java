package view.structures.components;

public class EngineView extends ComponentView {
    private int power;

    public EngineView(int ID, int[] connectors, int power) {
        super(ID, connectors);
        this.power = power;
    }

    public float getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    @Override
    public void drawComponentGui() {
        //TODO: Implement the GUI drawing logic for the Engine component here
    }

    @Override
    public String drawLineTui(int line) {
        if (isCovered()) return super.drawLineTui(line);

        return switch (line) {
            case 0, 3, 4 -> super.drawLineTui(line);
            case 1 -> super.drawLeft(line) + "  Engine   " + super.drawRight(line);
            case 2 -> super.drawLeft(line) + "    " + drawEngine() + "    " + super.drawRight(line);
            default -> throw new IndexOutOfBoundsException("Unexpected value: " + line);
        };
    }

    private String drawEngine(){
        return switch (getClockwiseRotation()) {
            case 0 -> power == 1 ? " " + ArrowUp + " " : ArrowUp + " " + ArrowUp;
            case 1 -> power == 1 ? " " + ArrowRight + " " : ArrowRight + " " + ArrowRight;
            case 2 -> power == 1 ? " " + ArrowDown + " " : ArrowDown + " " + ArrowDown;
            case 3 -> power == 1 ? " " + ArrowLeft + " " : ArrowLeft + " " + ArrowLeft;
            default -> throw new IllegalStateException("Unexpected value: " + getClockwiseRotation());
        };
    }
}
