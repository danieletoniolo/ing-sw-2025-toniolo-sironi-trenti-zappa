package it.polimi.ingsw.model.state;

import it.polimi.ingsw.event.game.serverToClient.dice.DiceRolled;
import it.polimi.ingsw.event.game.serverToClient.energyUsed.BatteriesLoss;
import it.polimi.ingsw.event.game.serverToClient.energyUsed.CannonsUsed;
import it.polimi.ingsw.event.game.serverToClient.energyUsed.EnginesUsed;
import it.polimi.ingsw.event.game.serverToClient.energyUsed.ShieldUsed;
import it.polimi.ingsw.event.game.serverToClient.goods.GoodsSwapped;
import it.polimi.ingsw.event.game.serverToClient.goods.UpdateGoodsExchange;
import it.polimi.ingsw.event.game.serverToClient.spaceship.CanProtect;
import it.polimi.ingsw.event.game.serverToClient.spaceship.ComponentDestroyed;
import it.polimi.ingsw.event.game.serverToClient.spaceship.Fragments;
import it.polimi.ingsw.event.game.serverToClient.spaceship.UpdateCrewMembers;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.cards.hits.Hit;
import it.polimi.ingsw.model.good.Good;
import it.polimi.ingsw.model.good.GoodType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.Component;
import it.polimi.ingsw.model.spaceship.SpaceShip;
import it.polimi.ingsw.model.spaceship.Storage;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.*;

/**
 * Handler class to manage the actions related to the {@link State} of the game.
 * This class contains static methods we are used by multiple {@link State} in order to organize the code better
 * and avoid code duplication.
 */
public class Handler {

    static Event protectFromHit(PlayerData player, Pair<Component, Integer> protectionResult, int batteryID) {
        SpaceShip ship = player.getSpaceShip();
        Component component = protectionResult.getValue0();
        int protectionType = protectionResult.getValue1();

        if (protectionType == 0 || protectionType == -1) {
            if (batteryID != -1 && protectionType != -1) {
                ship.useEnergy(batteryID);
                return new ShieldUsed(player.getUsername(), new Pair<>(batteryID, ship.getBattery(batteryID).getEnergyNumber()));
            } else {
                ship.destroyComponent(component.getRow(), component.getColumn());

                ArrayList<Pair<Integer, Integer>> destroyedComponents = new ArrayList<>();
                destroyedComponents.add(new Pair<>(component.getRow(), component.getColumn()));
                return new ComponentDestroyed(player.getUsername(), destroyedComponents);
            }
        }
        // TODO: If we do not need to protect, we should notify via event or throw an exception
        return null;
    }

    static Event checkForFragments(PlayerData player, List<List<Pair<Integer, Integer>>> fragments) {
        SpaceShip ship = player.getSpaceShip();
        fragments.addAll(ship.getDisconnectedComponents());
        if (fragments.size() > 1) {
            return new Fragments(player.getUsername(), fragments);
        } else {
            return null;
        }
    }

    static Event destroyFragment(PlayerData player, List<Pair<Integer, Integer>> fragments) {
        SpaceShip ship = player.getSpaceShip();
        for (Pair<Integer, Integer> fragment : fragments) {
            ship.destroyComponent(fragment.getValue0(), fragment.getValue1());
        }
        return new ComponentDestroyed(player.getUsername(), fragments);
    }

    static Pair<Event, Event> rollDice(PlayerData player, Hit hit, Pair<Component, Integer> protectionResult) {
        int firstDice = (int) (Math.random() * 6) + 1;
        int secondDice = (int) (Math.random() * 6) + 1;
        SpaceShip ship = player.getSpaceShip();
        Pair<Component, Integer> result = ship.canProtect(firstDice + secondDice, hit);
        protectionResult.setAt0(result.getValue0());
        protectionResult.setAt1(result.getValue1());

        return new Pair<>(new CanProtect(player.getUsername(), new Pair<>(result.getValue0().getID(), result.getValue1())), new DiceRolled(player.getUsername(), firstDice, secondDice));
    }

    static Event useExtraStrength(PlayerData player, int type, List<Integer> cannonsOrEnginesID, List<Integer> batteriesID) throws IllegalStateException {
        if (cannonsOrEnginesID.size() != batteriesID.size()) {
            throw new IllegalStateException("The number of cannons and batteries must be the same");
        }
        SpaceShip ship = player.getSpaceShip();
        Map<Integer, Integer> batteriesMap = new HashMap<>();
        for (int batteryID : batteriesID) {
            batteriesMap.merge(batteryID, 1, Integer::sum);
        }
        for (int batteryID : batteriesMap.keySet()) {
            if (ship.getBattery(batteryID).getEnergyNumber() < batteriesMap.get(batteryID)) {
                throw new IllegalStateException("Not enough energy in battery " + batteryID);
            }
        }
        Event event = null;
        switch (type) {
            case 0 -> {
                // Check if the engines are valid
                for (int engineID : cannonsOrEnginesID) {
                    ship.getEngine(engineID);
                }
                event = new EnginesUsed(player.getUsername(), cannonsOrEnginesID, batteriesID.stream().map(t -> new Pair<>(t, ship.getBattery(t).getEnergyNumber())).toList());
            }
            case 1 -> {
                // Check if the cannons are valid
                for (int cannonID : cannonsOrEnginesID) {
                    ship.getCannon(cannonID);
                }
                event  = new CannonsUsed(player.getUsername(), cannonsOrEnginesID, batteriesID.stream().map(t -> new Pair<>(t, ship.getBattery(t).getEnergyNumber())).toList());
            }
        }
        for (int batteryID : batteriesMap.keySet()) {
            ship.useEnergy(batteryID);
        }
        return event;
    }

