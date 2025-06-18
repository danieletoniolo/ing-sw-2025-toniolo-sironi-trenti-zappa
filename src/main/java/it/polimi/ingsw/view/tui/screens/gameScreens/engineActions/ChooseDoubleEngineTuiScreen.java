package it.polimi.ingsw.view.tui.screens.gameScreens.engineActions;

import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.gameScreens.cannonsActions.CannonsBatteryTuiScreen;
import it.polimi.ingsw.view.tui.screens.gameScreens.cannonsActions.ChooseDoubleCannonsTuiScreen;

import java.util.ArrayList;

public class ChooseDoubleEngineTuiScreen extends ManagerEnginesTuiScreen{

    public ChooseDoubleEngineTuiScreen() {
        super(new ArrayList<>(){{
            if (enginesIDs == null) {
                enginesIDs = new ArrayList<>();
            }
            spaceShipView.getMapDoubleEngines().forEach(
                    (key, value) -> {
                        if (!enginesIDs.contains(key)) {
                            add("Active double engines (" + value.getRow() + " " + value.getCol() + ")");
                        }
                    }
            );
            add("Cancel");
            add("Done");
        }});
    }

    @Override
    protected String lineBeforeInput() {
        return "Choose double engines to activate";
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        int num = (int) spaceShipView.getMapDoubleEngines().keySet().stream()
                .filter(key -> !enginesIDs.contains(key))
                .count();

        if (selected == num) {
            destroyStatic();
            setMessage(null);
            return new ChooseDoubleEngineTuiScreen();
        }

        if (selected == num + 1) {
            return new EngineBatteryTuiScreen();
        }

        spaceShipView.getMapDoubleEngines().keySet().stream()
                .filter(key -> !enginesIDs.contains(key))
                .skip(selected)
                .findFirst()
                .ifPresent(enginesIDs::add);

        StringBuilder line = new StringBuilder();
        for (Integer ID : enginesIDs) {
            line.append("(").append(spaceShipView.getMapDoubleEngines().get(ID).getRow()).append(" ").append(spaceShipView.getMapDoubleEngines().get(ID).getCol()).append(") ");
        }

        setMessage("You are activating " + line);
        return new ChooseDoubleEngineTuiScreen();
    }
}
