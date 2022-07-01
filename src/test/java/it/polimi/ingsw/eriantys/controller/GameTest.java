package it.polimi.ingsw.eriantys.controller;

import com.google.gson.JsonObject;
import it.polimi.ingsw.eriantys.client.Client;
import it.polimi.ingsw.eriantys.messages.client.GameSetupSelection;
import it.polimi.ingsw.eriantys.messages.client.Handshake;
import it.polimi.ingsw.eriantys.messages.client.HelpRequest;
import it.polimi.ingsw.eriantys.messages.client.SelectCloud;
import it.polimi.ingsw.eriantys.model.Color;
import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;
import it.polimi.ingsw.eriantys.model.exceptions.IslandNotFoundException;
import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;
import it.polimi.ingsw.eriantys.server.Server;
import it.polimi.ingsw.eriantys.server.exceptions.NoConnectionException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

	static Server server;

	static {
		try {
			server = new Server(9133);
			server.start();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	static Game construct() {
		return construct(2, false);
	}

	static Game construct(int lobbySize, boolean expertMode) {
		return new Game(server, 1, "Tom", lobbySize, expertMode);
	}

	static Game construct(Server server) {
		return new Game(server, 1, "Tom", 2, false);
	}

	@Test
	void getInfo_NormalPreConditions_ReturnCorrectInfo() {
		Game game = construct(3, true);
		GameInfo gameInfo = game.getInfo();
		assertNotNull(gameInfo);
		assertEquals(1, gameInfo.getGameId());
		assertEquals(3, gameInfo.getLobbySize());
		assertTrue(gameInfo.isExpertMode());
		assertEquals("Tom", gameInfo.getCreator());
		assertEquals(0, gameInfo.getCurrentPlayers());
	}

	@Test
	void getCurrentPlayer_GameNotStarted_ReturnNull() {
		Game game = construct();
		assertNull(game.getCurrentPlayer());
	}

	@Test
	void getCurrentPlayer_GameStarted_ReturnFirstPlayer() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);
		assertTrue(game.isStarted());
		assertEquals("P1", game.getCurrentPlayer());
	}

	@Test
	void checkCredentials_NonexistentPlayer_ReturnFalse() {
		Game game = construct();
		assertFalse(game.checkCredentials("P1", "3019"));
	}

	@Test
	void checkCredentials_IncorrectPasscode_ReturnFalse() {
		Game game = construct();
		game.addPlayer("P1");
		assertFalse(game.checkCredentials("P1", "x"));
	}

	@Test
	void checkCredentials_CorrectCredentials_ReturnTrue() {
		Game game = construct();
		String passcode = game.addPlayer("P1");
		assertTrue(game.checkCredentials("P1", passcode));
	}

	@Test
	void isStarted_GameNotStarted_ReturnFalse() {
		Game game = construct();
		assertFalse(game.isStarted());
	}

	@Test
	void isStarted_GameStarted_ReturnTrue() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);
		assertTrue(game.isStarted());
	}

	@Test
	void meetsStartupCondition_NoPlayers_ReturnFalse() {
		Game game = construct();
		assertFalse(game.meetsStartupCondition());
	}

	@Test
	void meetsStartupCondition_NotEnoughPlayers_ReturnFalse() {
		Game game = construct();
		game.addPlayer("P1");
		assertFalse(game.meetsStartupCondition());
	}

	@Test
	void meetsStartupCondition_EnoughPlayers_ReturnTrue() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertTrue(game.meetsStartupCondition());
	}

	@Test
	void setup_NormalPreconditions_NoExceptions() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);
	}

	@Test
	void promptSelection() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);
		assertDoesNotThrow(game::promptSelection);
	}

	@Test
	void start_NormalPreconditions_LoadAllAssistantCards() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		assertDoesNotThrow(() -> game.setupPlayer("P1", "WHITE", "SNOW"));
		assertDoesNotThrow(() -> game.setupPlayer("P2", "BLACK", "SKY"));

		assertDoesNotThrow(game::start);

		Map<String, List<String>> asstCards = game.getAssistantCards();
		for (String p : asstCards.keySet())
			assertEquals(10, asstCards.get(p).size());
	}

	@Test
	void newRound_NormalPreconditions_LoadAllAssistantCards() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);
		assertThrowsExactly(NullPointerException.class, game::newRound);
		Map<String, List<String>> asstCards = game.getAssistantCards();
		for (String p : asstCards.keySet())
			assertEquals(10, asstCards.get(p).size());
	}

	@Test
	void newTurn_NormalPreconditions_SetCorrectPlayer() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		Map<String, String> playedCards = new HashMap<>();
		playedCards.put("P2", "CHEETAH");
		playedCards.put("P1", "OSTRICH");
		assertThrowsExactly(NullPointerException.class, () -> game.newTurn(playedCards));

		assertEquals("P2", game.getCurrentPlayer());
	}

	@Test
	void advanceTurn_FirstPlayerTurnBefore_SetSecondPlayerTurn() throws IOException {
		Server server = new Server(12345);
		server.start();

		Game game = construct(server);
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		Client client1 = new Client("localhost", 12345, false);
		client1.start();
		client1.write(new Handshake("P1"));
		Client client2 = new Client("localhost", 12345, false);
		client2.start();
		client2.write(new Handshake("P2"));

		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		assertDoesNotThrow(() -> game.setupPlayer("P1", "WHITE", "SNOW"));
		assertDoesNotThrow(() -> game.setupPlayer("P2", "BLACK", "SKY"));

		assertEquals("P1", game.getCurrentPlayer());
		assertThrowsExactly(NullPointerException.class, game::start);

		assertThrowsExactly(NullPointerException.class, game::advanceTurn);
		assertEquals("P2", game.getCurrentPlayer());
	}

	@Test
	void receiveMotherNatureMovement_NormalPreconditions_NoExceptions() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		assertDoesNotThrow(() -> game.setupPlayer("P1", "WHITE", "SNOW"));
		assertDoesNotThrow(() -> game.setupPlayer("P2", "BLACK", "SKY"));

		assertDoesNotThrow(game::start);
		assertDoesNotThrow(game::receiveMotherNatureMovement);
	}

	@Test
	void receiveCloudSelection_NormalPreconditions_NoExceptions() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		assertDoesNotThrow(() -> game.setupPlayer("P1", "WHITE", "SNOW"));
		assertDoesNotThrow(() -> game.setupPlayer("P2", "BLACK", "SKY"));

		assertDoesNotThrow(game::start);
		assertDoesNotThrow(game::receiveCloudSelection);
	}

	@Test
	void addPlayer_ExistingPlayer_ReturnNull() {
		Game game = construct();
		game.addPlayer("P1");
		assertNull(game.addPlayer("P1"));
	}

	@Test
	void addPlayer_FullLobby_ReturnNull() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertNull(game.addPlayer("P3"));
	}

	@Test
	void addPlayer_NonexistentPlayer_ReturnPasscodeAndAddPlayer() {
		Game game = construct();
		assertNotNull(game.addPlayer("P1"));
		assertTrue(game.removePlayer("P1"));
	}

	@Test
	void removePlayer_ExistingPlayer_ReturnTrueAndRemovePlayer() {
		Game game = construct();
		game.addPlayer("P1");
		assertTrue(game.removePlayer("P1"));
		assertNotNull(game.addPlayer("P1"));
	}

	@Test
	void removePlayer_NonexistentPlayer_ReturnFalse() {
		Game game = construct();
		assertFalse(game.removePlayer("P1"));
	}

	@Test
	void disconnect_NonexistentPlayer_NoChange() {
		Game game = construct();
		game.addPlayer("P1");
		assertDoesNotThrow(() -> game.disconnect("pippo"));
		assertTrue(game.removePlayer("P1"));
		assertFalse(game.removePlayer("pippo"));
	}

	@Test
	void disconnect_ExistingPlayerBeforeStarting_RemovePlayer() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(() -> game.disconnect("P1"));
		assertFalse(game.removePlayer("P1"));
		assertTrue(game.removePlayer("P2"));
	}

	@Test
	void disconnect_BothPlayersDisconnected_SendGameOverUpdate() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);
		assertDoesNotThrow(() -> game.disconnect("P1"));
		assertFalse(game.isStarted());
	}

	@Test
	void disconnect_OnePlayerConnected_SendDisconnectionUpdate() throws IOException {
		Server server = new Server(7822);
		server.start();

		Game game = construct(server);
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		Client client = new Client("localhost", 7822, false);
		client.start();
		client.write(new Handshake("P2"));

		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		assertThrowsExactly(NullPointerException.class, () -> game.disconnect("P1"));
	}

	@Test
	void disconnect_IdleFor60Seconds_GameOver() throws IOException {
		Server server = new Server(6873);
		server.start();

		Game game = construct(server);
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		assertDoesNotThrow(() -> game.disconnect("P1"));

		try {
			Thread.sleep(62500);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		assertFalse(game.isStarted());
	}

	@Test
	void reconnect_PreviouslyIdle_ResumeGame() throws IOException {
		Server server = new Server(9763);
		server.start();

		Game game = construct(server);
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		Client client2 = new Client("localhost", 9763, false);
		client2.start();
		client2.write(new Handshake("P2"));

		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		assertThrowsExactly(NullPointerException.class, () -> game.disconnect("P1"));
		assertThrowsExactly(NullPointerException.class, () -> game.reconnect("P1"));

		assertTrue(game.isStarted());
	}

	@Test
	void reconnect_NormalPreconditions_NoExceptions() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);
		assertDoesNotThrow(() -> game.reconnect("p1"));
	}

	@Test
	void isEmpty_NoPlayers_ReturnTrue() {
		Game game = construct();
		assertTrue(game.isEmpty());
	}

	@Test
	void isEmpty_SomePlayers_ReturnFalse() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertFalse(game.isEmpty());
	}

	@Test
	void setupPlayer_ValidParameters_NoExceptions() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);
		assertDoesNotThrow(() -> game.setupPlayer("P1", "WHITE", "SNOW"));
	}

	@Test
	void setupPlayer_NonexistentPlayer_ThrowException() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);
		assertThrowsExactly(InvalidArgumentException.class, () -> game.setupPlayer("pippo", "WHITE", "SNOW"));
	}

	@Test
	void setupPlayer_InvalidEnumLiterals_ThrowException() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);
		assertThrowsExactly(InvalidArgumentException.class, () -> game.setupPlayer("P1", "xxx", "SNOW"));
		assertThrowsExactly(InvalidArgumentException.class, () -> game.setupPlayer("P1", "WHITE", "xxx"));
	}

	@Test
	void getAssistantCards_GameStarted_ReturnNonEmptyMap() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		assertDoesNotThrow(() -> game.setupPlayer("P1", "WHITE", "SNOW"));
		assertDoesNotThrow(() -> game.setupPlayer("P2", "BLACK", "SKY"));

		assertDoesNotThrow(game::start);

		Map<String, List<String>> asstCards = game.getAssistantCards();
		assertFalse(asstCards.isEmpty());
		for (String p : asstCards.keySet())
			assertEquals(10, asstCards.get(p).size());
	}

	@Test
	void getAssistantCards_GameNotStarted_ReturnEmptyMap() {
		Game game = construct();
		assertTrue(game.getAssistantCards().isEmpty());
	}

	@Test
	void moveStudent_ValidParameters_NoExceptions() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);
		assertThrows(NullPointerException.class, game::start);

		for (Color c : Color.values()) {
			try {
				game.moveStudent("P1", c.toString(), "01");
			} catch (InvalidArgumentException | IslandNotFoundException e) {
				fail();
			} catch (NoMovementException e) {
				continue;
			}
			return;
		}

		fail();
	}

	@Test
	void moveStudent_NonexistentPlayer_ThrowException() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);
		assertThrows(NullPointerException.class, game::start);

		assertThrowsExactly(InvalidArgumentException.class, () -> game.moveStudent("pippo", "RED", "01"));
	}

	@Test
	void moveStudent_NonexistentDestination_ThrowException() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);
		assertThrows(NullPointerException.class, game::start);

		assertThrowsExactly(IslandNotFoundException.class, () -> game.moveStudent("P1", "RED", "420"));
	}

	@Test
	void moveStudent_NonexistentColor_ThrowException() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);
		assertThrows(NullPointerException.class, game::start);

		assertThrowsExactly(InvalidArgumentException.class, () -> game.moveStudent("P1", "x", "01"));
	}

	@Test
	void getCloudSize_TwoPlayers_ReturnThree() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		assertEquals(3, game.getCloudSize());
	}

	@Test
	void getCloudSize_ThreePlayers_ReturnFour() {
		Game game = construct(3, false);
		game.addPlayer("P1");
		game.addPlayer("P2");
		game.addPlayer("P3");
		assertDoesNotThrow(game::setup);

		assertEquals(4, game.getCloudSize());
	}

	@Test
	void moveMotherNature_NonexistentIsland_ThrowException() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);
		assertThrows(NullPointerException.class, game::start);

		Map<String, String> playedCards = new HashMap<>();
		playedCards.put("P2", "CHEETAH");
		playedCards.put("P1", "OSTRICH");
		assertThrowsExactly(NullPointerException.class, () -> game.newTurn(playedCards));

		assertThrowsExactly(IslandNotFoundException.class, () -> game.moveMotherNature("x"));
	}

	@Test
	void selectCloud_ValidParametersEmptyCloud_ThrowException() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		assertThrowsExactly(NoMovementException.class, () -> game.selectCloud("P1", 0));
	}

	@Test
	void selectCloud_IndexOutOfBounds_ThrowException() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		assertThrowsExactly(InvalidArgumentException.class, () -> game.selectCloud("P1", 4));
	}

	@Test
	void selectCloud_NonexistentPlayer_ThrowException() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		assertThrowsExactly(InvalidArgumentException.class, () -> game.selectCloud("pippo", 0));
	}

	@Test
	void playCharacterCard_NoExpertMode_ThrowException() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);
		assertThrowsExactly(NullPointerException.class, () -> game.playCharacterCard(0, new JsonObject()));
	}

	@Test
	void playCharacterCard_IndexOutOfBounds_ThrowException() {
		Game game = construct(2, true);
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		assertThrowsExactly(InvalidArgumentException.class, () -> game.playCharacterCard(12, new JsonObject()));
	}

	@Test
	void playCharacterCard_PlayerNotSet_ThrowException() {
		Game game = construct(2, true);
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		assertThrowsExactly(NullPointerException.class, () -> game.playCharacterCard(1, new JsonObject()));
	}

	@Test
	void handleMessage_SenderNotInGame_ThrowNoConnection() {
		Game game = construct();
		assertThrowsExactly(NoConnectionException.class, () -> game.handleMessage(new GameSetupSelection("pippo", "BLACK", "SKY")));
	}

	@Test
	void handleMessage_SenderNotCurrentPlayer_ThrowNoConnection() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);
		assertThrowsExactly(NoConnectionException.class, () -> game.handleMessage(new GameSetupSelection("P2", "BLACK", "SKY")));
	}

	@Test
	void handleMessage_SenderIsCurrentPlayer_ThrowNoConnection() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);
		assertThrowsExactly(NoConnectionException.class, () -> game.handleMessage(new GameSetupSelection("P1", "BLACK", "SKY")));
	}

	@Test
	void handleMessage_UnexpectedMessage_ThrowNoConnection() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);
		assertThrowsExactly(NoConnectionException.class, () -> game.handleMessage(new SelectCloud("P1", 1)));
	}

	@Test
	void refuseRequest_ConnectedPlayer_NoExceptions() throws IOException {
		Server server = new Server(9174);
		server.start();

		Game game = construct(server);
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		Client client = new Client("localhost", 9174, false);
		client.start();
		client.write(new Handshake("P1"));

		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		assertDoesNotThrow(() -> game.refuseRequest(new SelectCloud("P1", 0), ""));
	}

	@Test
	void acceptRequest_ConnectedPlayer_NoExceptions() throws IOException {
		Server server = new Server(8752);
		server.start();

		Game game = construct(server);
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		Client client = new Client("localhost", 8752, false);
		client.start();
		client.write(new Handshake("P1"));

		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		assertDoesNotThrow(() -> game.acceptRequest(new SelectCloud("P1", 0)));
	}

	@Test
	void sendHelp_NoHandler_NormalBehavior() throws IOException {
		Server server = new Server(4716);
		server.start();

		Game game = construct(server);
		game.addPlayer("P1");

		Client client = new Client("localhost", 4716, false);
		client.start();
		client.write(new Handshake("P1"));

		while (true) {
			try {
				game.sendHelp(new HelpRequest("P1"));
				return;
			} catch (NoConnectionException e) {
				try {
					Thread.sleep(250);
				} catch (InterruptedException ex) {
					throw new RuntimeException(ex);
				}
			}
		}
	}
}