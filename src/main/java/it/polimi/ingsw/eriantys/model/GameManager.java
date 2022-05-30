package it.polimi.ingsw.eriantys.model;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.eriantys.model.characters.*;
import it.polimi.ingsw.eriantys.model.exceptions.*;
import it.polimi.ingsw.eriantys.model.influence.CommonInfluence;
import it.polimi.ingsw.eriantys.model.influence.InfluenceCalculator;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * This class represents the current state of a game, including all game objects and participating players. It exposes
 * various modifier methods, which are only meant to be called by the controller, as {@link GameManager} constitutes the
 * only access point to the model. It also exposes observer methods, which return representations of the desired game
 * objects based solely on {@code java.util} classes and interfaces, such as {@code List} and {@code Map}: this ensures
 * that the internal structure of the model is not exposed.
 */
public class GameManager {
	private final Board board;
	private final PlayerList players;
	private Player currPlayer;
	private final ProfessorOwnership professors;
	private InfluenceCalculator calc;
	private final boolean expertMode;
	private final CharacterCard[] characters;
	private boolean lastRound;

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
		players = new PlayerList(nicknames, constants.getEntranceSize(), constants.getTowerNumber());
		professors = new ProfessorOwnership(this::currentPlayer);
		calc = new CommonInfluence();
		lastRound = false;

