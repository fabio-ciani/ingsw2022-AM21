package it.polimi.ingsw.eriantys.model;

import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;
import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;

import java.util.*;

/**
 * This class provides a container for student discs.
 * It is meant to model every game object on which student discs can be placed.
 * It can be used as is, or it can be extended in order to include additional methods and attributes.
 * It exposes various methods to move students between two {@code StudentContainer} objects
 * and a method returning the number of students of a given {@link Color} in the container.
 */
public class StudentContainer {

	/**
	 * The default maximum size for containers which do not intrinsically have a limit on the number of students.
	 * The value 130 is the total number of student discs in the game, so no container should have to hold more.
	 */
	private static final int NO_MAX_SIZE = 130;

	/**
	 * The total number of student discs of each color in the game.
	 */
	private static final int MAX_STUDENTS_PER_COLOR = 26;

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
	 * Constructs a {@code StudentContainer} with maximum size equal to the total number of student discs
	 * in the game (130). This constructor is to be used only to construct a {@code StudentContainer}
	 * with no specified maximum size.
	 */
	public StudentContainer() {
		this.maxSize = NO_MAX_SIZE;
		this.students = new EnumMap<>(Color.class);
		for (Color color : Color.values())
			students.put(color, 0);
	}

	/**
	 * Constructs an empty {@code StudentContainer} with the specified maximum size. The size has an upper bound of 130.
	 * @param maxSize the maximum number of students allowed in the container at any moment.
	 */
	public StudentContainer(int maxSize) {
		this.maxSize = Math.min(maxSize, NO_MAX_SIZE);
		this.students = new EnumMap<>(Color.class);
		for (Color color : Color.values())
			students.put(color, 0);
	}

	/**
	 * A getter for the amount of students of color {@code color} currently in the container.
	 * @param color the color of students whose amount is requested.
	 * @return the amount of students of color {@code color} currently in the container,
	 * or -1 if {@code color} is {@code null}
	 */
	public int getQuantity(Color color) {
		if (color == null)
			return -1;

		Integer res = students.putIfAbsent(color, 0);
		return (res == null) ? 0 : res;
	}

	/**
	 * Moves a student of color {@code color} from {@code this} to the destination container {@code dest}.
	 * No movement occurs if {@code this} is empty or the destination container has no remaining capacity.
	 * @param dest the destination {@code StudentContainer}
	 * @param color the {@link Color} of the student to be moved
	 * @throws InvalidArgumentException if any argument is {@code null}
	 * @throws NoMovementException if {@code this} is empty or the destination container is full
	 */
	public void moveTo(StudentContainer dest, Color color) throws InvalidArgumentException, NoMovementException {
		if (dest == null)
			throw new InvalidArgumentException("dest argument is null");	// this should not happen
		if (color == null)
			throw new InvalidArgumentException("color argument is null");	// this should not happen
		if (!dest.hasRemainingCapacity(color))
			throw new NoMovementException("0/1 - the destination container is full");	// this should not happen

		Integer srcAmount = this.students.putIfAbsent(color, 0);
		Integer destAmount = dest.students.get(color);

		if (srcAmount == null || srcAmount == 0)
			throw new NoMovementException("0/1 - the source container is empty.");	// TODO this could happen (Thief)

		if (destAmount == null)
			destAmount = 0;

		this.students.put(color, srcAmount - 1);
		dest.students.put(color, destAmount + 1);
	}

	/**
	 * Moves {@code amount} students of random colors from {@code this} to the destination container {@code dest}.
	 * Students are moved one by one until {@code amount} is reached or no colors that
	 * can be both removed from {@code this} and added to {@code dest} can be found,
	 * at which point no subsequent movements occur.
	 * @param dest the destination {@code StudentContainer}
	 * @param amount the amount of students to be moved
	 * @throws InvalidArgumentException if {@code dest} is {@code null}
	 * @throws NoMovementException if no colors that can be both removed
	 * from {@code this} and added to {@code dest} can be found
	 */
	public void moveTo(StudentContainer dest, int amount) throws InvalidArgumentException, NoMovementException {
		if (dest == null)
			throw new InvalidArgumentException("dest argument is null");	// this should not happen
		for (int i = 0; i < amount; i++) {
			Color color = this.getRandomColor(dest);
			if (color == null)
				throw new NoMovementException(i + "/" + amount + " - no matching colors were found");	// this should not happen
			moveTo(dest, color);
		}
	}

