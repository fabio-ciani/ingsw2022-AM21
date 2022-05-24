package it.polimi.ingsw.eriantys.client.gui.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class WaitingRoomController extends Controller {
	@FXML private TableView<Player> info;
	@FXML private TableColumn<Player, String> usernames;
	@FXML private TableColumn<Player, String> tower_colors;
	@FXML private TableColumn<Player, String> wizards;
	@FXML private Button leave;

	public WaitingRoomController() {
		info = new TableView<>();
		usernames = new TableColumn<>();
		tower_colors = new TableColumn<>();
		wizards = new TableColumn<>();
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		usernames.setCellValueFactory(c -> c.getValue().usernameProperty());
		tower_colors.setCellValueFactory(c -> c.getValue().towerColorProperty());
		wizards.setCellValueFactory(c -> c.getValue().wizardProperty());

		leave.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			client.leaveLobby();
			event.consume();
		});
	}

	public void updatePlayers(List<String> players) {
		ObservableList<Player> content = info.getItems();
		content.clear();
		for (String p : players)
			content.add(new Player(p));
	}

	public void updateSelections(Map<String, String> towerColors, Map<String, String> wizards) {
		ObservableList<Player> content = info.getItems();
		for (Player player : content) {
			String username = player.username;
			if (towerColors.containsKey(username)) {
				content.remove(player);
				content.add(new Player(username, towerColors.get(username), wizards.get(username)));
			}
		}
	}

	public void promptSelection(List<String> towerColors, List<String> wizards) {
		// TODO popup
	}

	private static class Player {
		private final String username;
		private final String towerColor;
		private final String wizard;

		public Player(String username) {
			this.username = username;
			this.towerColor = null;
			this.wizard = null;
		}

		public Player(String username, String towerColor, String wizard) {
			this.username = username;
			this.towerColor = towerColor;
			this.wizard = wizard;
		}

		public ObservableValue<String> usernameProperty() {
			StringProperty res = new SimpleStringProperty();
			res.setValue(username);
			return res;
		}

		public ObservableValue<String> towerColorProperty() {
			StringProperty res = new SimpleStringProperty();
			res.setValue(towerColor);
			return res;
		}

		public ObservableValue<String> wizardProperty() {
			StringProperty res = new SimpleStringProperty();
			res.setValue(wizard);
			return res;
		}
	}
}
