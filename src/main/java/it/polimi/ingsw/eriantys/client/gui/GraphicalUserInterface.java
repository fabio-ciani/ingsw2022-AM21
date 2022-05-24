package it.polimi.ingsw.eriantys.client.gui;

import it.polimi.ingsw.eriantys.client.UserInterface;
import it.polimi.ingsw.eriantys.client.gui.controllers.LobbiesController;
import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.messages.server.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.Map;

public class GraphicalUserInterface extends UserInterface {
	private GraphicalApplication app;

	public GraphicalUserInterface() throws IOException {
		super();
	}

	@Override
	public void showInfo(String details) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Info");
		alert.setHeaderText("Info");
		alert.setContentText(details);
		alert.show();
	}

	@Override
	public void showError(String details) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText("Error");
		alert.setContentText(details);
		alert.show();
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
		/*
		if (client.hasReconnectSettings()) {
			showInfo("Reconnection available, type /r or /reconnect to join");
		}
		 */
	}

	@Override
	public void handleMessage(AcceptedJoinLobby message) {
		client.setGameId(message.getGameId());
		client.putReconnectSettings(message);
		app.changeScene(SceneName.WAITING_ROOM);
	}

	@Override
	public void handleMessage(AcceptedLeaveLobby message) {
		client.removeReconnectSettings();
		app.changeScene(SceneName.LOBBIES);
	}

	@Override
	public void handleMessage(HelpResponse message) {
		handleMessage((Message) message);
	}

	@Override
	public void handleMessage(AvailableLobbies message) {
		if (app.getCurrentScene() != SceneName.LOBBIES) return;
		LobbiesController controller = (LobbiesController) app.getCurrentController();
		controller.updateLobbies(message.getLobbies());
	}

	@Override
	public void handleMessage(LobbyUpdate message) {

	}

	@Override
	public void handleMessage(AssistantCardUpdate message) {
		client.setAvailableCards(message.getAvailableCards().get(client.getUsername()));
		Map<String, String> playedCards = message.getPlayedCards();
		for (String player : playedCards.keySet()) {
			String cardName = playedCards.get(player);
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Played assistant card");
			alert.setHeaderText(player + " played the " + cardName + " card");
			ImageView image = new ImageView();
			image.setPreserveRatio(true);
			image.setFitWidth(102);
			image.setImage(new Image(getClass().getResource("/graphics/AssistantCards/" + cardName + ".png").toExternalForm()));
			alert.setGraphic(image);
		}
	}

	@Override
	public void handleMessage(BoardUpdate message) {

	}

	@Override
	public void handleMessage(CharacterCardUpdate message) {

	}

	@Override
	public void handleMessage(UserSelectionUpdate message) {

	}

	@Override
	public void handleMessage(GameOverUpdate message) {

	}

	@Override
	public void handleMessage(InitialBoardStatus message) {

	}

	@Override
	public void handleMessage(ReconnectionUpdate message) {

	}

	@Override
	public void handleMessage(DisconnectionUpdate message) {

	}
}
