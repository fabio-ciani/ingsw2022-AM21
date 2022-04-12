package it.polimi.ingsw.eriantys.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.eriantys.model.characters.*;
import it.polimi.ingsw.eriantys.model.exceptions.*;
import it.polimi.ingsw.eriantys.model.influence.CommonInfluence;
import it.polimi.ingsw.eriantys.model.influence.InfluenceCalculator;

import java.util.*;

public class GameManager {
	private final Board board;
	private final PlayerList players;
	private Player currPlayer;
	private final ProfessorOwnership professors;
	private InfluenceCalculator calc;
	private final CharacterCard[] characters;
	protected final int CLOUD_SIZE;
	protected final int CLOUD_NUMBER;
	protected final int ENTRANCE_SIZE;
	protected final int TOWER_NUMBER;

	/**
	 * Constant used as a possible destination when the player moves students at the beginning of the Action Phase.
	 */
	public static final String DINING_ROOM = "dining room";

	// TODO: Will the constants be managed with a GameConfig object or by declaring them as attributes of GameManager?

	/**
	 * Constructs a {@code GameManager} that fits the number of players and the selected game mode.
	 * @param nicknames the nicknames of the players.
	 * @param expertMode {@code true} if and only if the instantiated game is to be played in expert mode.
	 */
	public GameManager(List<String> nicknames, boolean expertMode) {
		int numPlayers = nicknames.size();
		CLOUD_NUMBER = numPlayers;

		if (numPlayers == 3) {
			CLOUD_SIZE = 4;
			ENTRANCE_SIZE = 9;
			TOWER_NUMBER = 6;
		} else {
			CLOUD_SIZE = 3;
			ENTRANCE_SIZE = 7;
			TOWER_NUMBER = 8;
		}

		board = new Board(CLOUD_NUMBER, CLOUD_SIZE);
		players = new PlayerList(nicknames);
		professors = new ProfessorOwnership(this::currentPlayer);
		calc = new CommonInfluence();

		if (expertMode) {
			characters = new CharacterCard[3];
			initCharacterCards();
		} else
			characters = null;
	}

	public String getCurrPlayer() {
		return currPlayer.getNickname();
	}

	/**
	 * Prepares the game by setting up the board and, if the game is in expert mode, the selected character cards.
	 * @throws InvalidArgumentException if an error occurs while setting up the board or the character cards.
	 * @throws NoMovementException if an error occurs while setting up the character cards.
	 */
	public void setupBoard() throws InvalidArgumentException, NoMovementException {
		board.setup();

		for (CharacterCard character : characters) {
			character.setupEffect();
		}
	}

	public void setupPlayer(String nickname, TowerColor towerColor, Wizard wizard) {

	}

	/**
	 * Prepares the board for a new round to be played by refilling the cloud tiles.
	 * @throws InvalidArgumentException if an error occurs while refilling the cloud tiles.
	 * @throws NoMovementException if an error occurs while refilling the cloud tiles.
	 */
	public void setupRound() throws InvalidArgumentException, NoMovementException {
		board.refillClouds();
	}

	public void handleAssistantCards(Map<Player, AssistantCard> playedCards) {
		Player min = null;
		for (Player p : playedCards.keySet())
			if (min == null)
			min = p;
			else if (playedCards.get(p).value() < playedCards.get(min).value())
			min = p;
	
		players.setFirst(min);
	}

	/**
	 * A controller dedicated getter for a {@link List} containing the turn order of the current round.
	 * @return the reference to a {@link List} containing the nicknames of the players and stating the turn order
	 */
	public List<String> getTurnOrder() {
		List<Player> playerOrder = players.getTurnOrder();

		return playerOrder
			.stream()
			.map(Player::getNickname)
			.toList();
	}

	// TODO after moving students to the dining room (line 132) SchoolBoard.checkForCoins(student) should be called
	// TODO professors.update(student) should also be called
	// TODO: 05/04/2022 DAVIDE - Change (doc and implementation) when moving constants to the Config class
	/**
	 * Receives a {@link  List} of pairs,
	 * each containing the {@link Color} of the student that the player wants to move and the corresponding destination.
	 * The destination can be an island (ID of the {@link IslandGroup}) or the dining room of a player ({@code DINING_ROOM} constant).
	 *
	 * @param nickname Nickname of the player moving the students.
	 * @param movedStudents List of pairs [color, destination] representing which students to move and where.
	 */
	public void handleMovedStudents(String nickname, List<Pair<String, String>> movedStudents)
			throws NoMovementException, IslandNotFoundException {
		Player player = players.get(nickname);
		StudentContainer entrance = player.getEntrance();
		StudentContainer diningRoom = player.getDiningRoom();
		for (Pair<String, String> colorDest : movedStudents) {
			Color student = Color.valueOf(colorDest.value0());
			try {
				if (colorDest.value1().equals(DINING_ROOM)) {
					entrance.moveTo(diningRoom, student);
				} else {
					IslandGroup island = board.getIsland(colorDest.value1());
					entrance.moveTo(island, student);
				}
			} catch (InvalidArgumentException | NoMovementException e) {
				throw new NoMovementException(e.getMessage() + " Trying to move " + colorDest.value0() + " student to " + colorDest.value1(), e);
			}
		}
	}

