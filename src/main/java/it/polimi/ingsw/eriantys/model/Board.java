package it.polimi.ingsw.eriantys.model;

import it.polimi.ingsw.eriantys.model.exceptions.IncompatibleControllersException;
import it.polimi.ingsw.eriantys.model.exceptions.IslandNotFoundException;
import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;

import java.util.*;
import java.util.function.Consumer;

/**
 * This class contains the game objects that every player can interact with. It exposes methods which should generally
 * be called by the {@link GameManager}, including one to handle the setup of the game, one to refill the cloud tiles
 * and one to automatically check and unify islands.
 */
public class Board {

	/**
	 * The number of single islands in the game. Initially there are 12 isolated island groups, but that number decreases
	 * as the game proceeds and islands are unified.
	 */
	private static final int NUMBER_OF_ISLANDS = 12;

	/**
	 * The list containing all the current {@link IslandGroup} objects. This list will change as a result of islands being
	 * unified, but the order of the islands will be maintained throughout the game.
	 */
	private final List<IslandGroup> islands;

	/**
	 * The index of the island where the Mother Nature pawn is currently located. It should always be an integer between 0
	 * and the current value of {@code islands.size()}.
	 */
	private int motherNatureIslandIndex;

	/**
	 * The bag initially containing all the student discs, which are then moved around between the islands, the cloud
	 * tiles and each player's school board.
	 */
	private final Bag bag;

	/**
	 * The cloud tiles, which are refilled at the beginning of every round and later on used by players to add students to
	 * their school board's entrance.
	 */
	private final StudentContainer[] cloudTiles;

	// TODO	there needs to be a setter and a resolve() method which calls on the island to remove a tile passing this
	private Consumer<Integer> returnTile;

	/**
	 * Constructs a {@code Board}, initializing the islands, bag and cloud tiles. The number and capacity of the cloud
	 * tiles are constants in {@link GameManager}.
	 */
	public Board() {
		this.islands = new ArrayList<>();
		for (int i = 0; i < NUMBER_OF_ISLANDS; i++)
			islands.add(new IslandGroup(String.format("%02d", i+1)));

		this.bag = new Bag();
		this.motherNatureIslandIndex = -1;

		// TODO export constant based on number of players for number of cloud tiles
		this.cloudTiles = new StudentContainer[2];
		for (int i = 0; i < 2; i++)
			// TODO export constant based on number of players for size of cloud tiles
			cloudTiles[i] = new StudentContainer(3);
	}

	/**
	 * Returns the {@link IslandGroup} whose {@code id} matches the specified one.
	 * @param id the requested island's identifier.
	 * @return the {@link IslandGroup} whose {@code id} matches the specified one.
	 * @throws IslandNotFoundException if no island matching the specified {@code id} can be found.
	 */
	public IslandGroup getIsland(String id) throws IslandNotFoundException {
		int index = getIslandIndex(id);

		if (index == -1)
			throw new IslandNotFoundException("Requested: " + id + ".");
		return islands.get(index);
	}

	public IslandGroup getMotherNatureIsland() throws IslandNotFoundException {
		if (motherNatureIslandIndex == -1)
			throw new IslandNotFoundException("Mother Nature has not been deployed.");
		return islands.get(motherNatureIslandIndex);
	}

	/**
	 * Sets up the game by placing Mother Nature on a random island and placing a random student on each island, excluding
	 * the one with Mother Nature on it and the one opposite to it.
	 * @see Bag#setupDraw()
	 */
	public void setup() {
		motherNatureIslandIndex = new Random().nextInt(NUMBER_OF_ISLANDS);

		List<Color> colors = bag.setupDraw();

		for (int i = 0; i < NUMBER_OF_ISLANDS; i++)
			if (i != motherNatureIslandIndex && i != (motherNatureIslandIndex + 6) % 12)
				try {
					bag.moveTo(islands.get(i), colors.remove(0));
				} catch (NoMovementException e) {
					// TODO handle exception
					e.printStackTrace();
				}
	}


	/**
	 * Moves all the students on a cloud tile to the {@link SchoolBoard} entrance of the {@code recipient}.
	 * @param cloudIndex the target cloud tile's index.
	 * @param recipient the target {@link Player}.
	 * @throws NoMovementException if the operation could not be completed
	 */
	public void drawStudents(int cloudIndex, Player recipient) throws NoMovementException {
		cloudTiles[cloudIndex].moveAllTo(recipient.getEntrance());
	}

	/**
	 * Refills the cloud tiles by taking the necessary amount of students from the {@code bag}.
	 */
	public void refillClouds() {
		for (StudentContainer cloud : cloudTiles) {
			try {
				cloud.refillFrom(bag);
			} catch (NoMovementException e) {
				// TODO handle exception
				e.printStackTrace();
			}
		}
	}

	/**
	 * Checks if the island whose id matches {@code target} can be unified with any of the neighboring islands, and if so
	 * unifies them. This method should be called every time the player controlling an island changes.
	 * @param target the target island's identifier.
	 * @throws IslandNotFoundException if no island matching the specified {@code id} can be found.
	 */
	public void unifyIslands(String target) throws IslandNotFoundException {
		int targetIndex = getIslandIndex(target);
		if (targetIndex == -1)
			throw new IslandNotFoundException("Requested: " + target + ".");

		IslandGroup targetIsland = islands.get(targetIndex);
		IslandGroup prevIsland = islands.get(targetIndex - 1);
		IslandGroup nextIsland = islands.get(targetIndex + 1);

		int startIndex = targetIndex;
		IslandGroup newIslandPrev;
		IslandGroup newIslandNext;

		newIslandPrev = tryMerge(prevIsland, targetIsland);
		if (newIslandPrev != null)
			startIndex--;
		else
			newIslandPrev = targetIsland;

		newIslandNext = tryMerge(newIslandPrev, nextIsland);
		if (newIslandNext != null)
			islands.add(startIndex, newIslandNext);
		else
			islands.add(startIndex, newIslandPrev);
	}

	/**
	 * Returns the index of the island with the specified {@code id} within the {@code islands} list, or -1 if no such
	 * island can be found.
	 * @param id the requested island's identifier.
	 * @return the index of the island with the specified {@code id} within the {@code islands} list, or -1 if no such
	 * island can be found.
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
	 * Merges {@code target} and {@code neighbor} and returns the resulting {@link IslandGroup}, or returns {@code null}
	 * if the islands are controlled by different players and cannot be merged.
	 * @param target the first of the islands to merge.
	 * @param neighbor the second of the islands to merge.
	 * @return the {@link IslandGroup} resulting from merging {@code target} and {@code neighbor}, or {@code null} if the
	 * operation cannot be completed.
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
