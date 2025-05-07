package controller.event.game;

import Model.Good.Good;
import Model.Player.PlayerColor;
import controller.event.Event;

import java.io.Serializable;
import java.util.ArrayList;

public record ExchangeGoods(
        PlayerColor player,
        int storageID1,
        int storageID2,
        ArrayList<Good> goods1to2,
        ArrayList<Good> goods2to1
) implements Event, Serializable {
}
