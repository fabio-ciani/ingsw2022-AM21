package it.polimi.ingsw.eriantys.controller.phases;

import it.polimi.ingsw.eriantys.controller.Game;
import it.polimi.ingsw.eriantys.messages.GameMessage;
import it.polimi.ingsw.eriantys.messages.client.GameSetupSelection;
import it.polimi.ingsw.eriantys.messages.server.Accepted;
import it.polimi.ingsw.eriantys.messages.server.Refused;
import it.polimi.ingsw.eriantys.model.TowerColor;
import it.polimi.ingsw.eriantys.model.Wizard;
import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;
import it.polimi.ingsw.eriantys.server.ClientConnection;
import it.polimi.ingsw.eriantys.server.exceptions.NoConnectionException;

import java.util.HashMap;
import java.util.Map;

public class GameSetupHandler implements MessageHandler {
	private final Game game;
	private final Map<String, String> towerColors;
	private final Map<String, String> wizards;

	public GameSetupHandler(Game game) {
		this.game = game;
		this.towerColors = new HashMap<>();
		this.wizards = new HashMap<>();
	}

	@Override
	public void handle(GameMessage m) throws NoConnectionException {
		if (m instanceof GameSetupSelection gameSetupSelection)
			process(gameSetupSelection);
		else
			game.refuseRequest(m, "Unexpected message");
	}

	private void process(GameSetupSelection message) throws NoConnectionException {
		String sender = message.getSender();
		String towerColor = message.getTowerColor();
		String wizard = message.getWizard();

		if (towerColors.containsKey(sender) || wizards.containsKey(sender))
			game.refuseRequest(message, "Already completed game setup");
		else if (towerColors.containsValue(towerColor))
			game.refuseRequest(message, "Unavailable tower color: " + towerColor);
		else if (wizards.containsValue(wizard))
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
			System.out.println("Confirmed: tower color " + towerColor + ", wizard " + wizard);
			game.acceptRequest(message);
			game.nextPlayer();
			checkStateTransition();
		}
	}

	private void checkStateTransition() {
		int numPlayers = game.getInfo().getLobbySize();
		if (towerColors.keySet().size() == numPlayers || wizards.keySet().size() == numPlayers)
			game.start();
	}
}
