package it.polimi.ingsw.eriantys.messages.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.eriantys.messages.GameMessage;
import it.polimi.ingsw.eriantys.model.characters.CharacterCard;

/**
 * A {@link GameMessage} sent by a client in order to inform the server of
 * the {@link CharacterCard} literal chosen by the user in the current action phase.
 */
public class PlayCharacterCard extends GameMessage {
	private final int characterCard;
	private final String paramsJson;

	public PlayCharacterCard(String sender, int characterCard, String paramsJson) {
		super(sender);
		this.characterCard = characterCard;
		this.paramsJson = paramsJson;
	}

	/**
	 * A getter for the {@link CharacterCard} literal requested by the user.
	 * @return the chosen {@link CharacterCard} literal
	 */
	public int getCharacterCard() {
		return characterCard;
	}

	/**
	 * A getter for the arguments which the requested {@link CharacterCard} require.
	 * @return a {@link JsonObject} containing all the information regarding the required arguments, if present,
	 * which the user will need to specify in order to play the card
	 */
	public JsonObject getParams() {
		return new Gson().fromJson(paramsJson, JsonObject.class);
	}
}
