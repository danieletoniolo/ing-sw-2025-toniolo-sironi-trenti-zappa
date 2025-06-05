package it.polimi.ingsw.view.miniModel.cards.hit;

public class HitView {
    private HitTypeView type;
    private HitDirectionView direction;

    public HitView(HitTypeView type, HitDirectionView direction) {
        this.type = type;
        this.direction = direction;
    }

    public String drawHitTui(){
        return type.drawTui() + " " + direction.drawTui();
    }
}

