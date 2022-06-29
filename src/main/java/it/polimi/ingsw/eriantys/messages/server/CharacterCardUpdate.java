package it.polimi.ingsw.eriantys.messages.server;

import it.polimi.ingsw.eriantys.model.characters.CharacterCard;

/**
 * A message of type {@link UserActionUpdate} sent by the server in order to
 * notify the client of the activation of a {@link CharacterCard} effect.
 */
public class CharacterCardUpdate extends UserActionUpdate {
	private final int card;

	public CharacterCardUpdate(int card) {
		super();
		this.card = card;
	}

	/**
	 * A getter for the target {@link CharacterCard} literal.
	 * @return the played card
	 */
	public int getCard() {
		return card;
	}
}
