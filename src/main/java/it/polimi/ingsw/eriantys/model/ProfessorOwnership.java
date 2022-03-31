package it.polimi.ingsw.eriantys.model;

import it.polimi.ingsw.eriantys.model.characters.Farmer;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This class represents the association between each {@link Color}'s professor and its respective owner. It exposes a
 * method to get all the professors owned by a {@link Player}, two methods to activate and deactivate the effect of the
 * {@link Farmer} character card, and a method to update the professors owners after a movement of students.
 */
public class ProfessorOwnership {
	private final Map<Color, Player> ownerships;
	private Comparator<Integer> comparator;
	private final Supplier<Player> playerSupplier;

	public ProfessorOwnership(Supplier<Player> playerSupplier) {
		this.playerSupplier = playerSupplier;
		ownerships = new HashMap<>();
		for (Color color : Color.values())
			ownerships.put(color, null);
	}

	/**
	 * Returns the respective {@link Color} of each professor owned by {@code player}.
	 * @param player the {@link Player} whose professors' colors are returned.
	 * @return the respective {@link Color} of each professor owned by {@code player}.
	 */
	public Set<Color> getProfessors(Player player) {
		return ownerships.keySet().stream().filter(c -> ownerships.get(c) != null).collect(Collectors.toSet());
	}

	/**
	 * Activates the {@link Farmer} character card's effect.
	 */
	public void activateEffect() {
		this.comparator = (n1, n2) -> (n1 >= n2 ? 1 : -1);
	}

	/**
	 * Deactivates the {@link Farmer} character card's effect.
	 */
	public void deactivateEffect() {
		this.comparator = Integer::compareTo;
	}

	/**
	 * Updates the owner for each professor whose respective {@link Color} is contained in {@code target}.
	 * @param target the {@link Set} of {@link Color}s whose respective professors' owners are updated.
	 */
	public void update(Set<Color> target) {
		Player currentPlayer = playerSupplier.get();
		Player res;

		for (Color color : target) {
			Player currentOwner = ownerships.get(color);
			if (!currentPlayer.equals(currentOwner)) {
				int currAmt = currentPlayer.getDiningRoom().getQuantity(color);
				int ownerAmt = currentOwner.getDiningRoom().getQuantity(color);

				res = comparator.compare(currAmt, ownerAmt) > 0 ? currentPlayer : currentOwner;
				ownerships.put(color, res);
			}
		}
	}
}
