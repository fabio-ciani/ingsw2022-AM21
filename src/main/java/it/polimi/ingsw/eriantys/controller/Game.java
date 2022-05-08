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

	public Game(Server server, int gameId, String creator, int lobbySize, boolean expertMode) {
		this.server = server;
		this.info = new GameInfo(gameId, creator, lobbySize, expertMode);
		this.players = new ArrayList<>();
		this.currentPlayer = 0;
		this.availableAssistantCards = new HashMap<>();
		this.messageHandler = null;
		this.gameManager = null;
	}

	public GameInfo getInfo() {
		return info;
	}

	public boolean isStarted() {
		return started;
	}

	public boolean meetsStartupCondition() {
		return players.size() == info.getLobbySize();
	}

	public void setup() throws NoConnectionException {
		started = true;
		setGameManager();
		messageHandler = new GameSetupHandler(this);

		for (String player : players) {
			ClientConnection connection = server.getConnection(player);
			connection.setGame(this);
		}
	}

	public void start() throws NoConnectionException {
		try {
			gameManager.setupBoard();
		} catch (InvalidArgumentException | NoMovementException e) {
			e.printStackTrace();
		}

		broadcast(new InitialBoardStatus());
		newRound();
	}

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

	public void newTurn(Map<String, String> playedCards) {
		// TODO do something with this
		boolean lastRound = gameManager.handleAssistantCards(playedCards);
		players = gameManager.getTurnOrder();
		currentPlayer = 0;
		messageHandler = new MoveStudentHandler(this);
		updateCurrentPlayer();
	}

	public void advanceTurn() {
		nextPlayer();
		if (currentPlayer == 0)
			newRound();
		else {
			messageHandler = new MoveStudentHandler(this);
			updateCurrentPlayer();
		}
	}

	public void receiveMotherNatureMovement() {
		messageHandler = new MotherNatureDestinationHandler(this);
	}

	public void receiveCloudSelection() {
		messageHandler = new SelectCloudHandler(this);
	}

	public boolean addPlayer(String username) {
		if (players.contains(username)) return false;
		players.add(username);
		return true;
	}

	public boolean removePlayer(String username) {
		if (!players.contains(username)) return false;
		players.remove(username);
		return true;
	}

	public void notifyLobbyChange() throws NoConnectionException {
		LobbyUpdate res = new LobbyUpdate(new ArrayList<>(this.players));
		broadcast(res);
	}

	public void setupPlayer(String username, String towerColor, String wizard) throws InvalidArgumentException {
		gameManager.setupPlayer(username, towerColor, wizard);
	}

	public Map<String, List<String>> getAssistantCards() {
		return availableAssistantCards;
	}

	public void moveStudent(String username, String color, String destination)
			throws IslandNotFoundException, NoMovementException {
		gameManager.handleMovedStudent(username, color, destination);
	}

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
