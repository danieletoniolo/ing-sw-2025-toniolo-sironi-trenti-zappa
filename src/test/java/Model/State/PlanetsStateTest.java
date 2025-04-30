package Model.State;

import Model.Cards.Planets;
import Model.Game.Board.Board;
import Model.Game.Board.Level;
import Model.Good.Good;
import Model.Good.GoodType;
import Model.Player.PlayerColor;
import Model.Player.PlayerData;
import Model.SpaceShip.ConnectorType;
import Model.SpaceShip.SpaceShip;
import Model.SpaceShip.Storage;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.javatuples.Triplet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlanetsStateTest {
    PlanetsState state;

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

        Board board = new Board(Level.SECOND);
        board.setPlayer(p0, 0);
        board.setPlayer(p1, 1);
        board.setPlayer(p2, 2);
        board.setPlayer(p3, 3);

        List<List<Good>> planets = List.of((List.of(new Good(GoodType.YELLOW))), (List.of(new Good(GoodType.GREEN))));
        Planets c1 = new Planets(2, 1, planets, 3);

        state = new PlanetsState(board, c1);
        assertNotNull(state);
    }

    @RepeatedTest(5)
    void selectPlanet_withUnselectedPlanet() {
        PlayerData player = state.getPlayers().getFirst();
        assertDoesNotThrow(() -> state.selectPlanet(player, 0));
        assertEquals(player, state.getPlanetSelected()[0]);
    }

    @RepeatedTest(5)
    void selectPlanet_withAlreadySelectedPlanet() {
        PlayerData player1 = state.getPlayers().get(0);
        PlayerData player2 = state.getPlayers().get(1);
        state.selectPlanet(player1, 0);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> state.selectPlanet(player2, 0));
        assertTrue(exception.getMessage().contains(player1.getUsername()));
    }

    @RepeatedTest(5)
    void selectPlanet_withInvalidPlanetIndex() {
        PlayerData player = state.getPlayers().getFirst();

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> state.selectPlanet(player, -1));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> state.selectPlanet(player, state.getCard().getPlanetNumbers()));
    }

    @RepeatedTest(5)
    void setGoodsToExchange_withValidExchangeData() {
        PlayerData player = state.getPlayers().getFirst();
        ArrayList<Triplet<ArrayList<Good>, ArrayList<Good>, Integer>> exchangeData = new ArrayList<>();
        exchangeData.add(new Triplet<>(new ArrayList<>(), new ArrayList<>(), 1));

        assertDoesNotThrow(() -> state.setGoodsToExchange(player, exchangeData));
    }

    @RepeatedTest(5)
    void entry_doesNotThrowException() {
        assertDoesNotThrow(() -> state.entry());
    }

    @RepeatedTest(5)
    void execute_withValidExchangeData() {
        PlayerData player = state.getPlayers().getFirst();
        state.playersStatus.replace(player.getColor(), PlayerStatus.PLAYING);
        ArrayList<Triplet<ArrayList<Good>, ArrayList<Good>, Integer>> exchangeData = new ArrayList<>();
        ConnectorType[] connector = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE};
        Storage s = new Storage(2, connector, true, 2);
        s.addGood(new Good(GoodType.YELLOW));
        s.addGood(new Good(GoodType.BLUE));
        player.getSpaceShip().placeComponent(s, 6,7);
        exchangeData.add(new Triplet<>(new ArrayList<>(), new ArrayList<>(), 2));
        state.setGoodsToExchange(player, exchangeData);

        assertDoesNotThrow(() -> state.execute(player));
        assertEquals(PlayerStatus.PLAYED, state.playersStatus.get(player.getColor()));
    }

    @RepeatedTest(5)
    void execute_withWaitingPlayer() {
        PlayerData player = state.getPlayers().getFirst();
        state.playersStatus.replace(player.getColor(), PlayerStatus.WAITING);

        assertDoesNotThrow(() -> state.execute(player));
        assertEquals(PlayerStatus.SKIPPED, state.playersStatus.get(player.getColor()));
    }

    @RepeatedTest(5)
    void execute_withPlayerNotInPlayingState() {
        PlayerData player = state.getPlayers().getFirst();
        state.playersStatus.replace(player.getColor(), PlayerStatus.PLAYED);

        assertDoesNotThrow(() -> state.execute(player));
        assertEquals(PlayerStatus.PLAYED, state.playersStatus.get(player.getColor()));
    }

    @RepeatedTest(5)
    void exit_withAllPlayersPlayed() {
        state.getPlayers().forEach(player -> state.playersStatus.replace(player.getColor(), PlayerStatus.PLAYED));
        int initialSteps = state.getPlayers().getFirst().getStep();
        int flightDays = state.getCard().getFlightDays();

        assertDoesNotThrow(() -> state.exit());
        assertTrue(initialSteps - flightDays >= state.getPlayers().getFirst().getStep());
    }

    @RepeatedTest(5)
    void exit_withPlayerInWaitingState_or_withPlayerInPlayingState() {
        state.playersStatus.replace(state.getPlayers().getFirst().getColor(), PlayerStatus.WAITING);
        assertThrows(IllegalStateException.class, () -> state.exit());

        state.playersStatus.replace(state.getPlayers().getFirst().getColor(), PlayerStatus.PLAYING);
        assertThrows(IllegalStateException.class, () -> state.exit());
    }

    @RepeatedTest(5)
    void getPlayerPosition() {
        PlayerData player = state.getPlayers().getFirst();
        int position = state.getPlayerPosition(player);
        assertEquals(0, position);
    }

    @RepeatedTest(5)
    void getPlayerPosition_withPlayerNotInList_or_withNullPlayer() {
        PlayerData nonExistentPlayer = new PlayerData("123e4567-e89b-12d3-a456-426614174006", PlayerColor.YELLOW, new SpaceShip(Level.SECOND, PlayerColor.YELLOW));
        assertThrows(IllegalArgumentException.class, () -> state.getPlayerPosition(nonExistentPlayer));

        assertThrows(IllegalArgumentException.class, () -> state.getPlayerPosition(null));
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
        state.playersStatus.put(state.getPlayers().getFirst().getColor(), PlayerStatus.WAITING);
        assertFalse(state.haveAllPlayersPlayed());
    }

    @RepeatedTest(5)
    void setStatusPlayers() {
        state.setStatusPlayers(PlayerStatus.PLAYING);
        for (PlayerData player : state.getPlayers()) {
            assertEquals(PlayerStatus.PLAYING, state.playersStatus.get(player.getColor()));
        }
    }

    @RepeatedTest(5)
    void setStatusPlayers_withNullStatus() {
        assertThrows(NullPointerException.class, () -> state.setStatusPlayers(null));
    }

    @RepeatedTest(5)
    void getCurrentPlayer_returnsFirstWaitingPlayer() {
        state.setStatusPlayers(PlayerStatus.PLAYED);
        state.playersStatus.put(state.getPlayers().get(1).getColor(), PlayerStatus.WAITING);
        state.playersStatus.put(state.getPlayers().get(2).getColor(), PlayerStatus.WAITING);
        PlayerData currentPlayer = state.getCurrentPlayer();
        assertEquals(state.getPlayers().get(1), currentPlayer);
    }

    @RepeatedTest(5)
    void getCurrentPlayer_whenAllPlayersHavePlayed() {
        state.setStatusPlayers(PlayerStatus.PLAYED);
        assertThrows(IllegalStateException.class, () -> state.getCurrentPlayer());
    }

    @RepeatedTest(5)
    void play_updatesPlayerStatusToPlaying() {
        PlayerData player = state.getPlayers().getFirst();
        state.play(player);
        assertEquals(PlayerStatus.PLAYING, state.playersStatus.get(player.getColor()));
    }

    @RepeatedTest(5)
    void play_withNullPlayer() {
        assertThrows(NullPointerException.class, () -> state.play(null));
    }

    @RepeatedTest(5)
    void play_withPlayerAlreadyPlaying() {
        PlayerData player = state.getPlayers().getFirst();
        state.playersStatus.put(player.getColor(), PlayerStatus.PLAYING);
        state.play(player);
        assertEquals(PlayerStatus.PLAYING, state.playersStatus.get(player.getColor()));
    }
}