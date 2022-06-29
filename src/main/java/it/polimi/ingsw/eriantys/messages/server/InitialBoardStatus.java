package it.polimi.ingsw.eriantys.messages.server;

import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.model.Board;
import it.polimi.ingsw.eriantys.model.BoardStatus;
import it.polimi.ingsw.eriantys.model.GameManager;
import it.polimi.ingsw.eriantys.server.Server;

/**
 * A {@link Message} sent by the server in order to enclose the status of the {@link Board} at the start of the game.
 * @see BoardStatus
 */
public class InitialBoardStatus extends Message {
	private final BoardStatus status;

	public InitialBoardStatus(GameManager gm) {
		super(Server.name);
		this.status = new BoardStatus(gm);
	}

	/**
	 * A getter for the status of the board.
	 * @return the internal representation of the status
	 */
	public BoardStatus getStatus() {
		return status;
	}
}
