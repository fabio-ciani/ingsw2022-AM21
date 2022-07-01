package it.polimi.ingsw.eriantys.controller.phases;

import it.polimi.ingsw.eriantys.client.Client;
import it.polimi.ingsw.eriantys.controller.Game;
import it.polimi.ingsw.eriantys.messages.client.Handshake;
import it.polimi.ingsw.eriantys.messages.client.PlayAssistantCard;
import it.polimi.ingsw.eriantys.messages.client.SelectCloud;
import it.polimi.ingsw.eriantys.server.HelpContent;
import it.polimi.ingsw.eriantys.server.Server;
import it.polimi.ingsw.eriantys.server.exceptions.NoConnectionException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PlayAssistantCardHandlerTest {

	static Server server;

	static {
		try {
			server = new Server(8731);
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

	static Game construct(Server server, int lobbySize, boolean expertMode) {
		return new Game(server, 1, "Tom", lobbySize, expertMode);
	}

	@Test
	void handle_UnexpectedMessage_RefuseRequest() throws IOException {
		Server server = new Server(9863);
		server.start();

		Game game = construct(server, 2, false);
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		Client client1 = new Client("localhost", 9863, false);
		client1.start();
		client1.write(new Handshake("P1"));
		Client client2 = new Client("localhost", 9863, false);
		client2.start();
		client2.write(new Handshake("P2"));

		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		PlayAssistantCardHandler handler = new PlayAssistantCardHandler(game);
		assertDoesNotThrow(() -> handler.handle(new SelectCloud("P1", 0)));
	}

	@Test
	void handle_CorrectMessage_ProcessRequest() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		assertDoesNotThrow(() -> game.setupPlayer("P1", "WHITE", "SNOW"));
		assertDoesNotThrow(() -> game.setupPlayer("P2", "BLACK", "SKY"));

		assertDoesNotThrow(game::start);

		PlayAssistantCardHandler handler = new PlayAssistantCardHandler(game);
		assertThrowsExactly(NoConnectionException.class, () -> handler.handle(new PlayAssistantCard("P1", "CAT")));
	}

	@Test
	void handle_RepeatedMessage_RefuseRequest() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		assertDoesNotThrow(() -> game.setupPlayer("P1", "WHITE", "SNOW"));
		assertDoesNotThrow(() -> game.setupPlayer("P2", "BLACK", "SKY"));

		assertDoesNotThrow(game::start);

		PlayAssistantCardHandler handler = new PlayAssistantCardHandler(game);
		assertThrowsExactly(NoConnectionException.class, () -> handler.handle(new PlayAssistantCard("P1", "CAT")));
		assertThrowsExactly(NoConnectionException.class, () -> handler.handle(new PlayAssistantCard("P1", "CAT")));
	}

	@Test
	void handle_UnavailableCard_RefuseRequest() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		assertDoesNotThrow(() -> game.setupPlayer("P1", "WHITE", "SNOW"));
		assertDoesNotThrow(() -> game.setupPlayer("P2", "BLACK", "SKY"));

		assertDoesNotThrow(game::start);

		PlayAssistantCardHandler handler = new PlayAssistantCardHandler(game);
		assertThrowsExactly(NoConnectionException.class, () -> handler.handle(new PlayAssistantCard("P1", "CAT")));
		assertThrowsExactly(NoConnectionException.class, () -> handler.handle(new PlayAssistantCard("P2", "CAT")));
	}

	@Test
	void getHelp_NormalPreconditions_ReturnCorrectHelpMessage() {
		PlayAssistantCardHandler handler = new PlayAssistantCardHandler(construct());
		assertEquals(HelpContent.IN_GAME.getContent(), handler.getHelp());
	}

	@Test
	void handleDisconnectedUser_CallOnFirstPlayer_NoExceptions() {
		Game game = construct();

		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		assertDoesNotThrow(() -> game.setupPlayer("P1", "WHITE", "SNOW"));
		assertDoesNotThrow(() -> game.setupPlayer("P2", "BLACK", "SKY"));

		assertDoesNotThrow(game::start);

		PlayAssistantCardHandler handler = new PlayAssistantCardHandler(game);
		handler.handleDisconnectedUser("P1");
		assertDoesNotThrow(() -> handler.handleDisconnectedUser("P2"));
	}

	@Test
	void sendReconnectUpdate_NormalPreconditions_ThrowNullPointer() throws IOException {
		Server server = new Server(6173);
		server.start();

		Game game = construct(server, 2, false);
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		Client client1 = new Client("localhost", 6173, false);
		client1.start();
		client1.write(new Handshake("P1"));
		Client client2 = new Client("localhost", 6173, false);
		client2.start();
		client2.write(new Handshake("P2"));

		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		PlayAssistantCardHandler handler = new PlayAssistantCardHandler(game);
		assertThrowsExactly(NullPointerException.class, () -> handler.sendReconnectUpdate("P1"));
	}
}