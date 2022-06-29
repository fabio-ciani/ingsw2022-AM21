package it.polimi.ingsw.eriantys.messages.server;

import it.polimi.ingsw.eriantys.model.TowerColor;
import it.polimi.ingsw.eriantys.model.Wizard;

import java.util.List;
import java.util.Map;

/**
 * A message of type {@link UserActionUpdate} sent by the server in order to
 * notify the client of the available {@link TowerColor} and {@link Wizard} literals in the game.
 * The communication item carries, inside its structure, a mapping between usernames and selected literals.
 */
public class UserSelectionUpdate extends UserActionUpdate {
	private final List<String> availableTowerColors;
	private final List<String> availableWizards;
	private final Map<String, String> towerColors;
	private final Map<String, String> wizards;

	public UserSelectionUpdate(List<String> availableTowerColors, List<String> availableWizards,
							   Map<String, String> towerColors, Map<String, String> wizards) {
		super();
		this.availableTowerColors = availableTowerColors;
		this.availableWizards = availableWizards;
		this.towerColors = towerColors;
		this.wizards = wizards;
	}

	/**
	 * A getter for the available {@link TowerColor} literals in the game.
	 * @return the available literals
	 */
	public List<String> getAvailableTowerColors() {
		return availableTowerColors;
	}

	/**
	 * A getter for the available {@link Wizard} literals in the game.
	 * @return the available literals
	 */
	public List<String> getAvailableWizards() {
		return availableWizards;
	}

	/**
	 * A getter for the mapping between usernames and selected {@link TowerColor} literals.
	 * @return the internal representation of the mapping
	 */
	public Map<String, String> getTowerColors() {
		return towerColors;
	}

	/**
	 * A getter for the mapping between usernames and selected {@link Wizard} literals.
	 * @return the internal representation of the mapping
	 */
	public Map<String, String> getWizards() {
		return wizards;
	}
}
