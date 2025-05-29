package it.polimi.ingsw.view.miniModel.spaceship;

import it.polimi.ingsw.view.miniModel.Structure;
import it.polimi.ingsw.view.miniModel.components.ComponentView;

import java.io.Serializable;
import java.util.ArrayList;

public class DiscardReservedPileView implements Structure, Serializable {
    private String UpReserved1 =     "╭──────";
    private String LeftReserved2 =   "│      ";
    private String DownReserved1 =   "╰──────";

    private String UpReserved2 =     "──────╮";
    private String RightReserved2 =  "      │";
    private String DownReserved2 =   "──────╯";

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

    public ComponentView removeDiscardReserved(int ID) {
        int i;
        boolean found = false;
        for (i = 0; i < reserved.size(); i++) {
            if (reserved.get(i).getID() == ID) {
                found = true;
                break;
            }
        }

        return found ? reserved.remove(i) : null;
    }
}
