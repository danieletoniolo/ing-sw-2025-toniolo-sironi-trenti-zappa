package it.polimi.ingsw.view.miniModel.spaceship;

import it.polimi.ingsw.view.miniModel.Structure;
import it.polimi.ingsw.view.miniModel.components.ComponentView;

import java.util.ArrayList;

public class DiscardReservedPileView implements Structure {
    public String UpReserved1 =     "╭──────";
    public String LeftReserved2 =   "│      ";
    public String DownReserved1 =   "╰──────";

    public String UpReserved2 =     "──────╮";
    public String RightReserved2 =  "      │";
    public String DownReserved2 =   "──────╯";

    private ArrayList<ComponentView> reserved;

    public DiscardReservedPileView() {
        reserved = new ArrayList<>();
    }

    @Override
    public void drawGui() {

    }

    public static int getRowsToDraw() {
        return ComponentView.getRowsToDraw();
    }

    @Override
    public String drawLineTui(int line) {
        StringBuilder str = new StringBuilder();

        switch (line) {
            case 0:
                str.append(reserved.isEmpty() ? UpReserved1 : reserved.getFirst().drawLineTui(line));
                str.append(reserved.isEmpty() || reserved.size() == 1 ? UpReserved2 : reserved.getLast().drawLineTui(line));
                break;
            case 1:
                str.append(reserved.isEmpty() ? LeftReserved2 : reserved.getFirst().drawLineTui(line));
                str.append(reserved.isEmpty() || reserved.size() == 1 ? RightReserved2 : reserved.getLast().drawLineTui(line));
                break;
            case 2:
                str.append(reserved.isEmpty() ? DownReserved1 : reserved.getFirst().drawLineTui(line));
                str.append(reserved.isEmpty() || reserved.size() == 1 ? DownReserved2 : reserved.getLast().drawLineTui(line));
                break;
        }

        return str.toString();
    }


    public void addDiscardReserved(ComponentView component) {
        reserved.add(component);
    }

    public void removeDiscardReserved(ComponentView component) {
        reserved.remove(component);
    }
}
