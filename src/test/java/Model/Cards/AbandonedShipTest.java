package Model.Cards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AbandonedShipTest {
    AbandonedShip card;
    //PlayerData player;

    @BeforeEach
    void setUp() {
        card = new AbandonedShip(2,3,1,2);
        assertNotNull(card, "Variabile card non inizializzata correttamente");

        //player = new PlayerData();
        //assertNotNull(player, "Variabile player non inizializzata correttamente");
    }

    @Test
    void getCardLevel() {
        assertEquals(2,card.getCardLevel());
    }

    @Test
    void getCrewRequired() {
        assertEquals(3,card.getCrewRequired());
    }

    @Test
    void getFlightDays() {
        assertEquals(1,card.getFlightDays());
    }

    @Test
    void getCredit() {
        assertEquals(2,card.getCredit());
    }

    @Test
    void getCardType() {
        assertEquals(CardType.ABANDONEDSHIP, card.getCardType());
    }

    @Test
    void apply() {
    }
}