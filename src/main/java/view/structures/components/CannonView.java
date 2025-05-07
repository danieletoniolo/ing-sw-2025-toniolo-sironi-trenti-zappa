package view.structures.components;

public class CannonView extends ComponentView {
    private float power;

    public CannonView(int ID, int[] connectors, float power) {
        super(ID, connectors);
        this.power = power;
    }

    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        this.power = power;
    }

    @Override
    public void drawComponentGui() {
        //TODO: Implement the GUI drawing logic for the Cannon component here
    }

    @Override
    public String drawLineTui(int line) {
        if (isCovered()) return super.drawLineTui(line);

        return switch (line) {
            case 0, 4 -> super.drawLineTui(line);
            case 1 -> super.drawLeft(line) + "  Cannon   " + super.drawRight(line);
            case 3 -> super.drawLeft(line) + "  Pow:" + (getClockwiseRotation() == 0 ? getPower() : getPower()/2) + "  " + super.drawRight(line);
            case 2 -> super.drawLeft(line) + "     " + drawCannon() + "     " + super.drawRight(line);
            default -> throw new IndexOutOfBoundsException("Unexpected value: " + line);
        };
    }

    private String drawCannon(){
        return switch (getClockwiseRotation()) {
            case 0 -> ArrowUp;
            case 1 -> ArrowRight;
            case 2 -> ArrowDown;
            case 3 -> ArrowLeft;
            default -> throw new IllegalStateException("Unexpected value: " + getClockwiseRotation());
        };
    }
}
