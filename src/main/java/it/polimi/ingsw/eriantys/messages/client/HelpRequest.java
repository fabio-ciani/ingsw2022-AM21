package it.polimi.ingsw.eriantys.messages.client;

import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.server.HelpContent;

/**
 * A {@link Message} sent by a client in order to request the usage screen coded for the current application phase.
 * @see HelpContent
 */
public class HelpRequest extends Message {
	public HelpRequest(String sender) {
		super(sender);
	}
}
