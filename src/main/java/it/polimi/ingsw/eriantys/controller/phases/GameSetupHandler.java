package it.polimi.ingsw.eriantys.controller.phases;

import it.polimi.ingsw.eriantys.controller.Game;
import it.polimi.ingsw.eriantys.messages.GameMessage;
import it.polimi.ingsw.eriantys.messages.client.GameSetupSelection;
import it.polimi.ingsw.eriantys.messages.server.UserActionUpdate;
import it.polimi.ingsw.eriantys.messages.server.UserSelectionUpdate;
import it.polimi.ingsw.eriantys.model.TowerColor;
import it.polimi.ingsw.eriantys.model.Wizard;
import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;
import it.polimi.ingsw.eriantys.server.HelpContent;
import it.polimi.ingsw.eriantys.server.exceptions.NoConnectionException;

import java.util.*;

public class GameSetupHandler implements MessageHandler {
	private final Game game;
	private final Map<String, String> towerColors;
	private final Map<String, String> wizards;
	private final List<String> availableTowerColors;
	private final List<String> availableWizards;

	public GameSetupHandler(Game game) {
		this.game = game;
		this.towerColors = new HashMap<>();
		this.wizards = new HashMap<>();
		this.availableTowerColors = new ArrayList<>(TowerColor.stringLiterals());
		this.availableWizards = new ArrayList<>(Wizard.stringLiterals());

		if (this.game.getInfo().getLobbySize() != 3)
			this.availableTowerColors.remove("GREY");

		try {
			this.game.sendUpdate(new UserSelectionUpdate(availableTowerColors, availableWizards));
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
			game.refuseRequest(m, "Unexpected message");
	}

	@Override
	public String getHelp() {
		return HelpContent.GAME_SETUP.getContent();
	}

	@Override
	public void handleDisconnectedUser(String username) {
		String towerColor = availableTowerColors.get(0);
		String wizard = availableWizards.get(0);

		try {
			game.setupPlayer(username, towerColor, wizard);
		} catch (InvalidArgumentException e) {
			throw new RuntimeException(e);
		}

		towerColors.put(username, towerColor);
		wizards.put(username, wizard);
		availableTowerColors.remove(towerColor);
		availableWizards.remove(wizard);
		System.out.println("Confirmed: tower color " + towerColor + ", wizard " + wizard);
		game.nextPlayer();
	}

	@Override
	public void sendReconnectUpdate(String username) throws NoConnectionException {
		game.sendUpdate(new UserSelectionUpdate(availableTowerColors, availableWizards));
	}

	private void process(GameSetupSelection message) throws NoConnectionException {
		String sender = message.getSender();
		String towerColor = message.getTowerColor();
		String wizard = message.getWizard();

		if (towerColors.containsKey(sender) || wizards.containsKey(sender))
			game.refuseRequest(message, "Already completed game setup");
		else if (!availableTowerColors.contains(towerColor))
			game.refuseRequest(message, "Unavailable tower color: " + towerColor);
		else if (!availableWizards.contains(wizard))
			game.refuseRequest(message, "Unavailable wizard: " + wizard);
		else {
			try {
				game.setupPlayer(sender, towerColor, wizard);
			} catch (InvalidArgumentException e) {
				game.refuseRequest(message, e.getMessage());
				return;
			}

			towerColors.put(sender, towerColor);
			wizards.put(sender, wizard);
			availableTowerColors.remove(towerColor);
			availableWizards.remove(wizard);
			System.out.println("Confirmed: tower color " + towerColor + ", wizard " + wizard);
			game.acceptRequest(message);

			game.nextPlayer();
			checkStateTransition();
		}
	}

	private void checkStateTransition() throws NoConnectionException {
		int numPlayers = game.getInfo().getLobbySize();
		if (towerColors.keySet().size() == numPlayers || wizards.keySet().size() == numPlayers)
			game.start();
		else
			game.sendUpdate(new UserSelectionUpdate(availableTowerColors, availableWizards));
	}
}
