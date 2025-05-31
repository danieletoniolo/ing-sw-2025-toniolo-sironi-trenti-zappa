package it.polimi.ingsw.event.game.serverToClient.cards;

/**
 * This event is used to get the planets card.
 * @param ID    is the ID of the card.
 * @param level is the level of the card.
 */
public record GetCardPlanets(
        int ID,
        int level
) {
}
