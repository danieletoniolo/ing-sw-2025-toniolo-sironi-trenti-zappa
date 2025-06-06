package it.polimi.ingsw.view.tui.screens.gameScreens.cannonsActions;

import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.ArrayList;

public class ChooseDoubleCannonsTuiScreen extends MangerCannonsTuiScreen {
    private final TuiScreenView oldScreen;

    public ChooseDoubleCannonsTuiScreen(TuiScreenView oldScreen) {
        super(new ArrayList<>(){{
            if (cannonsIDs == null) {
                cannonsIDs = new ArrayList<>();
            }
            spaceShipView.getMapDoubleCannons().forEach(
                    (key, value) -> {
                        if (!cannonsIDs.contains(key)) {
                            add("Active double cannon (" + value.getRow() + " " + value.getCol() + ")");
                        }
                    }
            );
            add("Cancel");
            add("Done");
        }});
        this.oldScreen = oldScreen;
    }

    @Override
    protected String lineBeforeInput() {
        return "Choose double cannons to activate";
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        int num = (int) spaceShipView.getMapDoubleCannons().keySet().stream()
                .filter(key -> !cannonsIDs.contains(key))
                .count();

        if (selected == num) {
            destroyStatic();
            oldScreen.setMessage(null);
            return oldScreen;
        }

        if (selected == num + 1) {
            return new CannonsBatteryTuiScreen(oldScreen);
        }

        spaceShipView.getMapDoubleCannons().keySet().stream()
                .filter(key -> !cannonsIDs.contains(key))
                .skip(selected)
                .findFirst()
                .ifPresent(cannonsIDs::add);

        StringBuilder line = new StringBuilder();
        for (Integer ID : cannonsIDs) {
            line.append("(").append(spaceShipView.getMapDoubleCannons().get(ID).getRow()).append(" ").append(spaceShipView.getMapDoubleCannons().get(ID).getCol()).append(") ");
        }

        TuiScreenView newScreen = new ChooseDoubleCannonsTuiScreen(oldScreen);
        newScreen.setMessage("You are activating " + line);
        return newScreen;
    }
}