    static Event loseGoods(PlayerData player, List<Integer> storagesID, int requiredGoodLoss) throws IllegalStateException {
        Map <Integer, Integer> goodsMap = new HashMap<>();
        for (int storageID : storagesID) {
            goodsMap.merge(storageID, 1, Integer::sum);
        }
        // Check that the selected goods to discard are the most valuable
        PriorityQueue<Good> goodsToDiscardQueue = new PriorityQueue<>(Comparator.comparingInt(Good::getValue).reversed());
        for (int storageID : goodsMap.keySet()) {
            for (int i = 0; i < goodsMap.get(storageID); i++) {
                Good good = player.getSpaceShip().getStorage(storageID).peekGood(i);
                if (good == null) {
                    throw new IllegalStateException("Not enough goods in storage " + storageID);
                }
                goodsToDiscardQueue.add(good);
            }
        }
        PriorityQueue<Good> mostValuableGoods = new PriorityQueue<>(player.getSpaceShip().getGoods());

        if (goodsToDiscardQueue.size() < requiredGoodLoss && mostValuableGoods.size() < requiredGoodLoss) {
            throw new IllegalStateException("We have not set enough goods to discard");
        }
        if (goodsToDiscardQueue.size() > requiredGoodLoss) {
            throw new IllegalStateException("We have set too many goods to discard");
        }
        for (int i = 0; i < goodsToDiscardQueue.size(); i++) {
            if (goodsToDiscardQueue.peek().getValue() != mostValuableGoods.peek().getValue()) {
                throw new IllegalStateException("The goods to discard are not the most valuable");
            }
            goodsToDiscardQueue.poll();
            mostValuableGoods.poll();
        }
        // Remove the goods from the storages
        SpaceShip ship = player.getSpaceShip();
        for (int storageID : storagesID) {
            ship.pollGood(storageID);
        }

        return new UpdateGoodsExchange(
                player.getUsername(),
                storagesID.stream()
                        .map(t -> new Pair<>(
                                t,
                                ship.getStorage(t).getGoods().stream().map(g -> g.getColor().getValue()).toList()
                        )).toList()
        );
    }

    static Event loseBatteries(PlayerData player, List<Integer> batteriesID, int requiredBatteryLoss) throws IllegalStateException {
        // Check if there are the provided number of batteries in the provided batteries slots.
        Map<Integer, Integer> batteriesMap = new HashMap<>();
        for (int batteryID : batteriesID) {
            batteriesMap.merge(batteryID, 1, Integer::sum);
        }
        SpaceShip ship = player.getSpaceShip();
        for (int batteryID : batteriesMap.keySet()) {
            if (ship.getBattery(batteryID).getEnergyNumber() < batteriesMap.get(batteryID)) {
                throw new IllegalStateException("Not enough energy in battery " + batteryID);
            }
        }
        // Check if the number of batteries to remove is equal to the number of batteries required to lose
        // The number of batteries to lose is the number of goods to discard minus the number of goods already discarded
        if (batteriesID.size() > requiredBatteryLoss) {
            throw new IllegalStateException("We have set too many batteries to discard");
        }
        // Remove the batteries from the ship
        for (int batterieID : batteriesID) {
            ship.useEnergy(batterieID);
        }

        return new BatteriesLoss(
                player.getUsername(),
                batteriesID.stream().map(t -> new Pair<>(t, ship.getBattery(t).getEnergyNumber())).toList()
        );
    }

