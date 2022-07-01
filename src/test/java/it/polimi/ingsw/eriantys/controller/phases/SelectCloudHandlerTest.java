package it.polimi.ingsw.eriantys.controller.phases;

import it.polimi.ingsw.eriantys.client.Client;
import it.polimi.ingsw.eriantys.controller.Game;
import it.polimi.ingsw.eriantys.messages.client.Handshake;
import it.polimi.ingsw.eriantys.messages.client.MotherNatureDestination;
import it.polimi.ingsw.eriantys.messages.client.SelectCloud;
import it.polimi.ingsw.eriantys.server.HelpContent;
import it.polimi.ingsw.eriantys.server.Server;
import it.polimi.ingsw.eriantys.server.exceptions.NoConnectionException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class SelectCloudHandlerTest {

	static Server server;

	static {
		try {
			server = new Server(5182);
			server.start();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	static Game construct() {
		return new Game(server, 1, "Tom", 2, false);
	}

	static Game construct(Server server) {
		return new Game(server, 1, "Tom", 2, false);
	}

	@Test
	void handle_UnexpectedMessage_RefuseRequest() throws IOException {
		Server server = new Server(6815);
		server.start();

		Game game = construct(server);
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		Client client1 = new Client("localhost", 6815, false);
		client1.start();
		client1.write(new Handshake("P1"));
		Client client2 = new Client("localhost", 6815, false);
		client2.start();
		client2.write(new Handshake("P2"));

		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		SelectCloudHandler handler = new SelectCloudHandler(game);
		assertDoesNotThrow(() -> handler.handle(new MotherNatureDestination("P1", "xxx")));
	}

	@Test
	void handle_CorrectMessage_ProcessRequest() throws IOException {
		Server server = new Server(5187);
		server.start();

		Game game = construct(server);
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		Client client1 = new Client("localhost", 5187, false);
		client1.start();
		client1.write(new Handshake("P1"));
		Client client2 = new Client("localhost", 5187, false);
		client2.start();
		client2.write(new Handshake("P2"));

		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		SelectCloudHandler handler = new SelectCloudHandler(game);
		assertDoesNotThrow(() -> handler.handle(new SelectCloud("P1", 0)));
	}

	@Test
	void handle_NonexistentCloud_RefuseRequest() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		SelectCloudHandler handler = new SelectCloudHandler(game);
		assertThrowsExactly(NoConnectionException.class, () -> handler.handle(new SelectCloud("P1", 12)));
	}

	@Test
	void handle_EmptyCloud_RefuseRequest() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		SelectCloudHandler handler = new SelectCloudHandler(game);
		assertThrowsExactly(NoConnectionException.class, () -> handler.handle(new SelectCloud("P1", 0)));
	}

	@Test
	void getHelp_NormalPreconditions_ReturnCorrectHelpMessage() {
		SelectCloudHandler handler = new SelectCloudHandler(construct());
		assertEquals(HelpContent.IN_GAME.getContent(), handler.getHelp());
	}

	@Test
	void handleDisconnectedUser_CallOnBothPlayers_NoExceptions() {
		Game game = construct();

		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		assertDoesNotThrow(() -> game.setupPlayer("P1", "WHITE", "SNOW"));
		assertDoesNotThrow(() -> game.setupPlayer("P2", "BLACK", "SKY"));

		assertDoesNotThrow(game::start);

		SelectCloudHandler handler = new SelectCloudHandler(game);
		handler.handleDisconnectedUser("P1");
		handler.handleDisconnectedUser("P2");
	}

	@Test
	void sendReconnectUpdate_NormalPreconditions_ThrowNullPointer() throws IOException {
		Server server = new Server(5129);
		server.start();

		Game game = construct(server);
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		Client client1 = new Client("localhost", 5129, false);
		client1.start();
		client1.write(new Handshake("P1"));
		Client client2 = new Client("localhost", 5129, false);
		client2.start();
		client2.write(new Handshake("P2"));

		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		SelectCloudHandler handler = new SelectCloudHandler(game);
		assertThrowsExactly(NullPointerException.class, () -> handler.sendReconnectUpdate("P1"));
	}
}