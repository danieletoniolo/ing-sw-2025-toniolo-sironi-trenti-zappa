package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.event.game.serverToClient.deck.*;
import it.polimi.ingsw.event.game.serverToClient.dice.DiceRolled;
import it.polimi.ingsw.event.game.serverToClient.energyUsed.*;
import it.polimi.ingsw.event.game.serverToClient.goods.*;
import it.polimi.ingsw.event.game.serverToClient.pickedTile.PickedTileFromSpaceship;
import it.polimi.ingsw.event.game.serverToClient.placedTile.*;
import it.polimi.ingsw.event.game.serverToClient.planets.PlanetSelected;
import it.polimi.ingsw.event.game.serverToClient.player.*;
import it.polimi.ingsw.event.game.serverToClient.rotatedTile.RotatedTile;
import it.polimi.ingsw.event.game.serverToClient.spaceship.*;
import it.polimi.ingsw.event.game.serverToClient.timer.TimerFlipped;
import it.polimi.ingsw.event.lobby.serverToClient.*;
import it.polimi.ingsw.view.miniModel.GamePhases;
import it.polimi.ingsw.view.tui.input.ScreenChanged;
import it.polimi.ingsw.view.tui.screens.ChoosePositionTuiScreen;
import it.polimi.ingsw.view.tui.screens.buildingScreens.MainCommandsTuiScreen;
import it.polimi.ingsw.view.tui.screens.gameScreens.*;
import it.polimi.ingsw.view.tui.screens.gameScreens.enemyActions.*;
import it.polimi.ingsw.view.tui.screens.gameScreens.hitsActions.*;
import it.polimi.ingsw.view.tui.screens.gameScreens.looseScreens.*;
import it.polimi.ingsw.view.tui.screens.gameScreens.openSpaceAcitons.OpenSpaceTuiScreen;
import it.polimi.ingsw.view.tui.screens.gameScreens.planetsActions.PlanetsTuiScreen;
import it.polimi.ingsw.view.tui.screens.lobbyScreens.StartingTuiScreen;
import it.polimi.ingsw.view.tui.screens.validationScreens.ValidationFragments;
import it.polimi.ingsw.view.tui.screens.validationScreens.WaitingValidationTuiScreen;
import org.jline.terminal.Terminal;
import it.polimi.ingsw.view.Manager;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.cards.*;
import it.polimi.ingsw.view.tui.input.Parser;
import it.polimi.ingsw.view.tui.screens.*;


public class TuiManager implements Manager {
    private final Object stateLock = new Object();
    private TuiScreenView currentScreen;
    private final Parser parser;
    private final Terminal terminal;
    private TuiScreenView cardScreen;
    private volatile boolean running;

    public TuiManager(Terminal terminal, Parser parser) {
        this.terminal = terminal;
        this.parser = parser;

        currentScreen = new LogInTuiScreen();
        this.running = true;
    }

