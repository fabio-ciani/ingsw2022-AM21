package it.polimi.ingsw.eriantys.model;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

	public Set<Color> getProfessors(Player player) {
		return ownerships.keySet().stream().filter(c -> ownerships.get(c) != null).collect(Collectors.toSet());
	}

	public void activateEffect() {
		this.comparator = (n1, n2) -> (n1 >= n2 ? 1 : -1);
	}

	public void deactivateEffect() {
		this.comparator = Integer::compareTo;
	}

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
