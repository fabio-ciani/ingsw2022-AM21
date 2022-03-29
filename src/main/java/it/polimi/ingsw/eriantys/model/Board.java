package it.polimi.ingsw.eriantys.model;

import it.polimi.ingsw.eriantys.model.exceptions.IncompatibleControllersException;
import it.polimi.ingsw.eriantys.model.exceptions.IslandNotFoundException;
import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;

import java.util.*;

public class Board {
	private static final int NUMBER_OF_ISLANDS = 12;

	private final GameManager gameManager;
	private final List<IslandGroup> islands;
	private int motherNatureIslandIndex;
	private final Bag bag;
	private final StudentContainer[] cloudTiles;

	public Board(GameManager gameManager) {
		this.gameManager = gameManager;

		this.islands = new ArrayList<>();
		for (int i = 0; i < NUMBER_OF_ISLANDS; i++)
			islands.add(new IslandGroup(String.format("%02d", i+1)));

		this.bag = new Bag();

		// TODO export constant based on number of players for number of cloud tiles
		this.cloudTiles = new StudentContainer[2];
		for (int i = 0; i < 2; i++)
			// TODO export constant based on number of players for size of cloud tiles
			cloudTiles[i] = new StudentContainer(3);
	}

	public IslandGroup getIsland(String id) throws IslandNotFoundException {
		int index = getIslandIndex(id);

		if (index == -1)
			throw new IslandNotFoundException("Requested: " + id + ".");
		return islands.get(index);
	}

	public void setup() {
		motherNatureIslandIndex = new Random().nextInt(NUMBER_OF_ISLANDS);

		initializeIslands();

		// TODO player picks tower color
		// TODO player picks wizard
		// TODO player draw students
	}

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

	public void unifyIslands(String target) throws IslandNotFoundException, IncompatibleControllersException {
		int targetIndex = getIslandIndex(target);
		if (targetIndex == -1)
			throw new IslandNotFoundException("Requested: " + target + ".");

		IslandGroup targetIsland = islands.get(targetIndex);
		IslandGroup prevIsland = islands.get(targetIndex - 1);
		IslandGroup nextIsland = islands.get(targetIndex + 1);

		int startIndex = targetIndex;
		IslandGroup newIsland = targetIsland;

		if (targetIsland.hasSameController(prevIsland)) {
			newIsland = IslandGroup.merge(prevIsland, targetIsland);
			islands.remove(prevIsland);
			startIndex--;
		}
		if (targetIsland.hasSameController(nextIsland)) {
			newIsland = IslandGroup.merge(newIsland, nextIsland);
			islands.remove(nextIsland);
		}

		if (newIsland != targetIsland) {
			islands.remove(targetIsland);
			islands.add(startIndex, newIsland);
		}
	}

	// pick two students of each color, then place each of them on a random island
	// excluding the one where Mother Nature is and the one opposite to that
	private void initializeIslands() {
		List<Color> colors = new ArrayList<>(Arrays.asList(Color.values()));
		colors.addAll(Arrays.asList(Color.values()));
		Collections.shuffle(colors);

		for (int i = 0; i < NUMBER_OF_ISLANDS; i++)
			if (i != motherNatureIslandIndex && i != (motherNatureIslandIndex + 6) % 12)
				try {
					bag.moveTo(islands.get(i), colors.remove(0));
				} catch (NoMovementException e) {
					// TODO handle exception
					e.printStackTrace();
				}
	}

	private int getIslandIndex(String id) {
		for (int i = 0; i < islands.size(); i++)
			if (islands.get(i).getId().equals(id))
				return i;

		return -1;
	}
}
