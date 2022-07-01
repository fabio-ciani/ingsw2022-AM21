package it.polimi.ingsw.eriantys.client.gui.controllers;

import it.polimi.ingsw.eriantys.client.gui.PopupName;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class WaitingRoomController extends Controller {
	@FXML private BorderPane pane;
	@FXML private Text lobby;
	@FXML private TableView<Player> info;
	@FXML private TableColumn<Player, String> usernames;
	@FXML private TableColumn<Player, String> tower_colors;
	@FXML private TableColumn<Player, String> wizards;
	@FXML private Button leave;
	private String towerColor;
	private List<String> availableWizards;
	private String wizard;

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

	@Override
	public Pane getTopLevelPane() {
		return pane;
	}

	public void setText(String id) {
		lobby.setText("Lobby #" + id);
	}

	public void updatePlayers(List<String> players) {
		ObservableList<Player> content = info.getItems();
		content.clear();
		for (String p : players)
			content.add(new Player(p));
	}

	public void updateSelections(Map<String, String> towerColors, Map<String, String> wizards) {
		ObservableList<Player> content = info.getItems();
		if (content.isEmpty()) {
			for (String username : towerColors.keySet()) {
				content.add(new Player(username, towerColors.get(username), wizards.get(username)));
			}
			return;
		}
		List<Player> staticContent = info.getItems().stream().toList();
		for (Player player : staticContent) {
			String username = player.username;
			if (towerColors.containsKey(username)) {
				content.remove(player);
				content.add(new Player(username, towerColors.get(username), wizards.get(username)));
			}
		}
	}

	public void promptSelection(List<String> towerColors, List<String> wizards) {
		availableWizards = wizards;
		TowersController controller = (TowersController) app.getControllerForPopup(PopupName.TOWERS);
		controller.populate(towerColors);
		Platform.runLater(() -> app.showStickyPopup(PopupName.TOWERS));
	}

	public void setTowerColor(String towerColor) {
		// this.towerColor = towerColor;
		client.setTowerColor(towerColor);
		WizardsController controller = (WizardsController) app.getControllerForPopup(PopupName.WIZARDS);
		controller.populate(availableWizards);
		app.showStickyPopup(PopupName.WIZARDS);
		//Platform.runLater(() -> app.showStickyPopup(PopupName.WIZARDS));
	}

	public void setWizard(String wizard) {
		// this.wizard = wizard;
		client.setWizard(wizard);
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
