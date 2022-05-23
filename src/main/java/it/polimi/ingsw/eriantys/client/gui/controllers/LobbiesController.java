package it.polimi.ingsw.eriantys.client.gui.controllers;

import it.polimi.ingsw.eriantys.controller.GameInfo;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class LobbiesController extends Controller implements Initializable {
	@FXML private TableView<Lobby> lobbies;
	@FXML private TableColumn<Lobby, Integer> identifiers;
	@FXML private TableColumn<Lobby, String> creators;
	@FXML private TableColumn<Lobby, String> players;
	@FXML private TableColumn<Lobby, String> modes;
	@FXML private Button confirm;
	@FXML private Button refresh;

	public LobbiesController() {
		lobbies = new TableView<>();
		identifiers = new TableColumn<>();
		creators = new TableColumn<>();
		players = new TableColumn<>();
		modes = new TableColumn<>();
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		identifiers.setCellValueFactory(new PropertyValueFactory<>("id"));
		creators.setCellValueFactory(new PropertyValueFactory<>("creator"));
		players.setCellValueFactory(new PropertyValueFactory<>("counter"));
		modes.setCellValueFactory(new PropertyValueFactory<>("expert"));

		confirm.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			System.out.println("confirm - mouse clicked");
			Lobby selectedLobby = lobbies.getSelectionModel().getSelectedItem();
			/*
			if (selectedLobby == null)
				ui.showError("Please select a lobby!");
			else
				client.joinLobby(selectedLobby.getId());
			 */
		});

		refresh.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			System.out.println("refresh - mouse clicked");
			// client.askLobbies();
		});

		// TODO client.askLobbies();
		// TODO upon receiving AvailableLobbies, check if this scene is active and populate the table
		/*
		lobbies.getItems().add(new Lobby(1, "JohnDoe", "1/3", true));
		lobbies.getItems().add(new Lobby(2, "MarioRossi", "1/2", false));
	 */
	}

	public void updateLobbies(List<GameInfo> availableLobbies) {
		ObservableList<Lobby> content = lobbies.getItems();
		content.clear();
		for (GameInfo l : availableLobbies) {
			String counter = l.getCurrentPlayers() + "/" + l.getLobbySize();
			content.add(new Lobby(l.getGameId(), l.getCreator(), counter, l.isExpertMode()));
		}
	}

	private record Lobby(Integer id, String creator, String counter, Boolean expert) {
		public int getId() {
			return id;
		}
	}
}
