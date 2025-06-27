package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.event.game.serverToClient.deck.*;
import it.polimi.ingsw.event.game.serverToClient.dice.DiceRolled;
import it.polimi.ingsw.event.game.serverToClient.energyUsed.*;
import it.polimi.ingsw.event.game.serverToClient.forcingInternalState.ForcingPenalty;
import it.polimi.ingsw.event.game.serverToClient.forcingInternalState.ForcingPlaceMarker;
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
import it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.MenuGoodsCards;
import it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.exchangeGoods.ManagerExchangeGoodsCards;
import it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.swapGoods.ManagerSwapGoodCards;
import it.polimi.ingsw.view.tui.screens.gameScreens.hitsActions.*;
import it.polimi.ingsw.view.tui.screens.gameScreens.looseScreens.*;
import it.polimi.ingsw.view.tui.screens.gameScreens.openSpaceAcitons.OpenSpaceCards;
import it.polimi.ingsw.view.tui.screens.gameScreens.planetsActions.PlanetsCards;
import it.polimi.ingsw.view.tui.screens.lobbyScreens.Starting;
import it.polimi.ingsw.view.tui.screens.rewardScreens.MainReward;
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
    private final MiniModel mm = MiniModel.getInstance();

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
        if (!data.nickname().equals(mm.getNickname())) {
            synchronized (stateLock) {
                currentScreen = new Menu();
                currentScreen.setMessage(data.nickname() + " has created a new lobby");
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
        if (!data.nickname().equals(mm.getNickname()) && mm.getCurrentLobby() == null) {
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
        if (mm.getCurrentLobby() == null) {
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
        if ((currentScreen.getType().equals(TuiScreens.Building) || currentScreen.getType().equals(TuiScreens.MainBuilding) || currentScreen.getType().equals(TuiScreens.WatchingBuilding))  && !mm.getClientPlayer().getUsername().equals(data.nickname())) {
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
            rollDice = data.nickname() + " rolled the dice: " + data.diceValue1() + " + " + data.diceValue2() + " = " + (data.diceValue1() + data.diceValue2());
            currentScreen.setMessage(rollDice);
        }
    }

    @Override
    public void notifyBatteriesLoss(BatteriesLoss data) {
        if (!data.batteriesIDs().isEmpty()) {
            if (mm.getNickname().equals(data.nickname())) {
                synchronized (stateLock) {
                    stateLock.notifyAll();
                }
            }
        }
    }

    @Override
    public void notifyForcingGiveUp(ForcingGiveUp data) {
        synchronized (stateLock) {
            if (mm.getNickname().equals(data.nickname())) {
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
    public void notifyForcingPenalty(ForcingPenalty data) {
        if (mm.getNickname().equals(data.nickname())) {
            synchronized (stateLock) {
                switch (data.penaltyType()) {
                    case 0:
                        TuiScreenView crew = new LooseCrewCards();
                        currentScreen.setNextScreen(crew);
                        currentScreen = crew;
                        currentScreen.setMessage("You have to leave crew members");
                        break;
                    case 1:
                        TuiScreenView goods = new LooseGoodsCards();
                        currentScreen.setNextScreen(goods);
                        currentScreen = goods;
                        currentScreen.setMessage("You have to discard goods");
                        break;
                    case 2:
                        TuiScreenView batteries = new LooseBatteryCards();
                        currentScreen.setNextScreen(batteries);
                        currentScreen = batteries;
                        currentScreen.setMessage("You have no more goods, you must discard batteries");
                        break;
                    case 3:
                        TuiScreenView rollDice = new RollDiceCards();
                        currentScreen.setNextScreen(rollDice);
                        currentScreen = rollDice;
                        currentScreen.setMessage("New hit is coming! Good luck");
                        break;

                }
                parser.changeScreen();
            }
        } else {
            synchronized (stateLock) {
                TuiScreenView notTurn = new NotClientTurnCards();
                currentScreen.setNextScreen(notTurn);
                currentScreen = notTurn;
                currentScreen.setMessage("Waiting for " + data.nickname() + " to manage the penalty");
                parser.changeScreen();
            }
        }
    }

    @Override
    public void notifyForcingPlaceMarker(ForcingPlaceMarker data) {
        if (mm.getNickname().equals(data.nickname())) {
            synchronized (stateLock) {
                TuiScreenView placeMarker = new ChoosePosition(false);
                currentScreen.setNextScreen(placeMarker);
                currentScreen = placeMarker;
                currentScreen.setMessage("You have to place a marker on the board");
                parser.changeScreen();
            }
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
        if (data.nickname().equals(mm.getNickname())) {
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
        if (mm.getNickname().equals(nickname)) {
            synchronized (stateLock) {
                stateLock.notifyAll();
            }
        }
    }

    @Override
    public void notifyPlacedTileToBoard(PlacedTileToBoard data) {
        if ((currentScreen.getType().equals(TuiScreens.Building) || currentScreen.getType().equals(TuiScreens.MainBuilding) || currentScreen.getType().equals(TuiScreens.WatchingBuilding))) {
            synchronized (stateLock) {
                if (mm.getViewablePile().getViewableComponents().size() % mm.getViewablePile().getCols() == 1
                        && !mm.getNickname().equals(data.nickname())) {
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
        if (data.nickname().equals(mm.getNickname())) {
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
                if (mm.getNickname().equals(data.nickname())) {
                    currentScreen.setNextScreen(new NotClientTurnCards());
                }
                currentScreen.setMessage("It's a tie! Enemies lose interest... and seek a new target.");
            } else if (data.enemyDefeat()) { // Player win
                if (mm.getNickname().equals(data.nickname())) {
                    currentScreen.setNextScreen(new EnemyRewardsCards());
                }
                currentScreen.setMessage(data.nickname() + " has defeated enemies! Everyone is safe");
            } else { // Player loose
                if (mm.getNickname().equals(data.nickname())) {
                    TuiScreenView nextScreen;

                    CardViewType cardViewType = mm.getShuffledDeckView().getDeck().peek().getCardViewType();
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
    public void notifyRemoveMarker(RemoveMarker data) {
        synchronized (stateLock) {
            currentScreen.setMessage(data.nickname() + " has remove the marker from the board");
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
    public void notifyCombatZonePhase(CombatZonePhase data) {
        cardScreen = new CombatZoneCards();
    }

    @Override
    public void notifyCurrentPlayer(CurrentPlayer data) {
        synchronized (stateLock) {
            // Check if the current player is not the client
            if (!mm.getNickname().equals(data.nickname())) {
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
        synchronized (stateLock) {
            TuiScreenView reward = new MainReward();
            currentScreen.setNextScreen(reward);
            currentScreen = reward;
            parser.changeScreen();
        }
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
        if (mm.getNickname().equals(data.nickname())) {
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
        if (data.nickname().equals(mm.getNickname())) {
            synchronized (stateLock) {
                CardsGame nextScreen = null;
                switch (data.canProtect().getValue1()) {
                    case -1 -> {
                        nextScreen = new CantProtectCards();
                        currentScreen.setNextScreen(nextScreen);
                        currentScreen.setMessage(rollDice + " -> You can't protect from the hit");
                    }
                    case 0 -> {
                        nextScreen = new UseShieldCards();
                        currentScreen.setNextScreen(new UseShieldCards());
                        currentScreen.setMessage(rollDice + " -> You can protect from the hit, select a battery to use");
                    }
                    case 1 -> {
                        nextScreen = new ProtectionNotRequired();
                        currentScreen.setNextScreen(new ProtectionNotRequired());
                        currentScreen.setMessage(rollDice + " -> You don't need to protect from the hit");
                    }
                }
                currentScreen = nextScreen;
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
                stateLock.notifyAll();
            }
        }
    }

    @Override
    public void notifyFragments(Fragments data) {
        synchronized (stateLock) {
            if (data.nickname().equals(mm.getNickname())) {
                if (data.fragments().size() > 1) {
                    if (mm.getGamePhase() == GamePhases.VALIDATION) {
                        currentScreen.setNextScreen(new ValidationFragments());
                        stateLock.notifyAll();
                    } else if (mm.getGamePhase() == GamePhases.CARDS) {
                        CardsGame nextScreen = new ChooseFragmentsCards();
                        currentScreen.setNextScreen(nextScreen);
                        parser.changeScreen();
                    }
                }
            }
        }
    }

    @Override
    public void notifyInvalidComponents(InvalidComponents data) {
        if (currentScreen.getType().equals(TuiScreens.Validation)) {
            synchronized (stateLock) {
                if (data.nickname().equals(mm.getNickname())) {
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
    public void notifySetCannonStrength(SetCannonStrength data) {
        if (mm.getNickname().equals(data.nickname())) {
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
        if (mm.getNickname().equals(data.nickname())) {
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
                currentScreen = new ChoosePosition(true);
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
            switch (mm.getGamePhase()) {
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
                    CardView card = mm.getShuffledDeckView().getDeck().peek();
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

                }
                case FINISHED -> {
                    TuiScreenView menu = new Menu();
                    currentScreen.setNextScreen(menu);
                    currentScreen = menu;
                    currentScreen.setMessage("You are back to the lobbies menu, a player disconnected or the game is over");

                    MenuGoodsCards.destroyStatics();
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

            if (mm.getGamePhase() != GamePhases.CARDS && mm.getGamePhase() != GamePhases.REWARD) {
                parser.changeScreen();
            }
        }
    }
}
