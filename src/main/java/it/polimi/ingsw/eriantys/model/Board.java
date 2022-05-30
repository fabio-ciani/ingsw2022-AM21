package it.polimi.ingsw.eriantys.model;

import it.polimi.ingsw.eriantys.model.characters.HerbGranny;
import it.polimi.ingsw.eriantys.model.exceptions.IncompatibleControllersException;
import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;
import it.polimi.ingsw.eriantys.model.exceptions.IslandNotFoundException;
import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;

import java.util.*;
import java.util.function.Consumer;

/**
 * This class contains the game objects that every player can interact with.
 * It exposes methods which should generally be called by the {@link GameManager},
 * including one to handle the setup of the game, one to refill the cloud tiles and one to automatically check and unify islands.
 */
public class Board {

	/**
	 * The number of single islands in the game. Initially there are 12 isolated island groups,
	 * but that number decreases as the game proceeds and islands are unified.
	 */
	private static final int NUMBER_OF_ISLANDS = 12;

	/**
	 * The list containing all the current {@link IslandGroup} objects.
	 * This list will change as a result of islands being unified, but the order of the islands will be maintained throughout the game.
	 */
	private final List<IslandGroup> islands;

	/**
	 * The index of the island where the Mother Nature pawn is currently located.
	 * It should always be an integer between 0 and the current value of {@code islands.size()}.
	 */
	private int motherNatureIslandIndex;

	/**
	 * The bag initially containing all the student discs, which are then moved around between the islands,
	 * the cloud tiles and each player's school board.
	 */
	private final Bag bag;

	/**
	 * The cloud tiles, which are refilled at the beginning of every round and
	 * later on used by players to add students to their school board's entrance.
	 */
	private final StudentContainer[] cloudTiles;

	private Consumer<Integer> returnTile;

	/**
	 * Constructs a {@code Board}, initializing the islands, bag and cloud tiles.
	 * The number and capacity of the cloud tiles are constants in {@link GameManager}.
	 * @param cloudNumber the number of cloud tiles to be instantiated
	 * @param cloudSize the size of each cloud tile
	 */
	public Board(int cloudNumber, int cloudSize) {
		this.islands = new ArrayList<>();
		for (int i = 0; i < NUMBER_OF_ISLANDS; i++)
			islands.add(new IslandGroup(String.format("%02d", i+1)));

		this.bag = new Bag();
		this.motherNatureIslandIndex = -1;

		this.cloudTiles = new StudentContainer[cloudNumber];
		for (int i = 0; i < cloudNumber; i++)
			cloudTiles[i] = new StudentContainer(cloudSize);
	}

	/**
	 * Returns the {@link IslandGroup} whose {@code id} matches the specified one.
	 * @param id the requested island's identifier
	 * @return the {@link IslandGroup} whose {@code id} matches the specified one
	 * @throws IslandNotFoundException if no island matching the specified {@code id} can be found
	 */
	public IslandGroup getIsland(String id) throws IslandNotFoundException {
		int index = getIslandIndex(id);

		if (index == -1)
			throw new IslandNotFoundException("Requested: " + id);	// this should not happen
		return islands.get(index);
	}

	/**
	 * A getter for the current number of islands.
	 * @return the number of islands on the board
	 */
	public int getIslandNumber() {
		return islands.size();
	}

	/**
	 * A getter for the {@link IslandGroup} where Mother Nature is currently located.
	 * @return the {@link IslandGroup} where Mother Nature is currently located,
	 * or {@code null} if Mother Nature has not been deployed yet
	 */
	public IslandGroup getMotherNatureIsland() {
		if (motherNatureIslandIndex == -1)
			return null;
		return islands.get(motherNatureIslandIndex);
	}

	/**
	 * A getter for the {@link Bag} of this game object.
	 * @return the {@link Bag} containing the student discs
	 */
	public Bag getBag() {
		return bag;
	}