		this.expertMode = expertMode;
		if (expertMode) {
			characters = new CharacterCard[3];
			initCharacterCards();
		} else
			characters = null;
	}

	/**
	 * Sets the current player to the player matching {@code currentPlayerNickname}.
	 * @param currentPlayerNickname the nickname of the current player
	 */
	public void setCurrentPlayer(String currentPlayerNickname) throws InvalidArgumentException {
		Player nextPlayer = players.get(currentPlayerNickname);
		if (nextPlayer == null)
			 throw new InvalidArgumentException();
		currPlayer = players.get(currentPlayerNickname);
	}

	/**
	 * Prepares the game by setting up the board and, if the game is in expert mode, the selected character cards.
	 * @throws InvalidArgumentException if an error occurs while setting up the board or the character cards
	 * @throws NoMovementException if an error occurs while setting up the character cards
	 */
	public void setupBoard() throws InvalidArgumentException, NoMovementException {
		board.setup();

		if (expertMode)
			for (CharacterCard character : characters) {
				character.setupEffect();
			}
	}

	/**
	 * Prepares the game by setting up every player's school board entrance,
	 * filling it with the appropriate number of student discs.
	 * @throws InvalidArgumentException if an error occurs during one of the movements
	 */
	public void setupEntrances() throws InvalidArgumentException {
		for (Player player : players.getTurnOrder()) {
			try {
				player.getEntrance().refillFrom(board.getBag());
			} catch (NoMovementException e) {
				throw new RuntimeException(e);
			}
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
	 * @return {@code true} if and only if the current round will be the last in the game
	 */
	public boolean setupRound() throws InvalidArgumentException, NoMovementException {
		board.refillClouds();
		return lastRound();
	}

	/**
	 * A getter for each player's remaining assistant cards.
	 * @return a {@link Map} having the nickname of every player as key set and
	 * each player's remaining assistant cards as values
	 */
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

	/**
	 * A method to process the assistant cards chosen by the players in the current round.
	 * @param playedCards a {@link Map} which associates a {@link Player} with its played assistant card {@link String}
	 * @return {@code true} if and only if the current round will be the last in the game
	 */
	public boolean handleAssistantCards(Map<String, String> playedCards) {
		Player minPlayer = null;
		AssistantCard minCard = null;

		for (String username : playedCards.keySet()) {
			Player p = players.get(username);
			AssistantCard card = AssistantCard.valueOf(playedCards.get(username));
			p.playAssistantCard(card);
			if (minPlayer == null || card.value() < minCard.value()) {
				minPlayer = p;
				minCard = card;
			}
		}

		players.setFirst(minPlayer);

		return lastRound();
	}

	/**
	 * A controller-dedicated getter for a {@link List} containing the turn order of the current round.
	 * @return the reference to a {@link List} containing the nicknames of the players and stating the turn order
	 */
	public List<String> getTurnOrder() {
		List<Player> playerOrder = players.getTurnOrder();

		return playerOrder
			.stream()
			.map(Player::getNickname)
			.toList();
	}

	/**
	 * Receives a {@link String} corresponding to the {@link Color} of the student that
	 * the player wants to move and a {@link String} representing the destination.
	 * The destination can be an island (ID of the {@link IslandGroup})
	 * or the dining room of a player (constant in {@link #constants}).
	 * @param nickname the nickname of the {@link Player} moving the student
	 * @param studentColor the string corresponding to the name of the {@link Color}
	 * @param destination the string representing the destination
	 * @throws NoMovementException if an error occurs while moving the student
	 * @throws IslandNotFoundException if the specified island cannot be found
	 * @throws InvalidArgumentException if no player matches the specified nickname or
	 * no {@link Color} matches the specified color
	 */
	public void handleMovedStudent(String nickname, String studentColor, String destination)
			throws NoMovementException, IslandNotFoundException, InvalidArgumentException {
		Player player = players.get(nickname);
		if (player == null) throw new InvalidArgumentException();

		StudentContainer entrance = player.getEntrance();
		StudentContainer diningRoom = player.getDiningRoom();
		Color student;

		try {
			student = Color.valueOf(studentColor);
		} catch (IllegalArgumentException e) {
			throw new InvalidArgumentException();
		}

		try {
			if (destination.equals(GameConstants.DINING_ROOM)) {
				entrance.moveTo(diningRoom, student);
				if (expertMode && player.checkForCoins(student))
					player.updateCoins(1);
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
	 * @throws NotEnoughMovementsException if the player performing this action does not have enough
	 * Mother Nature movements in order to complete it
	 * @return {@code true} if and only if the game ends as a result of Mother Nature's movement
	 */
	public boolean handleMotherNatureMovement(String islandDestination)
			throws IslandNotFoundException, InvalidArgumentException, NotEnoughMovementsException {
		IslandGroup destination = tryGetIsland(islandDestination);

		if (destination == null)
			throw new IslandNotFoundException("Requested id: " + islandDestination);

		int requestedMovements = board.getDistanceFromMotherNature(destination);
		int actualMovements = currentPlayer().getMotherNatureMovements();
		if (requestedMovements < 1)
			throw new IllegalArgumentException("Must move Mother Nature by at least 1 island");
		if (requestedMovements > actualMovements)
			throw new NotEnoughMovementsException("requested " + requestedMovements + ", actual " + actualMovements);

		boolean movementSuccessful = board.moveMotherNature(destination);
		if (movementSuccessful) {
			boolean controllerChanged = resolve(destination);
			if (controllerChanged)
				board.unifyIslands(destination);
		}

		return gameOver();
	}

	/**
	 * Sets the specified {@link IslandGroup}'s controller to the player with the most influence on the island.
	 * @param island the island whose controller is set
	 * @return {@code true} if and only if the island's controller has changed as a result of this method
	 * @throws InvalidArgumentException if an error occurs while calculating a player's influence
	 */
	public boolean resolve(IslandGroup island) throws InvalidArgumentException {
		if (board.noEntryEnforced(island))
			return false;

		List<Player> players = this.players.getTurnOrder();
		Player maxInfluencePlayer = island.getController();
		int maxInfluence = maxInfluencePlayer == null ? 0 : calc.calculate(maxInfluencePlayer, island, professors.getProfessors(maxInfluencePlayer));

		for (Player player : players) {
			int influence = calc.calculate(player, island, professors.getProfessors(player));
			if (influence > maxInfluence) {
				maxInfluence = influence;
				maxInfluencePlayer = player;
			}
		}

		Player oldController = island.getController();
		boolean res = !Objects.equals(oldController, maxInfluencePlayer);
		island.setController(maxInfluencePlayer);
		if (res) {
			if (oldController != null && !oldController.returnTower()) throw new RuntimeException();
			if (maxInfluencePlayer != null &&	!maxInfluencePlayer.deployTower()) throw new RuntimeException();
		}
		return res;
	}

	/**
	 * Transfers all the students in the specified cloud tile to the specified player's entrance.
	 * @param nickname the nickname of the player whose entrance will be refilled
	 * @param cloudIndex the index of the cloud tile to be emptied
	 * @throws InvalidArgumentException if there is no such player with the specified nickname,
	 * or if the requested cloud tile index is out of bounds
	 * @throws NoMovementException if the selected cloud is empty
	 */
	public void handleSelectedCloud(String nickname, int cloudIndex)
			throws InvalidArgumentException, NoMovementException {
		Player recipient = players.get(nickname);
		if (recipient == null) throw new InvalidArgumentException();
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

	/**
	 * Applies the effect of the desired character card with the specified {@code params}.
	 * @param index the index of the desired character card
	 * @param params the parameters for the application of the specified character card's effect
	 * @return {@code true} if and only if the current round will be the last in the game
	 * @throws ItemNotAvailableException if an error has occurred while removing a no-entry tile from an island
	 * @throws NoMovementException if an error has occurred while moving one or more students
	 * @throws InvalidArgumentException if an error has occurred while applying the card's effect
	 * @throws DuplicateNoEntryTileException if an error has occurred while placing a no-entry tile on an island
	 */
	public boolean handleCharacterCard(int index, JsonObject params) throws
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

		if (index < 0 || index >= characters.length)
			throw new InvalidArgumentException("Character card index out of bounds");

		CharacterCard target = characters[index];
		int originalCost = target.getCost();

		if (currPlayer.getCoins() < originalCost)
			throw new InvalidArgumentException("Not enough coins to play the selected card");

		target.applyEffect(sourceColors, destinationColors, targetColor, targetIsland);
		currPlayer.updateCoins(-originalCost);

		return lastRound();
	}

	/**
	 * A getter for the winner of the game.
	 * @return the nickname of the winner of the game, or {@code null} if no winner has been declared yet
	 */
	public String getWinner() {
		if (!lastRound) return null;

		List<Player> playersWithMostTowers = mostTowersBuilt(players.getTurnOrder());
		if (playersWithMostTowers.size() == 1)
			return playersWithMostTowers.get(0).getNickname();

		List<Player> playersWithMostProfessors = mostProfessorsOwned(playersWithMostTowers);
		if (playersWithMostProfessors.size() == 1)
			return playersWithMostProfessors.get(0).getNickname();
		else
			return GameConstants.TIE;
	}

	/**
	 * A method called by {@link BoardStatus} in order to obtain a view-oriented representation
	 * within the MVC pattern for the game.
	 * @param username the username of the target player
	 * @return a representation for the entrance of the specified player
	 * @see StudentContainer#getRepresentation()
	 */
	public Map<String, Integer> entranceRepresentation(String username) {
		Player p = players.get(username);
		StudentContainer entrance = p.getEntrance();

		return entrance.getRepresentation();
	}

	/**
	 * A method called by {@link BoardStatus} in order to obtain a view-oriented representation
	 * within the MVC pattern for the game.
	 * @param username the username of the target player
	 * @return a representation for the dining room of the specified player
	 * @see StudentContainer#getRepresentation()
	 */
	public Map<String, Integer> diningRoomRepresentation(String username) {
		Player p = players.get(username);
		StudentContainer diningRoom = p.getDiningRoom();

		return diningRoom.getRepresentation();
	}

	/**
	 * A method called by {@link BoardStatus} in order to obtain a view-oriented representation
	 * within the MVC pattern for the game.
	 * @param username the username of the target player
	 * @return a representation for the tower color of the specified player
	 */
	public String towerColorRepresentation(String username) {
		return players.get(username).getTowerColor().toString();
	}

	/**
	 * A method called by {@link BoardStatus} in order to obtain a view-oriented representation
	 * within the MVC pattern for the game.
	 * @param username the username of the target player
	 * @return a representation for the number of towers on the school board of the specified player
	 */
	public Integer towersRepresentation(String username) {
		return players.get(username).getTowerQuantity();
	}

	/**
	 * A method called by {@link BoardStatus} in order to obtain a view-oriented representation
	 * within the MVC pattern for the game.
	 * @param username the username of the target player
	 * @return a representation for the number of coins owned by the specified player,
	 * or {@code null} if the game is not set on expert mode
	 */
	public Integer coinsRepresentation(String username) {
		if (!expertMode)
			return null;
		return players.get(username).getCoins();
	}

	/**
	 * A method called by {@link BoardStatus} in order to obtain a view-oriented representation
	 * within the MVC pattern for the game.
	 * @return a representation for the islands on the game board
	 * @see Board#getIslandsRepresentation()
	 */
	public List<String> islandsRepresentation() {
		return board.getIslandsRepresentation();
	}

	/**
	 * A method called by {@link BoardStatus} in order to obtain a view-oriented representation
	 * within the MVC pattern for the game.
	 * @param isle the identifier of the target isle
	 * @return a representation for the aggregate size
	 * @see IslandGroup#getSize()
	 */
	public Integer islandSizeRepresentation(String isle) {
		Integer rep = null;
		IslandGroup island;

		try {
			island = board.getIsland(isle);
			rep = island.getSize();
		} catch (IslandNotFoundException e) {
			// TODO: handle exception (?) + check NullPointerException on rep?
			// e.printStackTrace();
		}

		return rep;
	}

	/**
	 * A method called by {@link BoardStatus} in order to obtain a view-oriented representation
	 * within the MVC pattern for the game.
	 * @param isle the identifier of the target isle
	 * @return a representation for the students on the specified aggregate
	 * @see StudentContainer#getRepresentation()
	 */
	public Map<String, Integer> islandStudentsRepresentation(String isle) {
		Map<String, Integer> rep = null;
		IslandGroup island;

		try {
			island = board.getIsland(isle);
			rep = new LinkedHashMap<>();
			rep = island.getRepresentation();
		} catch (IslandNotFoundException e) {
			// TODO: handle exception (?) + check NullPointerException on rep?
			// e.printStackTrace();
		}

		return rep;
	}

	// TODO: more tests (?) (e.g., resolve() method)
	/**
	 * A method called by {@link BoardStatus} in order to obtain a view-oriented representation
	 * within the MVC pattern for the game.
	 * @param isle the identifier of the target isle
	 * @return a representation for the username of the controller of the specified aggregate
	 */
	public String islandControllerRepresentation(String isle) {
		String rep = null;
		IslandGroup island;

		try {
			island = board.getIsland(isle);
			if (island.getController() != null)
				rep = island.getController().getNickname();
		} catch (IslandNotFoundException e) {
			// TODO: handle exception (?) + check NullPointerException on rep?
			// e.printStackTrace();
		}

		return rep;
	}

	/**
	 * A method called by {@link BoardStatus} in order to obtain a view-oriented representation
	 * within the MVC pattern for the game.
	 * @return a representation for the identifier of the aggregate on which Mother Nature is currently placed
	 */
	public String motherNatureIslandRepresentation() {
		return board.getMotherNatureIsland().getId();
	}

	/**
	 * A method called by {@link BoardStatus} in order to obtain a view-oriented representation
	 * within the MVC pattern for the game.
	 * @param isle the identifier of the target isle
	 * @return a representation for the number of no-entry tiles placed on the specified aggregate
	 * if and only if expert mode is enabled
	 * @see IslandGroup#getNoEntryTiles()
	 */
	public Integer islandNoEntryTilesRepresentation(String isle) {
		if (!expertMode)
			return null;

		Integer rep = null;
		IslandGroup island;

		try {
			island = board.getIsland(isle);
			rep = island.getNoEntryTiles();
		} catch (IslandNotFoundException e) {
			// TODO: handle exception (?) + check NullPointerException on rep?
			// e.printStackTrace();
		}

		return rep;
	}

	/**
	 * A method called by {@link BoardStatus} in order to obtain a view-oriented representation
	 * within the MVC pattern for the game.
	 * @return a representation for the cloud tiles on the game board
	 * @see Board#getCloudTiles()
	 */
	public Map<String, Map<String, Integer>> cloudTilesRepresentation() {
		return board.getCloudTiles();
	}

	/**
	 * A method called by {@link BoardStatus} in order to obtain a view-oriented representation
	 * within the MVC pattern for the game.
	 * @return a representation for the username of the owner, when existent, of the game professors
	 * @see ProfessorOwnership#getOwnership(Color)
	 */
	public Map<String, String> professorsRepresentation() {
		Map<String, String> rep = new LinkedHashMap<>();
		Player p;

		for (Color c : Color.values()) {
			p = professors.getOwnership(c);
			if (p != null)
				rep.put(c.toString(), p.getNickname());
			else
				rep.put(c.toString(), null);
		}

		return rep;
	}

	/**
	 * A method called by {@link BoardStatus} in order to obtain a view-oriented representation
	 * within the MVC pattern for the game.
	 * @return a representation for the character cards of the game if and only if expert mode is enabled
	 * @see CharacterCard#getName()
	 */
	public List<String> charactersRepresentation() {
		if (!expertMode)
			return null;
		return Arrays.stream(characters).map(CharacterCard::getName).toList();
	}

	/**
	 * A method called by {@link BoardStatus} in order to obtain a view-oriented representation
	 * within the MVC pattern for the game.
	 * @param c the target character card literal
	 * @return a representation for the cost of the specified character card if and only if expert mode is enabled
	 */
	public Integer characterCostRepresentation(String c) {
		if (!expertMode)
			return null;

		for (CharacterCard card : characters)
			if (card.getName().equals(c))
				return card.getCost();

		return null;
	}

	/**
	 * A method called by {@link BoardStatus} in order to obtain a view-oriented representation
	 * within the MVC pattern for the game.
	 * @param c the target character card literal
	 * @return a representation for the students on the specified character card if and only if expert mode is enabled
	 * @see StudentContainer#getRepresentation()
	 */
	public Map<String, Integer> characterStudentsRepresentation(String c) {
		if (!expertMode)
			return null;

		for (CharacterCard card : characters)
			if (card.getName().equals(c))
				if (card instanceof ContainerCharacterCard)
					return ((ContainerCharacterCard) card).getRepresentation();
				else return null;

		return null;
	}

	/**
	 * A method called by {@link BoardStatus} in order to obtain a view-oriented representation
	 * within the MVC pattern for the game.
	 * @param c the target character card literal
	 * @return a representation for the number of no-entry tiles placed on the Herb Granny character card
	 * if and only if expert mode is enabled and the target literal coincide with {@link HerbGranny#getName()} return value
	 */
	public Integer characterNoEntryTilesRepresentation(String c) {
		if (!expertMode)
			return null;

		if (!c.equals("HerbGranny"))
			return null;

		for (CharacterCard card : characters)
			if (card.getName().equals(c))
				return ((HerbGranny) card).getNoEntryTiles();

		return null;
	}

	private boolean gameOver() {
		for (Player player : players.getTurnOrder())
			if (player.getTowerQuantity() == 0) {
				lastRound = true;
				return true;
			}

		if (board.getIslandNumber() <= 3) {
			lastRound = true;
			return true;
		}

		return false;
	}

	private boolean lastRound() {
		for (Player player : players.getTurnOrder())
			if (player.getDeck().isEmpty()) {
				lastRound = true;
				return true;
			}

		lastRound = board.getBag().isEmpty();
		return board.getBag().isEmpty();
	}

	private GameConstants loadConstants(int numPlayers) {
		Gson gson = new Gson();

		InputStream configIn = getClass().getClassLoader().getResourceAsStream("config.json");

		if (configIn == null)
			throw new NullPointerException();

		String jsonString =
				gson.fromJson(new InputStreamReader(configIn), JsonObject.class)
				.get(Integer.toString(numPlayers)).getAsJsonObject().toString();

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

	private List<Player> mostTowersBuilt(List<Player> candidates) {
		List<Player> potentialWinners = new ArrayList<>();
		potentialWinners.add(candidates.get(0));
		int minTowersLeft = candidates.get(0).getTowerQuantity();

		for (Player player : candidates) {
			int numTowersLeft = player.getTowerQuantity();
			if (numTowersLeft < minTowersLeft) {
				minTowersLeft = numTowersLeft;
				potentialWinners.clear();
				potentialWinners.add(player);
			} else if (numTowersLeft == minTowersLeft)
				potentialWinners.add(player);
		}

		return potentialWinners;
	}

	private List<Player> mostProfessorsOwned(List<Player> candidates) {
		List<Player> potentialWinners = new ArrayList<>();
		potentialWinners.add(candidates.get(0));
		int maxProfessors = professors.getProfessors(candidates.get(0)).size();

		for (Player player : candidates) {
			int playerProfessors = professors.getProfessors(player).size();
			if (playerProfessors < maxProfessors) {
				maxProfessors = playerProfessors;
				potentialWinners.clear();
				potentialWinners.add(player);
			} else if (playerProfessors == maxProfessors)
				potentialWinners.add(player);
		}

		return potentialWinners;
	}
}