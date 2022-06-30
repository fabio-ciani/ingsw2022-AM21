package it.polimi.ingsw.eriantys.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.eriantys.controller.phases.PhaseName;
import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.messages.Ping;
import it.polimi.ingsw.eriantys.messages.server.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

/**
 * This abstract class represents the generic user interface, which is associated with a single {@link Client}.
 */
public abstract class UserInterface implements Runnable, ClientMessageHandler {
	/**
	 * Reference to the client object to which this user interface is associated.
	 */
	protected Client client;

	/**
	 * JSON structured object containing information about character cards that should be available to the player.
	 */
	protected final JsonObject characterCardInfo;

	protected boolean running; // TODO: 10/05/2022 Set to false when "quit" command is typed, also handle client.running

	/**
	 * Reads the JSON file and saves the information about character cards.
	 *
	 * @throws IOException if the file can't be opened or read
	 */
	public UserInterface() throws IOException {
		try (InputStream in = getClass().getClassLoader().getResourceAsStream("help/characters.json")) {
			if (in == null) throw new FileNotFoundException();
			InputStreamReader reader = new InputStreamReader(in);
			Gson gson = new Gson();
			this.characterCardInfo = gson.fromJson(reader, JsonObject.class);
		}
	}

	/**
	 * Sets the reference to the client.
	 *
	 * @param client {@link Client} object to which this user interface is associated
	 */
	public void setClient(Client client) {
		this.client = client;
	}

	/**
	 * This method can be overridden if there are initialization steps that can only be processed after the UI has started.
	 */
	public void init() {}

	/**
	 * Shows an information message to the user.
	 *
	 * @param details The message to show
	 */
	public abstract void showInfo(String details);

	/**
	 * Shows an error message to the user.
	 *
	 * @param details The message to show
	 */
	public abstract void showError(String details);

	/**
	 * {@inheritDoc}
	 * Shows an error because the message was not recognized.
	 *
	 * @param message The received message
	 */
	@Override
	public void handleMessage(Message message) {
		showError("Unrecognized message:\n" + message.getClass());
	}

	/**
	 * {@inheritDoc}
	 * Shows an error with the details of the {@link Refused} message.
	 *
	 * @param message The received message
	 */
	@Override
	public void handleMessage(Refused message) {
		showError(message.getDetails());
	}

	/**
	 * {@inheritDoc}
	 * Shows an error with the {@link RefusedReconnect} message
	 * and removes reconnection settings from the client.
	 *
	 * @param message The received message
	 */
	@Override
	public void handleMessage(RefusedReconnect message) {
		showError(message.getDetails());
		client.removeReconnectSettings();
	}

	@Override
	public void handleMessage(AssistantCardUpdate message) {
		String nextPlayer= message.getNextPlayer();
		if (nextPlayer != null) {
			if (Objects.equals(client.getUsername(), nextPlayer)) {
				showInfo("It's time to play an assistant card");
			} else {
				showInfo(String.format("%s is playing an assistant card...", nextPlayer));
			}
		}
	}

	@Override
	public void handleMessage(BoardUpdate message) {
		String nextPlayer = message.getNextPlayer();
		PhaseName phase = message.getPhase();
		if (nextPlayer != null && phase != null) {
			if (Objects.equals(client.getUsername(), nextPlayer)) {
				switch (phase) {
					case MOVE_STUDENT -> showInfo("It's time to move your students");
					case MOTHER_NATURE -> showInfo("It's time to move Mother Nature");
					case SELECT_CLOUD -> showInfo("It's time to select a cloud tile");
				}
			} else if (phase == PhaseName.MOVE_STUDENT) {
				showInfo(String.format("%s is playing...", message.getNextPlayer()));
			}
		}
	}

	@Override
	public void handleMessage(UserSelectionUpdate message) {
		String nextPlayer = message.getNextPlayer();
		if (nextPlayer != null && !Objects.equals(client.getUsername(), nextPlayer)) {
			showInfo(String.format("%s is choosing...", nextPlayer));
		}
	}

	/**
	 * {@inheritDoc}
	 * Sends back a new {@link Ping} to the server.
	 *
	 * @param message The received message
	 */
	@Override
	public void handleMessage(Ping message) {
		client.write(new Ping());
	}
}
