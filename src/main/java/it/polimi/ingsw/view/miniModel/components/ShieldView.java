package it.polimi.ingsw.view.miniModel.components;

public class ShieldView extends ComponentView {
    public static String UpShield = "∩";
    public static String DownShield = "∪";
    public static String LeftShield = "(";
    public static String RightShield = ")";
    private String lightGreen = "\033[92m";
    private String reset = "\033[0m";
    private final boolean[] shields;

    public ShieldView(int ID, int[] connectors, boolean[] shields) {
        super(ID, connectors);
        this.shields = shields;
    }

    @Override
    public void drawGui(){
        //TODO: Implement the GUI drawing logic for the Shield component here
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
    public TilesTypeView getType() {
        return TilesTypeView.SHIELD;
    }

    @Override
    public ShieldView clone() {
        return new ShieldView(this.getID(), this.getConnectors(), this.shields);
    }
}
