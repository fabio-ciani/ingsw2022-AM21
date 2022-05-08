package it.polimi.ingsw.eriantys.model;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.eriantys.model.characters.*;
import it.polimi.ingsw.eriantys.model.exceptions.*;
import it.polimi.ingsw.eriantys.model.influence.CommonInfluence;
import it.polimi.ingsw.eriantys.model.influence.InfluenceCalculator;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class GameManager {
	private final Board board;
	private final PlayerList players;
	private Player currPlayer;
	private final ProfessorOwnership professors;
	private InfluenceCalculator calc;
	private final CharacterCard[] characters;

	public final GameConstants constants;

	/**
	 * Constructs a {@code GameManager} that fits the number of players and the selected game mode.
	 * @param nicknames the nicknames of the players
	 * @param expertMode {@code true} if and only if the instantiated game is to be played in expert mode
	 */
	public GameManager(List<String> nicknames, boolean expertMode) {
		int numPlayers = nicknames.size();
		constants = loadConstants(numPlayers);

		board = new Board(constants.getCloudNumber(), constants.getCloudSize());
		players = new PlayerList(nicknames);
		professors = new ProfessorOwnership(this::currentPlayer);
		calc = new CommonInfluence();

		if (expertMode) {
			characters = new CharacterCard[3];
			initCharacterCards();
		} else
			characters = null;
	}

	/**
	 * Prepares the game by setting up the board and, if the game is in expert mode, the selected character cards.
	 * @throws InvalidArgumentException if an error occurs while setting up the board or the character cards
	 * @throws NoMovementException if an error occurs while setting up the character cards
	 */
	public void setupBoard() throws InvalidArgumentException, NoMovementException {
		board.setup();

		for (CharacterCard character : characters) {
			character.setupEffect();
		}
	}

	/**
	 * A method to complete the setup of a {@link Player} in the game.
	 * @param nickname the {@link Player}'s nickname
	 * @param towerColorLiteral the string value of the {@link TowerColor} which the {@link Player} selected
	 * @param wizardLiteral the string value of the {@link Wizard} which the {@link Player} selected
	 * @throws InvalidArgumentException if at least one of the enum literals is not a legal value
	 */
	public void setupPlayer(String nickname, String towerColorLiteral, String wizardLiteral)
			throws InvalidArgumentException {
		Player p = players.get(nickname);
		TowerColor towerColor;
		Wizard wizard;

		try {
			towerColor = TowerColor.valueOf(towerColorLiteral);
		} catch (IllegalArgumentException e) {
			throw new InvalidArgumentException("Nonexistent tower color: " + towerColorLiteral);
		}

		try {
			wizard = Wizard.valueOf(wizardLiteral);
		} catch (IllegalArgumentException e) {
			throw new InvalidArgumentException("Nonexistent wizard: " + wizardLiteral);
		}

		p.setTowerColor(towerColor);
		p.setWizard(wizard);
	}

	/**
	 * Prepares the board for a new round to be played by refilling the cloud tiles.
	 * @throws InvalidArgumentException if an error occurs while refilling the cloud tiles
	 * @throws NoMovementException if an error occurs while refilling the cloud tiles
	 */
	public void setupRound() throws InvalidArgumentException, NoMovementException {
		board.refillClouds();
	}

	public Map<String, List<String>> getAvailableAssistantCards() {
		Map<String, List<String>> res = new HashMap<>();
		for (Player player : players.getTurnOrder()) {
			res.put(
					player.getNickname(),
					player.getDeck().stream().map(AssistantCard::toString).toList()
			);
		}
		return res;
	}

	// TODO: Add a method to send to the controller a list of available cards to play for a specific player.
	/**
	 * A method to process the assistant cards chosen by the players in the current round.
	 * @param playedCards a {@link Map} which associates a {@link Player} with its played assistant card {@link String}
	 */
	public void handleAssistantCards(Map<String, String> playedCards) {
		Player minPlayer = null;
		AssistantCard minCard = null;

		for (String username : playedCards.keySet()) {
			Player p = players.get(username);
			AssistantCard playedCard = AssistantCard.valueOf(playedCards.get(username));
			p.playAssistantCard(playedCard);
			if (minPlayer == null || playedCard.value() < minCard.value()) {
				minPlayer = p;
				minCard = playedCard;
			}
		}

		players.setFirst(minPlayer);
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
	/**
	 * Receives a {@link String} corresponding to the {@link Color} of the student that the player
	 * wants to move and a {@link String} representing the destination.
	 * The destination can be an island (ID of the {@link IslandGroup})
	 * or the dining room of a player (constant in {@link #constants}).
	 * @param nickname the nickname of the {@link Player} moving the student
	 * @param studentColor the string corresponding to the name of the {@link Color}
	 * @param destination the string representing the destination
	 */
	public void handleMovedStudent(String nickname, String studentColor, String destination)
			throws NoMovementException, IslandNotFoundException {
		Player player = players.get(nickname);
		StudentContainer entrance = player.getEntrance();
		StudentContainer diningRoom = player.getDiningRoom();
		Color student = Color.valueOf(studentColor);
		try {
			if (destination.equals(constants.getDiningRoom())) {
				entrance.moveTo(diningRoom, student);
			} else {
				IslandGroup island = board.getIsland(destination);
				entrance.moveTo(island, student);
			}
			professors.update(Set.of(student));
		} catch (InvalidArgumentException | NoMovementException e) {
			throw new NoMovementException(e.getMessage() + " Trying to move " + studentColor + " student to " + destination, e);
		}
	}

	/**
	 * Moves the Mother Nature pawn to the specified destination island, then resolves that island.
	 * @param islandDestination the destination {@link IslandGroup}
	 * @throws IslandNotFoundException if no island matching the specified id can be found
	 * @throws InvalidArgumentException if an error occurs while resolving the destination island
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
	 * @param island the island whose controller is set
	 * @return {@code true} if and only if the island's controller has changed as a result of this method
	 * @throws InvalidArgumentException if an error occurs while calculating a player's influence
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
	 * @param nickname the nickname of the player whose entrance will be refilled
	 * @param cloudIndex the index of the cloud tile to be emptied
	 * @throws InvalidArgumentException if there is no such player with the specified nickname,
	 * or if the requested cloud tile index is out of bounds
	 * @throws NoMovementException if an error occurs while transferring the students
	 */
	public void handleSelectedCloud(String nickname, int cloudIndex)
			throws InvalidArgumentException, NoMovementException {
		Player recipient = players.get(nickname);
		board.drawStudents(cloudIndex, recipient);
	}

	/**
	 * A method to change the internal influence calculation definition for the {@link InfluenceCalculator} state pattern.
	 * @param calculator the new state (i.e., a concrete instance) for {@link InfluenceCalculator}
	 * @throws InvalidArgumentException if the passed parameter is {@code null}
	 */
	public void changeInfluenceState(InfluenceCalculator calculator) throws InvalidArgumentException {
		if (calculator == null)
			throw new InvalidArgumentException("Parameter should not be null.");
		calc = calculator;
	}

	public void handleCharacterCard(int index, JsonObject params) throws
			ItemNotAvailableException,
			NoMovementException,
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

	private GameConstants loadConstants(int numPlayers) {
		Gson gson = new Gson();

		InputStream constantsIn = getClass().getClassLoader().getResourceAsStream("constants.json");
		InputStream configIn = getClass().getClassLoader().getResourceAsStream("config.json");

		if (constantsIn == null || configIn == null)
			throw new NullPointerException();

		/* String constants =
				new BufferedReader(new InputStreamReader(constantsIn)).lines().collect(Collectors.joining("\n")); */
		String constants = new BufferedReader(new InputStreamReader(constantsIn)).lines()
				.collect(Collectors.joining(System.getProperty("line.separator")));
		String config =
				gson.fromJson(new InputStreamReader(configIn), JsonObject.class)
						.get(Integer.toString(numPlayers)).getAsJsonObject().toString();

		// String jsonString = constants.substring(0, constants.length() - 2) + ",\n\"gameConfig\": " + config + "}";
		String jsonString = constants
				.substring(0, constants.length() - 2) + "," + System.getProperty("line.separator") + "\"gameConfig\": " + config + "}";

		return gson.fromJson(jsonString, GameConstants.class);
	}

	private Player currentPlayer() {
		return currPlayer;
	}

	private void initCharacterCards() {
		List<Integer> indexes = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
		Collections.shuffle(indexes);

		for (int i = 0; i < characters.length; i++)
			characters[i] = getCharacter(indexes.remove(0));
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

	private IslandGroup tryGetIsland(String islandId) {
		try {
			return board.getIsland(islandId);
		} catch (IslandNotFoundException e) {
			return null;
		}
	}
}