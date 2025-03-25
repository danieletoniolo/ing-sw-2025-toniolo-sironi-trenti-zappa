package Model.Cards;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

// 📌 Permette a Jackson di capire quale sottoclasse usare
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Slavers.class, name = "Slavers"),
        @JsonSubTypes.Type(value = Smugglers.class, name = "Smugglers"),
        @JsonSubTypes.Type(value = Pirates.class, name = "Pirates"),
        @JsonSubTypes.Type(value = StarDust.class, name = "StarDust"),
        @JsonSubTypes.Type(value = OpenSpace.class, name = "OpenSpace"),
        @JsonSubTypes.Type(value = MeteorSwarm.class, name = "MeteorSwarm"),
        @JsonSubTypes.Type(value = Planets.class, name = "Planets"),
        @JsonSubTypes.Type(value = CombatZone.class, name = "CombatZone"),
        @JsonSubTypes.Type(value = AbandonedShip.class, name = "AbandonedShip"),
        @JsonSubTypes.Type(value = AbandonedStation.class, name = "AbandonedStation")
})
public abstract class Card {
    private final int level;
    private final int ID;
    /**
     *
     * @param level level of the card
     */
    public Card(int level, int ID) {
        this.level = level;
        this.ID = ID;
    }

    /**
     * Abstract method : get the card type
     * @return card type
     */
    public abstract CardType getCardType();

    /**
     * Get the card level
     * @return card level
     */
    public int getCardLevel() {
        return level;
    }

    /**
     * Get the card ID
     * @return card ID
     */
    public int getID() {
        return ID;
    }
}