	/**
	 * Sets the {@code returnTile} attribute to {@code returnTileFunction}.
	 * @param returnTileFunction the desired {@code returnTile} {@link Consumer<Integer>}
	 * @throws InvalidArgumentException if {@code returnTileFunction} is {@code null}
	 */
	public void setReturnNoEntryTile(Consumer<Integer> returnTileFunction) throws InvalidArgumentException {
		if (returnTileFunction == null)
			throw new InvalidArgumentException("returnTileFunction argument is null.");
		this.returnTile = returnTileFunction;
	}

	/**
	 * Sets up the game by placing Mother Nature on a random island and placing a random student on each island,
	 * excluding the one with Mother Nature on it and the one opposite to it.
	 * @throws InvalidArgumentException if one or more of the islands and colors used are {@code null}
	 * @throws NoMovementException if the bag is empty
	 * @see Bag#setupDraw()
	 */
	public void setup() throws InvalidArgumentException, NoMovementException {
		motherNatureIslandIndex = new Random().nextInt(NUMBER_OF_ISLANDS);

		List<Color> colors = bag.setupDraw();

		for (int i = 0; i < NUMBER_OF_ISLANDS; i++)
			if (i != motherNatureIslandIndex && i != (motherNatureIslandIndex + 6) % 12)
				bag.moveTo(islands.get(i), colors.remove(0));
	}

	/**
	 * Moves all the students on a cloud tile to the {@link SchoolBoard} entrance of the {@code recipient}.
	 * @param cloudIndex the target cloud tile's index
	 * @param recipient the target {@link Player}
	 * @throws InvalidArgumentException if {@code recipient} is {@code null} or {@code cloudIndex} is out of bounds
	 * @throws NoMovementException if the cloud at index {@code cloudIndex} is empty
	 */
	public void drawStudents(int cloudIndex, Player recipient) throws InvalidArgumentException, NoMovementException {
		if (recipient == null)
			throw new InvalidArgumentException("recipient argument is null");	// this should not happen

		if (cloudIndex < 0 || cloudIndex >= cloudTiles.length)
			throw new InvalidArgumentException("cloudIndex argument is out of bounds");	// this should not happen

		StudentContainer cloud = cloudTiles[cloudIndex];

		if (Arrays.stream(Color.values()).mapToInt(cloud::getQuantity).reduce(0, Integer::sum) == 0)
			throw new NoMovementException("Cloud is empty."); // TODO this could happen (ReusedCloudException)

		cloud.moveAllTo(recipient.getEntrance());
	}

	/**
	 * Returns {@code true} if and only if {@code destination} is a valid island,
	 * in which case the Mother Nature pawn is placed on the specified island.
	 * @param destination the desired destination for the Mother Nature pawn
	 * @return {@code true} if and only if {@code destination} is a valid island
	 */
	public boolean moveMotherNature(IslandGroup destination) {
		int destinationIndex = islands.indexOf(destination);

		if (destinationIndex == -1)
			return false;

		motherNatureIslandIndex = destinationIndex;
		return true;
	}

	/**
	 * Refills the cloud tiles by taking the necessary amount of students from the {@code bag}.
	 * @throws InvalidArgumentException if the bag is {@code null}
	 * @throws NoMovementException if the bag does not contain enough students to fill the clouds
	 */
	public void refillClouds() throws InvalidArgumentException, NoMovementException {
		for (StudentContainer cloud : cloudTiles) {
			cloud.refillFrom(bag);
		}
	}

	/**
	 * Returns {@code true} if and only if the specified island has at least one no-entry tile on it,
	 * in which case the last added tile is removed and returned to the {@link HerbGranny} character card.
	 * @param island the {@link IslandGroup} on which the no-entry tile could be placed
	 * @return {@code true} if and only if the specified island has at least one no-entry tile on it
	 */
	public boolean noEntryEnforced(IslandGroup island) {
		Integer tileId = island.popNoEntryTile();

		if (tileId == null)
			return false;
		this.returnTile.accept(tileId);
		return true;
	}

