package it.polimi.ingsw.eriantys.messages.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.eriantys.messages.GameMessage;

public class PlayCharacterCard extends GameMessage {
	private final int characterCard;
	private final String paramsJson;

	public PlayCharacterCard(String sender, int characterCard, String paramsJson) {
		super(sender);
		this.characterCard = characterCard;
		this.paramsJson = paramsJson;
	}

	public int getCharacterCard() {
		return characterCard;
	}

	public JsonObject getParams() {
		return new Gson().fromJson(paramsJson, JsonObject.class);
	}
}
