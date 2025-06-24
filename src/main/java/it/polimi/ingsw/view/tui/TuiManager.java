package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.event.game.serverToClient.deck.*;
import it.polimi.ingsw.event.game.serverToClient.dice.DiceRolled;
import it.polimi.ingsw.event.game.serverToClient.energyUsed.*;
import it.polimi.ingsw.event.game.serverToClient.forcingInternalState.ForcingBatteriesPenalty;
import it.polimi.ingsw.event.game.serverToClient.goods.*;
import it.polimi.ingsw.event.game.serverToClient.pickedTile.PickedTileFromSpaceship;
import it.polimi.ingsw.event.game.serverToClient.placedTile.*;
import it.polimi.ingsw.event.game.serverToClient.planets.PlanetSelected;
import it.polimi.ingsw.event.game.serverToClient.player.*;
import it.polimi.ingsw.event.game.serverToClient.forcingInternalState.ForcingGiveUp;
import it.polimi.ingsw.event.game.serverToClient.rotatedTile.RotatedTile;
import it.polimi.ingsw.event.game.serverToClient.spaceship.*;
import it.polimi.ingsw.event.game.serverToClient.timer.TimerFlipped;
import it.polimi.ingsw.event.lobby.serverToClient.*;
import it.polimi.ingsw.view.miniModel.GamePhases;
import it.polimi.ingsw.view.tui.input.ScreenChanged;
import it.polimi.ingsw.view.tui.screens.ChoosePosition;
import it.polimi.ingsw.view.tui.screens.buildingScreens.MainBuilding;
import it.polimi.ingsw.view.tui.screens.crewScreens.MainCrew;
import it.polimi.ingsw.view.tui.screens.gameScreens.*;
import it.polimi.ingsw.view.tui.screens.gameScreens.cannonsActions.ManagerCannonsCards;
import it.polimi.ingsw.view.tui.screens.gameScreens.enemyActions.*;
import it.polimi.ingsw.view.tui.screens.gameScreens.engineActions.ManagerEnginesCards;
import it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.exchangeGoods.ManagerExchangeGoodsCards;
import it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.swapGoods.ManagerSwapGoodCards;
import it.polimi.ingsw.view.tui.screens.gameScreens.hitsActions.*;
import it.polimi.ingsw.view.tui.screens.gameScreens.looseScreens.*;
import it.polimi.ingsw.view.tui.screens.gameScreens.openSpaceAcitons.OpenSpaceCards;
import it.polimi.ingsw.view.tui.screens.gameScreens.planetsActions.PlanetsCards;
import it.polimi.ingsw.view.tui.screens.lobbyScreens.Starting;
import it.polimi.ingsw.view.tui.screens.validation.MainValidation;
import it.polimi.ingsw.view.tui.screens.validation.ValidationFragments;
import it.polimi.ingsw.view.tui.screens.validation.WaitingValidation;
import it.polimi.ingsw.view.Manager;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.cards.*;
import it.polimi.ingsw.view.tui.input.Parser;
import it.polimi.ingsw.view.tui.screens.*;

import java.util.Objects;


public class TuiManager implements Manager {
    private final Object stateLock = new Object();
    private TuiScreenView currentScreen;
    private final Parser parser;
    private TuiScreenView cardScreen;
    private volatile boolean running;
    private String rollDice;

