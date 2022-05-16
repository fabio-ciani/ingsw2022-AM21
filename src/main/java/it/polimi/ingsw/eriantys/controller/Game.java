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
import it.polimi.ingsw.eriantys.server.HelpContent;
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
	private boolean lastRound = false;
	private List<String> players;
	private final Map<String, String> playerPasscodes;
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
		this.playerPasscodes = new HashMap<>();
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
	 * Returns {@code true} if and only if the specified credentials are valid and the player in question is allowed to
	 * reconnect to the game.
	 * @param username the username of the player trying to reconnect to the game.
	 * @param passcode the passcode sent by the player, to be checked against the one stored in the game.
	 * @return {@code true} if and only if the specified credentials are valid and the player in question is allowed to
	 * reconnect to the game.
	 */
	public boolean checkCredentials(String username, String passcode) {
		return
				players.contains(username) &&
				playerPasscodes.containsKey(username) &&
				playerPasscodes.get(username).equals(passcode);
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
	 * Sets up the game.
	 */
	public void setup() {
		started = true;
		setGameManager();
		messageHandler = new GameSetupHandler(this);
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

		broadcast(new InitialBoardStatus(gameManager));
		newRound();
	}

	/**
	 * Advances to the next round of the game.
	 */
	public void newRound() throws NoConnectionException {
		if (lastRound) gameOver();
		try {
			lastRound = gameManager.setupRound();
			if (lastRound) broadcast(new LastRoundUpdate());
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
	public void newTurn(Map<String, String> playedCards) throws NoConnectionException {
		lastRound = gameManager.handleAssistantCards(playedCards);
		if (lastRound) broadcast(new LastRoundUpdate());
		players = gameManager.getTurnOrder();
		currentPlayer = 0;
		messageHandler = new MoveStudentHandler(this);
		updateCurrentPlayer();
	}

	/**
	 * Advances to the next player's turn in the current round.
	 */
	public void advanceTurn() throws NoConnectionException {
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
	 * If the specified username is already in the game returns {@code null}, otherwise adds the username to the players
	 * and returns the passcode which the player can use to reconnect to the game.
	 * @param username the username of the player to add to the game.
	 * @return the passcode which the player can use to reconnect to the game, or {@code null} if the specified username
	 * is already in the game.
	 */
	public String addPlayer(String username) {
		if (players.contains(username)) return null;
		players.add(username);
		String passcode = Integer.toHexString((int) (Math.random() * 65536));
		playerPasscodes.put(username, passcode);
		info.setCurrentPlayers(info.getCurrentPlayers() + 1);
		return passcode;
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
		playerPasscodes.remove(username);
		info.setCurrentPlayers(info.getCurrentPlayers() - 1);
		return true;
	}

	/**
	 * Disconnects the specified player from the game.
	 * @param username the username of the player which has disconnected.
	 * @throws NoConnectionException if no connection can be retrieved for the specified player.
	 */
	public void disconnect(String username) throws NoConnectionException {
		if (!isStarted()) {
			removePlayer(username);
			notifyLobbyChange();
		} else {
			if (players.get(currentPlayer).equals(username)) {
				messageHandler.handleDisconnectedUser(username);  // e.g. select random tower color
			}
		}
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
	 * Returns {@code true} if and only if no players are currently participating in the game.
	 * @return {@code true} if and only if no players are currently participating in the game.
	 */
	public boolean isEmpty() {
		return players.size() == 0;
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
			throws IslandNotFoundException, NoMovementException, InvalidArgumentException {
		gameManager.handleMovedStudent(username, color, destination);
	}

	/**
	 * Returns the size of the clouds for this game.
	 * @return the size of the clouds for this game.
	 */
	public int getCloudSize() {
		return gameManager.constants.getCloudSize();
	}

	/**
	 * Handles Mother Nature's movement to the specified destination.
	 * @param destination the destination of the movement, which could be an island or the player's dining room.
	 * @return {@code true} if and only if the game ends as a result of Mother Nature's movement.
	 * @throws InvalidArgumentException if an error occurs while resolving the destination island.
	 * @throws IslandNotFoundException if no island matching the specified id can be found.
	 */
	public boolean moveMotherNature(String destination) throws InvalidArgumentException, IslandNotFoundException {
		return gameManager.handleMotherNatureMovement(destination);
	}

	/**
	 * Handles the choice of a cloud tile from which the player takes new student discs.
	 * @param sender the username of the player performing this action.
	 * @param cloud the index of the chosen cloud tile.
	 * @throws InvalidArgumentException if there is an error while retrieving the player, or if the {@code cloud} index is
	 * out of bounds.
	 * @throws NoMovementException if an error occurs while transferring the students.
	 */
	public void selectCloud(String sender, int cloud) throws InvalidArgumentException, NoMovementException {
		gameManager.handleSelectedCloud(sender, cloud);
	}

	/**
	 * Handles the selection of a character card to play.
	 * @param card the index of the desired character card.
	 * @param params the parameters for the application of the specified character card's effect.
	 * @throws InvalidArgumentException if an error has occurred while applying the card's effect.
	 * @throws ItemNotAvailableException if an error has occurred while removing a No Entry tile from an island.
	 * @throws DuplicateNoEntryTileException if an error has occurred while placing a No Entry tile on an island.
	 * @throws NoMovementException if an error has occurred while moving one or more students.
	 * @throws NoConnectionException if no connection can be retrieved for one or more players.
	 */
	public void playCharacterCard(int card, JsonObject params)
			throws InvalidArgumentException, ItemNotAvailableException, DuplicateNoEntryTileException, NoMovementException,
			NoConnectionException {
		lastRound = gameManager.handleCharacterCard(card, params);
		if (lastRound) broadcast(new LastRoundUpdate());
	}

	/**
	 * Handles a {@link GameMessage} sent by a client through the current phase's message handler.
	 * @param message the received message.
	 * @throws NoConnectionException if no connection can be retrieved for the sender of the message.
	 */
	public void handleMessage(GameMessage message) throws NoConnectionException {
		String sender = message.getSender();
		if (!players.contains(sender))
			refuseRequest(message, "Access denied to game: " + info.getGameId());
		else if (!sender.equals(players.get(currentPlayer)))
			refuseRequest(message, "Not your turn");
		else
			messageHandler.handle(message);
	}

	/**
	 * Responds to a {@link GameMessage} sent by a client with a {@link Refused} message with the specified detail string.
	 * @param message the client request to refuse.
	 * @param details a detail message explaining the reason why the request was refused.
	 * @throws NoConnectionException if no connection can be retrieved for the sender of the message.
	 */
	public void refuseRequest(GameMessage message, String details) throws NoConnectionException {
		String sender = message.getSender();
		ClientConnection connection = server.getConnection(sender);
		System.out.println(details);
		connection.write(new Refused(details));
	}

	/**
	 * Responds to a {@link GameMessage} sent by a client with an {@link Accepted} message.
	 * @param message the client request to accept.
	 * @throws NoConnectionException if no connection can be retrieved for the sender of the message.
	 */
	public void acceptRequest(GameMessage message) throws NoConnectionException {
		String sender = message.getSender();
		ClientConnection connection = server.getConnection(sender);
		connection.write(new Accepted());
	}

	/**
	 * Advances the current player to the next player in the current turn order.
	 */
	public void nextPlayer() {
		currentPlayer = (currentPlayer + 1) % players.size();
	}

	/**
	 * Sends the specified {@link UserActionUpdate} message to every player.
	 * @param message the message to be sent to the players.
	 * @throws NoConnectionException if no connection can be retrieved for one or more players.
	 */
	public void sendUpdate(UserActionUpdate message) throws NoConnectionException {
		message.setNextPlayer(players.get(currentPlayer));
		broadcast(message);
	}

	/**
	 * Sends a {@link BoardUpdate} message to every player.
	 * @throws NoConnectionException if no connection can be retrieved for one or more players.
	 * @see Game#sendUpdate(UserActionUpdate)
	 */
	public void sendBoardUpdate() throws NoConnectionException {
		sendUpdate(new BoardUpdate(gameManager));
	}

	/**
	 * Sends a {@link HelpResponse} detailing the possible user actions to the client which sent the {@link HelpRequest}.
	 * @param helpRequest the help request being handled.
	 * @throws NoConnectionException if no connection can be retrieved for the sender of the help request.
	 */
	public void sendHelp(HelpRequest helpRequest) throws NoConnectionException {
		String sender = helpRequest.getSender();
		ClientConnection connection = server.getConnection(sender);
		String responseContent;
		if (messageHandler == null)
			responseContent = HelpContent.IN_GAME.getContent();
		else
			responseContent = messageHandler.getHelp();
		connection.write(new HelpResponse(responseContent));
	}

	/**
	 * Ends the game by notifying every player and deleting every reference to {@code this}.
	 * @throws NoConnectionException if no connection can be retrieved for one or more players.
	 */
	public void gameOver() throws NoConnectionException {
		sendUpdate(new GameOverUpdate(gameManager.getWinner()));  // TODO client should clear reconnect.json
		server.gameOver(this, players);
	}

	private void broadcast(Message message) throws NoConnectionException {
		for (String player : players) {
			ClientConnection c = server.getConnection(player);
			if (c != null) c.write(message);
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
