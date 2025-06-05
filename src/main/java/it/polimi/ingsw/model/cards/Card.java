package it.polimi.ingsw.model.cards;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

// 📌 Allows Jackson to understand which subclass to use.
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
        @JsonSubTypes.Type(value = AbandonedStation.class, name = "AbandonedStation"),
        @JsonSubTypes.Type(value = Epidemic.class, name = "Epidemic")
})
public abstract class Card implements Serializable {
    @JsonProperty
    private int level;
    @JsonProperty
    private int ID;
    /**
     *
     * @param level level of the card
     */
    public Card(int level, int ID) {
        this.level = level;
        this.ID = ID;
    }

    public Card(){

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