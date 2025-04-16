package view.tui.card;

import Model.Cards.*;
import Model.Cards.Hits.Hit;
import Model.Good.Good;
import Model.Good.GoodType;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;

public class CardView {
    private final String clean =   "|                     |";

    public void drawCardsOnOneLine(ArrayList<Pair<Card, Boolean>> cards) {
        for (int i = 0; i < 15; i++) {
            for (Pair<Card, Boolean> card : cards) {
                Card c = card.getValue0();
                Boolean check = card.getValue1();
                for (int j = 0; j < 11; j++) {
                    String spaces = "                       ";
                    switch (i) {
                        case 0, 14:
                            if (j == 0) {
                                String line = ". _ _ _ _ _ _ _ _ _ _ .";
                                System.out.print(c != null ? line : spaces);
                            }
                            break;
                        case 1:
                            if (j == 0) {
                                System.out.print(c != null ? check ? firstLine(c) : clean : spaces);
                            }
                            break;
                        case 2, 4, 6, 8, 10, 12:
                            if (j == 0) {
                                System.out.print(c != null ? clean : spaces);
                            }
                            break;
                        case 3:
                            if (j == 0) {
                                String covered = "|       Covered       |";
                                System.out.print(c != null ? check ? thirdLine(c) : covered : spaces);
                            }
                            break;
                        case 5:
                            if (j == 0) {
                                System.out.print(c != null ? check ? fifthLine(c) : clean : spaces);
                            }
                            break;
                        case 7:
                            if (j == 0) {
                                System.out.print(c != null ? check ? seventhLine(c) : clean : spaces);
                            }
                            break;
                        case 9:
                            if (j == 0) {
                                System.out.print(c != null ? check ? ninthLine(c) : clean : spaces);
                            }
                            break;
                        case 11:
                            if (j == 0) {
                                System.out.print(c != null ? check ? eleventhLine(c) : clean : spaces);
                            }
                            break;
                        case 13:
                            if (j == 0) {
                                System.out.print(c != null ? check ? thirteenthLine(c) : clean : spaces);
                            }
                            break;
                    }
                }
            }
            System.out.println();
        }
    }

    private String printGoods(int n, Planets p) {
        StringBuilder goods = new StringBuilder();
        for (Good g : p.getPlanet(n)) {
            GoodType type = g.getColor();
            switch (type) {
                case RED -> goods.append("R ");
                case GREEN -> goods.append("G ");
                case BLUE -> goods.append("B ");
                case YELLOW -> goods.append("Y ");
            }
        }
        return goods.toString().trim();
    }

    private String printGoodsStation(List<Good> a) {
        StringBuilder goods = new StringBuilder();
        for (Good g : a) {
            GoodType type = g.getColor();
            switch (type) {
                case RED -> goods.append("R ");
                case GREEN -> goods.append("G ");
                case BLUE -> goods.append("B ");
                case YELLOW -> goods.append("Y ");
            }
        }
        return goods.toString().trim();
    }

    private String printHit(List<Hit> a, int n) {
        StringBuilder goods = new StringBuilder();
        Hit h = a.get(n);
        switch (h.getType()) {
            case LARGEMETEOR -> goods.append("LMeteor ");
            case SMALLMETEOR -> goods.append("SMeteor ");
            case HEAVYFIRE -> goods.append("HFire ");
            case LIGHTFIRE -> goods.append("LFire ");
        }
        switch (h.getDirection()) {
            case NORTH -> goods.append("^ ");
            case SOUTH -> goods.append("v ");
            case EAST -> goods.append("> ");
            case WEST -> goods.append("< ");
        }
        return goods.toString().trim();
    }

    private String firstLine(Card c) {
        CardType t = c.getCardType();
        return switch (t) {
            case PLANETS ->          "|       PLANETS       |";
            case ABANDONEDSHIP ->    "|    ABANDONEDSHIP    |";
            case ABANDONEDSTATION -> "|  ABANDONEDSTATION   |";
            case SMUGGLERS ->        "|      SMUGGLERS      |";
            case SLAVERS ->          "|       SLAVERS       |";
            case PIRATES ->          "|       PIRATES       |";
            case OPENSPACE ->        "|      OPENSPACE      |";
            case METEORSWARM ->      "|     METEORSWARM     |";
            case COMBATZONE ->       "|      COMBATZONE     |";
            case STARDUST ->         "|       STARDUST      |";
            case EPIDEMIC ->         "|       EPIDEMIC      |";
        };
    }

