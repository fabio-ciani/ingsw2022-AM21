package it.polimi.ingsw.eriantys.controller.phases;

import it.polimi.ingsw.eriantys.client.Client;
import it.polimi.ingsw.eriantys.controller.Game;
import it.polimi.ingsw.eriantys.messages.client.GameSetupSelection;
import it.polimi.ingsw.eriantys.messages.client.Handshake;
import it.polimi.ingsw.eriantys.messages.client.SelectCloud;
import it.polimi.ingsw.eriantys.server.HelpContent;
import it.polimi.ingsw.eriantys.server.Server;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class GameSetupHandlerTest {

	static Server server;

	static {
		try {
			server = new Server(7552);
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
		Server server = new Server(9861);
		server.start();

		Game game = construct(server, 2, false);
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		Client client1 = new Client("localhost", 9861, false);
		client1.start();
		client1.write(new Handshake("P1"));
		Client client2 = new Client("localhost", 9861, false);
		client2.start();
		client2.write(new Handshake("P2"));

		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		GameSetupHandler handler = new GameSetupHandler(game);
		assertDoesNotThrow(() -> handler.handle(new SelectCloud("P1", 0)));
	}

	@Test
	void handle_CorrectMessage_ProcessRequest() throws IOException {
		Server server = new Server(4917);
		server.start();

		Game game = construct(server, 2, false);
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		Client client1 = new Client("localhost", 4917, false);
		client1.start();
		client1.write(new Handshake("P1"));
		Client client2 = new Client("localhost", 4917, false);
		client2.start();
		client2.write(new Handshake("P2"));

		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		GameSetupHandler handler = new GameSetupHandler(game);
		assertThrowsExactly(NullPointerException.class, () -> handler.handle(new GameSetupSelection("P1", "BLACK", "SKY")));
	}

	@Test
	void handle_RepeatedMessage_RefuseRequest() throws IOException {
		Server server = new Server(7151);
		server.start();

		Game game = construct(server, 2, false);
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		Client client1 = new Client("localhost", 7151, false);
		client1.start();
		client1.write(new Handshake("P1"));
		Client client2 = new Client("localhost", 7151, false);
		client2.start();
		client2.write(new Handshake("P2"));

		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		GameSetupHandler handler = new GameSetupHandler(game);
		assertThrowsExactly(NullPointerException.class, () -> handler.handle(new GameSetupSelection("P1", "BLACK", "SKY")));
		assertDoesNotThrow(() -> handler.handle(new GameSetupSelection("P1", "BLACK", "SKY")));
	}

	@Test
	void handle_UnavailableColor_RefuseRequest() throws IOException {
		Server server = new Server(8124);
		server.start();

		Game game = construct(server, 2, false);
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		Client client1 = new Client("localhost", 8124, false);
		client1.start();
		client1.write(new Handshake("P1"));
		Client client2 = new Client("localhost", 8124, false);
		client2.start();
		client2.write(new Handshake("P2"));

		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		GameSetupHandler handler = new GameSetupHandler(game);
		assertThrowsExactly(NullPointerException.class, () -> handler.handle(new GameSetupSelection("P1", "BLACK", "SKY")));
		assertDoesNotThrow(() -> handler.handle(new GameSetupSelection("P2", "BLACK", "SNOW")));
	}

	@Test
	void handle_UnavailableWizard_RefuseRequest() throws IOException {
		Server server = new Server(6572);
		server.start();

		Game game = construct(server, 2, false);
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		Client client1 = new Client("localhost", 6572, false);
		client1.start();
		client1.write(new Handshake("P1"));
		Client client2 = new Client("localhost", 6572, false);
		client2.start();
		client2.write(new Handshake("P2"));

		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		GameSetupHandler handler = new GameSetupHandler(game);
		assertThrowsExactly(NullPointerException.class, () -> handler.handle(new GameSetupSelection("P1", "BLACK", "SKY")));
		assertDoesNotThrow(() -> handler.handle(new GameSetupSelection("P2", "WHITE", "SKY")));
	}

	@Test
	void getHelp_NormalPreconditions_ReturnCorrectHelpMessage() {
		GameSetupHandler handler = new GameSetupHandler(construct());
		assertEquals(HelpContent.GAME_SETUP.getContent(), handler.getHelp());
	}

	@Test
	void handleDisconnectedUser_CallOnFirstPlayer_PlayBothAndChangeState() {
		Game game = construct();
		GameSetupHandler handler = new GameSetupHandler(game);

		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		handler.handleDisconnectedUser("P1");
		assertThrowsExactly(NullPointerException.class, () -> handler.handleDisconnectedUser("P2"));
	}

	@Test
	void handleDisconnectedUser_CallOnNonexistentPlayer_ThrowException() {
		Game game = construct();
		GameSetupHandler handler = new GameSetupHandler(game);

		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		assertThrowsExactly(RuntimeException.class, () -> handler.handleDisconnectedUser("pippo"));
	}

	@Test
	void sendReconnectUpdate_NormalPreconditions_ThrowNullPointer() throws IOException {
		Server server = new Server(8411);
		server.start();

		Game game = construct(server, 2, false);
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		Client client1 = new Client("localhost", 8411, false);
		client1.start();
		client1.write(new Handshake("P1"));
		Client client2 = new Client("localhost", 8411, false);
		client2.start();
		client2.write(new Handshake("P2"));

		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		GameSetupHandler handler = new GameSetupHandler(game);
		assertThrowsExactly(NullPointerException.class, () -> handler.sendReconnectUpdate("P1"));
	}
}