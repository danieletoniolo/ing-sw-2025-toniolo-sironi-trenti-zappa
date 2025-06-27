package it.polimi.ingsw.view.miniModel.components;

public class ShieldView extends ComponentView {
    public static final String UpShield = "∩";
    public static final String DownShield = "∪";
    public static final String LeftShield = "(";
    public static final String RightShield = ")";
    private static final String lightGreen = "\033[92m";
    private static final String reset = "\033[0m";
    private boolean[] shields;

    public ShieldView(int ID, int[] connectors, int clockWise, boolean[] shields) {
        super(ID, connectors, clockWise);
        this.shields = shields;
    }

    public boolean[] getShields() {
        return shields;
    }

    public void setShields(boolean[] shields) {
        this.shields = shields;
    }

    @Override
    public String drawLineTui(int line) {
        if (isCovered()) return super.drawLineTui(line);

        return switch (line) {
            case 0, 2 -> super.drawLineTui(line);
            case 1 -> super.drawLeft(line) + drawShield() + super.drawRight(line);
            default -> throw new IllegalStateException("Unexpected value: " + line);
        };
    }

    private String drawShield() {
        StringBuilder str = new StringBuilder();
        str.append(" ");
        if (shields[1]) {
            str.append(lightGreen).append(LeftShield).append(reset);
            if (shields[0]) str.append(lightGreen).append(" ").append(UpShield).append(reset);
            if (shields[2]) str.append(lightGreen).append(" ").append(DownShield).append(reset);
        }
        if (shields[3]) {
            if (shields[0]) str.append(lightGreen).append(UpShield).append(reset);
            if (shields[2]) str.append(lightGreen).append(DownShield).append(reset);
            str.append(" ").append(lightGreen).append(RightShield).append(reset);
        }
        str.append(" ");
        return str.toString();
    }

    @Override
    public ComponentTypeView getType() {
        return ComponentTypeView.SHIELD;
    }

    @Override
    public ShieldView clone() {
        ShieldView copy = new ShieldView(this.getID(), this.getConnectors(), this.getClockWise(), this.shields);
        copy.setIsWrong(this.getIsWrong());
        return copy;
    }
}
