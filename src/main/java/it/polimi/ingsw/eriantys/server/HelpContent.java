package it.polimi.ingsw.eriantys.server;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public enum HelpContent {
	NO_GAME("no_game.txt"),
	GAME_SETUP("game_setup.txt"),
	IN_GAME("in_game.txt"),
	UTILITY("utility.txt");

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
