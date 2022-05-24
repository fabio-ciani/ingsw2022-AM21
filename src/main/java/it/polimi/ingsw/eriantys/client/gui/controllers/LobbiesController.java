package it.polimi.ingsw.eriantys.client.gui.controllers;

import it.polimi.ingsw.eriantys.controller.GameInfo;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class LobbiesController extends Controller {
	@FXML private TableView<Lobby> lobbies;
	@FXML private TableColumn<Lobby, Integer> identifiers;
	@FXML private TableColumn<Lobby, String> creators;
	@FXML private TableColumn<Lobby, String> players;
	@FXML private TableColumn<Lobby, String> modes;
	@FXML private Button create;
	@FXML private Button list;

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

		create.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			System.out.println("confirm - mouse clicked");
			Lobby selectedLobby = lobbies.getSelectionModel().getSelectedItem();
			if (selectedLobby == null)
				showError.accept("Please select a lobby!");
			else
				client.joinLobby(Integer.toString(selectedLobby.getId()));
		});

		list.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			System.out.println("refresh - mouse clicked");
			client.askLobbies();
		});
	}

	@Override
	public void onChangeScene() {
		if (client.hasReconnectSettings()) {
			showInfo.accept("Reconnection available, type /r or /reconnect to join");
		}
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
