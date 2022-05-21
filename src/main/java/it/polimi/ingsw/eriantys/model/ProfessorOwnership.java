package it.polimi.ingsw.eriantys.model;

import it.polimi.ingsw.eriantys.model.characters.Farmer;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This class represents the association between each {@link Color}'s professor and its respective owner.
 * It exposes a method to get all the professors owned by a {@link Player},
 * two methods to activate and deactivate the effect of the {@link Farmer} character card,
 * and a method to update the professors owners after a movement of students.
 */
public class ProfessorOwnership {
	private final Map<Color, Player> ownerships;
	private Comparator<Integer> comparator;
	private final Supplier<Player> playerSupplier;

	public ProfessorOwnership(Supplier<Player> playerSupplier) {
		this.playerSupplier = playerSupplier;
		this.comparator = Integer::compareTo;
		this.ownerships = new HashMap<>();
		for (Color color : Color.values())
			this.ownerships.put(color, null);
	}

	/**
	 * Returns the {@link Color} of the professors owned by {@code player}.
	 * @param player the {@link Player} whose professors' colors are returned
	 * @return the respective {@link Color} of each professor owned by {@code player}
	 */
	public Set<Color> getProfessors(Player player) {
		if (player == null)
			return new HashSet<>();
		return ownerships.keySet().stream()
					.filter(c -> ownerships.get(c) != null && ownerships.get(c).equals(player)).collect(Collectors.toSet());
	}

	/**
	 * Activates the {@link Farmer} character card's effect.
	 */
	public void activateEffect() {
		//noinspection ComparatorMethodParameterNotUsed
		this.comparator = (n1, n2) -> (n1 >= n2) ? 1 : -1;
	}

	/**
	 * Deactivates the {@link Farmer} character card's effect.
	 */
	public void deactivateEffect() {
		this.comparator = Integer::compareTo;
	}

	/**
	 * Updates the owner for each professor whose respective {@link Color} is contained in {@code target}.
	 * @param target the {@link Set} of {@link Color}s whose respective professors' owners are updated
	 */
	public void update(Set<Color> target) {
		Player currentPlayer = playerSupplier.get();
		boolean changeOwner;

		if (currentPlayer == null)
			return;

		for (Color color : target) {
			Player currentOwner = ownerships.get(color);
			if (!currentPlayer.equals(currentOwner)) {
				int currAmt = currentPlayer.getDiningRoom().getQuantity(color);
				int ownerAmt = currentOwner == null ? 0 : currentOwner.getDiningRoom().getQuantity(color);

				changeOwner = currAmt > 0 && comparator.compare(currAmt, ownerAmt) > 0;
				ownerships.put(color, changeOwner ? currentPlayer : currentOwner);
			}
		}
	}

	/**
	 * A helper-getter method to fulfill the {@link BoardStatus} creation process.
	 * @param c the target {@link Color}
	 * @return a representation for the {@link Player} owning the professor of the specified {@link Color}
	 */
	public Player getOwnership(Color c) {
		return ownerships.get(c);
	}
}
