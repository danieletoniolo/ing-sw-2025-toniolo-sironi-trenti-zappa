package view.structures.cards;

import Model.Cards.Hits.Hit;

import java.util.List;

public class MeteorSwarmView extends CardView {
    public List<Hit> hits;

    public MeteorSwarmView(int ID, boolean covered, List<Hit> hits) {
        super(ID, covered);
        this.hits = hits;
    }

    @Override
    public void drawCardGui(){

    }

    @Override
    public String drawLineTui(int l){
        if(isCovered()) return super.drawLineTui(l);

        return switch(l) {
            case 0 -> Up;
            case 1 -> "│     METEORSWARM     │";
            case 2,8 -> Clear;
            case 3 -> {
                String line = "│  Hit1: " + printHit(getHits(), 0);
                while (line.length() < 22) {
                    line += " ";
                }
                line += "│";
                yield line;
            }
            case 4 -> {
                String line = "│  Hit2: " + printHit(getHits(), 1);
                while (line.length() < 22) {
                    line += " ";
                }
                line += "│";
                yield line;
            }
            case 5 -> {
                String line = "│  Hit3: " + printHit(getHits(), 2);
                while (line.length() < 22) {
                    line += " ";
                }
                line += "│";
                yield line;
            }
            case 6 -> {
                if (getHits().size() < 4) {
                    yield Clear;
                } else {
                    String line = "│  Hit4: " + printHit(getHits(), 3);
                    while (line.length() < 22) {
                        line += " ";
                    }
                    line += "│";
                    yield line;
                }
            }
            case 7 -> {
                if (getHits().size() < 5) {
                    yield Clear;
                } else {
                    String line = "│  Hit5: " + printHit(getHits(), 4);
                    while (line.length() < 22) {
                        line += " ";
                    }
                    line += "│";
                    yield line;
                }
            }
            case 9 -> Down;
            default -> null;
        };
    }

    public List<Hit> getHits() {
        return hits;
    }

    public void setHits(List<Hit> hits) {
        this.hits = hits;
    }
}