    private String thirdLine(Card c) {
        CardType t = c.getCardType();
        return switch (t) {
            case PLANETS -> {
                if (((Planets) c).getPlanetNumbers() < 1) {
                    yield clean;
                } else {
                    String line = "|  P1: " + printGoods(0, (Planets) c);
                    while (line.length() < 22) {
                        line += " ";
                    }
                    line += "|";
                    yield line;
                }
            }
            case ABANDONEDSHIP -> {
                AbandonedShip a = (AbandonedShip) c;
                String line = "|  CrewLost: " + a.getCrewRequired();
                while (line.length() < 22) {
                    line += " ";
                }
                line += "|";
                yield line;
            }
            case ABANDONEDSTATION -> {
                AbandonedStation a = (AbandonedStation) c;
                String line = "|  CrewRequired: " + a.getCrewRequired();
                while (line.length() < 22) {
                    line += " ";
                }
                line += "|";
                yield line;
            }
            case SMUGGLERS -> {
                Smugglers a = (Smugglers) c;
                String line = "|  StrenghtReq: " + a.getCannonStrengthRequired();
                while (line.length() < 22) {
                    line += " ";
                }
                line += "|";
                yield line;
            }
            case SLAVERS -> {
                Slavers a = (Slavers) c;
                String line = "|  StrenghtReq: " + a.getCannonStrengthRequired();
                while (line.length() < 22) {
                    line += " ";
                }
                line += "|";
                yield line;
            }
            case PIRATES -> {
                Pirates a = (Pirates) c;
                String line = "|  StrenghtReq: " + a.getCannonStrengthRequired();
                while (line.length() < 22) {
                    line += " ";
                }
                line += "|";
                yield line;
            }
            case OPENSPACE -> clean;
            case METEORSWARM -> {
                MeteorSwarm a = (MeteorSwarm) c;
                String line = "|  Hit1: " + printHit(a.getMeteors(), 0);
                while (line.length() < 22) {
                    line += " ";
                }
                line += "|";
                yield line;
            }
            case COMBATZONE -> {
                CombatZone a = (CombatZone) c;
                String line = "";
                if(a.getID() == 15){
                    line = "|  Cr ";
                } else {
                    line = "|  Ca ";
                }
                line += "=> FDays: " + a.getFlightDays();
                while (line.length() < 22) {
                    line += " ";
                }
                line += "|";
                yield line;
            }
            case STARDUST -> clean;
            case EPIDEMIC -> clean;
        };
    }

    private String fifthLine(Card c) {
        CardType t = c.getCardType();
        return switch (t) {
            case PLANETS -> {
                if (((Planets) c).getPlanetNumbers() < 2) {
                    yield clean;
                } else {
                    String line = "|  P2: " + printGoods(1, (Planets) c);
                    while (line.length() < 22) {
                        line += " ";
                    }
                    line += "|";
                    yield line;
                }
            }
            case ABANDONEDSHIP -> {
                AbandonedShip a = (AbandonedShip) c;
                String line = "|  Credit: " + a.getCredit();
                while (line.length() < 22) {
                    line += " ";
                }
                line += "|";
                yield line;
            }
            case ABANDONEDSTATION -> {
                AbandonedStation a = (AbandonedStation) c;
                String line = "|  Goods: " + printGoodsStation(a.getGoods());
                while (line.length() < 22) {
                    line += " ";
                }
                line += "|";
                yield line;
            }
            case SMUGGLERS -> {
                Smugglers a = (Smugglers) c;
                String line = "|  GoodLost: " + a.getGoodsLoss();
                while (line.length() < 22) {
                    line += " ";
                }
                line += "|";
                yield line;
            }
            case SLAVERS -> {
                Slavers a = (Slavers) c;
                String line = "|  CrewLost: " + a.getCrewLost();
                while (line.length() < 22) {
                    line += " ";
                }
                line += "|";
                yield line;
            }
            case PIRATES -> {
                Pirates a = (Pirates) c;
                String line = "|  Hit1: " + printHit(a.getFires(), 0);
                while (line.length() < 22) {
                    line += " ";
                }
                line += "|";
                yield line;
            }
            case OPENSPACE -> clean;
            case METEORSWARM -> {
                MeteorSwarm a = (MeteorSwarm) c;
                String line = "|  Hit2: " + printHit(a.getMeteors(), 1);
                while (line.length() < 22) {
                    line += " ";
                }
                line += "|";
                yield line;
            }
            case COMBATZONE -> {
                CombatZone a = (CombatZone) c;
                String line = "|  En => GoodL: " + a.getLost();
                while (line.length() < 22) {
                    line += " ";
                }
                line += "|";
                yield line;
            }
            case STARDUST -> clean;
            case EPIDEMIC -> clean;
        };
    }

