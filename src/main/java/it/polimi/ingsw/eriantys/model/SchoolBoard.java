package it.polimi.ingsw.eriantys.model;

/**
 * This class models a {@link Player}'s school board: it contains an entrance and a dining room
 * (both {@link StudentContainer}s) and an integer representing the number of towers it contains.
 * It exposes methods to get and put towers from and into the board and
 * a method to check if the {@link Player} who owns the board should receive a coin based on the students it contains.
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
	 * The number of towers contained in the {@code SchoolBoard}, an integer between 0 and
	 * the maximum number of towers according to the number of {@link Player}s in the game.
	 */
	private int towers;

	private final int towerNumber;

	/**
	 * Constructs a {@code SchoolBoard} initially containing the maximum number of towers,
	 * with an empty {@code entrance} of maximum size according to the game rules and an empty {@code diningRoom}.
	 */
	public SchoolBoard(int entranceSize, int towerNumber) {
		this.towerNumber = towerNumber;
		entrance = new StudentContainer(entranceSize);
		diningRoom = new DiningRoom();
		towers = towerNumber;
	}

	/**
	 * A getter for the object's entrance.
	 * @return this {@code SchoolBoard}'s entrance
	 */
	public StudentContainer getEntrance() {
		return entrance;
	}

	/**
	 * A getter for the object's dining room.
	 * @return this {@code SchoolBoard}'s dining room
	 */
	public DiningRoom getDiningRoom() {
		return diningRoom;
	}

	/**
	 * Returns {@code true} if and only if the {@link Player} who owns this {@code SchoolBoard} is entitled to receive a
	 * coin based on the number of students of color {@code color} their {@link DiningRoom} contains.
	 * @param color the {@link Color} whose amount of students is checked
	 * @return {@code true} if and only if the {@link Player} who owns this {@code DiningRoom} is entitled
	 * to receive a coin based on the number of students of color {@code color} their {@link DiningRoom} contains
	 */
	public boolean checkForCoins(Color color) {
		return diningRoom.checkForCoins(color);
	}

	/**
	 * If this {@code SchoolBoard} has at least one tower prior to this method's invocation,
	 * decreases the number of towers by 1 and returns {@code true}, otherwise returns {@code false}.
	 * @return {@code true} if and only if this {@code SchoolBoard} can deploy an additional tower
	 */
	public boolean getTower() {
		if (towers == 0)
			return false;
		towers--;
		return true;
	}

	/**
	 * If this {@code SchoolBoard} has less than the maximum number of towers prior to this method's invocation,
	 * increases the number of towers by 1 and returns {@code true}, otherwise returns {@code false}.
	 * @return {@code true} if and only if this {@code SchoolBoard} can contain an additional tower
	 */
	public boolean putTower() {
		if (towers == towerNumber)
			return false;
		towers++;
		return true;
	}

	/**
	 * A helper-getter method to fulfill the {@link BoardStatus} creation process.
	 * @return a representation of the number of towers owned by the {@link Player}
	 */
	public int getTowerQuantity() {
		return towers;
	}
}