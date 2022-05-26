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
		LobbiesController controller = (LobbiesController) app.getControllerForScene(SceneName.LOBBIES);
		controller.updateLobbies(message.getLobbies());
	}

	@Override
	public void handleMessage(LobbyUpdate message) {
		WaitingRoomController controller = (WaitingRoomController) app.getControllerForScene(SceneName.WAITING_ROOM);
		controller.updatePlayers(message.getPlayers());
	}

	@Override
	public void handleMessage(AssistantCardUpdate message) {
		client.setAvailableCards(message.getAvailableCards().get(client.getUsername()));
		AssistantCardsController controller = (AssistantCardsController) app.getControllerForPopup(PopupName.ASSISTANT_CARDS);
		controller.populate(message.getAvailableCards().get(client.getUsername()), message.getPlayedCards());
	}

	@Override
	public void handleMessage(BoardUpdate message) {
		client.setBoardStatus(message.getStatus());
		// TODO: trigger current controller update -> switch?
		Platform.runLater(() -> {
			SchoolBoardController schoolBoard = (SchoolBoardController) app.getControllerForScene(SceneName.SCHOOLBOARD);
			schoolBoard.load();
			BoardController board = (BoardController) app.getControllerForScene(SceneName.BOARD);
			board.load();
		});
	}

	@Override
	public void handleMessage(CharacterCardUpdate message) {

	}

	@Override
	public void handleMessage(UserSelectionUpdate message) {
		if (app.getCurrentScene() != SceneName.WAITING_ROOM) return;
		WaitingRoomController controller = (WaitingRoomController) app.getCurrentController();
		controller.updateSelections(message.getTowerColors(), message.getWizards());
		if (isNextPlayer(message.getNextPlayer()))
			controller.promptSelection(message.getAvailableTowerColors(), message.getAvailableWizards());
	}

	@Override
	public void handleMessage(GameOverUpdate message) {
		if (Objects.equals(message.getWinner(), GameConstants.TIE))
			showInfo("The game ends in a tie!");
		else showInfo(message.getWinner() + " is the winner of the game!");
	}

	@Override
	public void handleMessage(InitialBoardStatus message) {
		client.setBoardStatus(message.getStatus());
		Platform.runLater(() -> {
			SchoolBoardController schoolBoard = (SchoolBoardController) app.getControllerForScene(SceneName.SCHOOLBOARD);
			schoolBoard.load();
			BoardController board = (BoardController) app.getControllerForScene(SceneName.BOARD);
			board.load();
			CharacterCardsController controller = (CharacterCardsController) app.getControllerForScene(SceneName.CHARACTER_CARDS);
			controller.load();
			app.changeScene(SceneName.SCHOOLBOARD);
		});
	}

	@Override
	public void handleMessage(ReconnectionUpdate message) {

	}

	@Override
	public void handleMessage(DisconnectionUpdate message) {

	}

	private boolean isNextPlayer(String username) {
		if (!Objects.equals(client.getUsername(), username)) {
			showInfo(String.format("%s is playing...", username));
			return false;
		}
		return true;
	}
}
