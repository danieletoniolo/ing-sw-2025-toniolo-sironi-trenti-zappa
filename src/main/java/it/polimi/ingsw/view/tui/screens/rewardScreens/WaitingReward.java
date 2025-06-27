package it.polimi.ingsw.view.tui.screens.rewardScreens;

import it.polimi.ingsw.view.tui.screens.Reward;

public class WaitingReward extends Reward {
    public WaitingReward() {
        super(null);
    }

    protected String lineBeforeInput() {
        return "View players' ship while waiting for your turn to claim rewards:";
    }
}
