package it.polimi.ingsw.eriantys.messages.client;

import com.google.gson.JsonObject;
import it.polimi.ingsw.eriantys.messages.GameMessage;

public class PlayCharacterCard extends GameMessage {
	private final int characterCard;
	private final JsonObject params;

	public PlayCharacterCard(String sender, int characterCard, JsonObject params) {
		super(sender);
		this.characterCard = characterCard;
		this.params = params;
	}

	public int getCharacterCard() {
		return characterCard;
	}

	public JsonObject getParams() {
		return params;
	}
}
