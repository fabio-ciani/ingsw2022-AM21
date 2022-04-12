package it.polimi.ingsw.eriantys.model;

/**
 * This class models a {@link Player}'s school board: it contains an entrance and a dining room (both
 * {@link StudentContainer}s) and an integer representing the number of towers it contains. It exposes methods to get
 * and put towers from and into the board and a method to check if the {@link Player} who owns the board should receive
 * a coin based on the students it contains.
 */
public class SchoolBoard {

	/**
	 * The {@code SchoolBoard}'s entrance.
	 */
	private final StudentContainer entrance;

	/**
	 * The {@code SchoolBoard}'s dining room.
	 */
	private final DiningRoom diningRoom;

	/**
	 * The number of towers contained in the {@code SchoolBoard}, an integer between 0 and 8.
	 */
	private int towers;

	/**
	 * Constructs a {@code SchoolBoard} initially containing 8 towers, with an empty {@code entrance} of maximum size 7
	 * and an empty {@code diningRoom}.
	 */
	public SchoolBoard() {
		// TODO use constants based on number of players for entrance.maxSize and towers
		entrance = new StudentContainer(7);
		diningRoom = new DiningRoom();
		towers = 8;
	}

	/**
	 * Returns this {@code SchoolBoard}'s entrance.
	 * @return this {@code SchoolBoard}'s entrance.
	 */
	public StudentContainer getEntrance() {
		return entrance;
	}

	/**
	 * Returns this {@code SchoolBoard}'s dining room.
	 * @return this {@code SchoolBoard}'s dining room.
	 */
	public DiningRoom getDiningRoom() {
		return diningRoom;
	}

	/**
	 * Returns {@code true} if and only if the {@link Player} who owns this {@code SchoolBoard} is entitled to receive a
	 * coin based on the number of students of color {@code color} their {@link DiningRoom} contains.
	 * @param color the {@link Color} whose amount of students is checked.
	 * @return {@code true} if and only if the {@link Player} who owns this {@code DiningRoom} is entitled to receive a
	 * coin based on the number of students of color {@code color} their {@link DiningRoom} contains.
	 */
	public boolean checkForCoins(Color color) {
		return diningRoom.checkForCoins(color);
	}

	/**
	 * If this {@code SchoolBoard} has at least one tower prior to this method's invocation, decreases the number of
	 * towers by 1 and returns {@code true}, otherwise returns {@code false}.
	 * @return {@code true} if and only if this {@code SchoolBoard} can deploy an additional tower.
	 */
	public boolean getTower() {
		if (towers == 0)
			return false;
		towers--;
		return true;
	}

	/**
	 * If this {@code SchoolBoard} has less than 8 towers prior to this method's invocation, increases the number of
	 * towers by 1 and returns {@code true}, otherwise returns {@code false}.
	 * @return {@code true} if and only if this {@code SchoolBoard} can contain an additional tower.
	 */
	public boolean putTower() {
		// TODO use constants based on number of players for entrance.maxSize and towers
		if (towers == 8)
			return false;
		towers++;
		return true;
	}
}