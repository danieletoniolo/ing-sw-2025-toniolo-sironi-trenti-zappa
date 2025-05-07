package view.structures.components;

public class ShieldView extends ComponentView {

    public ShieldView(int ID, int[] connecotrs) {
        super(ID, connecotrs);
    }

    @Override
    public void drawComponentGui(){
        //TODO: Implement the GUI drawing logic for the Shield component here
    }

    @Override
    public String drawLineTui(int line) {
        if (isCovered()) return super.drawLineTui(line);

        return switch (line) {
            case 0, 3, 4 -> super.drawLineTui(line);
            case 1 -> super.drawLeft(line) + "  Shield   " + super.drawRight(line);
            case 2 -> super.drawLeft(line) + drawShield() + super.drawRight(line);
            default -> throw new IllegalStateException("Unexpected value: " + line);
        };
    }

    private String drawShield() {
        return switch (getClockwiseRotation()) {
            case 0 -> "    ⌢  )    ";
            case 1 -> "    ⌣  )    ";
            case 2 -> "   (  ⌣     ";
            case 3 -> "   (  ⌢     ";
            default -> throw new IllegalStateException("Unexpected value: " + getClockwiseRotation());
        };
    }
}
