package it.polimi.ingsw.eriantys.server;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * An enumeration to enclose the {@code /help} command responses within a CLI client.
 * The displayed content may vary with respect to the situation from which a client request is sent to the server.
 */
public enum HelpContent {
	NO_GAME("help/no_game.txt"),
	NOT_STARTED("help/not_started.txt"),
	GAME_SETUP("help/game_setup.txt"),
	IN_GAME("help/in_game.txt");

	private final String file;

	HelpContent(String file) {
		this.file = file;
	}

	public String getContent() {
		String out = null;

		try (InputStream in = HelpContent.class.getClassLoader().getResourceAsStream(file)) {
			if (in == null)
				throw new RuntimeException();	// TODO: Is this necessary?
			out = new String(in.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return out;
	}
}
