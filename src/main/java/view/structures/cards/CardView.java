package view.structures.cards;

import Model.Cards.Hits.Hit;
import Model.Good.Good;
import Model.Good.GoodType;

import java.util.List;

public abstract class CardView {
    public String Up =      "╭─────────────────────╮";
    public String Clear =   "│                     │";
    public String Covered = "│       Covered       │";
    public String Empty =   "                       ";
    public String Down =    "╰─────────────────────╯";
    private int ID;
    private boolean covered;
    
    public CardView(int ID, boolean covered) {
        this.ID = ID;
        this.covered = covered;
    }

    public void drawCardGui(){

    }

    public String printGoodsStation(List<Good> goods){
        StringBuilder good = new StringBuilder();
        for (Good g : goods) {
            GoodType type = g.getColor();
            switch (type) {
                case RED -> good.append("R ");
                case GREEN -> good.append("G ");
                case BLUE -> good.append("B ");
                case YELLOW -> good.append("Y ");
            }
        }
        return good.toString().trim();
    }

    public String printGoods(List<Good> good) {
        StringBuilder goods = new StringBuilder();
        for (Good g : good) {
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

    public String printHit(List<Hit> a, int n) {
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

    public String drawLineTui(int line){
        return switch(line) {
            case 0 -> Up;
            case 3 -> Covered;
            case 1,2,4,5,6,7,8 -> Clear;
            case 9 -> Down;
            default -> null;
        };
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public boolean isCovered() {
        return covered;
    }

    public void setCovered(boolean covered) {
        this.covered = covered;
    }
}
