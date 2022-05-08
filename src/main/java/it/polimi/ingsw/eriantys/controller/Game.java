package it.polimi.ingsw.eriantys.controller;

import com.google.gson.JsonObject;
import it.polimi.ingsw.eriantys.controller.phases.*;
import it.polimi.ingsw.eriantys.messages.GameMessage;
import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.messages.client.HelpRequest;
import it.polimi.ingsw.eriantys.messages.server.*;
import it.polimi.ingsw.eriantys.model.GameManager;
import it.polimi.ingsw.eriantys.model.exceptions.*;
import it.polimi.ingsw.eriantys.server.ClientConnection;
import it.polimi.ingsw.eriantys.server.Server;
import it.polimi.ingsw.eriantys.server.exceptions.NoConnectionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
	private final Server server;
	private final GameInfo info;
	private boolean started = false;
	private List<String> players;
	private int currentPlayer;
	private Map<String, List<String>> availableAssistantCards;
	private MessageHandler messageHandler;
	private GameManager gameManager;

	/**
	 * Constructs a {@code Game} that fits the specified parameters.
	 * @param server the {@link Server} in charge of exchanging messages with the clients.
	 * @param gameId the unique id of this {@code Game}.
	 * @param lobbySize the number of players allowed in the game.
	 * @param expertMode {@code true} if and only if the instantiated game is to be played in expert mode.
	 */
	public Game(Server server, int gameId, String creator, int lobbySize, boolean expertMode) {
		this.server = server;
		this.info = new GameInfo(gameId, creator, lobbySize, expertMode);
		this.players = new ArrayList<>();
		this.currentPlayer = 0;
		this.availableAssistantCards = new HashMap<>();
		this.messageHandler = null;
		this.gameManager = null;
	}

	/**
	 * Returns a {@link GameInfo} object containing the unique id of the game, the number of players allowed to
	 * participate, the expert mode flag and the username of the game's creator.
	 * @return a {@link GameInfo} object containing the unique id of the game, the number of players allowed to
	 * participate, the expert mode flag and the username of the game's creator.
	 */
	public GameInfo getInfo() {
		return info;
	}

	/**
	 * Returns {@code true} if this game has started, or {@code false} if it is still waiting for new players to join.
	 * @return {@code true} if this game has started, or {@code false} if it is still waiting for new players to join.
	 */
	public boolean isStarted() {
		return started;
	}

	/**
	 * Returns {@code true} if and only if the game has reached the desired number of participants and can start.
	 * @return {@code true} if and only if the game has reached the desired number of participants and can start.
	 */
	public boolean meetsStartupCondition() {
		return players.size() == info.getLobbySize();
	}

	/**
	 * Sets up the game and each player's {@link ClientConnection}.
	 * @throws NoConnectionException if no connection can be retrieved for one or more players.
	 */
	public void setup() throws NoConnectionException {
		started = true;
		setGameManager();
		messageHandler = new GameSetupHandler(this);

		for (String player : players) {
			ClientConnection connection = server.getConnection(player);
			connection.setGame(this);
		}
	}

	/**
	 * Starts the game and notifies each player about the initial status of the game objects.
	 * @throws NoConnectionException if no connection can be retrieved for one or more players.
	 */
	public void start() throws NoConnectionException {
		try {
			gameManager.setupBoard();
		} catch (InvalidArgumentException | NoMovementException e) {
			e.printStackTrace();
		}

		broadcast(new InitialBoardStatus());
		newRound();
	}

	/**
	 * Advances to the next round of the game.
	 */
	public void newRound() {
		try {
			// TODO do something with this
			boolean lastRound = gameManager.setupRound();
		} catch (InvalidArgumentException | NoMovementException e) {
			e.printStackTrace();
		}
		availableAssistantCards = gameManager.getAvailableAssistantCards();
		messageHandler = new PlayAssistantCardHandler(this);
	}

	/**
	 * Handles the assistant cards played by the players and sets up the new order of the players.
	 * @param playedCards the assistant card played by each player.
	 */
	public void newTurn(Map<String, String> playedCards) {
		// TODO do something with this
		boolean lastRound = gameManager.handleAssistantCards(playedCards);
		players = gameManager.getTurnOrder();
		currentPlayer = 0;
		messageHandler = new MoveStudentHandler(this);
		updateCurrentPlayer();
	}

	/**
	 * Advances to the next player's turn in the current round.
	 */
	public void advanceTurn() {
		nextPlayer();
		if (currentPlayer == 0)
			newRound();
		else {
			messageHandler = new MoveStudentHandler(this);
			updateCurrentPlayer();
		}
	}

	/**
	 * Advances to the step of the turn when the current player can move Mother Nature.
	 */
	public void receiveMotherNatureMovement() {
		messageHandler = new MotherNatureDestinationHandler(this);
	}

	/**
	 * Advances to the step of the turn when the current player can select a cloud tile.
	 */
	public void receiveCloudSelection() {
		messageHandler = new SelectCloudHandler(this);
	}

	/**
	 * If the specified username is already in the game returns {@code false}, otherwise adds the username to the players
	 * and returns {@code true}.
	 * @param username the username of the player to add to the game.
	 * @return {@code true} if and only if the specified username was not already a player of this game.
	 */
	public boolean addPlayer(String username) {
		if (players.contains(username)) return false;
		players.add(username);
		return true;
	}

	/**
	 * If the specified username is not in the game returns {@code false}, otherwise removes the username from the players
	 * and returns {@code true}.
	 * @param username the username of the player to remove from the game.
	 * @return {@code true} if and only if the specified username was a player of this game.
	 */
	public boolean removePlayer(String username) {
		if (!players.contains(username)) return false;
		players.remove(username);
		return true;
	}

	/**
	 * Notifies every player in the game about the players currently waiting for the game to start.
	 * @throws NoConnectionException if no connection can be retrieved for one or more players.
	 */
	public void notifyLobbyChange() throws NoConnectionException {
		LobbyUpdate res = new LobbyUpdate(new ArrayList<>(this.players));
		broadcast(res);
	}

	/**
	 * Sets up a player with the specified parameters.
	 * @param username the player's username.
	 * @param towerColor the player's tower color.
	 * @param wizard the player's wizard.
	 * @throws InvalidArgumentException if {@code towerColor} or {@code wizard} are not legal enum literals.
	 */
	public void setupPlayer(String username, String towerColor, String wizard) throws InvalidArgumentException {
		gameManager.setupPlayer(username, towerColor, wizard);
	}

	/**
	 * Returns a map having the nickname of every player as the key set and each player's remaining assistant cards as the
	 * values.
	 * @return having the nickname of every player as the key set and each player's remaining assistant cards as the
	 * values.
	 */
	public Map<String, List<String>> getAssistantCards() {
		return availableAssistantCards;
	}

	/**
	 * Handles the movement of the student specified by the parameters.
	 * @param username the player performing the action.
	 * @param color the specified student's color.
	 * @param destination the destination of the movement.
	 * @throws IslandNotFoundException if {@code destination} is not a valid island id.
	 * @throws NoMovementException if an error occurs during the student movement.
	 */
	public void moveStudent(String username, String color, String destination)
			throws IslandNotFoundException, NoMovementException {
		gameManager.handleMovedStudent(username, color, destination);
	}

	/**
	 * Returns the size of the clouds for this game.
	 * @return the size of the clouds for this game.
	 */
	public int getCloudSize() {
		return gameManager.constants.getCloudSize();
	}

	public String moveMotherNature(String destination) throws InvalidArgumentException, IslandNotFoundException {
		return gameManager.handleMotherNatureMovement(destination);
	}

	public void selectCloud(String sender, int cloud) throws InvalidArgumentException, NoMovementException {
		gameManager.handleSelectedCloud(sender, cloud);
	}

	public void playCharacterCard(int card, JsonObject params)
			throws InvalidArgumentException, ItemNotAvailableException, DuplicateNoEntryTileException, NoMovementException {
		// TODO do something with this
		boolean lastRound = gameManager.handleCharacterCard(card, params);
	}

	public void handleMessage(GameMessage message) throws NoConnectionException {
		String sender = message.getSender();
		if (!players.contains(sender))
			refuseRequest(message, "Access denied to game: " + info.getGameId());
		else if (!sender.equals(players.get(currentPlayer)))
			refuseRequest(message, "Not your turn");
		else
			messageHandler.handle(message);
	}

	public void refuseRequest(GameMessage message, String details) throws NoConnectionException {
		String sender = message.getSender();
		ClientConnection connection = server.getConnection(sender);
		System.out.println(details);
		connection.write(new Refused(details));
	}

	public void acceptRequest(GameMessage message) throws NoConnectionException {
		String sender = message.getSender();
		ClientConnection connection = server.getConnection(sender);
		connection.write(new Accepted());
	}

	public void nextPlayer() {
		currentPlayer = (currentPlayer + 1) % players.size();
	}

	public void sendUpdate(UserActionUpdate message) throws NoConnectionException {
		message.setNextPlayer(players.get(currentPlayer));
		broadcast(message);
	}

	public void sendBoardUpdate() throws NoConnectionException {
		sendUpdate(new BoardUpdate());
	}

	public void sendHelp(HelpRequest helpRequest) throws NoConnectionException {
		String sender = helpRequest.getSender();
		ClientConnection connection = server.getConnection(sender);
		connection.write(new HelpResponse(messageHandler.getHelp()));
	}

	private void broadcast(Message message) throws NoConnectionException {
		for (String player : players) {
			ClientConnection c = server.getConnection(player);
			c.write(message);
		}
	}

	private void setGameManager() {
		if (gameManager == null)
			gameManager = new GameManager(players, getInfo().isExpertMode());
	}

	private void updateCurrentPlayer() {
		gameManager.setCurrentPlayer(players.get(currentPlayer));
	}
}
