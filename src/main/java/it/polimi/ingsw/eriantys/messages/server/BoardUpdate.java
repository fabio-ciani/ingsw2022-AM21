package it.polimi.ingsw.eriantys.messages.server;

import it.polimi.ingsw.eriantys.model.BoardStatus;
import it.polimi.ingsw.eriantys.model.GameManager;

import java.util.function.Supplier;

public class BoardUpdate extends UserActionUpdate {
	private final BoardStatus status;

	public BoardUpdate(GameManager gm) {
		super();
		this.status = new BoardStatus(gm);
	}
}
