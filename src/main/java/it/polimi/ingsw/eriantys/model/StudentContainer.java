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
	 * Constructs an empty {@code StudentContainer} with the specified maximum size.
	 * @param maxSize the maximum number of students allowed in the container at any moment.
	 */
	public StudentContainer(int maxSize) {
		this.maxSize = maxSize;
		this.students = new HashMap<>();
		for (Color color : Color.values())
			students.put(color, 0);
	}

	/**
	 * Returns the maximum size allowed for the container.
	 * @return the maximum size allowed for the container.
	 */
	public int getMaxSize() {
		return maxSize;
	}

	/**
	 * Returns the amount of students of color {@code color} currently in the container.
	 * @param color the color of students whose amount is requested.
	 * @return the amount of students of color {@code color} currently in the container.
	 */
	public int getQuantity(Color color) {
		Integer res = students.putIfAbsent(color, 0);
		return (res == null) ? 0 : res;
	}

	/**
	 * Moves a student of color {@code color} from the source container {@code from} to the destination container
	 * {@code to}. No movement occurs if the destination container has reached its {@code maxSize}.
	 * @param from the source {@code StudentContainer}.
	 * @param to the destination {@code StudentContainer}.
	 * @param color the {@link Color} of the student to be moved.
	 * @throws NoMovementException if the source container is empty or the destination container is full.
	 */
	public static void move(StudentContainer from, StudentContainer to, Color color) throws NoMovementException {
		if (to.remainingCapacity() == 0)
			return;

		Integer srcAmount = from.students.putIfAbsent(color, 0);
		Integer destAmount = to.students.get(color);

		if (srcAmount == null || srcAmount == 0)
			throw new NoMovementException("Moved 0/1: the source container is empty.");

		if (destAmount == null)
			destAmount = 0;
		else if (destAmount == to.maxSize)
			throw new NoMovementException("Moved 0/1: the destination container is full.");

		from.students.put(color, srcAmount - 1);
		to.students.put(color, destAmount + 1);
	}

	/**
	 * Moves {@code amount} students of random colors from the source container {@code from} to the destination container
	 * {@code to}. Students are moved one by one until the destination container has reached its {@code maxSize}, at which
	 * point no subsequent movements occur.
	 * @param from the source {@code StudentContainer}.
	 * @param to the destination {@code StudentContainer}.
	 * @param amount the amount of students to be moved.
	 * @throws NoMovementException if the source container has less than {@code amount} students or the destination
	 * container has less than {@code amount} remaining capacity.
	 */
	public static void move(StudentContainer from, StudentContainer to, int amount) throws NoMovementException {
		int maxMovements = Math.min(to.remainingCapacity(), amount);

		for (int i = 0; i < maxMovements; i++) {
			Color color = from.getRandomColor();
			if (color == null)
				throw new NoMovementException("Moved " + i + "/" + amount + ": the source container is empty.");
			move(from, to, from.getRandomColor());
		}

		if (maxMovements < amount)
			throw new NoMovementException("Moved " + maxMovements + "/" + amount + ": the destination container is full.");
	}

	/**
	 * Moves all the students in the source container {@code from} to the destination container {@code to}. Students are
	 * moved color by color until the destination container has reached its {@code maxSize}, at which point no subsequent
	 * movements occur. The source container {@code from} is effectively emptied as a result of this method, unless it
	 * contained more students than the destination container could add.
	 * @param from the source {@code StudentContainer}.
	 * @param to the destination {@code StudentContainer}.
	 * @throws NoMovementException if the source container has more students than the destination container can contain.
	 */
	public static void moveAll(StudentContainer from, StudentContainer to) throws NoMovementException {
		for (Color color : Color.values()) {
			Integer srcAmount = from.students.get(color);
			Integer destAmount = to.students.get(color);

			if (srcAmount == null)
				srcAmount = 0;

			if (destAmount == null)
				destAmount = 0;

			int maxMovements = Math.min(to.remainingCapacity(), srcAmount);

			from.students.put(color, srcAmount - maxMovements);
			to.students.put(color, destAmount + maxMovements);

			if (maxMovements < srcAmount)
				throw new NoMovementException("Moved " + maxMovements + "/" + srcAmount + ": the destination container is full.");
		}
	}

	/**
	 * Returns a random {@link Color} such that {@code this} contains students of that color, or {@code null} if the
	 * container is empty.
	 * @return a random {@link Color} such that {@code this} contains students of that color, or {@code null} if the
	 * container is empty.
	 */
	private Color getRandomColor() {
		List<Color> available = students.keySet().stream().filter(c -> students.get(c) > 0).collect(Collectors.toList());

		if (available.size() == 0)
			return null;
		return available.get(new Random().nextInt(available.size()));
	}

	/**
	 * Returns the remaining capacity of the container, an integer between 0 and {@code maxSize}.
	 * @return the remaining capacity of the container, an integer between 0 and {@code maxSize}.
	 */
	private int remainingCapacity() {
		int currentOccupation = students.values().stream().reduce(0, Integer::sum);
		return maxSize - currentOccupation;
	}
}
