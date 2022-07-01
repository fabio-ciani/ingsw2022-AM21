package it.polimi.ingsw.eriantys.controller.phases;

import it.polimi.ingsw.eriantys.client.Client;
import it.polimi.ingsw.eriantys.controller.Game;
import it.polimi.ingsw.eriantys.messages.client.Handshake;
import it.polimi.ingsw.eriantys.messages.client.MoveStudent;
import it.polimi.ingsw.eriantys.messages.client.SelectCloud;
import it.polimi.ingsw.eriantys.server.HelpContent;
import it.polimi.ingsw.eriantys.server.Server;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class MoveStudentHandlerTest {
	static Server server;

	static {
		try {
			server = new Server(6191);
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
		Server server = new Server(8761);
		server.start();

		Game game = construct(server);
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		Client client1 = new Client("localhost", 8761, false);
		client1.start();
		client1.write(new Handshake("P1"));
		Client client2 = new Client("localhost", 8761, false);
		client2.start();
		client2.write(new Handshake("P2"));

		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		MoveStudentHandler handler = new MoveStudentHandler(game);
		assertDoesNotThrow(() -> handler.handle(new SelectCloud("P1", 0)));
	}

	@Test
	void handle_NonexistentIsland_RefuseRequest() throws IOException {
		Server server = new Server(8172);
		server.start();

		Game game = construct(server);
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		Client client1 = new Client("localhost", 8172, false);
		client1.start();
		client1.write(new Handshake("P1"));
		Client client2 = new Client("localhost", 8172, false);
		client2.start();
		client2.write(new Handshake("P2"));

		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		MoveStudentHandler handler = new MoveStudentHandler(game);
		assertDoesNotThrow(() -> handler.handle(new MoveStudent("P1", "RED", "123")));
	}

	@Test
	void handle_NonexistentColor_RefuseRequest() throws IOException {
		Server server = new Server(8199);
		server.start();

		Game game = construct(server);
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		Client client1 = new Client("localhost", 8199, false);
		client1.start();
		client1.write(new Handshake("P1"));
		Client client2 = new Client("localhost", 8199, false);
		client2.start();
		client2.write(new Handshake("P2"));

		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		MoveStudentHandler handler = new MoveStudentHandler(game);
		assertDoesNotThrow(() -> handler.handle(new MoveStudent("P1", "xxx", "02")));
	}

	@Test
	void getHelp_NormalPreconditions_ReturnCorrectHelpMessage() {
		MoveStudentHandler handler = new MoveStudentHandler(construct());
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

		MoveStudentHandler handler = new MoveStudentHandler(game);
		handler.handleDisconnectedUser("P1");
		handler.handleDisconnectedUser("P2");
	}

	@Test
	void sendReconnectUpdate_NormalPreconditions_ThrowNullPointer() throws IOException {
		Server server = new Server(9872);
		server.start();

		Game game = construct(server);
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		Client client1 = new Client("localhost", 9872, false);
		client1.start();
		client1.write(new Handshake("P1"));
		Client client2 = new Client("localhost", 9872, false);
		client2.start();
		client2.write(new Handshake("P2"));

		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		MoveStudentHandler handler = new MoveStudentHandler(game);
		assertThrowsExactly(NullPointerException.class, () -> handler.sendReconnectUpdate("P1"));
	}
}