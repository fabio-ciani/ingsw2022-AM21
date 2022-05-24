package it.polimi.ingsw.eriantys.model;

import it.polimi.ingsw.eriantys.messages.server.BoardUpdate;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A class which contains the representation of a game and its objects,
 * used as a payload for the {@link BoardUpdate} message.
 */
public class BoardStatus implements Serializable {
	// Note: transient modifier neutralizes final keyword.
	private transient final GameManager gm;
	private final PlayersInfo playersInfo;
	private final IslandsInfo islandsInfo;
	private final Map<String, Map<String, Integer>> cloudTiles;
	private final Map<String, String> professors;
	private final CharacterCardsInfo charactersInfo;

	public BoardStatus(GameManager gm) {
		this.gm = gm;
		this.playersInfo = new PlayersInfo();
		this.islandsInfo = new IslandsInfo();
		this.cloudTiles = gm.cloudTilesRepresentation();
		this.professors = gm.professorsRepresentation();
		this.charactersInfo = new CharacterCardsInfo();
	}

	/**
	 * An inner class which holds player-related information.
	 */
	private class PlayersInfo implements Serializable {
		private final List<String> players;
		private final Map<String, Map<String, Integer>> playerEntrances, playerDiningRooms;
		private final Map<String, String> playerTowerColors;
		private final Map<String, Integer> playerTowers;
		private final Map<String, Integer> playerCoins;

		private PlayersInfo() {
			this.players = gm.getTurnOrder();

			this.playerEntrances = new LinkedHashMap<>();
			this.playerDiningRooms = new LinkedHashMap<>();
			this.playerTowerColors = new LinkedHashMap<>();
			this.playerTowers = new LinkedHashMap<>();
			this.playerCoins = new LinkedHashMap<>();

			for (String p : players) {
				playerEntrances.put(p, gm.entranceRepresentation(p));
				playerDiningRooms.put(p, gm.diningRoomRepresentation(p));
				playerTowerColors.put(p, gm.towerColorRepresentation(p));
				playerTowers.put(p, gm.towersRepresentation(p));
				playerCoins.put(p, gm.coinsRepresentation(p));
			}
		}
	}

	/**
	 * An inner class which holds island-related information.
	 */
	private class IslandsInfo implements Serializable {
		private final List<String> islands;
		private final Map<String, Integer> islandSizes;
		private final Map<String, Map<String, Integer>> islandStudents;
		private final Map<String, String> islandControllers;
		private final String motherNatureIsland;
		private final Map<String, Integer> islandNoEntryTiles;

		private IslandsInfo() {
			this.islands = gm.islandsRepresentation();

			this.islandSizes = new LinkedHashMap<>();
			this.islandStudents = new LinkedHashMap<>();
			this.islandControllers = new LinkedHashMap<>();

			this.motherNatureIsland = gm.motherNatureIslandRepresentation();

			this.islandNoEntryTiles = new LinkedHashMap<>();

			for (String isle : islands) {
				this.islandSizes.put(isle, gm.islandSizeRepresentation(isle));
				this.islandStudents.put(isle, gm.islandStudentsRepresentation(isle));
				this.islandControllers.put(isle, gm.islandControllerRepresentation(isle));
				this.islandNoEntryTiles.put(isle, gm.islandNoEntryTilesRepresentation(isle));
			}
		}
	}

	/**
	 * An inner class which holds cards-related information.
	 */
	private class CharacterCardsInfo implements Serializable {
		private final List<String> characterCards;
		private final Map<String, Integer> characterCardsCost;
		private final Map<String, Map<String, Integer>> characterCardsStudents;
		private final Map<String, Integer> characterCardsNoEntryTiles;

		private CharacterCardsInfo() {
			this.characterCards = gm.charactersRepresentation();

			this.characterCardsCost = new LinkedHashMap<>();
			this.characterCardsStudents = new LinkedHashMap<>();
			this.characterCardsNoEntryTiles = new LinkedHashMap<>();

			for (String c : characterCards) {
				characterCardsCost.put(c, gm.characterCostRepresentation(c));
				characterCardsStudents.put(c, gm.characterStudentsRepresentation(c));
				characterCardsNoEntryTiles.put(c, gm.characterNoEntryTilesRepresentation(c));
			}
		}
	}

	public List<String> getCharacterCards() {
		return charactersInfo.characterCards;
	}

	public Map<String, Integer> getCharacterCardsCost() {
		return charactersInfo.characterCardsCost;
	}

	public Map<String, Map<String, Integer>> getCharacterCardsStudents() {
		return charactersInfo.characterCardsStudents;
	}

	public Map<String, Integer> getCharacterCardsNoEntryTiles() {
		return charactersInfo.characterCardsNoEntryTiles;
	}

	public Map<String, Map<String, Integer>> getCloudTiles() {
		return cloudTiles;
	}

	public Map<String, String> getProfessors() {
		return professors;
	}

	public List<String> getIslands() {
		return islandsInfo.islands;
	}

	public Map<String, Integer> getIslandSizes() {
		return islandsInfo.islandSizes;
	}

	public Map<String, Map<String, Integer>> getIslandStudents() {
		return islandsInfo.islandStudents;
	}

	public Map<String, String> getIslandControllers() {
		return islandsInfo.islandControllers;
	}

	public String getMotherNatureIsland() {
		return islandsInfo.motherNatureIsland;
	}

	public Map<String, Integer> getIslandNoEntryTiles() {
		return islandsInfo.islandNoEntryTiles;
	}

	public List<String> getPlayers() {
		return playersInfo.players;
	}

	public Map<String, Map<String, Integer>> getPlayerEntrances() {
		return playersInfo.playerEntrances;
	}

	public Map<String, Map<String, Integer>> getPlayerDiningRooms() {
		return playersInfo.playerDiningRooms;
	}

	public Map<String, Integer> getPlayerTowers() {
		return playersInfo.playerTowers;
	}

	public Map<String, Integer> getPlayerCoins() {
		return playersInfo.playerCoins;
	}
}
