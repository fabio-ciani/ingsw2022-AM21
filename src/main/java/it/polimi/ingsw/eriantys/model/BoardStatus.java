package it.polimi.ingsw.eriantys.model;

import it.polimi.ingsw.eriantys.controller.Game;
import it.polimi.ingsw.eriantys.messages.server.BoardUpdate;
import it.polimi.ingsw.eriantys.model.characters.CharacterCard;

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

	// PlayersInfo getters

	/**
	 * A getter for the nicknames of the users in the {@link Game}.
	 * @return the nicknames of the users, following the current turn order
	 */
	public List<String> getPlayers() {
		return playersInfo.players;
	}

	/**
	 * A getter for the references to the {@link SchoolBoard} entrance of every user in the {@link Game}.
	 * @return the {@link SchoolBoard} entrances within a data structure
	 * which maps a nickname to the actual queried representation
	 * @see GameManager#entranceRepresentation(String)
	 */
	public Map<String, Map<String, Integer>> getPlayerEntrances() {
		return playersInfo.playerEntrances;
	}

	/**
	 * A getter for the references to the {@link SchoolBoard} dining room of every user in the {@link Game}.
	 * @return the {@link SchoolBoard} dining rooms within a data structure
	 * which maps a nickname to the actual queried representation
	 * @see GameManager#diningRoomRepresentation(String)
	 */
	public Map<String, Map<String, Integer>> getPlayerDiningRooms() {
		return playersInfo.playerDiningRooms;
	}

	/**
	 * A getter for the {@link TowerColor} literals of the users in the {@link Game}.
	 * @return the {@link TowerColor} literals within a data structure
	 * which maps a nickname to the actual queried representation
	 */
	public Map<String, String> getPlayerTowerColors() {
		return playersInfo.playerTowerColors;
	}

	/**
	 * A getter for the total number of towers on the users' {@link SchoolBoard}.
	 * @return the number of towers within a data structure
	 * which maps a nickname to the actual queried representation
	 */
	public Map<String, Integer> getPlayerTowers() {
		return playersInfo.playerTowers;
	}

	/**
	 * A getter for the total number of coins owned by the users' in the {@link Game}.
	 * @return the number of coins within a data structure
	 * which maps a nickname to the actual queried representation,
	 * generating a {@code null} reference value if the game is not set on expert mode
	 */
	public Map<String, Integer> getPlayerCoins() {
		return playersInfo.playerCoins;
	}

	// IslandsInfo getters

	/**
	 * A getter for the {@link IslandGroup} identifiers.
	 * @return the identifiers of the islands on the {@link Board} of the {@link Game}
	 */
	public List<String> getIslands() {
		return islandsInfo.islands;
	}

	/**
	 * A getter for the number of islands composing an aggregate on the {@link Board} of the {@link Game}.
	 * @return the {@link IslandGroup} sizes within a data structure
	 * which maps an island identifier to the actual queried representation
	 */
	public Map<String, Integer> getIslandSizes() {
		return islandsInfo.islandSizes;
	}

	/**
	 * A getter for the students placed on an aggregate on the {@link Board} of the {@link Game}.
	 * @return the students on an {@link IslandGroup} within a data structure
	 * which maps an island identifier to the actual queried representation
	 * @see GameManager#islandStudentsRepresentation(String)
	 */
	public Map<String, Map<String, Integer>> getIslandStudents() {
		return islandsInfo.islandStudents;
	}

	/**
	 * A getter for the controller, if present, of an aggregate on the {@link Board} of the {@link Game}.
	 * @return the user nickname of the {@link Player} controlling the {@link IslandGroup} within a data structure
	 * which maps an island identifier to the actual queried representation
	 */
	public Map<String, String> getIslandControllers() {
		return islandsInfo.islandControllers;
	}

	/**
	 * A getter for the {@link IslandGroup} on which Mother Nature is currently placed.
	 * @return the identifier of the {@link IslandGroup} on which Mother Nature is placed.
	 */
	public String getMotherNatureIsland() {
		return islandsInfo.motherNatureIsland;
	}

	/**
	 * A getter for the total number of no-entry tiles placed on an aggregate on the {@link Board} of the {@link Game}.
	 * @return the number of no-entry tiles on an {@link IslandGroup} within a data structure
	 * which maps an island identifier to the actual queried representation,
	 * generating a {@code null} reference value if the game is not set on expert mode
	 */
	public Map<String, Integer> getIslandNoEntryTiles() {
		return islandsInfo.islandNoEntryTiles;
	}

	// CharacterCardsInfo getters

	/**
	 * A getter for the {@link CharacterCard}s randomly extracted for the {@link Game}.
	 * @return the card literals, or {@code null} if the {@link Game} is not set on expert mode
	 */
	public List<String> getCharacterCards() {
		return charactersInfo.characterCards;
	}

	/**
	 * A getter for the total number of coins required to play a {@link CharacterCard} of the {@link Game}.
	 * @return the cost of a {@link CharacterCard} within a data structure
	 * which maps a card identifier to the actual queried representation,
	 * or {@code null} if the game is not set on expert mode
	 */
	public Map<String, Integer> getCharacterCardsCost() {
		return charactersInfo.characterCardsCost;
	}

	/**
	 * A getter for the students placed on a {@link CharacterCard} of the {@link Game}
	 * if the card itself involves this additional information and status.
	 * @return the students placed on a {@link CharacterCard} within a data structure
	 * which maps a card identifier to the actual queried representation,
	 * or {@code null} if the game is not set on expert mode
	 * @see GameManager#characterStudentsRepresentation(String)
	 */
	public Map<String, Map<String, Integer>> getCharacterCardsStudents() {
		return charactersInfo.characterCardsStudents;
	}

	/**
	 * A getter for the total number of no-entry tiles placed on a {@link CharacterCard} of the {@link Game}
	 * if the card itself involves this additional information and status.
	 * @return the number of no-entry tiles on a {@link CharacterCard} within a data structure
	 * which maps a card identifier to the actual queried representation,
	 * or {@code null} if the game is not set on expert mode
	 */
	public Map<String, Integer> getCharacterCardsNoEntryTiles() {
		return charactersInfo.characterCardsNoEntryTiles;
	}

	// BoardStatus getters

	/**
	 * A getter for the students placed on a cloud tile on the {@link Board} of the {@link Game}.
	 * @return the students on a cloud tile within a data structure
	 * which maps a tile (numerical) identifier to the actual queried representation
	 */
	public Map<String, Map<String, Integer>> getCloudTiles() {
		return cloudTiles;
	}

	/**
	 * A getter for the professors owned by the users' in the {@link Game}.
	 * @return the professor literals within a data structure
	 * which maps a nickname to the actual queried representation
	 */
	public Map<String, String> getProfessors() {
		return professors;
	}
}
