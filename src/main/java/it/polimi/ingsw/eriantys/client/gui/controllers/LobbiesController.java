package it.polimi.ingsw.eriantys.client.gui.controllers;

import it.polimi.ingsw.eriantys.client.gui.SceneName;
import it.polimi.ingsw.eriantys.controller.GameInfo;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * A class representing the controller for the {@code LOBBIES} scene.
 * @see SceneName#LOBBIES
 * @see javafx.scene.Scene
 */
public class LobbiesController extends Controller {
	@FXML private BorderPane pane;
	@FXML private TableView<Lobby> lobbies;
	@FXML private TableColumn<Lobby, Integer> identifiers;
	@FXML private TableColumn<Lobby, String> creators;
	@FXML private TableColumn<Lobby, String> players;
	@FXML private TableColumn<Lobby, String> modes;
	@FXML private ChoiceBox<Integer> c_players;
	@FXML private CheckBox c_expert;
	@FXML private Button join;
	@FXML private Button create;
	@FXML private Button list;

	private static final int TWO_PLAYERS = 2;
	private static final int THREE_PLAYERS = 3;

	public LobbiesController() {
		lobbies = new TableView<>();
		identifiers = new TableColumn<>();
		creators = new TableColumn<>();
		players = new TableColumn<>();
		modes = new TableColumn<>();
	}

	/**
	 * Gets all the child nodes representing the elements of the scene from the FXML.
	 * Associates the event handlers with the buttons on the scene.
	 */
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		lobbies.setRowFactory(t -> {
			TableRow<Lobby> tableRow = new TableRow<>();
			tableRow.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
				int count = event.getClickCount();
				if (count == 2 && !tableRow.isEmpty()) {
					client.joinLobby(Integer.toString(tableRow.getItem().id));
				}
				event.consume();
			});
			return tableRow;
		});

		identifiers.setCellValueFactory(c -> c.getValue().idProperty());
		creators.setCellValueFactory(c -> c.getValue().creatorProperty());
		players.setCellValueFactory(c -> c.getValue().counterProperty());
		modes.setCellValueFactory(c -> c.getValue().modeProperty());

		c_players.getItems().addAll(TWO_PLAYERS, THREE_PLAYERS);

		join.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			Lobby selectedLobby = lobbies.getSelectionModel().getSelectedItem();
			if (selectedLobby == null)
				showError.accept("Please select a lobby!");
			else
				client.joinLobby(Integer.toString(selectedLobby.id));
			event.consume();
		});

		create.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			Integer numPlayers = c_players.getSelectionModel().getSelectedItem();
			Boolean expertMode = c_expert.isSelected();

			if (numPlayers == null)
				showError.accept("Please select the number of players!");
			else
				client.createLobby(numPlayers.toString(), expertMode.toString());

			event.consume();
		});

		list.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			client.askLobbies();
			event.consume();
		});
	}

	/**
	 * {@inheritDoc}
	 * Asks for the available lobbies list and shows a reconnection message if related settings are found.
	 */
	@Override
	public void onChangeScene() {
		client.askLobbies();
		if (client.hasReconnectSettings())
			showReconnect();
	}

	@Override
	public Pane getTopLevelPane() {
		return pane;
	}

	/**
	 * Gets the information about lobbies from passed parameter and shows the list in the scene.
	 * @param availableLobbies the lobbies inside which the user could join
	 */
	public void updateLobbies(List<GameInfo> availableLobbies) {
		ObservableList<Lobby> content = lobbies.getItems();
		content.clear();
		for (GameInfo l : availableLobbies) {
			String counter = l.getCurrentPlayers() + "/" + l.getLobbySize();
			content.add(new Lobby(l.getGameId(), l.getCreator(), counter, l.isExpertMode()));
		}
	}

	private void showReconnect() {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

		alert.setTitle("Confirmation");
		alert.setHeaderText("Reconnection available");
		alert.setContentText("Do you want to reconnect?");
		alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

		alert.showAndWait().ifPresent(type -> {
			if (type == ButtonType.YES) client.sendReconnect();
		});
	}

	private record Lobby(int id, String creator, String counter, boolean expert) {
		public ObservableValue<Integer> idProperty() {
				IntegerProperty res = new SimpleIntegerProperty();
				res.setValue(id);
				return res.asObject();
			}

			public ObservableValue<String> creatorProperty() {
				StringProperty res = new SimpleStringProperty();
				res.setValue(creator);
				return res;
			}

			public ObservableValue<String> counterProperty() {
				StringProperty res = new SimpleStringProperty();
				res.setValue(counter);
				return res;
			}

			public ObservableValue<String> modeProperty() {
				StringProperty res = new SimpleStringProperty();
				res.setValue(expert ? "Expert" : "Simplified");
				return res;
			}
		}
}
