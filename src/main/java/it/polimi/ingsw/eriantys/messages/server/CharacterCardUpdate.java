package it.polimi.ingsw.eriantys.messages.server;

public class CharacterCardUpdate extends UserActionUpdate {
	private final int card;

	public CharacterCardUpdate(int card) {
		super();
		this.card = card;
	}

	public int getCard() {
		return card;
	}
}
