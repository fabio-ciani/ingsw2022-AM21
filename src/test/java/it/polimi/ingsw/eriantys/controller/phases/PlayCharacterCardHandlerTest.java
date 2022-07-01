package it.polimi.ingsw.eriantys.controller.phases;

import it.polimi.ingsw.eriantys.client.Client;
import it.polimi.ingsw.eriantys.controller.Game;
import it.polimi.ingsw.eriantys.messages.client.Handshake;
import it.polimi.ingsw.eriantys.messages.client.PlayCharacterCard;
import it.polimi.ingsw.eriantys.messages.client.SelectCloud;
import it.polimi.ingsw.eriantys.server.HelpContent;
import it.polimi.ingsw.eriantys.server.Server;
import it.polimi.ingsw.eriantys.server.exceptions.NoConnectionException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PlayCharacterCardHandlerTest {

	static Server server;

	static {
		try {
			server = new Server(6123);
			server.start();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	static Game construct() {
		return new Game(server, 1, "Tom", 2, false);
	}

	static Game construct(Server server, boolean expertMode) {
		return new Game(server, 1, "Tom", 2, expertMode);
	}

	@Test
	void handle_UnexpectedMessage_RefuseRequest() throws IOException {
		Server server = new Server(7186);
		server.start();

		Game game = construct(server, false);
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		Client client1 = new Client("localhost", 7186, false);
		client1.start();
		client1.write(new Handshake("P1"));
		Client client2 = new Client("localhost", 7186, false);
		client2.start();
		client2.write(new Handshake("P2"));

		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		PlayCharacterCardHandler handler = new MoveStudentHandler(game);
		assertDoesNotThrow(() -> handler.handle(new SelectCloud("P1", 0)));
	}

	@Test
	void handle_CorrectMessage_ProcessRequest() throws IOException {
		Server server = new Server(4912);
		server.start();

		Game game = construct(server, true);
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		Client client1 = new Client("localhost", 4912, false);
		client1.start();
		client1.write(new Handshake("P1"));
		Client client2 = new Client("localhost", 4912, false);
		client2.start();
		client2.write(new Handshake("P2"));

		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		assertDoesNotThrow(() -> game.setupPlayer("P1", "WHITE", "SNOW"));
		assertDoesNotThrow(() -> game.setupPlayer("P2", "BLACK", "SKY"));
		assertThrowsExactly(NullPointerException.class, game::start);

		PlayCharacterCardHandler handler = new SelectCloudHandler(game);
		assertDoesNotThrow(() -> handler.handle(new PlayCharacterCard("P1", 0, "{}")));
	}

	@Test
	void handle_NoExpertMode_RefuseRequest() {
		Game game = construct();
		game.addPlayer("P1");
		game.addPlayer("P2");
		assertDoesNotThrow(game::setup);

		PlayCharacterCardHandler handler = new SelectCloudHandler(game);
		assertThrowsExactly(NoConnectionException.class, () -> handler.handle(new PlayCharacterCard("P1", 12, "{}")));
	}

	@Test
	void getHelp_NormalPreconditions_ReturnCorrectHelpMessage() {
		PlayCharacterCardHandler handler = new MoveStudentHandler(construct());
		assertEquals(HelpContent.IN_GAME.getContent(), handler.getHelp());
	}
}