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
import java.util.Objects;

public class GraphicalUserInterface extends UserInterface {
	private GraphicalApplication app;

	public GraphicalUserInterface() throws IOException {
		super();
	}

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

	@Override
	public void init() {
		app = GraphicalApplication.getInstance();
	}

	@Override
	public void run() {
		GraphicalApplication.setClient(client);
		GraphicalApplication.setShowInfo(this::showInfo);
		GraphicalApplication.setShowError(this::showError);
		Application.launch(GraphicalApplication.class);
	}

	@Override
	public void handleMessage(Accepted message) {
		System.out.println("Ok");
	}

	@Override
	public void handleMessage(AcceptedUsername message) {
		client.setUsername(message.getUsername());
		Platform.runLater(() -> app.changeScene(SceneName.LOBBIES));
	}

	@Override
	public void handleMessage(AcceptedJoinLobby message) {
		client.setGameId(message.getGameId());
		client.putReconnectSettings(message);
		Platform.runLater(() -> app.changeScene(SceneName.WAITING_ROOM));
	}

	@Override
	public void handleMessage(AcceptedLeaveLobby message) {
		client.removeReconnectSettings();
		Platform.runLater(() -> app.changeScene(SceneName.LOBBIES));
	}

	@Override
	public void handleMessage(HelpResponse message) {
		handleMessage((Message) message);
	}

	@Override
	public void handleMessage(AvailableLobbies message) {
		Platform.runLater(() -> {
			LobbiesController controller = (LobbiesController) app.getControllerForScene(SceneName.LOBBIES);
			controller.updateLobbies(message.getLobbies());
		});
	}

	@Override
	public void handleMessage(LobbyUpdate message) {
		Platform.runLater(() -> {
			WaitingRoomController controller = (WaitingRoomController) app.getControllerForScene(SceneName.WAITING_ROOM);
			controller.updatePlayers(message.getPlayers());
		});
	}

	@Override
	public void handleMessage(AssistantCardUpdate message) {
		client.setAvailableCards(message.getAvailableCards().get(client.getUsername()));
		Platform.runLater(() -> {
			AssistantCardsController controller = (AssistantCardsController) app.getControllerForPopup(PopupName.ASSISTANT_CARDS);
			controller.populate(message.getAvailableCards().get(client.getUsername()), message.getPlayedCards());
		});
	}

	@Override
	public void handleMessage(BoardUpdate message) {
		client.setBoardStatus(message.getStatus());
		// TODO: update assistant cards view
		// TODO: trigger current controller update -> switch?
		Platform.runLater(this::updateInGameControllers);
	}

	@Override
	public void handleMessage(CharacterCardUpdate message) {

	}

	@Override
	public void handleMessage(UserSelectionUpdate message) {
		Platform.runLater(() -> {
			if (app.getCurrentScene() != SceneName.WAITING_ROOM) return;
			WaitingRoomController controller = (WaitingRoomController) app.getCurrentController();
			controller.updateSelections(message.getTowerColors(), message.getWizards());
			if (isNextPlayer(message.getNextPlayer()))
				controller.promptSelection(message.getAvailableTowerColors(), message.getAvailableWizards());
		});
	}

	@Override
	public void handleMessage(GameOverUpdate message) {
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
				// System.exit(0);
			});
		});
	}

	@Override
	public void handleMessage(InitialBoardStatus message) {
		client.setBoardStatus(message.getStatus());
		Platform.runLater(() -> {
			updateInGameControllers();
			app.changeScene(SceneName.SCHOOLBOARD);
		});
	}

	@Override
	public void handleMessage(ReconnectionUpdate message) {
		Platform.runLater(() -> app.changeScene(SceneName.SCHOOLBOARD));
	}

	@Override
	public void handleMessage(DisconnectionUpdate message) {
		String subject = message.getSubject();
		int numPlayers = message.getNumPlayers();
		boolean gameIdle = message.isGameIdle();

		Platform.runLater(() ->
				showInfo(subject + " has disconnected, " + numPlayers + " players currently connected"
				+ (gameIdle ? "\nGame idle" : ""))
		);
	}

	private boolean isNextPlayer(String username) {
		if (username != null && !Objects.equals(client.getUsername(), username)) {
			showInfo(String.format("%s is playing...", username));
			return false;
		}
		return true;
	}

	private void updateInGameControllers() {
		SchoolBoardController schoolBoard = (SchoolBoardController) app.getControllerForScene(SceneName.SCHOOLBOARD);
		schoolBoard.load();
		BoardController board = (BoardController) app.getControllerForScene(SceneName.BOARD);
		board.load();
		CharacterCardsController controller = (CharacterCardsController) app.getControllerForScene(SceneName.CHARACTER_CARDS);
		controller.load();
	}
}
