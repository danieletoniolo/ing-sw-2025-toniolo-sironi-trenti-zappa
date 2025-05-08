package view.structures.cards;

import view.structures.cards.hit.HitView;

import java.util.List;

public class MeteorSwarmView extends CardView {
    public List<HitView> hits;

    public MeteorSwarmView(int ID, boolean covered, int level, List<HitView> hits) {
        super(ID, covered, level);
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
                String line = "│  Hit1: " + hits.get(0).drawHitTui();
                while (line.length() < 22) {
                    line += " ";
                }
                line += "│";
                yield line;
            }
            case 4 -> {
                String line = "│  Hit2: " + hits.get(1).drawHitTui();
                while (line.length() < 22) {
                    line += " ";
                }
                line += "│";
                yield line;
            }
            case 5 -> {
                String line = "│  Hit3: " + hits.get(2).drawHitTui();
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
                    String line = "│  Hit4: " + hits.get(3).drawHitTui();
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
                    String line = "│  Hit5: " + hits.get(4).drawHitTui();
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

    public List<HitView> getHits() {
        return hits;
    }

    public void setHits(List<HitView> hits) {
        this.hits = hits;
    }
}
