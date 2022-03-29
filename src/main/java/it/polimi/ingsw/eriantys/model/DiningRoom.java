package it.polimi.ingsw.eriantys.model;

public class DiningRoom extends StudentContainer {
	private static final int MAX_STUDENTS_PER_COLOR = 10;

	public DiningRoom() {
		super(MAX_STUDENTS_PER_COLOR * NUMBER_OF_COLORS);
	}

	/**
	 * Returns the remaining capacity of the container for the specified {@link Color}, an integer between 0 and
	 * {@link DiningRoom#MAX_STUDENTS_PER_COLOR}.
	 * @param color the color whose remaining capacity is returned.
	 * @return the remaining capacity of the container for the specified {@link Color}, an integer between 0 and
	 * {@link DiningRoom#MAX_STUDENTS_PER_COLOR}.
	 */
	@Override
	protected int remainingCapacity(Color color) {
		return MAX_STUDENTS_PER_COLOR - getQuantity(color);
	}
}