    static Event loseCrew(PlayerData player, List<Integer> cabinsID, int requiredCrewLoss) throws IllegalStateException {
        // Check if there are the provided number of crew members in the provided cabins
        Map<Integer, Integer> cabinCrewMap = new HashMap<>();
        for (int cabinID : cabinsID) {
            cabinCrewMap.merge(cabinID, 1, Integer::sum);
        }
        SpaceShip ship = player.getSpaceShip();
        for (int cabinID : cabinCrewMap.keySet()) {
            if (ship.getCabin(cabinID).getCrewNumber() < cabinCrewMap.get(cabinID)) {
                throw new IllegalStateException("Not enough crew members in cabin " + cabinID);
            }
        }
        // Check if the number of crew members to remove is equal to the number of crew members required to lose
        if (cabinsID.size() > requiredCrewLoss) {
            throw new IllegalStateException("To many crew members to lose");
        }
        if (cabinsID.size() < requiredCrewLoss && ship.getCrewNumber() > cabinsID.size()) {
            throw new IllegalStateException("We have not set enough crew members to lose");
        }
        // Remove the crew members from the cabins
        for (int cabinID : cabinCrewMap.keySet()) {
            ship.removeCrewMember(cabinID, cabinCrewMap.get(cabinID));
        }
        // Convert the cabin crew map to a format expected by the event
        List<Triplet<Integer, Integer, Integer>> cabins = cabinCrewMap.entrySet().stream()
                .map(entry -> new Triplet<>(entry.getKey(), entry.getValue(), ship.getCabin(entry.getKey()).hasBrownAlien() ? 1 : ship.getCabin(entry.getKey()).hasPurpleAlien() ? 2 : 0)).toList();

        // Trigger the event to update the crew members
        return new UpdateCrewMembers(player.getUsername(), cabins);
    }

    static Event exchangeGoods(PlayerData player, List<Triplet<List<Good>, List<Good>, Integer>> exchangeData, List<Good> goodsReward) throws IllegalStateException {
        SpaceShip ship = player.getSpaceShip();
        for (Triplet<List<Good>, List<Good>, Integer> triplet : exchangeData) {
            Storage storage;
            // Check that the storage exists
            try {
                storage = ship.getStorage(triplet.getValue2());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid storage ID: " + triplet.getValue2());
            }
            // Check that the goods to get are in the planet selected
            for (Good good : triplet.getValue0()) {
                if (!goodsReward.contains(good)) {
                    throw new IllegalArgumentException ("The good " + good + " the player want to get is not available");
                }
                // Check if there is dangerous goods
                if (good.getColor() == GoodType.RED && !storage.isDangerous()) {
                    throw new IllegalArgumentException ("The good " + good + " is dangerous and the storage is not dangerous");
                }
            }
            // Check that the goods to leave are in the storage
            for (Good good : triplet.getValue1()) {
                if (!storage.hasGood(good)) {
                    throw new IllegalArgumentException ("The Good " + good + " the player want to leave is not in storage " + triplet.getValue2());
                }
            }
            // Check that we can store the goods in the storage
            if (storage.getGoodsCapacity() + triplet.getValue1().size() < triplet.getValue0().size()) {
                throw new IllegalArgumentException ("The storage " + triplet.getValue2() + " does not have enough space to store the goods");
            }
        }
        // If we reach this point, the exchange data is valid so we can execute the exchange
        for (Triplet<List<Good>, List<Good>, Integer> triplet : exchangeData) {
            ship.exchangeGood(triplet.getValue0(), triplet.getValue1(), triplet.getValue2());
        }
        // Convert the exchange data to the format expected by the event and trigger the event
        List<Pair<Integer, List<Integer>>> convertedData = exchangeData.stream()
                .map(t -> new Pair<>(
                        t.getValue2(),
                        ship.getStorage(t.getValue2()).getGoods().stream()
                                .map(g -> g.getColor().getValue())
                                .toList()
                )).toList();
        return new UpdateGoodsExchange(player.getUsername(), convertedData);
    }

    static Event swapGoods(PlayerData player, int storageID1, int storageID2, List<Good> goods1to2, List<Good> goods2to1) throws IllegalStateException {
        // Check that the storage exists
        SpaceShip ship = player.getSpaceShip();
        Storage storage1, storage2;
        try {
            storage1 = ship.getStorage(storageID1);
            storage2 = ship.getStorage(storageID2);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid storage ID: " + storageID1 + " or " + storageID2);
        }
        // Check that the goods to leave are in the storage 1
        for (Good good : goods1to2) {
            if (!storage1.hasGood(good)) {
                throw new IllegalArgumentException ("The Good " + good + " the player want to leave is not in storage " + storageID1);
            }
        }
        // Check that the goods to leave are in the storage 2
        for (Good good : goods2to1) {
            if (!storage2.hasGood(good)) {
                throw new IllegalArgumentException ("The Good " + good + " the player want to leave is not in storage " + storageID2);
            }
        }
        // Check that we can store the goods in the storage 1
        if (storage1.getGoodsCapacity() + goods1to2.size() < goods2to1.size()) {
            throw new IllegalArgumentException ("The storage " + storageID1 + " does not have enough space to store the goods");
        }
        // Check that we can store the goods in the storage 2
        if (storage2.getGoodsCapacity() + goods2to1.size() < goods1to2.size()) {
            throw new IllegalArgumentException ("The storage " + storageID2 + " does not have enough space to store the goods");
        }
        // Swap the goods
        ship.exchangeGood(goods1to2, goods2to1, storageID1);
        ship.exchangeGood(goods2to1, goods1to2, storageID2);

        return new GoodsSwapped(player.getUsername(), storageID1, storageID2, goods1to2.stream().map(Good::getValue).toList(), goods2to1.stream().map(Good::getValue).toList());
    }
}
