package it.polimi.ingsw.eriantys.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A {@link GameManager} helper class to handle the players of the game,
 * in particular for the variability of the turn order.
 */
public class PlayerList {
	private final Player[] players;
	private int firstInRound;

	public PlayerList(List<String> nicknames, int entranceSize, int towerNumber) {
		players = new Player[nicknames.size()];
		for (int i = 0; i < nicknames.size(); i++)
			players[i] = new Player(nicknames.get(i), entranceSize, towerNumber);
	}

	/**
	 * A setter for the {@link Player} who is entitled to play as first in the current round.
	 * @param target the {@link Player} which will play as first
	 */
	public void setFirst(Player target) {
		int index = new ArrayList<>(Arrays.asList(players)).indexOf(target);

		if (index != -1)
			firstInRound = index;
	}

	/**
	 * A getter for a {@link List} containing the turn order referred to the current round.
	 * @return the reference to a {@link List} stating the turn order
	 */
	public List<Player> getTurnOrder() {
		List<Player> temp = new ArrayList<>(List.of(players));
		Collections.rotate(temp, -firstInRound);
		return temp;
	}

	/**
	 * A getter for the {@link Player} object associated to a nickname.
	 * @param nickname the identificator of the {@link Player}
	 * @return the {@link Player} corresponding to the given nickname if it exists, {@code null} otherwise
	 */
	public Player get(String nickname) {
		Player temp = new Player(nickname, 0, 0);
		int index = new ArrayList<>(Arrays.asList(players)).indexOf(temp);

		if (index != -1)
			return players[index];
		return null;
	}
}