    private String seventhLine(Card c) {
        CardType t = c.getCardType();
        return switch (t) {
            case PLANETS -> {
                if (((Planets) c).getPlanetNumbers() < 3) {
                    yield clean;
                } else {
                    String line = "|  P3: " + printGoods(2, (Planets) c);
                    while (line.length() < 22) {
                        line += " ";
                    }
                    line += "|";
                    yield line;
                }
            }
            case ABANDONEDSHIP -> clean;
            case ABANDONEDSTATION -> clean;
            case SMUGGLERS -> {
                Smugglers a = (Smugglers) c;
                String line = "|  Good: " + printGoodsStation(a.getGoodsReward());
                while (line.length() < 22) {
                    line += " ";
                }
                line += "|";
                yield line;
            }
            case SLAVERS -> {
                Slavers a = (Slavers) c;
                String line = "|  Credit: " + a.getCredit();
                while (line.length() < 22) {
                    line += " ";
                }
                line += "|";
                yield line;
            }
            case PIRATES -> {
                Pirates a = (Pirates) c;
                String line = "|  Hit2: " + printHit(a.getFires(), 1);
                while (line.length() < 22) {
                    line += " ";
                }
                line += "|";
                yield line;
            }
            case OPENSPACE -> clean;
            case METEORSWARM -> {
                MeteorSwarm a = (MeteorSwarm) c;
                String line = "|  Hit3: " + printHit(a.getMeteors(), 2);
                while (line.length() < 22) {
                    line += " ";
                }
                line += "|";
                yield line;
            }
            case COMBATZONE -> {
                CombatZone a = (CombatZone) c;
                String line = "";
                if(a.getID() == 15){
                    line += "|  Ca ";
                } else {
                    line += "|  Cr ";
                }
                line += "=> H1: " + printHit(a.getFires(), 0);
                while (line.length() < 22) {
                    line += " ";
                }
                line += "|";
                yield line;
            }
            case STARDUST -> clean;
            case EPIDEMIC -> clean;
        };
    }

    private String ninthLine(Card c) {
        CardType t = c.getCardType();
        return switch (t) {
            case PLANETS -> {
                if (((Planets) c).getPlanetNumbers() < 4) {
                    yield clean;
                } else {
                    String line = "|  P4: " + printGoods(3, (Planets) c);
                    while (line.length() < 22) {
                        line += " ";
                    }
                    line += "|";
                    yield line;
                }
            }
            case ABANDONEDSHIP -> clean;
            case ABANDONEDSTATION -> clean;
            case SMUGGLERS -> clean;
            case SLAVERS -> clean;
            case PIRATES -> {
                Pirates a = (Pirates) c;
                String line = "|  Hit3: " + printHit(a.getFires(), 2);
                while (line.length() < 22) {
                    line += " ";
                }
                line += "|";
                yield line;
            }
            case OPENSPACE -> clean;
            case METEORSWARM -> {
                if (((MeteorSwarm) c).getMeteors().size() < 4) {
                    yield clean;
                } else {
                    String line = "|  Hit4: " + printHit(((MeteorSwarm) c).getMeteors(), 3);
                    while (line.length() < 22) {
                        line += " ";
                    }
                    line += "|";
                    yield line;
                }
            }
            case COMBATZONE -> {
                CombatZone a = (CombatZone) c;
                String line = "|        H2: " + printHit(a.getFires(), 1);
                while (line.length() < 22) {
                    line += " ";
                }
                line += "|";
                yield line;
            }
            case STARDUST -> clean;
            case EPIDEMIC -> clean;
        };
    }

