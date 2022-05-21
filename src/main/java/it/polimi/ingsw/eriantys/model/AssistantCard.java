package it.polimi.ingsw.eriantys.model;

/**
 * An enumeration which defines the assistant cards inside a {@link Player}'s deck.
 */
public enum AssistantCard {
	CHEETAH(1, 1), OSTRICH(2, 1),
	CAT(3, 2), EAGLE(4, 2),
	FOX(5, 3), LIZARD(6, 3),
	OCTOPUS(7, 4), DOG(8, 4),
	ELEPHANT(9, 5), TURTLE(10, 5);

	private final int value, movement;

	AssistantCard(int value, int movement) {
		this.value = value;
		this.movement = movement;
	}

	/**
	 * The number graphically written in the top-left corner of the card.
	 * @return the value of the given {@code AssistantCard} object
	 */
	public int value() {
		return value;
	}

	/**
	 * The number graphically written in the top-right corner of the card.
	 * @return the quantity of movements that Mother Nature may perform by playing the given {@code AssistantCard} object
	 */
	public int movement() {
		return movement;
	}
}