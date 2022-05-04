package it.polimi.ingsw.eriantys.messages.server;

import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.server.Server;

public class HelpResponse extends Message {
	private final String content;

	public HelpResponse(String content) {
		super(Server.name);
		this.content = content;
	}

	public String getContent() {
		return content;
	}
}
