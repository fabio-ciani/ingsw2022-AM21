package it.polimi.ingsw.eriantys.messages.server;

import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.model.BoardStatus;
import it.polimi.ingsw.eriantys.model.GameManager;
import it.polimi.ingsw.eriantys.server.Server;

public class InitialBoardStatus extends Message {
	private final BoardStatus status;

	public InitialBoardStatus(GameManager gm) {
		super(Server.name);
		this.status = new BoardStatus(gm);
	}

	public BoardStatus getStatus() {
		return status;
	}
}
