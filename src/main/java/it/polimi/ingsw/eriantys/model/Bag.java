package it.polimi.ingsw.eriantys.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This class models the actual bag in the board game, which initially contains all the students discs.
 * It exposes a method which returns a list of {@link Color}, used during the game setup.
 * @see StudentContainer
 */
public class Bag extends StudentContainer {
	private static final int MAX_STUDENTS_PER_COLOR = 26;

	/**
	 * Constructs an empty {@code Bag}, then fills it with 26 students for each color, 130 total.
	 * @see StudentContainer#fill()
	 */
	public Bag() {
		super();
		fill();
	}

	/**
	 * Sets up the islands at the start of the game.
	 * @return a list containing two students of each {@link Color} in random order
	 * @see Board#setup()
	 */
	public List<Color> setupDraw() {
		List<Color> colors = new ArrayList<>(Arrays.asList(Color.values()));
		colors.addAll(Arrays.asList(Color.values()));
		Collections.shuffle(colors);

		return colors;
	}

	/**
	 * A method which states if the {@code Bag} is empty.
	 * @return {@code true} if and only if the object is empty
	 */
	public boolean isEmpty() {
		return super.empty();
	}

	/**
	 * Returns the remaining capacity of the container for the specified {@link Color}, an integer between 0 and
	 * {@link Bag#MAX_STUDENTS_PER_COLOR}, or {@code -1} if {@code color} is {@code null}.
	 * @param color the color whose remaining capacity is returned.
	 * @return the remaining capacity of the container for the specified {@link Color}, an integer between 0 and
	 * {@link Bag#MAX_STUDENTS_PER_COLOR}, or {@code -1} if {@code color} is {@code null}.
	 */
	@Override
	protected int remainingCapacity(Color color) {
		if (color == null)
			return -1;

		return MAX_STUDENTS_PER_COLOR - getQuantity(color);
	}
}