	/**
	 * Moves all the students from {@code this} to the destination container {@code dest}.
	 * Students are moved color by color until the source container {@code this} is empty
	 * or the destination container has no remaining capacity, at which point no subsequent movements occur.
	 * Generally, the source container {@code this} should be emptied as a result of this method.
	 * @param dest the destination {@code StudentContainer}
	 * @throws InvalidArgumentException if {@code dest} is {@code null}
	 * @throws NoMovementException if {@code this} contains more students than the destination container can add
	 */
	public void moveAllTo(StudentContainer dest) throws InvalidArgumentException, NoMovementException {
		if (dest == null)
			throw new InvalidArgumentException("dest argument is null.");

		boolean destinationFull = false;

		for (Color color : Color.values()) {
			if (!destinationFull) {
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
		}

		if (destinationFull)
			throw new NoMovementException("?/? - the destination container is full");	// this should not happen
	}

	/**
	 * Refills {@code this} to its maximum capacity by picking random students from {@code source}.
	 * Students are moved one by one until {@code this} is full or {@code source} is empty,
	 * at which point no subsequent movements occur.
	 * @param source the source {@code StudentContainer}
	 * @throws InvalidArgumentException if {@code source} is {@code null}
	 * @throws NoMovementException if {@code source} is emptied before {@code this} can be filled
	 * @see StudentContainer#moveTo(StudentContainer, int)
	 */
	public void refillFrom(StudentContainer source) throws InvalidArgumentException, NoMovementException {
		if (source == null)
			throw new InvalidArgumentException("source argument is null");	// this should not happen

		source.moveTo(this, this.remainingCapacity());
	}

	/**
	 * Swaps two student discs between {@code this} and {@code that}:
	 * one student of color {@code thisColor} is removed from {@code this} and
	 * added to {@code that}, and one student of color {@code thatColor} is removed from {@code that}
	 * and added to {@code this}. If any container does not have enough students or enough capacity to perform the swap,
	 * the operation is aborted.
	 * @param that the second {@code StudentContainer}
	 * @param thisColor the {@link Color} of the student being moved from {@code this} to {@code that}
	 * @param thatColor the {@link Color} of the student being moved from {@code that} to {@code this}
	 * @throws InvalidArgumentException if any argument is {@code null}
	 * @throws NoMovementException if any container does not have enough students or enough capacity to perform the swap
	 */
	public void swap(StudentContainer that, Color thisColor, Color thatColor)
				throws InvalidArgumentException, NoMovementException {
		if (that == null)
			throw new InvalidArgumentException("that argument is null.");
		if (thisColor == null)
			throw new InvalidArgumentException("thisColor argument is null.");
		if (thatColor == null)
			throw new InvalidArgumentException("thatColor argument is null.");

		if (thisColor == thatColor)
			return;

		Integer thisAmount = this.students.putIfAbsent(thisColor, 0);
		Integer thatAmount = that.students.putIfAbsent(thatColor, 0);

		if (thisAmount == null || thisAmount == 0 || thatAmount == null || thatAmount == 0)
			throw new NoMovementException("0/1 - a container is empty");	// this should not happen

		this.students.put(thisColor, thisAmount - 1);
		that.students.put(thatColor, thatAmount - 1);

		if (!this.hasRemainingCapacity(thatColor) || !that.hasRemainingCapacity(thisColor)) {
			this.students.put(thisColor, thisAmount);
			that.students.put(thatColor, thatAmount);
			throw new NoMovementException("0/1 - a container is full.");	// TODO this could happen (NoSwapException)
		}

		this.students.put(thatColor, this.students.get(thatColor) + 1);
		that.students.put(thisColor, that.students.get(thisColor) + 1);
	}

	/**
	 * A helper-getter method to fulfill the {@link BoardStatus} creation process.
	 * @return a representation for the object, ordered by {@link Color} enum declarations
	 */
	public Map<String, Integer> getRepresentation() {
		Map<String, Integer> rep = new LinkedHashMap<>();

		List<String> colorLiterals = Arrays.stream(Color.values()).map(Color::toString).toList();
		for (String c : colorLiterals)
			rep.put(c, students.get(Color.valueOf(c)));

		return rep;
	}

	/**
	 * Returns a random {@link Color} such that {@code this} contains students of that color and {@code destination} has
	 * the capacity to contain more, or {@code null} if no such color can be found.
	 * @param destination the destination {@code StudentContainer}.
	 * @return a random {@link Color} such that {@code this} contains students of that color and {@code destination} has
	 * the capacity to contain more, or {@code null} if no such color can be found.
	 */
	private Color getRandomColor(StudentContainer destination) {
		List<Color> available = students.keySet().stream().filter(c -> students.get(c) > 0).toList();
		List<Color> destAvailable = destination.students.keySet().stream().filter(destination::hasRemainingCapacity).toList();
		available = available.stream().filter(destAvailable::contains).toList();

		if (available.size() == 0)
			return null;
		return available.get(new Random().nextInt(available.size()));
	}

	/**
	 * Returns {@code true} if and only if the container has some remaining capacity for the specified {@link Color}.
	 * @param color the color whose remaining capacity is checked.
	 * @return {@code true} if and only if the container has some remaining capacity for the specified {@link Color}.
	 * @see StudentContainer#remainingCapacity(Color)
	 */
	private boolean hasRemainingCapacity(Color color) {
		return remainingCapacity(color) > 0;
	}

	/**
	 * Returns the container's total remaining capacity.
	 * @return the container's total remaining capacity.
	 * @see StudentContainer#remainingCapacity(Color)
	 */
	private int remainingCapacity() {
		int currentOccupation = students.values().stream().reduce(0, Integer::sum);
		return maxSize - currentOccupation;
	}

	/**
	 * Returns the container's remaining capacity for the specified {@link Color}, an integer between 0 and
	 * {@link StudentContainer#MAX_STUDENTS_PER_COLOR}, or -1 if {@code color} is {@code null}.
	 * @param color the color whose remaining capacity is returned.
	 * @return the container's remaining capacity for the specified {@link Color}, an integer between 0 and
	 * {@link StudentContainer#MAX_STUDENTS_PER_COLOR}, or -1 if {@code color} is {@code null}.
	 * @see StudentContainer#remainingCapacity()
	 */
	protected int remainingCapacity(Color color) {
		if (color == null)
			return -1;
		return Math.min(remainingCapacity(), MAX_STUDENTS_PER_COLOR - getQuantity(color));
	}

	/**
	 * Fills the container with {@link StudentContainer#MAX_STUDENTS_PER_COLOR} (26) students of each color. This method
	 * is only to be called while constructing a {@link Bag} object.
	 */
	protected void fill() {
		for (Color color : Color.values())
			students.put(color, MAX_STUDENTS_PER_COLOR);
	}

	/**
	 * A method to state if the object has no students.
	 * @return true if and only if the container is empty
	 */
	protected boolean empty() {
		for (Color color : Color.values())
			if (students.get(color) != null && students.get(color) > 0)
				return false;

		return true;
	}
}