    private String eleventhLine(Card c) {
        CardType t = c.getCardType();
        return switch (t) {
            case PLANETS -> clean;
            case ABANDONEDSHIP -> clean;
            case ABANDONEDSTATION -> clean;
            case SMUGGLERS -> clean;
            case SLAVERS -> clean;
            case PIRATES -> {
                Pirates a = (Pirates) c;
                String line = "|  Credit: " + a.getCredit();
                while (line.length() < 22) {
                    line += " ";
                }
                line += "|";
                yield line;
            }
            case OPENSPACE -> clean;
            case METEORSWARM -> {
                if (((MeteorSwarm) c).getMeteors().size() < 5) {
                    yield clean;
                } else {
                    String line = "|  Hit5: " + printHit(((MeteorSwarm) c).getMeteors(), 4);
                    while (line.length() < 22) {
                        line += " ";
                    }
                    line += "|";
                    yield line;
                }
            }
            case COMBATZONE -> {
                CombatZone a = (CombatZone) c;
                if (a.getFires().size() < 3) {
                    yield clean;
                } else {
                    String line = "|        H3: " + printHit(a.getFires(), 2);
                    while (line.length() < 22) {
                        line += " ";
                    }
                    line += "|";
                    yield line;
                }
            }
            case STARDUST -> clean;
            case EPIDEMIC -> clean;
        };
    }

    private String thirteenthLine(Card c) {
        CardType t = c.getCardType();
        return switch (t) {
            case PLANETS -> {
                Planets p = (Planets) c;
                String line = "|  FlightDays: " + p.getFlightDays();
                while (line.length() < 22) {
                    line += " ";
                }
                line += "|";
                yield line;
            }
            case ABANDONEDSHIP -> {
                AbandonedShip a = (AbandonedShip) c;
                String line = "|  FlightDays: " + a.getFlightDays();
                while (line.length() < 22) {
                    line += " ";
                }
                line += "|";
                yield line;
            }
            case ABANDONEDSTATION -> {
                AbandonedStation a = (AbandonedStation) c;
                String line = "|  FlightDays: " + a.getFlightDays();
                while (line.length() < 22) {
                    line += " ";
                }
                line += "|";
                yield line;
            }
            case SMUGGLERS -> {
                Smugglers a = (Smugglers) c;
                String line = "|  FlightDays: " + a.getFlightDays();
                while (line.length() < 22) {
                    line += " ";
                }
                line += "|";
                yield line;
            }
            case SLAVERS -> {
                Slavers a = (Slavers) c;
                String line = "|  FlightDays: " + a.getFlightDays();
                while (line.length() < 22) {
                    line += " ";
                }
                line += "|";
                yield line;
            }
            case PIRATES -> {
                Pirates a = (Pirates) c;
                String line = "|  FlightDays: " + a.getFlightDays();
                while (line.length() < 22) {
                    line += " ";
                }
                line += "|";
                yield line;
            }
            case OPENSPACE -> clean;
            case METEORSWARM -> clean;
            case COMBATZONE -> {
                CombatZone a = (CombatZone) c;
                if (a.getFires().size() < 4) {
                    yield clean;
                } else {
                    String line = "|        H4: " + printHit(a.getFires(), 3);
                    while (line.length() < 22) {
                        line += " ";
                    }
                    line += "|";
                    yield line;
                }
            }
            case STARDUST -> clean;
            case EPIDEMIC -> clean;
        };
    }
}