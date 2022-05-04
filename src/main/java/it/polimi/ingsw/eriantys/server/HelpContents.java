package it.polimi.ingsw.eriantys.server;

public enum HelpContents {
	OUT_OF_LOBBY("Available commands:%nlobbies\tGet lobbies list%njoin\tJoin a lobby");

	HelpContents(String content) {
		this.content = content;
	}

	private final String content;

	public String getContent() {
		return content;
	}
}
