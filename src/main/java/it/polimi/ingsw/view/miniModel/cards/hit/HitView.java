package it.polimi.ingsw.view.miniModel.cards.hit;

/**
 * Represents a view for a hit in the game, containing information about
 * the type and direction of the hit for display purposes.
 */
public class HitView {
    private HitTypeView type;
    private HitDirectionView direction;

    /**
     * Constructs a new HitView with the specified type and direction.
     *
     * @param type the type of the hit
     * @param direction the direction of the hit
     */
    public HitView(HitTypeView type, HitDirectionView direction) {
        this.type = type;
        this.direction = direction;
    }

    /**
     * Draws the hit representation for text-based user interface.
     *
     * @return a string representation of the hit combining type and direction
     */
    public String drawHitTui(){
        return type.drawTui() + " " + direction.drawTui();
    }
}

