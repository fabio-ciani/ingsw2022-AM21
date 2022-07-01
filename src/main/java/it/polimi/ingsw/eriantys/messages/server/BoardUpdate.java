package it.polimi.ingsw.eriantys.messages.server;

import it.polimi.ingsw.eriantys.controller.phases.PhaseName;
import it.polimi.ingsw.eriantys.model.Board;
import it.polimi.ingsw.eriantys.model.BoardStatus;
import it.polimi.ingsw.eriantys.model.GameManager;

/**
 * A message of type {@link UserActionUpdate} sent by the server in order to
 * enclose the status of the {@link Board} of the game following a player action.
 * @see BoardStatus
 */
public class BoardUpdate extends UserActionUpdate {
	private final BoardStatus status;
	private final PhaseName phase;

	public BoardUpdate(GameManager gm) {
		super();
		this.status = new BoardStatus(gm);
		this.phase = null;
	}

	public BoardUpdate(GameManager gm, PhaseName phase) {
		super();
		this.status = new BoardStatus(gm);
		this.phase = phase;
	}

	/**
	 * A getter for the status of the board.
	 * @return the internal representation of the status
	 */
	public BoardStatus getStatus() {
		return status;
	}

	/**
	 * A getter for the phase name which prompts an information message to the UI.
	 * @return the phase name, or {@code null} if no message should be shown following the receiving of {@code this}
	 */
	public PhaseName getPhase() {
		return phase;
	}
}