	/**
	 * Checks if the island whose id matches {@code target} can be unified with any of the neighboring islands,
	 * and if so unifies them. This method should be called every time the player controlling an island changes.
	 * @param target the target island
	 * @throws IslandNotFoundException if the specified {@code target} cannot be found
	 * @throws InvalidArgumentException if {@code target} is {@code null}
	 */
	public void unifyIslands(IslandGroup target) throws IslandNotFoundException, InvalidArgumentException {
		if (target == null)
			throw new InvalidArgumentException("target argument is null");	// this should not happen

		int targetIndex = islands.indexOf(target);
		if (targetIndex == -1)
			throw new IslandNotFoundException("Requested id: " + target.getId());	// this should not happen

		IslandGroup prev = islands.get(targetIndex == 0 ? islands.size() - 1 : targetIndex - 1);
		IslandGroup next = islands.get(targetIndex == islands.size() - 1 ? 0 : targetIndex + 1);

		int startIndex = targetIndex;
		IslandGroup newIslandPrev;
		IslandGroup newIslandNext;

		newIslandPrev = tryMerge(prev, target);
		if (newIslandPrev != null) {
			startIndex--;
			motherNatureIslandIndex--;
		} else
			newIslandPrev = target;

		newIslandNext = tryMerge(newIslandPrev, next);
		if (newIslandNext != null)
			islands.add(startIndex, newIslandNext);
		else if (newIslandPrev != target)
			islands.add(startIndex, newIslandPrev);
	}

	/**
	 * A getter for the steps required by Mother Nature pawn to reach the {@code target} island.
	 * @param target the destination island
	 * @return the number of steps required for Mother Nature to move from
	 * the island where it is currently placed to the {@code target} island
	 */
	public int getDistanceFromMotherNature(IslandGroup target) {
		int targetIndex = islands.indexOf(target);
		if (targetIndex == -1) return -1;
		int diff = targetIndex - motherNatureIslandIndex;
		return diff < 0 ? diff + islands.size() : diff;
	}

	/**
	 * A helper-getter method to fulfill the {@link BoardStatus} creation process.
	 * @return a representation for the islands on the game field
	 */
	public List<String> getIslandsRepresentation() {
		List<String> rep = new ArrayList<>();

		for (IslandGroup i : islands)
			rep.add(i.getId());

		return rep;
	}

	/**
	 * A helper-getter method to fulfill the {@link BoardStatus} creation process.
	 * @return a representation for the cloud tiles on the game field
	 */
	public Map<String, Map<String, Integer>> getCloudTiles() {
		Map<String, Map<String, Integer>> rep = new LinkedHashMap<>();

		for (int i = 0; i < cloudTiles.length; i++)
			rep.put(Integer.toString(i), cloudTiles[i].getRepresentation());

		return rep;
	}

	/**
	 * A getter for the index of the island with the specified {@code id} within the {@code islands} list.
	 * @param id the requested island's identifier
	 * @return the index of the island with the specified {@code id} within the {@code islands} list,
	 * or -1 if no such island can be found
	 */
	private int getIslandIndex(String id) {
		if (id == null)
			return -1;

		for (int i = 0; i < islands.size(); i++)
			if (islands.get(i).getId().equals(id))
				return i;

		return -1;
	}

	/**
	 * Merges {@code target} and {@code neighbor} and returns the resulting {@link IslandGroup},
	 * or returns {@code null} if the islands are controlled by different players and cannot be merged.
	 * @param target the first of the islands to merge
	 * @param neighbor the second of the islands to merge
	 * @return the {@link IslandGroup} resulting from merging {@code target} and {@code neighbor}, or {@code null} if the
	 * operation cannot be completed
	 */
	private IslandGroup tryMerge(IslandGroup target, IslandGroup neighbor) {
		IslandGroup newIsland;

		try {
			newIsland = IslandGroup.merge(target, neighbor);
		} catch (IncompatibleControllersException e) {
			return null;
		}

		islands.remove(target);
		islands.remove(neighbor);

		return newIsland;
	}
}
