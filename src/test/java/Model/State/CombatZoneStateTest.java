package Model.State;

import Model.Cards.CombatZone;
import Model.Cards.Hits.Direction;
import Model.Cards.Hits.Hit;
import Model.Cards.Hits.HitType;
import Model.Game.Board.Board;
import Model.Game.Board.Level;
import Model.Good.Good;
import Model.Good.GoodType;
import Model.Player.PlayerColor;
import Model.Player.PlayerData;
import Model.SpaceShip.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CombatZoneStateTest {
    CombatZoneState state;
    CombatZoneState stateL;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        SpaceShip ship0 = new SpaceShip(Level.SECOND, PlayerColor.BLUE);
        SpaceShip ship1 = new SpaceShip(Level.SECOND, PlayerColor.RED);
        SpaceShip ship2 = new SpaceShip(Level.SECOND, PlayerColor.GREEN);
        SpaceShip ship3 = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        PlayerData p0 = new PlayerData("123e4567-e89b-12d3-a456-426614174001", PlayerColor.BLUE, ship0);
        PlayerData p1 = new PlayerData("123e4567-e89b-12d3-a456-426614174002", PlayerColor.RED, ship1);
        PlayerData p2 = new PlayerData("123e4567-e89b-12d3-a456-426614174003", PlayerColor.GREEN, ship2);
        PlayerData p3 = new PlayerData("123e4567-e89b-12d3-a456-426614174004", PlayerColor.YELLOW, ship3);

        PlayerData p00 = new PlayerData("123e4567-e89b-12d3-a456-426614174001", PlayerColor.BLUE, ship0);
        PlayerData p11 = new PlayerData("123e4567-e89b-12d3-a456-426614174002", PlayerColor.RED, ship1);
        PlayerData p22 = new PlayerData("123e4567-e89b-12d3-a456-426614174003", PlayerColor.GREEN, ship2);
        PlayerData p33 = new PlayerData("123e4567-e89b-12d3-a456-426614174004", PlayerColor.YELLOW, ship3);

        Board board = new Board(Level.SECOND);
        board.clearInGamePlayers();
        board.setPlayer(p0, 0);
        board.setPlayer(p1, 1);
        board.setPlayer(p2, 2);
        board.setPlayer(p3, 3);

        Board board1 = new Board(Level.LEARNING);
        board1.clearInGamePlayers();
        board1.setPlayer(p00, 0);
        board1.setPlayer(p11, 1);
        board1.setPlayer(p22, 2);
        board1.setPlayer(p33, 3);

        ArrayList<Hit> fires = new ArrayList<>(List.of(new Hit(HitType.HEAVYFIRE, Direction.NORTH), new Hit(HitType.LIGHTFIRE, Direction.SOUTH), new Hit(HitType.SMALLMETEOR, Direction.EAST)));
        CombatZone c1 = new CombatZone(2, 3, fires, 2, 4);
        CombatZone c2 = new CombatZone(2, 3, fires, 1, 4);

        state = new CombatZoneState(board, c1);
        stateL = new CombatZoneState(board1, c2);
        assertNotNull(state);
        assertNotNull(stateL);
    }

    @Test
    void setFragmentChoice_validFragmentChoice() {
        state.setInternalState(CombatZoneInternalState.CREW);
        state.getFightHandler().setInternalState(FightHandlerInternalState.DESTROY_FRAGMENT);
        state.setFragmentChoice(1);

        assertEquals(1, state.getFightHandler().getFragmentChoice());
    }

    @Test
    void setFragmentChoice_invalidState() {
        state.setInternalState(CombatZoneInternalState.CREW);

        assertThrows(IllegalStateException.class, () -> state.setFragmentChoice(1));
    }

    @Test
    void setFragmentChoice_invalidFragmentChoice() {
        state.setInternalState(CombatZoneInternalState.ENGINES);

        assertThrows(IllegalStateException.class, () -> state.setFragmentChoice(-1));
    }

    @Test
    void setProtect_validProtect() {
        state.setInternalState(CombatZoneInternalState.CREW);
        state.getFightHandler().setInternalState(FightHandlerInternalState.PROTECTION);
        state.setProtect(true, 1);

        assertTrue(state.getFightHandler().getProtect());
        assertEquals(1, state.getFightHandler().getBatteryID());
    }

    @Test
    void setProtect_invalidState() {
        state.setInternalState(CombatZoneInternalState.CREW);

        assertThrows(IllegalStateException.class, () -> state.setProtect(true, 1));
    }

    @Test
    void setProtect_nullBatteryID() {
        state.setInternalState(CombatZoneInternalState.CANNONS);

        assertThrows(IllegalStateException.class, () -> state.setProtect(true, null));
    }

    @Test
    void setDice_validDice() {
        state.setInternalState(CombatZoneInternalState.CREW);
        state.getFightHandler().setInternalState(FightHandlerInternalState.CAN_PROTECT);
        state.setDice(3);

        assertEquals(3, state.getFightHandler().getDice());
    }

    @Test
    void setDice_invalidState() {
        state.setInternalState(CombatZoneInternalState.ENGINES);

        assertThrows(IllegalStateException.class, () -> state.setDice(2));
    }

    @Test
    void setDice_negativeDiceValue() {
        state.setInternalState(CombatZoneInternalState.CANNONS);

        assertThrows(IllegalStateException.class, () -> state.setDice(-1));
    }

    @Test
    void setCrewLoss_validCrewLoss_setsCrewLossSuccessfully() {
        state.setInternalState(CombatZoneInternalState.ENGINES);
        ArrayList<Pair<Integer, Integer>> crewLoss = new ArrayList<>();
        crewLoss.add(Pair.with(0, 1));
        crewLoss.add(Pair.with(1, 2));

        state.setCrewLoss(crewLoss);

        assertEquals(crewLoss, state.getCrewLoss());
    }

    @Test
    void setCrewLoss_invalidState() {
        state.setInternalState(CombatZoneInternalState.CANNONS);
        ArrayList<Pair<Integer, Integer>> crewLoss = new ArrayList<>();
        crewLoss.add(Pair.with(0, 1));

        assertThrows(IllegalStateException.class, () -> state.setCrewLoss(crewLoss));
    }

    @Test
    void setCrewLoss_crewLossNotMatchingCardLost() {
        state.setInternalState(CombatZoneInternalState.ENGINES);
        ArrayList<Pair<Integer, Integer>> crewLoss = new ArrayList<>();
        crewLoss.add(Pair.with(0, 1));

        assertThrows(IllegalStateException.class, () -> state.setCrewLoss(crewLoss));
    }

    @Test
    void setCrewLoss_emptyCrewLoss() {
        state.setInternalState(CombatZoneInternalState.ENGINES);
        ArrayList<Pair<Integer, Integer>> crewLoss = new ArrayList<>();

        assertThrows(IllegalStateException.class, () -> state.setCrewLoss(crewLoss));
    }

    /*
    @Test
    void useCannon_validStateAndStrength() {
        state.setInternalState(CombatZoneInternalState.CANNONS);
        state.entry();
        PlayerData player = state.players.getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        player.getSpaceShip().placeComponent(new Cannon(2, connectors, 1), 6, 7);
        player.getSpaceShip().placeComponent(new Battery(3, connectors, 3), 7, 6);
        player.getSpaceShip().placeComponent(new Battery(4, connectors, 3), 7, 7);
        state.useCannon(player, 5.0f, player.getSpaceShip().getBatteries().keySet().stream().toList());

        assertEquals(5.0f, state.getStats().get(CombatZoneInternalState.CANNONS.getIndex(state.getCard().getCardLevel())).get(player));
    }

    @Test
    void useCannon_invalidState() {
        state.setInternalState(CombatZoneInternalState.ENGINES);
        PlayerData player = state.players.getFirst();
        List<Integer> batteriesID = Arrays.asList(1, 2, 3);

        assertThrows(IllegalStateException.class, () -> state.useCannon(player, 5.0f, batteriesID));
    }

    @Test
    void useCannon_nullPlayer() {
        state.setInternalState(CombatZoneInternalState.CANNONS);
        List<Integer> batteriesID = Arrays.asList(1, 2, 3);

        assertThrows(NullPointerException.class, () -> state.useCannon(null, 5.0f, batteriesID));
    }

    @Test
    void useCannon_withInvalidBatteryIDs() {
        state.setInternalState(CombatZoneInternalState.CANNONS);
        PlayerData player = state.board.getInGamePlayers().getFirst();
        List<Integer> invalidBatteriesID = Arrays.asList(99, 100);
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        player.getSpaceShip().placeComponent(new Cannon(2, connectors, 1), 6, 7);

        assertThrows(NullPointerException.class, () -> state.useCannon(player, 5.0f, invalidBatteriesID));
    }

    @Test
    void useCannon_withNullBatteriesList() {
        state.setInternalState(CombatZoneInternalState.CANNONS);
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        player.getSpaceShip().placeComponent(new Cannon(2, connectors, 1), 6, 7);

        assertThrows(NullPointerException.class, () -> state.useCannon(player, 5.0f, null));
    }

    @Test
    void useEngine_validStateAndStrength() {
        state.setInternalState(CombatZoneInternalState.ENGINES);
        state.entry();
        PlayerData player = state.players.getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        player.getSpaceShip().placeComponent(new Engine(2, connectors, 1), 7, 6);
        player.getSpaceShip().placeComponent(new Battery(3, connectors, 3), 6, 7);
        player.getSpaceShip().placeComponent(new Battery(4, connectors, 3), 6, 8);
        state.useEngine(player, 4.5f, player.getSpaceShip().getBatteries().keySet().stream().toList());

        assertEquals(4.5f, state.getStats().get(CombatZoneInternalState.ENGINES.getIndex(state.getCard().getCardLevel())).get(player));
    }

    @Test
    void useEngine_invalidState() {
        state.setInternalState(CombatZoneInternalState.CANNONS);
        PlayerData player = state.players.getFirst();
        List<Integer> batteriesID = Arrays.asList(1, 2, 3);

        assertThrows(IllegalStateException.class, () -> state.useEngine(player, 3.0f, batteriesID));
    }

    @Test
    void useEngine_nullPlayer() {
        state.setInternalState(CombatZoneInternalState.ENGINES);
        List<Integer> batteriesID = Arrays.asList(1, 2, 3);

        assertThrows(NullPointerException.class, () -> state.useEngine(null, 2.0f, batteriesID));
    }

    @RepeatedTest(5)
    void useEngine_withInvalidBatteryIDs() {
        state.setInternalState(CombatZoneInternalState.ENGINES);
        PlayerData player = state.board.getInGamePlayers().getFirst();
        List<Integer> invalidBatteriesID = Arrays.asList(99, 100);
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        player.getSpaceShip().placeComponent(new Engine(2, connectors, 1), 7, 6);

        assertThrows(NullPointerException.class, () -> state.useEngine(player, 5.0f, invalidBatteriesID));
    }

    @RepeatedTest(5)
    void useEngine_withNullBatteriesList() {
        state.setInternalState(CombatZoneInternalState.ENGINES);
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        player.getSpaceShip().placeComponent(new Engine(2, connectors, 1), 7, 6);

        assertThrows(NullPointerException.class, () -> state.useEngine(player, 5.0f, null));
    }

     */

    @Test
    void setGoodsToDiscard_validGoodsToDiscard() {
        state.setInternalState(CombatZoneInternalState.ENGINES);
        ArrayList<Pair<ArrayList<Good>, Integer>> goodsToDiscard = new ArrayList<>();
        goodsToDiscard.add(Pair.with(new ArrayList<>(List.of(new Good(GoodType.YELLOW), new Good(GoodType.GREEN))), 1));
        state.setGoodsToDiscard(state.players.getFirst(), goodsToDiscard);

        assertEquals(goodsToDiscard, state.getGoodsToDiscard());
    }

    @Test
    void setGoodsToDiscard_invalidState() {
        state.setInternalState(CombatZoneInternalState.CANNONS);
        ArrayList<Pair<ArrayList<Good>, Integer>> goodsToDiscard = new ArrayList<>();
        goodsToDiscard.add(Pair.with(new ArrayList<>(List.of(new Good(GoodType.YELLOW))), 1));

        assertThrows(IllegalStateException.class, () -> state.setGoodsToDiscard(state.players.getFirst(), goodsToDiscard));
    }

    @Test
    void setGoodsToDiscard_emptyGoodsToDiscard() {
        state.setInternalState(CombatZoneInternalState.ENGINES);
        ArrayList<Pair<ArrayList<Good>, Integer>> goodsToDiscard = new ArrayList<>();
        state.setGoodsToDiscard(state.players.getFirst(), goodsToDiscard);

        assertTrue(state.getGoodsToDiscard().isEmpty());
    }

    @Test
    void entry_validState() {
        state.setInternalState(CombatZoneInternalState.CREW);
        state.entry();

        for (PlayerData player : state.players) {
            assertTrue(state.getStats().get(CombatZoneInternalState.CREW.getIndex(state.getCard().getCardLevel())).containsKey(player));
            assertTrue(state.getStats().get(CombatZoneInternalState.ENGINES.getIndex(state.getCard().getCardLevel())).containsKey(player));
            assertTrue(state.getStats().get(CombatZoneInternalState.CANNONS.getIndex(state.getCard().getCardLevel())).containsKey(player));
        }
    }

    @Test
    void entry_validStateWithCrew() {
        state.setInternalState(CombatZoneInternalState.CREW);
        state.entry();

        PlayerData expectedMinPlayer = state.players.stream()
                .min(Comparator.comparingInt(p -> p.getSpaceShip().getCrewNumber()))
                .orElse(null);

        assertEquals(expectedMinPlayer, state.getMinPlayerCrew());
    }

    @Test
    void entry_validStateWithEngines() {
        state.setInternalState(CombatZoneInternalState.ENGINES);
        ConnectorType[] c = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        LifeSupportBrown lsb = new LifeSupportBrown(2, c);
        Engine e = new Engine(2, c, 1);
        state.players.getFirst().getSpaceShip().placeComponent(e, 7, 6);
        state.players.getFirst().getSpaceShip().placeComponent(lsb, 6, 7);
        state.players.getFirst().getSpaceShip().getCabin(152).isValid();
        state.players.getFirst().getSpaceShip().addCrewMember(152, 1);
        state.entry();

        Float expectedStrength = state.players.getFirst().getSpaceShip().getSingleEnginesStrength() + SpaceShip.getAlienStrength();
        assertEquals(expectedStrength, state.getStats().get(CombatZoneInternalState.ENGINES.getIndex(state.getCard().getCardLevel())).get(state.players.getFirst()));
    }

    @Test
    void entry_validStateWithCannons() {
        state.setInternalState(CombatZoneInternalState.CANNONS);
        ConnectorType[] c = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        LifeSupportPurple lsb = new LifeSupportPurple(2, c);
        Cannon e = new Cannon(2, c, 1);
        state.players.getFirst().getSpaceShip().placeComponent(e, 6, 7);
        state.players.getFirst().getSpaceShip().placeComponent(lsb, 7, 6);
        state.players.getFirst().getSpaceShip().getCabin(152).isValid();
        state.players.getFirst().getSpaceShip().addCrewMember(152, 2);
        state.entry();

        Float expectedStrength = state.players.getFirst().getSpaceShip().getSingleCannonsStrength() + SpaceShip.getAlienStrength();
        assertEquals(expectedStrength, state.getStats().get(CombatZoneInternalState.CANNONS.getIndex(state.getCard().getCardLevel())).get(state.players.getFirst()));
    }

    //Execute with card level 2
    @Test
    void execute_levelSecond(){
        PlayerData player1 = state.board.getInGamePlayers().getFirst();
        PlayerData player2 = state.board.getInGamePlayers().get(1);
        PlayerData player3 = state.board.getInGamePlayers().get(2);
        PlayerData player4 = state.board.getInGamePlayers().get(3);

        //Setto ship
        player1.getSpaceShip().addCrewMember(152, 0);
        player1.getSpaceShip().placeComponent(new Cabin(2, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE}), 6, 7);
        player1.getSpaceShip().addCrewMember(2, 0);
        player1.getSpaceShip().placeComponent(new Storage(3, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE}, true, 3), 7, 6);
        Good g = new Good(GoodType.YELLOW);
        ArrayList<Good> lAdd = new ArrayList<>();
        lAdd.add(g);
        player1.getSpaceShip().exchangeGood(lAdd, null, 3);
        player2.getSpaceShip().placeComponent(new Cannon(2, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE}, 1), 6, 7);
        int startStep = player1.getStep();

        //Faccio conti execute
        state.entry();

        state.setInternalState(CombatZoneInternalState.CANNONS); //TODO: da eliminare
        state.execute(state.getMinPlayerCannons());
        assertEquals(CombatZoneInternalState.ENGINES, state.getInternalState());
        assertEquals(startStep - state.getCard().getFlightDays(), state.getMinPlayerCannons().getStep());

        ArrayList<Pair<Integer, Integer>> crewLoss = new ArrayList<>();
        crewLoss.add(Pair.with(152, 2));
        crewLoss.add(Pair.with(2, 1));
        state.setCrewLoss(crewLoss);
        ArrayList<Pair<ArrayList<Good>, Integer>> goodsToDiscard = new ArrayList<>();
        goodsToDiscard.add(Pair.with(lAdd, 3));
        state.setGoodsToDiscard(state.getMinPlayerEngines(), goodsToDiscard);
        state.execute(state.getMinPlayerEngines());
        assertEquals(CombatZoneInternalState.CREW, state.getInternalState());
        assertFalse(state.getMinPlayerEngines().getSpaceShip().getGoods().stream().anyMatch(lAdd::contains));
        //assertEquals(state.getCard().getLost(), startCrew - state.getMinPlayerEngines().getSpaceShip().getCrewNumber());
        //TODO: Non dovrebbe essere sui goods? -> vedi sotto

        state.getFightHandler().setInternalState(FightHandlerInternalState.CAN_PROTECT); //TODO: Capire
        state.setDice(7);
        state.execute(state.getMinPlayerCrew());
        assertEquals(CombatZoneInternalState.CREW, state.getInternalState());
        assertEquals(0, state.getFightHandler().getHitIndex());
        //TODO: Non dovremmo aggiornare status del player a played?
    }

    /*
    @Test
    void execute_validStateCrew() {
        state.setInternalState(CombatZoneInternalState.CREW);
        state.getFightHandler().setInternalState(FightHandlerInternalState.CAN_PROTECT);
        state.setDice(7);

        state.setInternalState(CombatZoneInternalState.CREW);
        state.players.getFirst().getSpaceShip().addCrewMember(152, 0);
        state.entry();
        state.execute(state.getMinPlayerCrew());

        assertEquals(CombatZoneInternalState.CREW, state.getInternalState());
        assertEquals(0, state.getFightHandler().getHitIndex());

        //TODO: Non dovremmo aggiornare status del player a played?
    }

    @Test
    void execute_validStateEngines() {
        state.setInternalState(CombatZoneInternalState.ENGINES);
        state.players.getFirst().getSpaceShip().addCrewMember(152, 0);
        state.players.getFirst().getSpaceShip().placeComponent(new Cabin(2, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE}), 6, 7);
        state.players.getFirst().getSpaceShip().addCrewMember(2, 0);
        state.players.getFirst().getSpaceShip().placeComponent(new Storage(3, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE}, true, 3), 7, 6);
        Good g = new Good(GoodType.YELLOW);
        ArrayList<Good> lAdd = new ArrayList<>();
        lAdd.add(g);
        state.players.getFirst().getSpaceShip().exchangeGood(lAdd, null, 3);

        state.entry();
        ArrayList<Pair<Integer, Integer>> crewLoss = new ArrayList<>();
        crewLoss.add(Pair.with(152, 2));
        crewLoss.add(Pair.with(2, 1));
        state.setCrewLoss(crewLoss);

        ArrayList<Pair<ArrayList<Good>, Integer>> goodsToDiscard = new ArrayList<>();
        goodsToDiscard.add(Pair.with(lAdd, 3));
        state.setGoodsToDiscard(state.getMinPlayerEngines(), goodsToDiscard);

        state.execute(state.getMinPlayerEngines());

        assertEquals(CombatZoneInternalState.CREW, state.getInternalState());
        assertFalse(state.getMinPlayerEngines().getSpaceShip().getGoods().stream().anyMatch(lAdd::contains));
        //assertEquals(state.getCard().getLost(), startCrew - state.getMinPlayerEngines().getSpaceShip().getCrewNumber());
            //TODO: Non dovrebbe essere sui goods? -> vedi sotto
    }

    @Test
    void execute_validStateCannons(){
        state.setInternalState(CombatZoneInternalState.CANNONS);
        state.entry();

        state.players.getFirst().getSpaceShip().placeComponent(new Cannon(2, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE}, 1), 6, 7);
        int startStep = state.players.getFirst().getStep();

        state.execute(state.getMinPlayerCannons());

        assertEquals(CombatZoneInternalState.ENGINES, state.getInternalState());
        assertEquals(startStep - state.getCard().getFlightDays(), state.getMinPlayerCannons().getStep());
    }*/

    //Execute with card level Learning
    @Test
    void execute_levelLearning(){
        PlayerData player1 = stateL.players.getFirst();
        PlayerData player2 = stateL.players.get(1);
        PlayerData player3 = stateL.players.get(2);
        PlayerData player4 = stateL.players.get(3);

        //Setto ship
        player1.getSpaceShip().addCrewMember(152, 0);
        player3.getSpaceShip().addCrewMember(153, 0);
        player4.getSpaceShip().addCrewMember(155, 0);

        player1.getSpaceShip().placeComponent(new Cabin(2, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE}), 6, 7);
        player1.getSpaceShip().addCrewMember(2, 0);
        player1.getSpaceShip().placeComponent(new Storage(3, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE}, true, 3), 7, 6);
        Good g = new Good(GoodType.YELLOW);
        ArrayList<Good> lAdd = new ArrayList<>();
        lAdd.add(g);
        player1.getSpaceShip().exchangeGood(lAdd, null, 3);

        player2.getSpaceShip().placeComponent(new Cannon(2, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE}, 1), 6, 7);

        //Faccio conti execute
        stateL.entry();

        int startStep = stateL.getMinPlayerCrew().getStep();
        stateL.execute(stateL.getMinPlayerCrew());
        assertEquals(CombatZoneInternalState.ENGINES, stateL.getInternalState());
        assertEquals(startStep - stateL.getCard().getFlightDays() - 2, stateL.getMinPlayerCrew().getStep());
        //TODO: Non dovremmo aggiornare status del player a played?

        ArrayList<Pair<Integer, Integer>> crewLoss = new ArrayList<>();
        crewLoss.add(Pair.with(152, 2));
        crewLoss.add(Pair.with(2, 1));
        int startCrew = stateL.players.getFirst().getSpaceShip().getCrewNumber();
        stateL.setCrewLoss(crewLoss);
        ArrayList<Pair<ArrayList<Good>, Integer>> goodsToDiscard = new ArrayList<>();
        goodsToDiscard.add(Pair.with(lAdd, 3));
        stateL.setGoodsToDiscard(stateL.getMinPlayerEngines(), goodsToDiscard);
        stateL.execute(stateL.getMinPlayerEngines());
        assertEquals(CombatZoneInternalState.CANNONS, stateL.getInternalState());
        assertEquals(stateL.getCard().getLost(), startCrew - stateL.getMinPlayerEngines().getSpaceShip().getCrewNumber());

        stateL.getFightHandler().setInternalState(FightHandlerInternalState.CAN_PROTECT); //TODO: Capire
        stateL.setDice(7);
        stateL.execute(stateL.getMinPlayerCannons());
        assertEquals(CombatZoneInternalState.CANNONS, stateL.getInternalState());
        assertEquals(0, stateL.getFightHandler().getHitIndex());
    }

    /*
    @Test
    void execute_validStateCrew_Learning() {
        stateL.setInternalState(CombatZoneInternalState.CREW);
        stateL.players.getFirst().getSpaceShip().addCrewMember(152, 0);
        stateL.players.get(2).getSpaceShip().addCrewMember(153, 0);
        stateL.players.get(3).getSpaceShip().addCrewMember(155, 0);
        stateL.entry();
        int startStep = stateL.getMinPlayerCrew().getStep();
        stateL.execute(stateL.getMinPlayerCrew());

        assertEquals(CombatZoneInternalState.ENGINES, stateL.getInternalState());
        assertEquals(startStep - stateL.getCard().getFlightDays() - 2, stateL.getMinPlayerCrew().getStep());

        //TODO: Non dovremmo aggiornare status del player a played?
    }

    @Test
    void execute_validStateEngines_Learning() {
        stateL.setInternalState(CombatZoneInternalState.ENGINES);
        stateL.players.getFirst().getSpaceShip().addCrewMember(152, 0);
        stateL.players.getFirst().getSpaceShip().placeComponent(new Cabin(2, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE}), 6, 7);
        stateL.players.getFirst().getSpaceShip().addCrewMember(2, 0);
        stateL.players.getFirst().getSpaceShip().placeComponent(new Storage(3, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE}, true, 3), 7, 6);
        Good g = new Good(GoodType.YELLOW);
        ArrayList<Good> lAdd = new ArrayList<>();
        lAdd.add(g);
        stateL.players.getFirst().getSpaceShip().exchangeGood(lAdd, null, 3);

        stateL.entry();
        ArrayList<Pair<Integer, Integer>> crewLoss = new ArrayList<>();
        crewLoss.add(Pair.with(152, 2));
        crewLoss.add(Pair.with(2, 1));
        int startCrew = stateL.players.getFirst().getSpaceShip().getCrewNumber();
        stateL.setCrewLoss(crewLoss);

        ArrayList<Pair<ArrayList<Good>, Integer>> goodsToDiscard = new ArrayList<>();
        goodsToDiscard.add(Pair.with(lAdd, 3));
        stateL.setGoodsToDiscard(stateL.getMinPlayerEngines(), goodsToDiscard);

        stateL.execute(stateL.getMinPlayerEngines());

        assertEquals(CombatZoneInternalState.CANNONS, stateL.getInternalState());
        assertEquals(stateL.getCard().getLost(), startCrew - stateL.getMinPlayerEngines().getSpaceShip().getCrewNumber());
    }

    @Test
    void execute_validStateCannons_Learning(){
        stateL.setInternalState(CombatZoneInternalState.CANNONS);
        stateL.getFightHandler().setInternalState(FightHandlerInternalState.CAN_PROTECT);
        stateL.setDice(7);

        stateL.setInternalState(CombatZoneInternalState.CANNONS);
        stateL.entry();

        stateL.players.getFirst().getSpaceShip().placeComponent(new Cannon(2, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE}, 1), 6, 7);

        stateL.execute(stateL.getMinPlayerCannons());

        assertEquals(CombatZoneInternalState.CANNONS, stateL.getInternalState());
        assertEquals(0, stateL.getFightHandler().getHitIndex());
    }
     */

    @Test
    void execute_invalidStateEngines() {
        state.setInternalState(CombatZoneInternalState.ENGINES);

        assertThrows(NullPointerException.class, () -> state.execute(state.getMinPlayerEngines()));
    }

    @Test
    void execute_invalidStateCannons() {
        state.setInternalState(CombatZoneInternalState.CANNONS);

        assertThrows(NullPointerException.class, () -> state.execute(state.getMinPlayerCannons()));
    }

    @RepeatedTest(5)
    void haveAllPlayersPlayed_withAllPlayersPlayed_or_whenAllPlayersSkipped() {
        state.setStatusPlayers(PlayerStatus.PLAYED);
        assertTrue(state.haveAllPlayersPlayed());

        state.setStatusPlayers(PlayerStatus.SKIPPED);
        assertTrue(state.haveAllPlayersPlayed());
    }

    @RepeatedTest(5)
    void haveAllPlayersPlayed_whenSomePlayersHaveNotPlayed_or_whenMixedStatuses() {
        state.setStatusPlayers(PlayerStatus.WAITING);
        assertFalse(state.haveAllPlayersPlayed());

        state.setStatusPlayers(PlayerStatus.PLAYED);
        state.playersStatus.put(state.board.getInGamePlayers().getFirst().getColor(), PlayerStatus.WAITING);
        assertFalse(state.haveAllPlayersPlayed());
    }

    @RepeatedTest(5)
    void setStatusPlayers() {
        state.setStatusPlayers(PlayerStatus.PLAYING);
        for (PlayerData player : state.board.getInGamePlayers()) {
            assertEquals(PlayerStatus.PLAYING, state.playersStatus.get(player.getColor()));
        }
    }

    @RepeatedTest(5)
    void getCurrentPlayer_returnsFirstWaitingPlayer() {
        state.setStatusPlayers(PlayerStatus.PLAYED);
        state.playersStatus.put(state.board.getInGamePlayers().get(1).getColor(), PlayerStatus.WAITING);
        state.playersStatus.put(state.board.getInGamePlayers().get(2).getColor(), PlayerStatus.WAITING);
        PlayerData currentPlayer = state.getCurrentPlayer();
        assertEquals(state.board.getInGamePlayers().get(1), currentPlayer);
    }

    @RepeatedTest(5)
    void getCurrentPlayer_whenAllPlayersHavePlayed() {
        state.setStatusPlayers(PlayerStatus.PLAYED);
        assertThrows(IllegalStateException.class, () -> state.getCurrentPlayer());
    }

    @RepeatedTest(5)
    void play_updatesPlayerStatusToPlaying() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        state.play(player);
        assertEquals(PlayerStatus.PLAYING, state.playersStatus.get(player.getColor()));
    }

    @RepeatedTest(5)
    void play_withNullPlayer() {
        assertThrows(NullPointerException.class, () -> state.play(null));
    }

    @RepeatedTest(5)
    void play_withPlayerAlreadyPlaying() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        state.playersStatus.put(player.getColor(), PlayerStatus.PLAYING);
        state.play(player);
        assertEquals(PlayerStatus.PLAYING, state.playersStatus.get(player.getColor()));
    }

    @RepeatedTest(5)
    void exit_withAllPlayersPlayed() {
        for (PlayerData player : state.board.getInGamePlayers()) {
            state.playersStatus.put(player.getColor(), PlayerStatus.PLAYED);
        }

        assertDoesNotThrow(() -> state.exit());
        assertTrue(state.played);
    }

    @RepeatedTest(5)
    void exit_withWaitingPlayer() {
        for (PlayerData player : state.board.getInGamePlayers()) {
            state.playersStatus.put(player.getColor(), PlayerStatus.PLAYED);
        }
        state.playersStatus.put(state.board.getInGamePlayers().getFirst().getColor(), PlayerStatus.WAITING);

        assertThrows(IllegalStateException.class, () -> state.exit());
    }

    @RepeatedTest(5)
    void exit_withNoPlayers() {
        state.players.clear();
        state.playersStatus.clear();

        assertDoesNotThrow(() -> state.exit());
        assertTrue(state.played);
    }
}