package it.polimi.ingsw.eriantys.controller;

import com.google.gson.JsonObject;
import it.polimi.ingsw.eriantys.controller.phases.*;
import it.polimi.ingsw.eriantys.messages.GameMessage;
import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.messages.client.HelpRequest;
import it.polimi.ingsw.eriantys.messages.server.*;
import it.polimi.ingsw.eriantys.model.Color;
import it.polimi.ingsw.eriantys.model.GameManager;
import it.polimi.ingsw.eriantys.model.TowerColor;
import it.polimi.ingsw.eriantys.model.Wizard;
import it.polimi.ingsw.eriantys.model.exceptions.*;
import it.polimi.ingsw.eriantys.server.ClientConnection;
import it.polimi.ingsw.eriantys.server.HelpContent;
import it.polimi.ingsw.eriantys.server.Server;
import it.polimi.ingsw.eriantys.server.exceptions.NoConnectionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class handles the control flow of a game according to the rules. It manages every round and turn and
 * communicates with the players in order to provide updates about the game, as well as interacting with the model
 * through {@link GameManager}. {@link Game} implements a state pattern in order to manage the various phases of the
 * game and exposes several methods which are used to advance the game.
 */
public class Game {
	private final Server server;
	private final GameInfo info;
	private boolean started = false;
	private boolean lastRound = false;
	private boolean idle = false;
	private Thread idleThread = null;
	private List<String> players;
	private final Map<String, String> playerPasscodes;
	private int currentPlayer;
	private boolean playedCharacterCard;
	private Map<String, List<String>> availableAssistantCards;
	private MessageHandler messageHandler;
	private GameManager gameManager;

