package Model.State;

import Model.Game.Board.Board;
import Model.Player.PlayerData;
import Model.State.interfaces.JoinableGame;

/**
 * This class represents the lobby state of the game. In this state, players can join, leave and start the game.
 * The lobby state is the initial state of the game, and it is created when the game is created by the first
 * {@link PlayerData}.
 * <p>
 * The method {@link #joinGame(PlayerData)} is used to add a player to the game, and the method
 * {@link #leaveGame(PlayerData)} is used to remove a player from the game. In order to start the game, the method
 * {@link #execute(PlayerData)} must be called from all the players in the lobby.
 * <p>
 * The lobby state is a subclass of the {@link State} class, which is the base class for all game states.
 * @see State
 * @author Daniele Toniolo
 */
public class LobbyState extends State implements JoinableGame {

    /**
     * Constructs a new LobbyState object associated with the given board.
     * When this object is created we just call the super constructor {@link State#State(Board)}.
     * @param board The board associated with this state.
     */
    public LobbyState(Board board) {
        super(board);
    }

    /**
     * Implementation of the {@link JoinableGame#joinGame(PlayerData)} method.
     * <p>
     * To join the game, we set the player in the board using the {@link Board#setPlayer(PlayerData, int)}
     * method, and we add the player to the list of players {@link State#players} in the LobbyState.
     * @see JoinableGame
     */
    public void joinGame(PlayerData player) {
        // Add the player to the board
        board.addInGamePlayers(player);

        // Add the player to the list of players in the LobbyState
        players.add(player);
    }

    /**
     * Implementation of the {@link JoinableGame#leaveGame(PlayerData)} method.
     * <p>
     * To leave the game, we remove the player from the board using the {@link Board#removeInGamePlayer(PlayerData)}
     * method, and we remove the player from the list of players {@link State#players} in the LobbyState.
     * @see JoinableGame
     */
    public void leaveGame(PlayerData player) {
        // Remove the player from the list of players in the LobbyState
        board.removeInGamePlayer(player);

        // Remove the player from the list of players in the LobbyState
        players.remove(player);
    }

    /**
     * The entry method in this state is called when the state is entered.
     * @see State#entry()
     */
    @Override
    public void entry() {
        // TODO: If we want to do something when entering the lobby state, we can do it here
        super.entry();
    }

    /**
     * The execute method in this state is used to communicate that the player is ready to play the game.
     * In order to do this we call the {@link State#execute(PlayerData)} that set the player status as
     * {@link PlayerStatus#PLAYED}.
     * @param player PlayerData of the player that is ready to play
     */
    @Override
    public void execute(PlayerData player) {
        super.execute(player);
    }

    /**
     * The exit method in this state is used to advance to the next state of the game.
     * @see State#exit()
     */
    @Override
    public void exit() {
        super.exit();
    }
}
