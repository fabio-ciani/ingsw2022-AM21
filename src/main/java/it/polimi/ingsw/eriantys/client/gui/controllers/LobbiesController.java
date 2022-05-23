package it.polimi.ingsw.eriantys.client.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class LobbiesController extends Controller {
	@FXML private TableView lobbies;
	@FXML private TableColumn identificators, creators, players, modes;

	public LobbiesController() {
		lobbies = new TableView<Lobby>();
		identificators = new TableColumn<Lobby, Integer>();
		creators = new TableColumn<Lobby, String>();
		players = new TableColumn<Lobby, String>();
		modes = new TableColumn<Lobby, String>();
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		/*
		lobbies.getColumns().stream()
				.forEach(x -> System.out.println(((TableColumn) x).getCellValueFactory()));
		*/

		identificators.setCellValueFactory(new PropertyValueFactory<>("id"));
		creators.setCellValueFactory(new PropertyValueFactory<>("creator"));
		players.setCellValueFactory(new PropertyValueFactory<>("counter"));
		modes.setCellValueFactory(new PropertyValueFactory<>("expert"));

		lobbies.getItems().add(new Lobby(1, "JohnDoe", "1/3", true));
		lobbies.getItems().add(new Lobby(2, "MarioRossi", "1/2", false));
	}

	private static class Lobby {
		private Integer id;
		private String creator;
		private String counter;
		private Boolean expert;

		private Lobby(Integer id, String creator, String counter, Boolean expert) {
			this.id = id;
			this.creator = creator;
			this.counter = counter;
			this.expert = expert;
		}

		public Integer getId() {
			return id;
		}

		public String getCreator() {
			return creator;
		}

		public String getCounter() {
			return counter;
		}

		public Boolean getExpert() {
			return expert;
		}
	}
}