    public void startTui(){
        //Reading inputs thread
        Thread parserThread = new Thread(() -> {
            while (running) {
                try {
                    currentScreen.readCommand(parser);
                    currentScreen = currentScreen.setNewScreen();
                } catch (ScreenChanged _) {}
                synchronized (stateLock) {
                    stateLock.notifyAll();
                }
            }
        });

        //Thread for printing the TUI
        Thread viewThread = new Thread(() -> {
            while (running) {
                try {
                    synchronized (stateLock) {
                        currentScreen.printTui(terminal);
                    }
                    if (currentScreen.getType().equals(TuiScreens.Ending)) {
                        try {
                            running = false;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.exit(0);
                    }
                    synchronized (stateLock){
                        stateLock.wait();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        parserThread.start();
        viewThread.start();
    }

    @Override
    public void notifyUserIDSet() {

    }

    @Override
    public void notifyConnectionLost() {
        synchronized (stateLock) {
            currentScreen = new ConnectionLostScreen();
            parser.changeScreen();
        }
    }

    @Override
    public void notifyNicknameSet() {

    }

    @Override
    public void notifyLobbies() {
        if (currentScreen.getType().equals(TuiScreens.Menu)) {
            synchronized (stateLock) {
                currentScreen = new MenuTuiScreen();
                parser.changeScreen();
            }
        }
    }

    @Override
    public void notifyCreatedLobby(LobbyCreated data) {
        if (!data.nickname().equals(MiniModel.getInstance().getNickname())) {
            synchronized (stateLock) {
                currentScreen = new MenuTuiScreen();
                currentScreen.setMessage(data.nickname() + " has created a new lobby: ");
                parser.changeScreen();
            }
        }
    }

    @Override
    public void notifyLobbyJoined(LobbyJoined data) {
        synchronized (stateLock) {
            currentScreen.setMessage(data.nickname() + " has joined the lobby");
            stateLock.notifyAll();
        }
    }

    @Override
    public void notifyLobbyLeft(LobbyLeft data) {
        synchronized (stateLock) {
            if (!data.nickname().equals(MiniModel.getInstance().getNickname())) {
                if (MiniModel.getInstance().getCurrentLobby().getLobbyName().equals(data.lobbyID())) {
                    currentScreen.setMessage(data.nickname() + " has left the lobby");
                }
            }
            stateLock.notifyAll();
        }
    }

    @Override
    public void notifyLobbyRemoved(LobbyRemoved data) {
        if (MiniModel.getInstance().getCurrentLobby() == null) {
            synchronized (stateLock) {
                currentScreen = new MenuTuiScreen();
                parser.changeScreen();
            }
        }
    }

    @Override
    public void notifyReadyPlayer() {
        if (currentScreen.getType().equals(TuiScreens.Lobby)) {
            synchronized (stateLock) {
                stateLock.notifyAll();
            }
        }
    }

    /**
     * Change the screen: from lobby screen to building screen
     */
    @Override
    public void notifyStartingGame(StartingGame data) {

    }

    /**
     * Refresh the starting countdown on the screen
     */
    @Override
    public void notifyCountDown() {
        if (currentScreen.getType().equals(TuiScreens.Lobby)) {
            synchronized (stateLock) {
                currentScreen = new StartingTuiScreen();
                parser.changeScreen();
            }
        }
    }

    @Override
    public void notifyPickedLeftDeck(PickedLeftDeck data) {
        if ((currentScreen.getType().equals(TuiScreens.Building) || currentScreen.getType().equals(TuiScreens.MainCommands) || currentScreen.getType().equals(TuiScreens.Watching))  && !MiniModel.getInstance().getClientPlayer().getUsername().equals(data.nickname())) {
            synchronized (stateLock) {
                if (data.usage() == 0) {
                    currentScreen.setMessage(data.nickname() + " has picked deck " + (data.deckIndex() + 1));
                }
                stateLock.notifyAll();
            }
        }
    }

    @Override
    public void notifyDiceRolled(DiceRolled data) {
        synchronized (stateLock) {
            currentScreen.setMessage(data.nickname() + " rolled the dice: " + data.diceValue1() + " " + data.diceValue2());
        }
    }

    @Override
    public void notifyBatteriesLoss(BatteriesLoss data) {
        synchronized (stateLock) {
            currentScreen.setMessage(data.nickname() + " has lost " + data.batteriesIDs().size() + " batteries!");
            stateLock.notifyAll();
        }
    }

    @Override
    public void notifyUpdateGoodsExchange(UpdateGoodsExchange data) {
        synchronized (stateLock) {
            currentScreen.setMessage(data.nickname() + " has modified own goods");
            stateLock.notifyAll();
        }
    }

    @Override
    public void notifyPickedTileFromBoard() {
        if ((currentScreen.getType().equals(TuiScreens.Building) || currentScreen.getType().equals(TuiScreens.MainCommands) || currentScreen.getType().equals(TuiScreens.Watching))) {
            synchronized (stateLock) {
                stateLock.notifyAll();
            }
        }
    }

    @Override
    public void notifyPickedTileFromSpaceShip(PickedTileFromSpaceship data) {
        if (data.nickname().equals(MiniModel.getInstance().getNickname())) {
            synchronized (stateLock) {
                stateLock.notifyAll();
            }
        }
        else {
            if (currentScreen.getType().equals(TuiScreens.Player) && ((PlayerTuiScreen) currentScreen).getPlayerToView().getUsername().equals(data.nickname())) {
                synchronized (stateLock) {
                    stateLock.notifyAll();
                }
            }
        }
    }

    @Override
    public void notifyPickedHiddenTile(String nickname) {
        if (MiniModel.getInstance().getNickname().equals(nickname)) {
            synchronized (stateLock) {
                stateLock.notifyAll();
            }
        }
    }

    @Override
    public void notifyPlacedTileToBoard(PlacedTileToBoard data) {
        if ((currentScreen.getType().equals(TuiScreens.Building) || currentScreen.getType().equals(TuiScreens.MainCommands) || currentScreen.getType().equals(TuiScreens.Watching))) {
            synchronized (stateLock) {
                if (MiniModel.getInstance().getViewablePile().getViewableComponents().size() % MiniModel.getInstance().getViewablePile().getCols() == 1
                        && !MiniModel.getInstance().getNickname().equals(data.nickname())) {
                    currentScreen = new MainCommandsTuiScreen();
                    parser.changeScreen();
                } else {
                    stateLock.notifyAll();
                }
            }
        }
    }

    @Override
    public void notifyPlacedTileToReserve(PlacedTileToReserve data) {
        if ((currentScreen.getType().equals(TuiScreens.Building) || currentScreen.getType().equals(TuiScreens.MainCommands) || currentScreen.getType().equals(TuiScreens.Player))) {
            synchronized (stateLock) {
                stateLock.notifyAll();
            }
        }
    }

    @Override
    public void notifyPlacedTileToSpaceship(PlacedTileToSpaceship data) {
        if (data.nickname().equals(MiniModel.getInstance().getNickname())) {
            synchronized (stateLock) {
                stateLock.notifyAll();
            }
        }
        else {
            if (currentScreen.getType().equals(TuiScreens.Player) && ((PlayerTuiScreen) currentScreen).getPlayerToView().getUsername().equals(data.nickname())) {
                synchronized (stateLock) {
                    stateLock.notifyAll();
                }
            }
        }
    }

    @Override
    public void notifyPlanetSelected(PlanetSelected data) {
        synchronized (stateLock) {
            currentScreen.setMessage(data.nickname() + " has selected planet " + (data.planetNumber() + 1));
            stateLock.notifyAll();
        }
    }

    @Override
    public void notifyEnemyDefeat(EnemyDefeat data) {
        synchronized (stateLock) {
            if (data.enemyDefeat() == null) { // Draw
                if (MiniModel.getInstance().getNickname().equals(data.nickname())) {
                    currentScreen.setNextScreen(new NotClientTurnTuiScreen());
                }
                currentScreen.setMessage("It's a tie! Enemies lose interest... and seek a new target.");
            } else if (data.enemyDefeat()) { // Player win
                if (MiniModel.getInstance().getNickname().equals(data.nickname())) {
                    currentScreen.setNextScreen(new EnemyRewardsTuiScreen());
                }
                currentScreen.setMessage(data.nickname() + " has defeated enemies! Everyone is safe");
            } else { // Player loose
                if (MiniModel.getInstance().getNickname().equals(data.nickname())) {
                    TuiScreenView nextScreen = switch (MiniModel.getInstance().getShuffledDeckView().getDeck().peek().getCardViewType()) {
                        case SLAVERS -> new LooseCrewTuiScreen();
                        case SMUGGLERS -> new LooseGoodsTuiScreen(new NotClientTurnTuiScreen());
                        case PIRATES -> new RollDiceTuiScreen();
                        default -> new NotClientTurnTuiScreen();
                    };
                    currentScreen.setNextScreen(nextScreen);
                }
                currentScreen.setMessage(data.nickname() + " has lost! Enemies are seeking a new target");
            }
            parser.changeScreen();
        }
    }

    @Override
    public void notifyMinPlayer(MinPlayer data) {

    }

    @Override
    public void notifyMoveMarker(MoveMarker data) {
        synchronized (stateLock) {
            currentScreen.setMessage(data.nickname() + " has moved");
            stateLock.notifyAll();
        }
    }

    @Override
    public void notifyPlayerGaveUp(PlayerGaveUp data) {
        synchronized (stateLock) {
            currentScreen.setMessage(data.nickname() + " has given up");
            stateLock.notifyAll();
        }
    }

    @Override
    public void notifyPlayerLost(PlayerLost data) {
        synchronized (stateLock) {
            currentScreen.setMessage("Oh no! You lost");
            stateLock.notifyAll();
        }
    }

    @Override
    public void notifyCardPlayed(CardPlayed data) {
        synchronized (stateLock) {
            currentScreen.setMessage(data.nickname() + " has accepted the card");
            stateLock.notifyAll();
        }
    }

    @Override
    public void notifyCurrentPlayer(CurrentPlayer data) {
        synchronized (stateLock) {
            if (!MiniModel.getInstance().getNickname().equals(data.nickname())) {
                TuiScreenView notTurn = new NotClientTurnTuiScreen();
                currentScreen.setNextScreen(notTurn);
                currentScreen = notTurn;
            } else {
                currentScreen.setNextScreen(cardScreen);
                currentScreen = cardScreen;
            }
            parser.changeScreen();
        }
    }

    @Override
    public void notifyScore(Score data) {

    }

    @Override
    public void notifyUpdateCoins(UpdateCoins data) {
        if (currentScreen.getType().equals(TuiScreens.Player)) {
            synchronized (stateLock) {
                currentScreen.setMessage(data.nickname() + " has updated coins");
                stateLock.notifyAll();
            }
        }
    }

    @Override
    public void notifyRotatedTile(RotatedTile data) {
        if (MiniModel.getInstance().getNickname().equals(data.nickname())) {
            synchronized (stateLock) {
                stateLock.notifyAll();
            }
        }
    }

    @Override
    public void notifyBestLookingShips(BestLookingShips data) {
        synchronized (stateLock) {
            StringBuilder message = new StringBuilder();
            if (data.nicknames().size() == 1) {
                message.append(data.nicknames().getFirst()).append(" has the best looking ship!");
            }
            else {
                message.append("The best looking ships are:\n");
                for (int i = 0; i < data.nicknames().size(); i++) {
                    message.append(data.nicknames().get(i));
                    if (i != data.nicknames().size() - 1) message.append(", ");
                }
            }
            currentScreen.setMessage(message.toString());
            stateLock.notifyAll();
        }
    }

    @Override
    public void notifyCanProtect(CanProtect data) {
        if (data.nickname().equals(MiniModel.getInstance().getNickname())) {
            synchronized (stateLock) {
                if (data.canProtect().getValue1() == -1) {
                    currentScreen.setNextScreen(new CantProtectTuiScreen());
                }
                else {
                    currentScreen.setNextScreen(new UseShieldTuiScreen());
                }
                stateLock.notifyAll();
            }
        }
    }

    @Override
    public void notifyComponentDestroyed(ComponentDestroyed data) {
        synchronized (stateLock) {
            if (!MiniModel.getInstance().getNickname().equals(data.nickname())) {
                currentScreen.setMessage(data.nickname() + " has lost " + data.destroyedComponents().size() + " components");
            }
            stateLock.notifyAll();
        }
    }

    @Override
    public void notifyFragments(Fragments data) {
        if (currentScreen.getType().equals(TuiScreens.Validation)) {
            synchronized (stateLock) {
                if (data.nickname().equals(MiniModel.getInstance().getNickname())) {
                    if (data.fragments().size() > 1) {
                        currentScreen.setNextScreen(new ValidationFragments());
                    }
                } else {
                    if (data.fragments().size() > 1) {
                        currentScreen.setMessage(data.nickname() + " has fragmented components");
                    }
                }
                stateLock.notifyAll();
            }
        }
    }

    @Override
    public void notifyInvalidComponents(InvalidComponents data) {
        if (currentScreen.getType().equals(TuiScreens.Validation)) {
            synchronized (stateLock) {
                if (data.nickname().equals(MiniModel.getInstance().getNickname())) {
                    if (data.invalidComponents().isEmpty()) {
                        currentScreen.setNextScreen(new WaitingValidationTuiScreen());
                    }
                    else {
                        currentScreen.setNextScreen(new ValidationTuiScreen());
                    }
                }
                stateLock.notifyAll();
            }
        }
    }

    @Override
    public void notifyNextHit(NextHit data) {
        synchronized (stateLock) {
            if (MiniModel.getInstance().getNickname().equals(data.nickname())) {
                currentScreen = new RollDiceTuiScreen();
                parser.changeScreen();
                currentScreen.setMessage("New hit is coming! Good luck");
            }
            else {
                currentScreen.setMessage("New hit is going to hit " + data.nickname());
                stateLock.notifyAll();
            }
        }
    }

    @Override
    public void notifyUpdateCrewMembers(UpdateCrewMembers data) {
        synchronized (stateLock) {
            currentScreen.setMessage("Crew on " + data.nickname() + "'s spaceship is changed");
            stateLock.notifyAll();
        }
    }

    @Override
    public void notifyLastTimerFlipped() {
        if (currentScreen.getType() == TuiScreens.Building || currentScreen.getType().equals(TuiScreens.MainCommands) || currentScreen.getType() == TuiScreens.Player || currentScreen.getType() == TuiScreens.Deck) {
            synchronized (stateLock) {
                currentScreen = new ChoosePositionTuiScreen();
                parser.changeScreen();
            }
        }
    }

    @Override
    public void notifyTimer(TimerFlipped data, boolean firstSecond) {
        if (firstSecond) {
            synchronized (stateLock) {
                if (data.nickname() != null) {
                    currentScreen.setMessage("Timer flipped by " + data.nickname());
                    stateLock.notifyAll();
                }
                else {
                    currentScreen = new MainCommandsTuiScreen();
                    parser.changeScreen();
                }
            }

        }
        if ((currentScreen.getType().equals(TuiScreens.Building) || currentScreen.getType().equals(TuiScreens.MainCommands) || currentScreen.getType().equals(TuiScreens.Watching))) {
            synchronized (stateLock) {
                stateLock.notifyAll();
            }
        }
    }

    @Override
    public void notifyTimerFinished(TimerFlipped data) {
        if (data.numberOfFlips() == data.maxNumberOfFlips() - 1) {
            synchronized (stateLock) {
                if (currentScreen.getType() == TuiScreens.MainCommands) {
                    currentScreen = new MainCommandsTuiScreen();
                }
                if (currentScreen.getType() == TuiScreens.Player) {
                    currentScreen.setNextScreen(new MainCommandsTuiScreen());
                }
                parser.changeScreen();
            }
        }
    }

    @Override
    public void notifyStateChange() {
        synchronized (stateLock) {
            switch (MiniModel.getInstance().getGamePhase()) {
                case LOBBY -> {
                    currentScreen = new LobbyTuiScreen();
                }
                case BUILDING -> {
                    currentScreen = new MainCommandsTuiScreen();
                }
                case VALIDATION -> {
                    TuiScreenView validation = new ValidationTuiScreen();
                    currentScreen.setNextScreen(validation);
                    currentScreen = validation;
                }
                case CREW -> {
                    TuiScreenView crew = new ModifyCrewTuiScreen();
                    currentScreen.setNextScreen(crew);
                    currentScreen = crew;
                }
                case CARDS -> {
                    CardView card = MiniModel.getInstance().getShuffledDeckView().getDeck().peek();
                    cardScreen = switch (card.getCardViewType()) {
                        case ABANDONEDSTATION -> new AbandonedStationTuiScreen();
                        case ABANDONEDSHIP -> new AbandonedShipTuiScreen();
                        case SMUGGLERS, PIRATES, SLAVERS -> new EnemyTuiScreen();
                        case PLANETS -> new PlanetsTuiScreen();
                        case COMBATZONE -> new CombatZoneTuiScreen();
                        case OPENSPACE -> new OpenSpaceTuiScreen();
                        case STARDUST -> new StarDustTuiScreen();
                        case EPIDEMIC -> new EpidemicTuiScreen();
                        case METEORSSWARM -> new MeteorsSwarmTuiScreen();
                    };
                }
                case REWARD -> currentScreen = new RewardTuiScreen();
                case FINISHED -> {
                    currentScreen = new MenuTuiScreen();
                    currentScreen.setMessage("A player disconnected, you are back to the lobbies menu");
                }
            }

            if (MiniModel.getInstance().getGamePhase() != GamePhases.CARDS) {
                parser.changeScreen();
            }
        }
    }
}
