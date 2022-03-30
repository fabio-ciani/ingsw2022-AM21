package it.polimi.ingsw.eriantys.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This class models the actual bag in the board game, which initially contains all the students discs. It exposes a
 * method which returns a list of {@link Color}, used during the game setup.
 * @see StudentContainer
 */
public class Bag extends StudentContainer {

	/**
	 * Constructs an empty {@code Bag}, then fills it with 26 students for each color, 130 total.
	 * @see StudentContainer#fill()
	 */
	public Bag() {
		super();
		fill();
	}

	/**
	 * Returns a list containing two students of each {@link Color} in random order, which is needed in order to set up
	 * the islands at the start of the game.
	 * @return a list containing two students of each {@link Color} in random order.
	 * @see Board#setup()
	 */
	public List<Color> setupDraw() {
		List<Color> colors = new ArrayList<>(Arrays.asList(Color.values()));
		colors.addAll(Arrays.asList(Color.values()));
		Collections.shuffle(colors);

		return colors;
	}
}
