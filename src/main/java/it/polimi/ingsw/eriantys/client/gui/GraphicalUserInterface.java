package it.polimi.ingsw.eriantys.client.gui;

import it.polimi.ingsw.eriantys.client.UserInterface;
import it.polimi.ingsw.eriantys.client.gui.controllers.*;
import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.messages.server.*;
import it.polimi.ingsw.eriantys.model.GameConstants;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This class represents the graphical user interface (GUI).
 * It handles messages received from the server and updates the JavaFX {@link Application}.
 */
public class GraphicalUserInterface extends UserInterface {
	private GraphicalApplication app;

	/**
	 * Constructs a {@link GraphicalUserInterface} object.
	 *
	 * @throws IOException if the JSON file with the character cards details cannot be opened
	 */
	public GraphicalUserInterface() throws IOException {
		super();
	}

	/**
	 * Shows an information alert with the given message.
	 *
	 * @param details the message to show
	 */
	@Override
	public void showInfo(String details) {
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Info");
			alert.setHeaderText("Info");
			alert.setContentText(details);
			alert.showAndWait();
		});
	}

	/**
	 * Shows an error alert with the given message.
	 *
	 * @param details the message to show
	 */
	@Override
	public void showError(String details) {
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Error");
			alert.setContentText(details);
			alert.showAndWait();
		});
	}

	/**
	 * Gets a reference of the running {@link GraphicalApplication}.
	 * It should be called after the {@link #run()} method.
	 */
	@Override
	public void init() {
		app = GraphicalApplication.getInstance();
	}

	/**
	 * Launches the JavaFX {@link Application}.
	 */
	@Override
	public void run() {
		GraphicalApplication.setClient(client);
		GraphicalApplication.setShowInfo(this::showInfo);
		GraphicalApplication.setShowError(this::showError);
		Application.launch(GraphicalApplication.class);
	}

	/**
	 * {@inheritDoc}
	 * This implementation does nothing.
	 *
	 * @param message the received message
	 */
	@Override
	public void handleMessage(Accepted message) {}

	/**
	 * {@inheritDoc}
	 * Saves the selected username and changes the scene to {@code LOBBIES}.
	 *
	 * @param message the received message
	 */
	@Override
	public void handleMessage(AcceptedUsername message) {
		client.setUsername(message.getUsername());
		Platform.runLater(() -> app.changeScene(SceneName.LOBBIES));
	}

	/**
	 * {@inheritDoc}
	 * Saves the game id and the reconnection settings.
	 * Changes the scene to {@code WAITING_ROOM}.
	 *
	 * @param message the received message
	 */
	@Override
	public void handleMessage(AcceptedJoinLobby message) {
		client.setGameId(message.getGameId());
		client.putReconnectSettings(message);
		Platform.runLater(() -> {
			WaitingRoomController controller = (WaitingRoomController) app.getControllerForScene(SceneName.WAITING_ROOM);
			controller.setText(Integer.toString(message.getGameId()));
			app.changeScene(SceneName.WAITING_ROOM);
		});
	}

	/**
	 * {@inheritDoc}
	 * Removes reconnection settings.
	 * Changes the scene to {@code LOBBIES}.
	 *
	 * @param message the received message
	 */
	@Override
	public void handleMessage(AcceptedLeaveLobby message) {
		client.removeReconnectSettings();
		Platform.runLater(() -> app.changeScene(SceneName.LOBBIES));
	}

	/**
	 * {@inheritDoc}
	 * This message should not be received in GUI mode.
	 *
	 * @param message the received message
	 */
	@Override
	public void handleMessage(HelpResponse message) {
		handleMessage((Message) message);
	}

	/**
	 * {@inheritDoc}
	 * Updates the list of lobbies calling {@link LobbiesController#updateLobbies(List)}.
	 *
	 * @param message the received message
	 */
	@Override
	public void handleMessage(AvailableLobbies message) {
		Platform.runLater(() -> {
			LobbiesController controller = (LobbiesController) app.getControllerForScene(SceneName.LOBBIES);
			controller.updateLobbies(message.getLobbies());
		});
	}

	/**
	 * {@inheritDoc}
	 * Updates the list of players in the lobby calling {@link WaitingRoomController#updatePlayers(List)}.
	 *
	 * @param message the received message
	 */
	@Override
	public void handleMessage(LobbyUpdate message) {
		Platform.runLater(() -> {
			WaitingRoomController controller = (WaitingRoomController) app.getControllerForScene(SceneName.WAITING_ROOM);
			controller.updatePlayers(message.getPlayers());
		});
	}

	/**
	 * {@inheritDoc}
	 * Updates the Assistant Cards popup calling {@link AssistantCardsController#populate(List, Map)}.
	 * If the player is the next one to play it shows an info alert.
	 *
	 * @param message the received message
	 */
	@Override
	public void handleMessage(AssistantCardUpdate message) {
		client.setAvailableCards(message.getAvailableCards().get(client.getUsername()));
		Platform.runLater(() -> {
			AssistantCardsController controller = (AssistantCardsController) app.getControllerForPopup(PopupName.ASSISTANT_CARDS);
			controller.populate(message.getAvailableCards().get(client.getUsername()), message.getPlayedCards());
		});
		super.handleMessage(message);
	}

	/**
	 * {@inheritDoc}
	 * Saves the board status.
	 * If the player is the next one to play it shows an info alert.
	 *
	 * @param message the received message
	 */
	@Override
	public void handleMessage(BoardUpdate message) {
		client.setBoardStatus(message.getStatus());
		Platform.runLater(() -> {
			updateInGameControllers();
			if (app.getCurrentScene() == SceneName.LOBBIES)
				app.changeScene(SceneName.SCHOOLBOARD);
		});
		super.handleMessage(message);
	}

	/**
	 * {@inheritDoc}
	 * Updates user selections calling {@link WaitingRoomController#updateSelections(Map, Map)}.
	 * If the players is the next one to choose, it prompts the selection popup.
	 *
	 * @param message the received message
	 */
	@Override
	public void handleMessage(UserSelectionUpdate message) {
		Platform.runLater(() -> {
			app.changeScene(SceneName.WAITING_ROOM);
			WaitingRoomController controller = (WaitingRoomController) app.getCurrentController();
			controller.updateSelections(message.getTowerColors(), message.getWizards());
			if (Objects.equals(client.getUsername(), message.getNextPlayer())) {
				controller.promptSelection(message.getAvailableTowerColors(), message.getAvailableWizards());
			}
		});
		super.handleMessage(message);
	}

	/**
	 * {@inheritDoc}
	 * Removes reconnection settings.
	 * Shows an alert with the username of the winner (or an appropriate message if it was a tie).
	 *
	 * @param message the received message
	 */
	@Override
	public void handleMessage(GameOverUpdate message) {
		client.removeReconnectSettings();

		String alertContent;
		if (Objects.equals(message.getWinner(), GameConstants.TIE))
			alertContent = "The game ends in a tie!";
		else
			alertContent = message.getWinner() + " is the winner of the game!";

		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Game over");
			alert.setHeaderText("Game over");
			alert.setContentText(alertContent);
			alert.showAndWait().ifPresent(type -> {
				client.setRunning(false);
				Platform.exit();
			});
		});
	}

	/**
	 * {@inheritDoc}
	 * Saves the initial board status.
	 * Changes the scene to {@code SCHOOLBOARD}.
	 *
	 * @param message the received message
	 */
	@Override
	public void handleMessage(InitialBoardStatus message) {
		client.setBoardStatus(message.getStatus());
		Platform.runLater(() -> {
			updateInGameControllers();
			app.changeScene(SceneName.SCHOOLBOARD);
		});
	}

	/**
	 * {@inheritDoc}
	 * Shows an info alert with information about the disconnection.
	 *
	 * @param message the received message
	 */
	@Override
	public void handleMessage(DisconnectionUpdate message) {
		String subject = message.getSubject();
		int numPlayers = message.getNumPlayers();
		boolean gameIdle = message.isGameIdle();

		Platform.runLater(() ->
				showInfo(subject + " has disconnected, "
						+ numPlayers + (numPlayers > 1 ? " players" : " player") + " currently connected"
						+ (gameIdle ? "\n\nGame idle" : ""))
		);
	}

	private void updateInGameControllers() {
		SchoolBoardController schoolBoard = (SchoolBoardController) app.getControllerForScene(SceneName.SCHOOLBOARD);
		schoolBoard.load();
		BoardController board = (BoardController) app.getControllerForScene(SceneName.BOARD);
		board.load();
		CharacterCardsController controller = (CharacterCardsController) app.getControllerForScene(SceneName.CHARACTER_CARDS);
		controller.load();
	}

	@Override
	public void quit() {
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Game over");
			alert.setHeaderText("Game over");
			alert.setContentText("An error has occurred, you have been disconnected");
			alert.showAndWait().ifPresent(type -> {
				client.setRunning(false);
				Platform.exit();
			});
		});
	}
}