	/**
	 * Constructs a {@code Game} that fits the specified parameters.
	 * @param server the {@link Server} in charge of exchanging messages with the clients.
	 * @param gameId the unique id of this {@code Game}.
	 * @param creator the username of the player who created the game.
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
	 * Returns the username of the current player, or {@code null} if the game has not started yet.
	 * @return the username of the current player, or {@code null} if the game has not started yet.
	 */
	public String getCurrentPlayer() {
		return started ? players.get(currentPlayer) : null;
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
	 * Sets up the game by setting the {@code started} flag to {@code true}, setting the game manager and the setting the
	 * starting message handler.
	 */
	public void setup() {
		started = true;
		setGameManager();
		messageHandler = new GameSetupHandler(this);
	}

	/**
	 * Sends an update to all players which prompts the first player to select the wizard and tower color. The message
	 * contains all available wizards and tower colors.
	 */
	public void promptSelection() {
		List<String> towerColorStringLiterals = new ArrayList<>(TowerColor.stringLiterals());
		if (getInfo().getLobbySize() != 3) towerColorStringLiterals.remove("GREY");
		sendUpdate(new UserSelectionUpdate(
						towerColorStringLiterals, Wizard.stringLiterals(),
						new HashMap<>(), new HashMap<>()),
				true);
		checkDisconnection();
	}

	/**
	 * Starts the game and notifies each player about the initial status of the game objects.
	 */
	public void start() {
		try {
			gameManager.setupBoard();
			gameManager.setupEntrances();
		} catch (InvalidArgumentException | NoMovementException e) {
			e.printStackTrace();
		}

		sendInitialBoardStatus();
		newRound();
	}

	/**
	 * Advances to the next round of the game, or ends the game if the round which just ended was the last to be played.
	 */
	public void newRound() {
		if (lastRound) gameOver();
		try {
			lastRound = gameManager.setupRound();
			if (lastRound) broadcast(new LastRoundUpdate());
		} catch (InvalidArgumentException | NoMovementException e) {
			e.printStackTrace();
		}
		availableAssistantCards = gameManager.getAvailableAssistantCards();
		messageHandler = new PlayAssistantCardHandler(this);
		sendUpdate(new AssistantCardUpdate(new HashMap<>(), getAssistantCards()),
				true);
		checkDisconnection();
	}

	/**
	 * Handles the assistant cards played by the players and sets up the new order of the players.
	 * @param playedCards the assistant card played by each player.
	 */
	public void newTurn(Map<String, String> playedCards) {
		lastRound = gameManager.handleAssistantCards(playedCards);
		if (lastRound) broadcast(new LastRoundUpdate());
		players = gameManager.getTurnOrder();
		currentPlayer = 0;
		messageHandler = new MoveStudentHandler(this);
		sendBoardUpdate(PhaseName.MOVE_STUDENT);
		updateCurrentPlayer();
		checkDisconnection();
	}

	/**
	 * Advances to the next player's turn in the current round, updating the message handler, and advances to the next
	 * round if necessary.
	 */
	public void advanceTurn() {
		// started attribute is reset to false after the game ends to provide an additional check
		if (!started) return;

		nextPlayer();
		checkConnectedPlayers();

		try {
			gameManager.cancelCharacterCardEffect();
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
		}

		if (currentPlayer == 0)
			newRound();
		else {
			messageHandler = new MoveStudentHandler(this);
			sendBoardUpdate(PhaseName.MOVE_STUDENT);
			updateCurrentPlayer();
			checkDisconnection();
		}
	}

	/**
	 * Advances to the step of the turn when the current player can move Mother Nature.
	 */
	public void receiveMotherNatureMovement() {
		messageHandler = new MotherNatureDestinationHandler(this);
		sendBoardUpdate(PhaseName.MOTHER_NATURE);
		checkDisconnection();
	}

	/**
	 * Advances to the step of the turn when the current player can select a cloud tile.
	 */
	public void receiveCloudSelection() {
		messageHandler = new SelectCloudHandler(this);
		sendBoardUpdate(PhaseName.SELECT_CLOUD);
		checkDisconnection();
	}

	/**
	 * If the specified username is already in the game or the lobby is full returns {@code null}, otherwise adds the
	 * username to the players and returns the passcode which the player can use to reconnect to the game.
	 * @param username the username of the player to add to the game.
	 * @return the passcode which the player can use to reconnect to the game, or {@code null} if the specified username
	 * is already in the game.
	 */
	public String addPlayer(String username) {
		if (players.contains(username) || players.size() == getInfo().getLobbySize()) return null;
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
	 * Disconnects the specified player from the game, removing them from the lobby if the game has not yet started or
	 * pausing the game if the game has started and there are less than 2 players currently connected.
	 * @param username the username of the player which has disconnected.
	 * @throws NoConnectionException if no connection can be retrieved for the specified player.
	 */
	public void disconnect(String username) throws NoConnectionException {
		if (!isStarted()) {
			removePlayer(username);
			notifyLobbyChange();
		} else {
			int connectedPlayers = checkConnectedPlayers();
			if (connectedPlayers == 0) return;
			broadcast(new DisconnectionUpdate(username, connectedPlayers, idle));
			if (!idle && players.get(currentPlayer).equals(username)) {
				messageHandler.handleDisconnectedUser(username);
			}
		}
	}

	/**
	 * Handles the reconnection of the specified player, notifying the rest of the players about the reconnection,
	 * resuming the game if necessary and broadcasting an update about the state of the game.
	 * @param username the username of the player who has reconnected to the game.
	 */
	public void reconnect(String username) {
		int connectedPlayers =
				players.stream().mapToInt(p -> server.isConnected(p) ? 1 : 0).reduce(0, Integer::sum);
		broadcast(new ReconnectionUpdate(username, connectedPlayers, resume()));
		messageHandler.sendReconnectUpdate(username);
	}

	/**
	 * Notifies every player in the game about the players currently waiting for the game to start.
	 */
	public void notifyLobbyChange() {
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
	 * @throws InvalidArgumentException if no player matches the specified nickname or if {@code towerColor} or
	 * {@code wizard} are not legal enum literals.
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
	 * @throws InvalidArgumentException if no player matches the specified nickname or
	 * no {@link Color} matches the specified color.
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
	 * @throws NotEnoughMovementsException if the player performing this action does not have enough mother nature
	 * movements in order to complete it
	 */
	public boolean moveMotherNature(String destination)
			throws InvalidArgumentException, IslandNotFoundException, NotEnoughMovementsException {
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
	 */
	public void playCharacterCard(int card, JsonObject params)
			throws Exception, InvalidArgumentException, ItemNotAvailableException, DuplicateNoEntryTileException, NoMovementException {
		if (playedCharacterCard) throw new Exception();
		lastRound = gameManager.handleCharacterCard(card, params);
		playedCharacterCard = true;
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
		else if (idle)
			refuseRequest(message, "Game in idle state");
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
		playedCharacterCard = false;
	}

	/**
	 * Sends the specified {@link UserActionUpdate} message to every player.
	 * @param message the message to be sent to the players.
	 * @param setNextPlayer specifies whether this method should set the next player property on the message being sent.
	 */
	public void sendUpdate(UserActionUpdate message, boolean setNextPlayer) {
		if (setNextPlayer) message.setNextPlayer(players.get(currentPlayer));
		broadcast(message);
	}

	/**
	 * Sends an {@link InitialBoardStatus} message to every player.
	 */
	public void sendInitialBoardStatus() {
		broadcast(new InitialBoardStatus(gameManager));
	}

	/**
	 * Sends a {@link BoardUpdate} message with the current {@link PhaseName} to every player.
	 * @param phase the current game phase.
	 * @see Game#sendUpdate(UserActionUpdate, boolean)
	 */
	public void sendBoardUpdate(PhaseName phase) {
		sendUpdate(new BoardUpdate(gameManager, phase), true);
	}

	/**
	 * Sends a {@link BoardUpdate} message to every player.
	 * @see Game#sendUpdate(UserActionUpdate, boolean)
	 */
	public void sendBoardUpdate() {
		sendUpdate(new BoardUpdate(gameManager), true);
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
			responseContent = HelpContent.NOT_STARTED.getContent();
		else
			responseContent = messageHandler.getHelp();
		connection.write(new HelpResponse(responseContent));
	}

	/**
	 * Ends the game by notifying every player and deleting every reference to it.
	 */
	public void gameOver() {
		started = false;
		sendUpdate(new GameOverUpdate(gameManager.getWinner()), false);
		server.gameOver(this, players);
	}

	private void gameOver(String winner) {
		started = false;
		sendUpdate(new GameOverUpdate(winner), false);
		server.gameOver(this, players);
	}

	private void broadcast(Message message) {
		for (String player : players) {
			try {
				ClientConnection connection = server.getConnection(player);
				if (connection.getGame().getInfo().getGameId() == this.info.getGameId())
					connection.write(message);
			} catch (NoConnectionException e) {
				System.out.println(player + " disconnected");
			}
		}
	}

	private void setGameManager() {
		if (gameManager == null)
			gameManager = new GameManager(players, getInfo().isExpertMode());
	}

	/**
	 * Sets the current player in the game manager.
	 */
	private void updateCurrentPlayer() {
		try {
			gameManager.setCurrentPlayer(players.get(currentPlayer));
		} catch (InvalidArgumentException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * If the current player is disconnected calls the disconnected turn handler.
	 */
	public void checkDisconnection() {
		if (!server.isConnected(getCurrentPlayer())) {
			try {
				messageHandler.handleDisconnectedUser(getCurrentPlayer());
			} catch (NoConnectionException e) {
				e.printStackTrace();
			}
		}
	}

	private int checkConnectedPlayers() {
		List<String> connectedPlayers = players.stream().filter(server::isConnected).toList();

		if (connectedPlayers.size() == 1 && !idle)
			pause(connectedPlayers.get(0));
		else if (connectedPlayers.size() == 0)
			gameOver();

		return connectedPlayers.size();
	}

	private void pause(String connectedPlayer) {
		idle = true;
		idleThread = new Thread(() -> {
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
			if (idle) gameOver(connectedPlayer);
		});
		idleThread.start();
	}

	private boolean resume() {
		if (!idle) return false;

		idle = false;
		idleThread.interrupt();
		return true;
	}
}
