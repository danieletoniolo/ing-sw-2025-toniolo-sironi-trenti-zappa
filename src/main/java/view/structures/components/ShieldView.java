package view.structures.components;

public class ShieldView extends ComponentView {
    public static String UpShield = " ⌢ ";
    public static String DownShield = " ⌣ ";
    public static String LeftShield = "(";
    public static String RightShield = ")";

    private boolean[] shields;

    public ShieldView(int ID, int[] connectors, boolean[] shields) {
        super(ID, connectors);
        this.shields = shields;
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
        StringBuilder str = new StringBuilder();
        str.append("   ");
        for (int i = 0; i < shields.length; i += 2) {
            if (i == 0 && shields[i]) str.append(UpShield);
            if (i == 2 && shields[i]) str.append(DownShield);
        }
        str.append("  ");
        for (int i = 1; i < shields.length; i += 2) {
            if (i == 1 && shields[i]) str.append(LeftShield);
            if (i == 3 && shields[i]) str.append(RightShield);
        }
        str.append("   ");

        return str.toString();
    }
}
