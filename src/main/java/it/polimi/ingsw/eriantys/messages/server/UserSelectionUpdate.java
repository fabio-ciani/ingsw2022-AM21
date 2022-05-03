package it.polimi.ingsw.eriantys.messages.server;

import java.util.List;

public class UserSelectionUpdate extends UserActionUpdate {
	private final List<String> availableTowerColors;
	private final List<String> availableWizards;

	public UserSelectionUpdate(List<String> availableTowerColors, List<String> availableWizards) {
		super();
		this.availableTowerColors = availableTowerColors;
		this.availableWizards = availableWizards;
	}

	public List<String> getAvailableTowerColors() {
		return availableTowerColors;
	}

	public List<String> getAvailableWizards() {
		return availableWizards;
	}
}
