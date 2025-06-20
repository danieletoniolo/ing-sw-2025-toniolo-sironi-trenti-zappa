package it.polimi.ingsw.view.miniModel.spaceship;

import it.polimi.ingsw.view.miniModel.Structure;
import it.polimi.ingsw.view.miniModel.components.ComponentView;

import java.util.ArrayList;

public class DiscardReservedPileView implements Structure {
    private final ArrayList<ComponentView> reserved;
    private boolean isDiscard;

    public DiscardReservedPileView() {
        reserved = new ArrayList<>();
        isDiscard = false;
    }

    public void setIsDiscarded(){
        isDiscard = true;
    }

    public int getRowsToDraw() {
        return ComponentView.getRowsToDraw() + 2;
    }

    @Override
    public String drawLineTui(int line) {
        StringBuilder str = new StringBuilder();
        String Up =   "╭──────────────╮";
        String Side = "│";
        String down = "╰──────────────╯";
        String Clear = "       ";

        switch (line) {
            case 0:
                String name = isDiscard ? "Discard pile: " : "Reserved pile: ";
                str.append(name).append(getReserved().size());
                break;
            case 1:
                str.append(Up);
                break;
            case 2, 3, 4:
                line -= 2; // Adjust line for reserved components
                str.append(Side);
                str.append(reserved.isEmpty() ? Clear : reserved.getFirst().drawLineTui(line));
                str.append(reserved.isEmpty() || reserved.size() == 1 ? Clear : reserved.getLast().drawLineTui(line));
                str.append(Side);
                break;
            case 5:
                str.append(down);
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

    public ArrayList<ComponentView> getReserved() {
        return reserved;
    }
}
