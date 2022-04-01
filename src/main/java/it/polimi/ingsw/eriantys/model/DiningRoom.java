package it.polimi.ingsw.eriantys.model;

/**
 * This class models a dining room, which is a student container with a maximum capacity of 10 students for every color.
 * The class should only be used as a component of a {@link SchoolBoard}.
 * @see SchoolBoard
 */
public class DiningRoom extends StudentContainer {
	private static final int MAX_STUDENTS_PER_COLOR = 10;

	/**
	 * Constructs an empty {@code DiningRoom} with a capacity of {@link DiningRoom#MAX_STUDENTS_PER_COLOR} (10) students
	 * for each color.
	 */
	public DiningRoom() {
		super(MAX_STUDENTS_PER_COLOR * Color.values().length);
	}

	/**
	 * Returns {@code true} if and only if the {@link Player} who owns this {@code DiningRoom} is entitled to receive a
	 * coin based on the number of students of color {@code color} it contains.
	 * @param color the {@link Color} whose amount of students is checked.
	 * @return {@code true} if and only if the {@link Player} who owns this {@code DiningRoom} is entitled to receive a
	 * coin based on the number of students of color {@code color} it contains.
	 */
	public boolean checkForCoins(Color color) {
		return (getQuantity(color) > 0 && getQuantity(color) % 3 == 0);
	}

	/**
	 * Returns the remaining capacity of the container for the specified {@link Color}, an integer between 0 and
	 * {@link DiningRoom#MAX_STUDENTS_PER_COLOR}, or {@link DiningRoom#MAX_STUDENTS_PER_COLOR} {@code +1} if
	 * {@code color} is {@code null}.
	 * @param color the color whose remaining capacity is returned.
	 * @return the remaining capacity of the container for the specified {@link Color}, an integer between 0 and
	 * {@link DiningRoom#MAX_STUDENTS_PER_COLOR}, or {@link DiningRoom#MAX_STUDENTS_PER_COLOR} {@code +1} if
	 * {@code color} is {@code null}.
	 */
	@Override
	protected int remainingCapacity(Color color) {
		return MAX_STUDENTS_PER_COLOR - getQuantity(color);
	}
}
