package it.polimi.ingsw.eriantys.controller.phases;

import it.polimi.ingsw.eriantys.controller.Game;
import it.polimi.ingsw.eriantys.messages.GameMessage;
import it.polimi.ingsw.eriantys.messages.client.GameSetupSelection;
import it.polimi.ingsw.eriantys.messages.server.UserSelectionUpdate;
import it.polimi.ingsw.eriantys.model.TowerColor;
import it.polimi.ingsw.eriantys.model.Wizard;
import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;
import it.polimi.ingsw.eriantys.server.HelpContent;
import it.polimi.ingsw.eriantys.server.exceptions.NoConnectionException;

import java.util.*;

public class GameSetupHandler implements MessageHandler {
	private final Game g;
	private final Map<String, String> towerColors;
	private final Map<String, String> wizards;
	private final List<String> availableTowerColors;
	private final List<String> availableWizards;

	public GameSetupHandler(Game g) {
		this.g = g;
		this.towerColors = new HashMap<>();
		this.wizards = new HashMap<>();
		this.availableTowerColors = new ArrayList<>(TowerColor.stringLiterals());
		this.availableWizards = new ArrayList<>(Wizard.stringLiterals());

		try {
			this.g.sendUpdate(new UserSelectionUpdate(availableTowerColors, availableWizards));
		} catch (NoConnectionException e) {
			// TODO handle exception
			throw new RuntimeException(e);
		}
	}

	@Override
	public void handle(GameMessage m) throws NoConnectionException {
		if (m instanceof GameSetupSelection gameSetupSelection)
			process(gameSetupSelection);
		else
			g.refuseRequest(m, "Unexpected message");
	}

	@Override
	public String getHelp() {
		return HelpContent.GAME_SETUP.getContent();
	}

	private void process(GameSetupSelection message) throws NoConnectionException {
		String sender = message.getSender();
		String towerColor = message.getTowerColor();
		String wizard = message.getWizard();

		if (towerColors.containsKey(sender) || wizards.containsKey(sender))
			g.refuseRequest(message, "Already completed game setup");
		else if (!availableTowerColors.contains(towerColor))
			g.refuseRequest(message, "Unavailable tower color: " + towerColor);
		else if (!availableWizards.contains(wizard))
			g.refuseRequest(message, "Unavailable wizard: " + wizard);
		else {
			try {
				g.setupPlayer(sender, towerColor, wizard);
			} catch (InvalidArgumentException e) {
				g.refuseRequest(message, e.getMessage());
				return;
			}

			towerColors.put(sender, towerColor);
			wizards.put(sender, wizard);
			availableTowerColors.remove(towerColor);
			availableWizards.remove(wizard);
			System.out.println("Confirmed: tower color " + towerColor + ", wizard " + wizard);
			g.acceptRequest(message);

			g.nextPlayer();
			checkStateTransition();
		}
	}

	private void checkStateTransition() throws NoConnectionException {
		int numPlayers = g.getInfo().getLobbySize();
		if (towerColors.keySet().size() == numPlayers || wizards.keySet().size() == numPlayers)
			g.start();
		else
			g.sendUpdate(new UserSelectionUpdate(availableTowerColors, availableWizards));
	}
}