	/**
	 * Moves the Mother Nature pawn to the specified destination island, then resolves that island.
	 * @param islandDestination the destination {@link IslandGroup}.
	 * @throws IslandNotFoundException if no island matching the specified id can be found.
	 * @throws InvalidArgumentException if an error occurs while resolving the destination island.
	 */
	public void handleMotherNatureMovement(String islandDestination)
			throws IslandNotFoundException, InvalidArgumentException {
		IslandGroup destination = tryGetIsland(islandDestination);

		if (destination == null)
			throw new IslandNotFoundException("Requested id: " + islandDestination + ".");

		boolean movementSuccessful = board.moveMotherNature(destination);
		if (movementSuccessful) {
			boolean controllerChanged = resolve(destination);
			if (controllerChanged)
				board.unifyIslands(destination);
		}
	}

	/**
	 * Sets the specified {@link IslandGroup}'s controller to the player with the most influence on the island, returning
	 * {@code true} if and only if the island's controller has changed as a result of this method.
	 * @param island the island whose controller is set.
	 * @return {@code true} if and only if the island's controller has changed as a result of this method.
	 * @throws InvalidArgumentException if an error occurs while calculating a player's influence.
	 */
	public boolean resolve(IslandGroup island) throws InvalidArgumentException {
		if (board.noEntryEnforced(island))
			return false;

		List<Player> players = this.players.getTurnOrder();
		Player maxInfluencePlayer = island.getController();
		int maxInfluence = calc.calculate(maxInfluencePlayer, island, professors.getProfessors(maxInfluencePlayer));

		for (Player player : players) {
			int influence = calc.calculate(player, island, professors.getProfessors(player));
			if (influence > maxInfluence) {
				maxInfluence = influence;
				maxInfluencePlayer = player;
			}
		}

		boolean res = !island.getController().equals(maxInfluencePlayer);
		island.setController(maxInfluencePlayer);
		return res;
	}

	/**
	 * Transfers all the students in the specified cloud tile to the specified player's entrance.
	 * @param nickname the nickname of the player whose entrance will be refilled.
	 * @param cloudIndex the index of the cloud tile to be emptied.
	 * @throws InvalidArgumentException if there is no such player with the specified nickname, or if the requested cloud
	 * tile index is out of bounds.
	 * @throws NoMovementException if an error occurs while transferring the students.
	 */
	public void handleSelectedCloud(String nickname, int cloudIndex)
			throws InvalidArgumentException, NoMovementException {
		Player recipient = players.get(nickname);
		board.drawStudents(cloudIndex, recipient);
	}

	public void changeInfluenceState(InfluenceCalculator calculator) throws InvalidArgumentException {
		if (calculator == null)
			throw new InvalidArgumentException("Parameter should not be null.");
		calc = calculator;
	}

	public void handleCharacterCard(int index, JsonObject params) throws
			ItemNotAvailableException,
			NoMovementException,
			IslandNotFoundException,
			InvalidArgumentException,
			DuplicateNoEntryTileException {
		List<Color> sourceColors = null, destinationColors = null;
		Color targetColor = null;
		IslandGroup targetIsland = null;

		if (params.has("sourceColors")) {
			sourceColors = new ArrayList<>();
			for (JsonElement elm : params.getAsJsonArray("sourceColors")) {
				sourceColors.add(Color.valueOf(elm.getAsString()));
			}
		}

		if (params.has("destinationColors")) {
			destinationColors = new ArrayList<>();
			for (JsonElement elm : params.getAsJsonArray("destinationColors")) {
				destinationColors.add(Color.valueOf(elm.getAsString()));
			}
		}

		if (params.has("targetColor")) {
			targetColor = Color.valueOf(params.get("targetColor").getAsString());
		}

		if (params.has("targetIsland")) {
			targetIsland = tryGetIsland(params.get("targetIsland").getAsString());
		}

		characters[index].applyEffect(sourceColors, destinationColors, targetColor, targetIsland);
	}

	public boolean gameOver() {
		// TODO: 05/04/2022 DAVIDE 
		return false;
	}

	/**
	 * Returns the game's current {@link ProfessorOwnership}.
	 * @return the game's current {@link ProfessorOwnership}.
	 */
	ProfessorOwnership getOwnerships() {
		return professors;
	}

	private void initCharacterCards() {
		List<Integer> indexes = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
		Collections.shuffle(indexes);
		int first = indexes.remove(0);
		int second = indexes.remove(0);
		int third = indexes.remove(0);

		characters[0] = getCharacter(first);
		characters[1] = getCharacter(second);
		characters[2] = getCharacter(third);
	}

	private CharacterCard getCharacter(int index) {
		return switch (index) {
			case 1 -> new Centaur(this);
			case 2 -> new Farmer(professors);
			case 3 -> new Herald(this);
			case 4 -> new HerbGranny(board);
			case 5 -> new Jester(board.getBag(), this::currentPlayer);
			case 6 -> new Knight(this, this::currentPlayer);
			case 7 -> new MagicPostman(this::currentPlayer);
			case 8 -> new Minstrel(this::currentPlayer);
			case 9 -> new Monk(board.getBag());
			case 10 -> new MushroomGuy(this);
			case 11 -> new SpoiledPrincess(board.getBag(), this::currentPlayer);
			case 12 -> new Thief(players.getTurnOrder(), board.getBag());
			default -> null;
		};
	}

	private Player currentPlayer() {
		return currPlayer;
	}

	private IslandGroup tryGetIsland(String islandId) {
		try {
			return board.getIsland(islandId);
		} catch (IslandNotFoundException e) {
			return null;
		}
	}
}