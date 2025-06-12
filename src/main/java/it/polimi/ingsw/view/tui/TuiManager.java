package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.Client;
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
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.cards.hits.Hit;
import it.polimi.ingsw.model.game.board.Deck;
import it.polimi.ingsw.model.game.board.Level;
import it.polimi.ingsw.model.good.Good;
import it.polimi.ingsw.model.spaceship.*;
import it.polimi.ingsw.view.miniModel.components.crewmembers.CrewMembers;
import it.polimi.ingsw.view.tui.screens.buildingScreens.ChoosePositionTuiScreen;
import it.polimi.ingsw.view.tui.screens.buildingScreens.MainCommandsTuiScreen;
import it.polimi.ingsw.view.tui.screens.gameScreens.*;
import it.polimi.ingsw.view.tui.screens.gameScreens.enemyActions.EnemyRewardsTuiScreen;
import it.polimi.ingsw.view.tui.screens.gameScreens.enemyActions.EnemyTuiScreen;
import it.polimi.ingsw.view.tui.screens.gameScreens.hitsActions.CantProtectTuiScreen;
import it.polimi.ingsw.view.tui.screens.gameScreens.hitsActions.RollDiceTuiScreen;
import it.polimi.ingsw.view.tui.screens.gameScreens.hitsActions.UseShieldTuiScreen;
import it.polimi.ingsw.view.tui.screens.gameScreens.looseScreens.LooseCrewTuiScreen;
import it.polimi.ingsw.view.tui.screens.gameScreens.looseScreens.LooseGoodsTuiScreen;
import it.polimi.ingsw.view.tui.screens.gameScreens.openSpaceAcitons.OpenSpaceTuiScreen;
import it.polimi.ingsw.view.tui.screens.gameScreens.planetsActions.PlanetsTuiScreen;
import it.polimi.ingsw.view.tui.screens.lobbyScreens.StartingTuiScreen;
import it.polimi.ingsw.view.tui.screens.validationScreens.ValidationFragments;
import it.polimi.ingsw.view.tui.screens.validationScreens.WaitingValidationTuiScreen;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import it.polimi.ingsw.view.Manager;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.board.BoardView;
import it.polimi.ingsw.view.miniModel.board.LevelView;
import it.polimi.ingsw.view.miniModel.cards.*;
import it.polimi.ingsw.view.miniModel.cards.hit.HitDirectionView;
import it.polimi.ingsw.view.miniModel.cards.hit.HitTypeView;
import it.polimi.ingsw.view.miniModel.cards.hit.HitView;
import it.polimi.ingsw.view.miniModel.components.*;
import it.polimi.ingsw.view.miniModel.deck.DeckView;
import it.polimi.ingsw.view.miniModel.good.GoodView;
import it.polimi.ingsw.view.miniModel.lobby.LobbyView;
import it.polimi.ingsw.view.miniModel.player.MarkerView;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.miniModel.spaceship.SpaceShipView;
import it.polimi.ingsw.view.tui.input.Parser;
import it.polimi.ingsw.view.tui.screens.*;
import it.polimi.ingsw.event.game.serverToClient.energyUsed.CannonsUsed;
import it.polimi.ingsw.event.game.serverToClient.energyUsed.EnginesUsed;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class TuiManager implements Manager {
    private final Object stateLock = new Object();
    private TuiScreenView currentScreen;
    private final Parser parser;
    private final Terminal terminal;
    private boolean printInput;
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
                    TuiScreenView screenToUse;
                    synchronized (stateLock) {
                        while (!printInput) stateLock.wait();
                        screenToUse = currentScreen;
                    }

                    screenToUse.readCommand(parser);

                    if (currentScreen == screenToUse) {
                        currentScreen = currentScreen.setNewScreen();
                        parser.changeScreen();
                    }

                    synchronized (stateLock) {
                        printInput = false;
                        stateLock.notifyAll();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //Thread for printing the TUI
        Thread viewThread = new Thread(() -> {
            while (running) {
                try {
                    if (currentScreen.getType().equals(TuiScreens.Ending)) {
                        try {
                            running = false;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.exit(0);
                    }
                    currentScreen.printTui(terminal);
                    printInput = true;
                    synchronized (stateLock){
                        stateLock.notifyAll();
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
    public void notifyNicknameSet() {

    }

    @Override
    public void notifyLobbies() {
        if (currentScreen.getType().equals(TuiScreens.Menu)) {
            synchronized (stateLock) {
                printInput = false;
                currentScreen = new MenuTuiScreen();
                parser.changeScreen();
                stateLock.notifyAll();
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
                printInput = false;
                stateLock.notifyAll();
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
                printInput = false;
                stateLock.notifyAll();
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
                printInput = false;
                stateLock.notifyAll();
            }
        }
    }

    @Override
    public void notifyDrawCard() {
        synchronized (stateLock) {
            if (MiniModel.getInstance().getClientPlayer().equals(MiniModel.getInstance().getCurrentPlayer())) {
                CardView card = MiniModel.getInstance().getShuffledDeckView().getDeck().peek();
                currentScreen = switch (card.getCardViewType()) {
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
            else{
                currentScreen = new NotClientTurnTuiScreen();
            }
            parser.changeScreen();
            printInput = false;
            currentScreen.setMessage("A new card has been drawn!");
            stateLock.notifyAll();
        }
    }

    @Override
    public void notifyPickedLeftDeck(PickedLeftDeck data) {
        if (currentScreen.getType().equals(TuiScreens.Building) && !MiniModel.getInstance().getClientPlayer().getUsername().equals(data.nickname())) {
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
    public void notifyBatteriesUsed(BatteriesLoss data) {
        synchronized (stateLock) {
            currentScreen.setMessage(data.nickname() + " has lost " + data.batteriesIDs().size() + " batteries!");
            stateLock.notifyAll();
        }
    }

    @Override
    public void notifyCannonsUsed(CannonsUsed data) {
        //if (currentScreen.getType().equals(TuiScreens.Player)) {
            //if (((PlayerTuiScreen) currentScreen).getPlayerToView().getUsername().equals(data.nickname())) {
                synchronized (stateLock) {
                    currentScreen.setMessage(data.nickname() + " has turned on " + data.batteriesIDs().size() + " cannons!");
                    stateLock.notifyAll();
                }
            //}
        //}
    }

    @Override
    public void notifyEnginesUsed(EnginesUsed data) {
        synchronized (stateLock) {
            currentScreen.setMessage(data.nickname() + " has turned on " + data.batteriesIDs().size() + " engines!");
            stateLock.notifyAll();
        }
    }

    @Override
    public void notifyShieldUsed(ShieldUsed data) {
        synchronized (stateLock) {
            currentScreen.setMessage(data.nickname() + " has turned on the shield");
        }
    }

    @Override
    public void notifyGoodsSwapped(GoodsSwapped data) {
        synchronized (stateLock) {
            currentScreen.setMessage(data.nickname() + " has swapped goods");
            stateLock.notifyAll();
        }
    }

    @Override
    public void notifyUpdateGoodsExchange(UpdateGoodsExchange data) {
        synchronized (stateLock) {
            currentScreen.setMessage(data.nickname() + " has exchanged goods");
            currentScreen.notifyAll();
        }
    }

    @Override
    public void notifyPickedTileFromBoard() {
        if (currentScreen.getType().equals(TuiScreens.Building)) {
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
        if (currentScreen.getType().equals(TuiScreens.Building)) {
            synchronized (stateLock) {
                stateLock.notifyAll();
            }
        }
    }

    @Override
    public void notifyPlacedTileToReserve(PlacedTileToReserve data) {
        if (currentScreen.getType().equals(TuiScreens.Building)) {
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
                        case SLAVERS -> new LooseCrewTuiScreen(new NotClientTurnTuiScreen());
                        case SMUGGLERS -> new LooseGoodsTuiScreen(new NotClientTurnTuiScreen());
                        case PIRATES -> new RollDiceTuiScreen();
                        default -> new NotClientTurnTuiScreen();
                    };
                    currentScreen.setNextScreen(nextScreen);
                }
                currentScreen.setMessage(data.nickname() + " has lost! Enemies are seeking a new target");
            }
            parser.changeScreen();
            stateLock.notifyAll();
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
    public void notifyPlaying(CurrentPlayer data) {
        if (!MiniModel.getInstance().getNickname().equals(data.nickname())) {
            synchronized (stateLock) {
                currentScreen = new NotClientTurnTuiScreen();
                parser.changeScreen();
                printInput = false;
                stateLock.notifyAll();
            }
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
                currentScreen.setMessage("New hit is coming! Good luck");
            }
            else {
                currentScreen.setMessage("New hit is going to hit " + data.nickname());
            }
            stateLock.notifyAll();
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
        if (currentScreen.getType().equals(TuiScreens.Building)) {
            synchronized (stateLock) {
                currentScreen = new ChoosePositionTuiScreen();
                parser.changeScreen();
                printInput = false;
                stateLock.notifyAll();
            }
        }
    }

    @Override
    public void notifyTimer(TimerFlipped data) {
        if (currentScreen.getType().equals(TuiScreens.Building)) {
            synchronized (stateLock) {
                stateLock.notifyAll();
            }
        }
    }

    @Override
    public void notifyTimerFinished(TimerFlipped data) {
        if (data.numberOfFlips() == data.maxNumberOfFlips() - 1) {
            synchronized (stateLock) {
                currentScreen = new MainCommandsTuiScreen();
                parser.changeScreen();
                printInput = false;
                stateLock.notifyAll();
            }
        }
    }

    @Override
    public void notifyStateChange() {
        synchronized (stateLock) {
            switch (MiniModel.getInstance().getGamePhase()) {
                case LOBBY -> currentScreen = new LobbyTuiScreen();
                case BUILDING -> {
                    currentScreen = new MainCommandsTuiScreen();
                }
                case VALIDATION -> {
                    currentScreen.setNextScreen(new ValidationTuiScreen());
                    currentScreen = new ValidationTuiScreen();
                }
                case CREW -> currentScreen = new ModifyCrewTuiScreen();
                case CARDS -> notifyDrawCard();
                case FINISHED -> currentScreen = new RewardTuiScreen();
            }
            parser.changeScreen();
            stateLock.notifyAll();
        }
    }

    public void set() {
        synchronized (stateLock) {
            currentScreen = new NotClientTurnTuiScreen();
            parser.changeScreen();
            stateLock.notifyAll();
        }
    }

    public static void main(String[] args) {
        Terminal terminal;
        try {
            terminal = TerminalBuilder.builder()
                    .system(true)
                    .build();
        } catch (Exception e) {
            System.err.println("Creation terminal error: " + e.getMessage());
            return;
        }
        Parser parser = new Parser(terminal);

        ArrayList<Component> tiles = TilesManager.getTiles();
        for (int i = 0; i < 152; i++) {
            ComponentView tileView = converter(tiles.get(i));
            tileView.setCovered(false);
            MiniModel.getInstance().getViewableComponents().add(tileView);
        }

        ArrayList<LobbyView> currentLobbies = MiniModel.getInstance().getLobbiesView();
        LobbyView currentLobby = new LobbyView("pippo", 0, 4, LevelView.SECOND);
        currentLobbies.add(new LobbyView("nico", 0, 4, LevelView.LEARNING));
        currentLobbies.add(new LobbyView("eli", 0, 4, LevelView.LEARNING));
        currentLobbies.add(new LobbyView("lolo", 0, 4, LevelView.LEARNING));
        currentLobbies.add(new LobbyView("lore", 0, 4, LevelView.LEARNING));
        currentLobbies.add(new LobbyView("vitto", 0, 4, LevelView.LEARNING));

        MiniModel.getInstance().setCurrentLobby(currentLobby);

        ArrayList<PlayerDataView> otherPlayers = MiniModel.getInstance().getOtherPlayers();
        PlayerDataView player = new PlayerDataView("Player1", MarkerView.YELLOW, new SpaceShipView(currentLobby.getLevel()));
        otherPlayers.add(new PlayerDataView("Player2", MarkerView.RED, new SpaceShipView(currentLobby.getLevel())));
        otherPlayers.add(new PlayerDataView("Player3", MarkerView.GREEN, new SpaceShipView(currentLobby.getLevel())));
        otherPlayers.add(new PlayerDataView("Player4", MarkerView.BLUE, new SpaceShipView(currentLobby.getLevel())));

        MiniModel.getInstance().setClientPlayer(player);
        MiniModel.getInstance().getClientPlayer().setHand(new GenericComponentView());
        MiniModel.getInstance().getClientPlayer().getHand().setCovered(false);

        Deck[] decks = CardsManager.createDecks(Level.SECOND);

        MiniModel.getInstance().setBoardView(new BoardView(currentLobby.getLevel()));
        if (currentLobby.getLevel() == LevelView.SECOND) {
            MiniModel.getInstance().getTimerView().setFlippedTimer(player);
        }

        for (int i = 0; i < 3; i++) {
            DeckView deckView = new DeckView();
            for (Card card : decks[i].getCards()) {
                deckView.addCard(convertCard(card));
            }
            MiniModel.getInstance().getDeckViews().getValue0()[i] = deckView;
            MiniModel.getInstance().getDeckViews().getValue1()[i] = true;
        }

        MiniModel.getInstance().setCurrentPlayer(otherPlayers.getFirst());

        Stack<Card> shuffled;
        do {
            shuffled = CardsManager.createLearningDeck();
        } while (!shuffled.peek().getCardType().equals(CardType.OPENSPACE));

        for (Card card : shuffled) {
            MiniModel.getInstance().getShuffledDeckView().getDeck().add(convertCard(card));
        }
        MiniModel.getInstance().getShuffledDeckView().setOnlyLast(true);

        int cont = 0;
        for (ComponentView tile : MiniModel.getInstance().getViewableComponents()) {
            if (tile instanceof StorageView && ((StorageView) tile).getGoods().length > 1) {
                MiniModel.getInstance().getClientPlayer().getShip().placeComponent(tile, 5, 5 + cont);
                ((StorageView) tile).addGood(GoodView.BLUE);
                ((StorageView) tile).addGood(GoodView.YELLOW);
                //((StorageView) tile).removeGood(GoodView.GREEN);
                tile.setIsWrong(true);
                if (cont == 1) {
                    break;
                }
                cont++;
            }
        }
        cont = 0;
        for (ComponentView tile : MiniModel.getInstance().getViewableComponents()) {
            if (tile instanceof CannonView && tile.getType().equals(TilesTypeView.DOUBLE_CANNON)) {
                MiniModel.getInstance().getClientPlayer().getShip().placeComponent(tile, 6, 5 + cont);
                if (cont == 1) {
                    break;
                }
                cont++;
            }
        }
        cont = 0;
        for (ComponentView tile : MiniModel.getInstance().getViewableComponents()) {
            if (tile instanceof BatteryView ) {
                ((BatteryView) tile).setNumberOfBatteries(2);
                MiniModel.getInstance().getClientPlayer().getShip().placeComponent(tile, 7, 7 + cont);
                if (cont == 1) {
                    break;
                }
                cont++;
            }
        }
        cont = 0;
        for (ComponentView tile : MiniModel.getInstance().getViewableComponents()) {
            if (tile instanceof EngineView && tile.getType().equals(TilesTypeView.DOUBLE_ENGINE) ) {
                MiniModel.getInstance().getClientPlayer().getShip().placeComponent(tile, 8, 7 + cont);
                if (cont == 1) {
                    break;
                }
                cont++;
            }
        }
        cont = 0;
        for (ComponentView tile : MiniModel.getInstance().getViewableComponents()) {
            if (tile instanceof CabinView) {
                ((CabinView) tile).setCrewNumber(1);
                ((CabinView) tile).setCrewType(CrewMembers.PURPLEALIEN);
                MiniModel.getInstance().getClientPlayer().getShip().placeComponent(tile, 8, 4 + cont);
                if (cont == 1) {
                    break;
                }
                cont++;
            }
        }


        TuiManager tui = new TuiManager(terminal, parser);
        tui.startTui();

        /*final int[] secondsRemaining = {15};
        new Thread(() -> {
            while (secondsRemaining[0] >= 0) {
                try {
                    MiniModel.getInstance().getTimerView().setSecondsRemaining(secondsRemaining[0]);
                    tui.notifyTimer();
                    Thread.sleep(1000);
                    secondsRemaining[0]--;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            tui.set();
        }).start();*/
    }

    private static ComponentView converter(Component tile) {
        int[] connectors = new int[4];
        for (int j = 0; j < 4; j++) {
            switch (tile.getConnection(j)) {
                case EMPTY -> connectors[j] = 0;
                case SINGLE -> connectors[j] = 1;
                case DOUBLE -> connectors[j] = 2;
                case TRIPLE -> connectors[j] = 3;
            }
        }

        return switch (tile.getComponentType()) {
            case BATTERY -> new BatteryView(tile.getID(), connectors, tile.getClockwiseRotation(), ((Battery) tile).getEnergyNumber());
            case CABIN -> new CabinView(tile.getID(), connectors, tile.getClockwiseRotation());
            case STORAGE -> new StorageView(tile.getID(), connectors, tile.getClockwiseRotation(), ((Storage) tile).isDangerous(), ((Storage) tile).getGoodsCapacity());
            case BROWN_LIFE_SUPPORT -> new LifeSupportBrownView(tile.getID(), connectors, tile.getClockwiseRotation());
            case PURPLE_LIFE_SUPPORT -> new LifeSupportPurpleView(tile.getID(), connectors, tile.getClockwiseRotation());
            case SINGLE_CANNON, DOUBLE_CANNON -> new CannonView(tile.getID(), connectors, tile.getClockwiseRotation(), ((Cannon) tile).getCannonStrength());
            case SINGLE_ENGINE, DOUBLE_ENGINE -> new EngineView(tile.getID(), connectors, tile.getClockwiseRotation(), ((Engine) tile).getEngineStrength());
            case SHIELD -> {
                boolean[] shields = new boolean[4];
                for (int i = 0; i < 4; i++) shields[i] = ((Shield) tile).canShield(i);
                yield new ShieldView(tile.getID(), connectors, tile.getClockwiseRotation(), shields);
            }
            case CONNECTORS -> new ConnectorsView(tile.getID(), connectors, tile.getClockwiseRotation());
            default -> throw new IllegalStateException("Unexpected value: " + tile.getComponentType());
        };
    }

    private static CardView convertCard(Card card) {
        switch (card.getCardType()) {
            case PIRATES:
                int cannon = ((Pirates) card).getCannonStrengthRequired();
                int credits = ((Pirates) card).getCredit();
                int flight = ((Pirates) card).getFlightDays();
                ArrayList<HitView> hits = new ArrayList<>();
                for (Hit hit : ((Pirates) card).getFires()) {
                    hits.add(new HitView(HitTypeView.valueOf(hit.getType().name()), HitDirectionView.valueOf(hit.getDirection().name())));
                }
                return new PiratesView(card.getID(), false, card.getCardLevel(), cannon, credits, flight, hits);
            case PLANETS:
                int numberOfPlanets = ((Planets) card).getPlanetNumbers();
                List<List<GoodView>> goodViews = new ArrayList<>();
                for (int i = 0; i < numberOfPlanets; i++) {
                    List<GoodView> goodList = new ArrayList<>();
                    for (Good good : ((Planets) card).getPlanet(i)) {
                        goodList.add(GoodView.valueOf(good.getColor().name()));
                    }
                    goodViews.add(goodList);
                }
                return new PlanetsView(card.getID(), false, card.getCardLevel(), ((Planets) card).getFlightDays(), goodViews);
            case SLAVERS:
                return new SlaversView(card.getID(), false, card.getCardLevel(), ((Slavers) card).getCannonStrengthRequired(), ((Slavers) card).getCredit(), ((Slavers) card).getFlightDays(), ((Slavers) card).getCrewLost());
            case EPIDEMIC:
                return new EpidemicView(card.getID(), false, card.getCardLevel());
            case STARDUST:
                return new StarDustView(card.getID(), false, card.getCardLevel());
            case OPENSPACE:
                return new OpenSpaceView(card.getID(), false, card.getCardLevel());
            case SMUGGLERS:
                int cannonStrength = ((Smugglers) card).getCannonStrengthRequired();
                int goodsLost = ((Smugglers) card).getGoodsLoss();
                int flightDays = ((Smugglers) card).getFlightDays();
                List<GoodView> goods = new ArrayList<>();
                for (Good good : ((Smugglers) card).getGoodsReward()) {
                    goods.add(GoodView.valueOf(good.getColor().name()));
                }
                return new SmugglersView(card.getID(), false, card.getCardLevel(), cannonStrength, goodsLost, flightDays, goods);
            case COMBATZONE:
                int loss = ((CombatZone) card).getLost();
                int flights = ((CombatZone) card).getFlightDays();
                List<HitView> hitsList = new ArrayList<>();
                for (Hit hit : ((CombatZone) card).getFires()) {
                    hitsList.add(new HitView(HitTypeView.valueOf(hit.getType().name()), HitDirectionView.valueOf(hit.getDirection().name())));
                }
                return new CombatZoneView(card.getID(), false, card.getCardLevel(), loss, flights, hitsList);
            case METEORSWARM:
                List<HitView> meteorHits = new ArrayList<>();
                for (Hit hit : ((MeteorSwarm) card).getMeteors()) {
                    meteorHits.add(new HitView(HitTypeView.valueOf(hit.getType().name()), HitDirectionView.valueOf(hit.getDirection().name())));
                }
                return new MeteorSwarmView(card.getID(), false, card.getCardLevel(), meteorHits);
            case ABANDONEDSHIP:
                int crewLost = ((AbandonedShip) card).getCrewRequired();
                int creditsRequired = ((AbandonedShip) card).getCredit();
                int flightDaysRequired = ((AbandonedShip) card).getFlightDays();
                return new AbandonedShipView(card.getID(), false, card.getCardLevel(), crewLost, creditsRequired, flightDaysRequired);
            case ABANDONEDSTATION:
                int crew = ((AbandonedStation) card).getCrewRequired();
                int days = ((AbandonedStation) card).getFlightDays();
                List<GoodView> goodsList = new ArrayList<>();
                for (Good good : ((AbandonedStation) card).getGoods()) {
                    goodsList.add(GoodView.valueOf(good.getColor().name()));
                }
                return new AbandonedStationView(card.getID(), false, card.getCardLevel(), crew, days, goodsList);
            default:
                return null;
        }
    }
}
