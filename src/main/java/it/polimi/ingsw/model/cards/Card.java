package it.polimi.ingsw.model.cards;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Configures Jackson JSON serialization to handle polymorphic deserialization.
 * The type information is stored in a "type" property in the JSON.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
/**
 * Maps JSON type names to their corresponding Card subclasses.
 * This allows Jackson to instantiate the correct subclass when deserializing JSON.
 */
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
/**
 * Abstract base class representing a game card.
 * All card types extend this class and must implement the getCardType() method.
 * Implements Serializable to support JSON serialization/deserialization.
 * @author Lorenzo Trenti
 */
public abstract class Card implements Serializable {
    /**
     * The level of the card, indicating its tier or difficulty.
     */
    @JsonProperty
    private int level;
    /**
     * The unique identifier for this card instance.
     */
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

    /**
     * Default constructor for Jackson JSON deserialization.
     * Creates a Card instance without initializing level or ID fields.
     * These fields should be set during deserialization process.
     */
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