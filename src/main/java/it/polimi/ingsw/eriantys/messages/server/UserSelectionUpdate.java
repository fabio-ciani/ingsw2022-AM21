package it.polimi.ingsw.eriantys.messages.server;

import java.util.List;
import java.util.Map;

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

	public List<String> getAvailableTowerColors() {
		return availableTowerColors;
	}

	public List<String> getAvailableWizards() {
		return availableWizards;
	}

	public Map<String, String> getTowerColors() {
		return towerColors;
	}

	public Map<String, String> getWizards() {
		return wizards;
	}
}
