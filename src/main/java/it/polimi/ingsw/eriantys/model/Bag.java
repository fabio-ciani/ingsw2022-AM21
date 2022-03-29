package it.polimi.ingsw.eriantys.model;

public class Bag extends StudentContainer {
	private static final int MAX_STUDENTS_PER_COLOR = 26;

	public Bag() {
		super();
		// TODO is there a better way to do this??
		for (Color color : Color.values())
			put(color, MAX_STUDENTS_PER_COLOR);
	}
}
