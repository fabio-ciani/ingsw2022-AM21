package it.polimi.ingsw.eriantys.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.messages.Ping;
import it.polimi.ingsw.eriantys.messages.server.Refused;
import it.polimi.ingsw.eriantys.messages.server.RefusedReconnect;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class UserInterface implements Runnable, ClientMessageHandler {
	protected Client client;
	protected final JsonObject characterCardInfo;
	protected boolean running; // TODO: 10/05/2022 Set to false when "quit" command is typed, also handle client.running

	public UserInterface() throws IOException {
		try (InputStream in = getClass().getClassLoader().getResourceAsStream("help/characters.json")) {
			if (in == null) throw new FileNotFoundException();
			InputStreamReader reader = new InputStreamReader(in);
			Gson gson = new Gson();
			this.characterCardInfo = gson.fromJson(reader, JsonObject.class);
		}
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public void init() {}

	public abstract void showInfo(String details);
	public abstract void showError(String details);

	@Override
	public void handleMessage(Message message) {
		showError("Unrecognized message:\n" + message.getClass());
	}

	@Override
	public void handleMessage(Refused message) {
		showError(message.getDetails());
	}

	@Override
	public void handleMessage(RefusedReconnect message) {
		showError(message.getDetails());
		client.removeReconnectSettings();
	}

	@Override
	public void handleMessage(Ping message) {
		client.write(new Ping());
	}
}