    public TuiManager(Parser parser) {
        this.parser = parser;

        currentScreen = new LogIn();
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
                        currentScreen.printTui();
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
    public void notifyNicknameSet(NicknameSet data) {

    }

    @Override
    public void notifyConnectionLost() {
        synchronized (stateLock) {
            currentScreen = new ConnectionLost();
            parser.changeScreen();
        }
    }

    @Override
    public void notifyLobbies() {
        if (currentScreen.getType().equals(TuiScreens.Menu)) {
            synchronized (stateLock) {
                currentScreen = new Menu();
                parser.changeScreen();
            }
        }
    }

    @Override
    public void notifyCreatedLobby(LobbyCreated data) {
        if (!data.nickname().equals(MiniModel.getInstance().getNickname())) {
            synchronized (stateLock) {
                currentScreen = new Menu();
                currentScreen.setMessage(data.nickname() + " has created a new lobby: ");
                parser.changeScreen();
            }
        }
    }

    @Override
    public void notifyLobbyJoined(LobbyJoined data) {
        if (currentScreen.getType() == TuiScreens.Menu) {
            synchronized (stateLock) {
                currentScreen = new Menu();
                parser.changeScreen();
            }
        }
        else if (currentScreen.getType() == TuiScreens.Lobby) {
            synchronized (stateLock) {
                currentScreen.setMessage(data.nickname() + " has joined the lobby");
                stateLock.notifyAll();
            }
        }
    }

    @Override
    public void notifyLobbyLeft(LobbyLeft data) {
        if (!data.nickname().equals(MiniModel.getInstance().getNickname()) && MiniModel.getInstance().getCurrentLobby() == null) {
            if (currentScreen.getType() == TuiScreens.Menu) {
                synchronized (stateLock) {
                    currentScreen = new Menu();
                    parser.changeScreen();
                }
            }

        }
        else {
            if (currentScreen.getType() == TuiScreens.Lobby) {
                synchronized (stateLock) {
                    currentScreen.setMessage(data.nickname() + " has left the lobby");
                    stateLock.notifyAll();
                }
            }
        }
    }

    @Override
    public void notifyLobbyRemoved(LobbyRemoved data) {
        if (MiniModel.getInstance().getCurrentLobby() == null) {
            synchronized (stateLock) {
                currentScreen = new Menu();
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
     * Refresh the starting countdown on the screen
     */
    @Override
    public void notifyCountDown() {
        if (currentScreen.getType() == TuiScreens.Lobby ) {
            synchronized (stateLock) {
                TuiScreenView starting = new Starting();
                currentScreen.setNextScreen(starting);
                currentScreen = starting;
                parser.changeScreen();
            }
        }
        else if (currentScreen.getType() == TuiScreens.StartingLobby) {
            synchronized (stateLock) {
                stateLock.notifyAll();
            }
        }
    }

    @Override
    public void notifyPickedLeftDeck(PickedLeftDeck data) {
        if ((currentScreen.getType().equals(TuiScreens.Building) || currentScreen.getType().equals(TuiScreens.MainBuilding) || currentScreen.getType().equals(TuiScreens.WatchingBuilding))  && !MiniModel.getInstance().getClientPlayer().getUsername().equals(data.nickname())) {
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
            rollDice = data.nickname() + " rolled the dice: " + data.diceValue1() + " " + data.diceValue2() + " -> " + (data.diceValue1() + data.diceValue2());
            currentScreen.setMessage(rollDice);
        }
    }

    @Override
    public void notifyBatteriesLoss(BatteriesLoss data) {
        if (!data.batteriesIDs().isEmpty()) {
            synchronized (stateLock) {
                currentScreen.setMessage(data.nickname() + " has lost " + data.batteriesIDs().size() + " batteries!");
                stateLock.notifyAll();
            }
        }
    }

    @Override
    public void notifyForcingBatteriesPenalty(ForcingBatteriesPenalty data) {
        if (MiniModel.getInstance().getNickname().equals(data.nickname())) {
            synchronized (stateLock) {
                currentScreen.setNextScreen(new LooseBatteryCards());
                currentScreen.setMessage("You have no more goods, you must discard batteries");
                stateLock.notifyAll();
            }
        }
        else {
            synchronized (stateLock) {
                currentScreen.setMessage(data.nickname() + " has no more goods, batteries must be discarded");
                stateLock.notifyAll();
            }
        }
    }

    @Override
    public void notifyForcingGiveUp(ForcingGiveUp data) {
        synchronized (stateLock) {
            if (MiniModel.getInstance().getNickname().equals(data.nickname())) {
                TuiScreenView forceGiveUp = new ForceGiveUp();
                currentScreen.setNextScreen(forceGiveUp);
                currentScreen = forceGiveUp;
                currentScreen.setMessage(data.message());
            }
            else {
                TuiScreenView notTurn = new NotClientTurnCards();
                currentScreen.setNextScreen(notTurn);
                currentScreen = notTurn;
                currentScreen.setMessage(data.nickname() + " is forced to give up");
            }
            parser.changeScreen();
        }
    }

    @Override
    public void notifyUpdateGoodsExchange(UpdateGoodsExchange data) {
        if (!data.exchangeData().isEmpty()) {
            synchronized (stateLock) {
                currentScreen.setMessage(data.nickname() + " has modified own goods");
                stateLock.notifyAll();
            }
        }
    }

    @Override
    public void notifyPickedTileFromBoard() {
        if ((currentScreen.getType().equals(TuiScreens.Building) || currentScreen.getType().equals(TuiScreens.MainBuilding) || currentScreen.getType().equals(TuiScreens.WatchingBuilding))) {
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
            if (currentScreen.getType().equals(TuiScreens.OtherPlayer) && ((OtherPlayer) currentScreen).getPlayerToView().getUsername().equals(data.nickname())) {
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
        if ((currentScreen.getType().equals(TuiScreens.Building) || currentScreen.getType().equals(TuiScreens.MainBuilding) || currentScreen.getType().equals(TuiScreens.WatchingBuilding))) {
            synchronized (stateLock) {
                if (MiniModel.getInstance().getViewablePile().getViewableComponents().size() % MiniModel.getInstance().getViewablePile().getCols() == 1
                        && !MiniModel.getInstance().getNickname().equals(data.nickname())) {
                    currentScreen = new MainBuilding();
                    parser.changeScreen();
                } else {
                    stateLock.notifyAll();
                }
            }
        }
    }

    @Override
    public void notifyPlacedTileToReserve(PlacedTileToReserve data) {
        if ((currentScreen.getType().equals(TuiScreens.Building) || currentScreen.getType().equals(TuiScreens.MainBuilding) || currentScreen.getType().equals(TuiScreens.OtherPlayer))) {
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
            if (currentScreen.getType().equals(TuiScreens.OtherPlayer) && ((OtherPlayer) currentScreen).getPlayerToView().getUsername().equals(data.nickname())) {
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
                    currentScreen.setNextScreen(new NotClientTurnCards());
                }
                currentScreen.setMessage("It's a tie! Enemies lose interest... and seek a new target.");
            } else if (data.enemyDefeat()) { // Player win
                if (MiniModel.getInstance().getNickname().equals(data.nickname())) {
                    currentScreen.setNextScreen(new EnemyRewardsCards());
                }
                currentScreen.setMessage(data.nickname() + " has defeated enemies! Everyone is safe");
            } else { // Player loose
                if (MiniModel.getInstance().getNickname().equals(data.nickname())) {
                    TuiScreenView nextScreen = null;

                    CardViewType cardViewType = MiniModel.getInstance().getShuffledDeckView().getDeck().peek().getCardViewType();
                    if (Objects.requireNonNull(cardViewType) == CardViewType.SLAVERS) {
                        nextScreen = new LooseCrewCards();
                    } else if (cardViewType == CardViewType.SMUGGLERS) {
                        nextScreen = new LooseGoodsCards();
                    } else if (cardViewType == CardViewType.PIRATES) {
                        nextScreen = new NotClientTurnCards();
                        nextScreen.setMessage("You have lost the fight against pirates, prepare your defenses! At the end of the turn you will have to avoid their fires!");
                    } else {
                        nextScreen = new NotClientTurnCards();
                    }

                    currentScreen.setNextScreen(nextScreen);
                }
                else {
                    currentScreen.setMessage(data.nickname() + " has lost! Enemies are seeking a new target");
                }
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
    public void notifyCardPlayed(CardPlayed data) {
        synchronized (stateLock) {
            currentScreen.setMessage(data.nickname() + " has accepted the card");
            stateLock.notifyAll();
        }
    }

    @Override
    public void notifyCurrentPlayer(CurrentPlayer data) {
        synchronized (stateLock) {
            // Check if the current player is not the client
            if (!MiniModel.getInstance().getNickname().equals(data.nickname())) {
                TuiScreenView notTurn = new NotClientTurnCards();
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
        if (currentScreen.getType().equals(TuiScreens.OtherPlayer)) {
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
                CardsGame nextScreen = null;
                switch (data.canProtect().getValue1()) {
                    case -1 -> {
                        nextScreen = new CantProtectCards();
                        currentScreen.setNextScreen(nextScreen);
                    }
                    case 0 -> {
                        nextScreen = new UseShieldCards();
                        currentScreen.setNextScreen(new UseShieldCards());
                    }
                    case 1 -> {
                        nextScreen = new ProtectionNotRequired();
                        currentScreen.setNextScreen(new ProtectionNotRequired());
                    }
                }
                currentScreen = nextScreen;
                currentScreen.setMessage(rollDice);
                parser.changeScreen();
            }
        }
        else {
            synchronized (stateLock) {
                TuiScreenView notTurn = new NotClientTurnCards();
                currentScreen.setNextScreen(notTurn);
                currentScreen = notTurn;
                currentScreen.setMessage(data.nickname() + " is deciding ");
                parser.changeScreen();
            }
        }
    }

    @Override
    public void notifyComponentDestroyed(ComponentDestroyed data) {
        if (!data.destroyedComponents().isEmpty()) {
            synchronized (stateLock) {
                if (!MiniModel.getInstance().getNickname().equals(data.nickname())) {
                    currentScreen.setMessage(data.nickname() + " has lost " + data.destroyedComponents().size() + " components");
                }
                stateLock.notifyAll();
            }
        }
    }

    @Override
    public void notifyFragments(Fragments data) {
        synchronized (stateLock) {
            if (data.nickname().equals(MiniModel.getInstance().getNickname())) {
                if (data.fragments().size() > 1) {
                    if (MiniModel.getInstance().getGamePhase() == GamePhases.VALIDATION) {
                        currentScreen.setNextScreen(new ValidationFragments());
                        stateLock.notifyAll();
                    } else if (MiniModel.getInstance().getGamePhase() == GamePhases.CARDS) {
                        CardsGame nextScreen = new ChooseFragmentsCards();
                        currentScreen.setNextScreen(nextScreen);
                        parser.changeScreen();
                    }
                }
            } else {
                if (data.fragments().size() > 1) {
                    currentScreen.setMessage(data.nickname() + " has fragmented components");
                    stateLock.notifyAll();
                }
            }
        }
    }

    @Override
    public void notifyInvalidComponents(InvalidComponents data) {
        if (currentScreen.getType().equals(TuiScreens.Validation)) {
            synchronized (stateLock) {
                if (data.nickname().equals(MiniModel.getInstance().getNickname())) {
                    if (data.invalidComponents().isEmpty()) {
                        currentScreen.setNextScreen(new WaitingValidation());
                    }
                    else {
                        currentScreen.setNextScreen(new MainValidation());
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
                TuiScreenView rollDice = new RollDiceCards();
                currentScreen.setNextScreen(rollDice);
                currentScreen = rollDice;
                currentScreen.setMessage("New hit is coming! Good luck");
            }
            else {
                TuiScreenView notTurn = new NotClientTurnCards();
                currentScreen.setNextScreen(notTurn);
                currentScreen = notTurn;
                currentScreen.setMessage("A new hit is coming, " + data.nickname() + " is rolling the dice for everyone");
            }
            parser.changeScreen();
        }
    }

    @Override
    public void notifySetCannonStrength(SetCannonStrength data) {
        if (MiniModel.getInstance().getNickname().equals(data.nickname())) {
            synchronized (stateLock) {
                stateLock.notifyAll();
            }
        }
        else {
            if (currentScreen.getType() == TuiScreens.OtherPlayer && ((OtherPlayer) currentScreen).getPlayerToView().getUsername().equals(data.nickname())) {
                synchronized (stateLock) {
                    stateLock.notifyAll();
                }
            }
        }
    }

    @Override
    public void notifySetEngineStrength(SetEngineStrength data) {
        if (MiniModel.getInstance().getNickname().equals(data.nickname())) {
            synchronized (stateLock) {
                stateLock.notifyAll();
            }
        }
        else {
            if (currentScreen.getType() == TuiScreens.OtherPlayer && ((OtherPlayer) currentScreen).getPlayerToView().getUsername().equals(data.nickname())) {
                synchronized (stateLock) {
                    stateLock.notifyAll();
                }
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
        if (currentScreen.getType() == TuiScreens.Building || currentScreen.getType().equals(TuiScreens.MainBuilding) || currentScreen.getType() == TuiScreens.OtherPlayer || currentScreen.getType() == TuiScreens.Deck) {
            synchronized (stateLock) {
                currentScreen = new ChoosePosition();
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
                    currentScreen = new MainBuilding();
                    parser.changeScreen();
                }
            }

        }
        if ((currentScreen.getType().equals(TuiScreens.Building) || currentScreen.getType().equals(TuiScreens.MainBuilding) || currentScreen.getType().equals(TuiScreens.WatchingBuilding))) {
            synchronized (stateLock) {
                stateLock.notifyAll();
            }
        }
    }

    @Override
    public void notifyTimerFinished(TimerFlipped data) {
        if (data.numberOfFlips() == data.maxNumberOfFlips() - 1) {
            synchronized (stateLock) {
                if (currentScreen.getType() == TuiScreens.MainBuilding) {
                    currentScreen = new MainBuilding();
                }
                if (currentScreen.getType() == TuiScreens.OtherPlayer) {
                    currentScreen.setNextScreen(new MainBuilding());
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
                    currentScreen = new Lobby();
                }
                case BUILDING -> {
                    currentScreen = new MainBuilding();
                }
                case VALIDATION -> {
                    TuiScreenView validation = new MainValidation();
                    currentScreen.setNextScreen(validation);
                    currentScreen = validation;
                }
                case CREW -> {
                    TuiScreenView crew = new MainCrew();
                    currentScreen.setNextScreen(crew);
                    currentScreen = crew;
                }
                case CARDS -> {
                    CardView card = MiniModel.getInstance().getShuffledDeckView().getDeck().peek();
                    cardScreen = switch (card.getCardViewType()) {
                        case ABANDONEDSTATION -> new AbandonedStationCards();
                        case ABANDONEDSHIP -> new AbandonedShipCards();
                        case SMUGGLERS, PIRATES, SLAVERS -> new EnemyCards();
                        case PLANETS -> new PlanetsCards();
                        case COMBATZONE -> new CombatZoneCards();
                        case OPENSPACE -> new OpenSpaceCards();
                        case STARDUST -> new StarDustCards();
                        case EPIDEMIC -> new EpidemicCards();
                        case METEORSSWARM -> new MeteorsSwarmCards();
                    };
                }
                case REWARD ->{
                    TuiScreenView reward = new Reward();
                    currentScreen.setNextScreen(reward);
                    currentScreen = reward;
                }
                case FINISHED -> {
                    currentScreen = new Menu();
                    currentScreen.setMessage("A player disconnected, you are back to the lobbies menu");

                    ManagerExchangeGoodsCards.destroyStatics();
                    ManagerSwapGoodCards.destroyStatics();
                    ManagerCannonsCards.destroyStatics();
                    ManagerEnginesCards.destroyStatics();
                    LooseCrewCards.destroyStatics();
                    LooseGoodsCards.destroyStatics();
                    LooseBatteryCards.destroyStatics();
                    Validation.destroyStatics();
                }
            }

            if (MiniModel.getInstance().getGamePhase() != GamePhases.CARDS) {
                parser.changeScreen();
            }
        }
    }
}
