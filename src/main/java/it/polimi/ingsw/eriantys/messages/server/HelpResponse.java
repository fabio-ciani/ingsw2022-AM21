package it.polimi.ingsw.eriantys.messages.server;

import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.messages.client.HelpRequest;
import it.polimi.ingsw.eriantys.server.HelpContent;
import it.polimi.ingsw.eriantys.server.Server;

/**
 * A {@link Message} sent by the server in order to fulfill the needs of a {@link HelpRequest} communication item.
 * @see HelpContent
 */
public class HelpResponse extends Message {
	private final String content;

	public HelpResponse(String content) {
		super(Server.name);
		this.content = content;
	}

	/**
	 * A getter for the result of the {@code /help} command coded for the current application phase.
	 * @return the content of the result of the command
	 */
	public String getContent() {
		return content;
	}
}
