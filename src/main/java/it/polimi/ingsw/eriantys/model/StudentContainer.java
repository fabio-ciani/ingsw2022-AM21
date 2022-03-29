package it.polimi.ingsw.eriantys.model;

import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class provides a container for student discs.
 * It is meant to be used in every class representing a game object on which student discs can be placed.
 * It exposes methods to move students between two {@code StudentContainer} objects, to get the container's maximum size
 * and to get the number of students of a given {@link Color} in the container.
 */
public class StudentContainer {

	private static final int NO_MAX_SIZE = 130;
	protected static final int NUMBER_OF_COLORS = 5;

	/**
	 * The {@link Map} containing the number of students stored in the container for each {@link Color}.
	 * Keys must be all and only the values of {@link Color}.
	 * Values must be non-{@code null} integers greater than or equal to 0.
	 */
	private final Map<Color, Integer> students;

	/**
	 * The maximum number of students allowed in the {@code StudentContainer} at any time.
	 */
	private final int maxSize;

	/**
	 * Constructs a {@code StudentContainer} with maximum size equal to the total number of student discs in the
	 * game (130). This constructor is to be used only to construct a {@code StudentContainer} with no specified
	 * maximum size.
	 */
	public StudentContainer() {
		this.maxSize = NO_MAX_SIZE;
		this.students = new HashMap<>();
		for (Color color : Color.values())
			students.put(color, 0);
	}

	/**
	 * Constructs an empty {@code StudentContainer} with the specified maximum size. The size has an upper bound equal to
	 * the total number of student discs in the game (130).
	 *
	 * @param maxSize the maximum number of students allowed in the container at any moment.
	 */
	public StudentContainer(int maxSize) {
		this.maxSize = Math.min(maxSize, 130);
		this.students = new HashMap<>();
		for (Color color : Color.values())
			students.put(color, 0);
	}

	/**
	 * Returns the amount of students of color {@code color} currently in the container.
	 * @param color the color of students whose amount is requested.
	 * @return the amount of students of color {@code color} currently in the container.
	 */
	public int getQuantity(Color color) {
		if (color == null)
			return -1;

		Integer res = students.putIfAbsent(color, 0);
		return (res == null) ? 0 : res;
	}

	/**
	 * Moves a student of color {@code color} from {@code this} to the destination container {@code dest}. No movement
	 * occurs if {@code this} is empty or the destination container has no remaining capacity.
	 * @param dest the destination {@code StudentContainer}.
	 * @param color the {@link Color} of the student to be moved.
	 * @throws NoMovementException if {@code this} is empty or the destination container is full.
	 */
	public void moveTo(StudentContainer dest, Color color) throws NoMovementException {
		if (!dest.hasRemainingCapacity(color))
			throw new NoMovementException("Moved 0/1: the destination container is full.");

		Integer srcAmount = this.students.putIfAbsent(color, 0);
		Integer destAmount = dest.students.get(color);

		if (srcAmount == null || srcAmount == 0)
			throw new NoMovementException("Moved 0/1: the source container is empty.");

		if (destAmount == null)
			destAmount = 0;

		this.students.put(color, srcAmount - 1);
		dest.students.put(color, destAmount + 1);
	}

	/**
	 * Moves {@code amount} students of random colors from {@code this} to the destination container {@code dest}.
	 * Students are moved one by one until no colors that can be both removed from {@code this} and added to {@code dest}
	 * can be found, at which point no subsequent movements occur.
	 * @param dest the destination {@code StudentContainer}.
	 * @param amount the amount of students to be moved.
	 * @throws NoMovementException if no colors that can be both removed from {@code this} and added to {@code dest}
	 * can be found.
	 */
	public void moveTo(StudentContainer dest, int amount) throws NoMovementException {
		for (int i = 0; i < amount; i++) {
			Color color = this.getRandomColor(dest.availableColors());
			if (color == null)
				throw new NoMovementException("Moved " + i + "/" + amount + ": no matching colors were found.");
			moveTo(dest, color);
		}
	}

	/**
	 * Moves all the students from {@code this} to the destination container {@code dest}. Students are moved color by
	 * color until the destination container has no remaining capacity, at which point no subsequent movements occur. The
	 * source container, {@code this}, is effectively emptied as a result of this method, unless it contained more
	 * students than the destination container could add.
	 * @param dest the destination {@code StudentContainer}.
	 * @throws NoMovementException if {@code this} contains more students than the destination container can add.
	 */
	public void moveAllTo(StudentContainer dest) throws NoMovementException {
		boolean destinationFull = false;

		for (Color color : Color.values()) {
			Integer srcAmount = this.students.get(color);
			Integer destAmount = dest.students.get(color);

			if (srcAmount == null)
				srcAmount = 0;
			if (destAmount == null)
				destAmount = 0;

			int maxMovements = Math.min(dest.remainingCapacity(color), srcAmount);
			this.students.put(color, srcAmount - maxMovements);
			dest.students.put(color, destAmount + maxMovements);
			if (maxMovements < srcAmount)
				destinationFull = true;
		}

		if (destinationFull)
			throw new NoMovementException("Source not emptied: the destination container is full.");
	}

	public void refillFrom(StudentContainer source) throws NoMovementException {
		source.moveTo(this, this.remainingCapacity());
	}

	/**
	 * Returns a random {@link Color} such that {@code this} contains students of that color, or {@code null} if the
	 * container is empty.
	 *
	 * @return a random {@link Color} such that {@code this} contains students of that color, or {@code null} if the
	 * container is empty.
	 */
	private Color getRandomColor(Set<Color> destAvailable) {
		List<Color> available = students.keySet().stream().filter(c -> students.get(c) > 0).toList();
		available.retainAll(destAvailable);

		if (available.size() == 0)
			return null;
		return available.get(new Random().nextInt(available.size()));
	}

	private Set<Color> availableColors() {
		return students.keySet().stream().filter(this::hasRemainingCapacity).collect(Collectors.toSet());
	}

	/**
	 * Returns {@code true} if and only if the container still has some capacity for the specified {@link Color}.
	 * @param color the color whose remaining capacity is checked.
	 * @return {@code true} if and only if the container still has some capacity for the specified {@link Color}.
	 * @see StudentContainer#remainingCapacity(Color)
	 */
	private boolean hasRemainingCapacity(Color color) {
		return remainingCapacity(color) > 0;
	}

	private int remainingCapacity() {
		int currentOccupation = students.values().stream().reduce(0, Integer::sum);
		return maxSize - currentOccupation;
	}

	protected int remainingCapacity(Color color) {
		return remainingCapacity();
	}

	protected void put(Color color, int amount) {
		students.put(color, amount);
	}
}
