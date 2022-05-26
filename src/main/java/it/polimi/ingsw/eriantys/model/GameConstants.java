package it.polimi.ingsw.eriantys.model;

/**
 * A class representing all the constants parameters of the game itself.
 */
public class GameConstants {
	private final int cloudSize;
	private final int cloudNumber;
	private final int entranceSize;
	private final int towerNumber;

	/**
	 * An entity to deal with operations involving {@link DiningRoom} objects.
	 */
	public static final String DINING_ROOM = "Dining Room";
	/**
	 * An entity to represent a tie.
	 */
	public static final String TIE = "Tie";

	private GameConstants() {
		cloudSize = 0;
		cloudNumber = 0;
		entranceSize = 0;
		towerNumber = 0;
	}

	/**
	 * A getter for the number of students on the cloud tiles of the {@link Board}.
	 * @return the quantity of students on a cloud tile
	 */
	public int getCloudSize() {
		return cloudSize;
	}

	/**
	 * A getter for the number of cloud tiles on the {@link Board}.
	 * @return the total quantity of cloud tiles
	 */
	public int getCloudNumber() {
		return cloudNumber;
	}

	/**
	 * A getter for the number of students on a {@link SchoolBoard} entrance.
	 * @return the quantity of students on the entrance of a {@link Player}'s {@link SchoolBoard}
	 */
	public int getEntranceSize() {
		return entranceSize;
	}

	/**
	 * A getter for the number of towers on a {@link SchoolBoard}.
	 * @return the quantity of towers on a {@link Player}'s {@link SchoolBoard}
	 */
	public int getTowerNumber() {
		return towerNumber;
	}
}
