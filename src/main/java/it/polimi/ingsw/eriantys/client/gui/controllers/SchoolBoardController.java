package it.polimi.ingsw.eriantys.client.gui.controllers;

import it.polimi.ingsw.eriantys.client.gui.SceneName;
import it.polimi.ingsw.eriantys.model.BoardStatus;
import it.polimi.ingsw.eriantys.model.Color;
import it.polimi.ingsw.eriantys.model.TowerColor;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

public class SchoolBoardController extends Controller {
	@FXML private ImageView schoolboard;
	@FXML private Text username;
	@FXML private GridPane dr_green;
	@FXML private GridPane dr_red;
	@FXML private GridPane dr_yellow;
	@FXML private GridPane dr_pink;
	@FXML private GridPane dr_blue;
	@FXML private GridPane professors;
	@FXML private GridPane entrance;
	@FXML private GridPane towers;
	@FXML private ImageView coins;
	@FXML private Text c_text;
	@FXML private Button characters;
	@FXML private Button board;
	@FXML private ChoiceBox<String> sb_username;
	@FXML private Button sb_button;

	private String currentUsername;
	private Map<String, Image> studentImages;
	private Map<String, Image> professorImages;
	private Map<String, Image> towerImages;
	private Map<String, Integer> towerWidths;
	private Image towerSlot;
	private Image coinImage;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		studentImages = new HashMap<>();
		professorImages = new HashMap<>();
		towerImages = new HashMap<>();
		towerWidths = new HashMap<>();
		initImages();
		characters.setOnMouseClicked(event -> {
			app.changeScene(SceneName.CHARACTER_CARDS);
			event.consume();
		});
		board.setOnMouseClicked(event -> {
			app.changeScene(SceneName.BOARD);
			event.consume();
		});
		sb_button.setOnMouseClicked(event -> {
			currentUsername = sb_username.getSelectionModel().getSelectedItem();
			onChangeScene();
		});
	}

	@Override
	public void onChangeScene() {
		BoardStatus boardStatus = client.getBoardStatus();
		if (currentUsername == null) {
			currentUsername = client.getUsername();
		}

		drawUsername();

		drawEntrance(boardStatus.getPlayerEntrances().get(currentUsername));

		drawDiningRoom(boardStatus.getPlayerDiningRooms().get(currentUsername));

		Map<String, String> professorOwnerships = boardStatus.getProfessors();
		drawProfessors(professorOwnerships.keySet().stream()
				.filter(k -> Objects.equals(professorOwnerships.get(k), currentUsername))
				.toList());

		drawTowerSlots();

		drawTowers(boardStatus.getPlayerTowers().get(currentUsername), boardStatus.getPlayerTowerColors().get(currentUsername));

		drawCoins(boardStatus.getPlayerCoins().get(currentUsername));

		setUsernames(boardStatus.getPlayers());
	}

	private void drawUsername() {
		username.setText(currentUsername + "’s school board");
	}

	private void drawEntrance(Map<String, Integer> entranceStudents) {
		Iterator<ImageView> entranceIterator = entrance.getChildren().stream()
				.filter(n -> n instanceof ImageView)
				.map(n -> (ImageView) n)
				.iterator();
		for (String color : entranceStudents.keySet()) {
			for (int i = 0; i < entranceStudents.get(color); i++) {
				ImageView imageView = entranceIterator.next();
				imageView.setImage(studentImages.get(color));
				imageView.setVisible(true);
			}
		}
		while (entranceIterator.hasNext()) {
			entranceIterator.next().setVisible(false);
		}
	}

	private void drawDiningRoom(Map<String, Integer> diningRoomStudents) {
		for (String color : diningRoomStudents.keySet()) {
			Iterator<ImageView> diningRoomIterator = getDiningRoomGridPane(color).getChildren().stream()
					.filter(n -> n instanceof ImageView)
					.map(n -> (ImageView) n)
					.iterator();
			for (int i = 0; i < diningRoomStudents.get(color); i++) {
				ImageView imageView = diningRoomIterator.next();
				if (imageView.getImage() == null) {
					imageView.setImage(studentImages.get(color));
				}
				imageView.setVisible(true);
			}
			while (diningRoomIterator.hasNext()) {
				diningRoomIterator.next().setVisible(false);
			}
		}
	}

	private void drawProfessors(List<String> ownedProfessors) {
		for (String color : Color.stringLiterals()) {
			Consumer<ImageView> consumer;
			if (ownedProfessors.contains(color)) {
				consumer = i -> {
					if (i.getImage() == null) {
						i.setImage(professorImages.get(color));
					}
					i.setVisible(true);
				};
			} else {
				consumer = i -> i.setVisible(false);
			}
			professors.getChildren().stream()
					.filter(n -> Objects.equals(n.getId(), "p_" + color.toLowerCase()))
					.findAny()
					.map(n -> (ImageView) n)
					.ifPresent(consumer);
		}
	}

	private void drawTowerSlots() {
		towers.getChildren().stream()
				.filter(n -> n instanceof ImageView)
				.map(n -> (ImageView) n)
				.filter(i -> i.getId() == null && i.getImage() == null)
				.forEach(i -> {
					i.setImage(towerSlot);
					i.setVisible(true);
				});
	}

	private void drawTowers(int amount, String color) {
		Iterator<ImageView> towerIterator = towers.getChildren().stream()
				.filter(n -> n instanceof ImageView)
				.map(n -> (ImageView) n)
				.iterator();
		for (int i = 0; i < amount; i++) {
			ImageView imageView;
			do {
				imageView = towerIterator.next();
			} while (imageView.getId() == null);
			if (imageView.getImage() == null) {
				imageView.setImage(towerImages.get(color));
				imageView.setFitWidth(towerWidths.get(color));
			}
			imageView.setVisible(true);
		}
		while (towerIterator.hasNext()) {
			ImageView imageView = towerIterator.next();
			if (imageView.getId() != null) {
				imageView.setVisible(false);
			}
		}
	}

	private void drawCoins(Integer amount) {
		if (coins.getImage() == null) {
			coins.setImage(coinImage);
			coins.setVisible(true);
		}
		c_text.setText(amount.toString());
	}

	private void setUsernames(List<String> usernames) {
		List<String> items = sb_username.getItems();
		items.clear();
		items.addAll(usernames);
	}

	private void initImages() {
		studentImages.put(Color.GREEN.name(), new Image(getClass().getResource("/graphics/Students/GreenStudent.png").toExternalForm()));
		studentImages.put(Color.RED.name(), new Image(getClass().getResource("/graphics/Students/RedStudent.png").toExternalForm()));
		studentImages.put(Color.YELLOW.name(), new Image(getClass().getResource("/graphics/Students/YellowStudent.png").toExternalForm()));
		studentImages.put(Color.PINK.name(), new Image(getClass().getResource("/graphics/Students/PinkStudent.png").toExternalForm()));
		studentImages.put(Color.BLUE.name(), new Image(getClass().getResource("/graphics/Students/BlueStudent.png").toExternalForm()));
		professorImages.put(Color.GREEN.name(), new Image(getClass().getResource("/graphics/Professors/GreenProfessor.png").toExternalForm()));
		professorImages.put(Color.RED.name(), new Image(getClass().getResource("/graphics/Professors/RedProfessor.png").toExternalForm()));
		professorImages.put(Color.YELLOW.name(), new Image(getClass().getResource("/graphics/Professors/YellowProfessor.png").toExternalForm()));
		professorImages.put(Color.PINK.name(), new Image(getClass().getResource("/graphics/Professors/PinkProfessor.png").toExternalForm()));
		professorImages.put(Color.BLUE.name(), new Image(getClass().getResource("/graphics/Professors/BlueProfessor.png").toExternalForm()));
		towerImages.put(TowerColor.BLACK.name(), new Image(getClass().getResource("/graphics/Towers/BlackTower.png").toExternalForm()));
		towerImages.put(TowerColor.GREY.name(), new Image(getClass().getResource("/graphics/Towers/GreyTower.png").toExternalForm()));
		towerImages.put(TowerColor.WHITE.name(), new Image(getClass().getResource("/graphics/Towers/WhiteTower.png").toExternalForm()));
		towerWidths.put(TowerColor.BLACK.name(), 54);
		towerWidths.put(TowerColor.GREY.name(), 58);
		towerWidths.put(TowerColor.WHITE.name(), 50);
		coinImage = new Image(getClass().getResource("/graphics/Coin.png").toExternalForm());
		towerSlot = new Image(getClass().getResource("/graphics/Circle.png").toExternalForm());
		schoolboard.setImage(new Image(getClass().getResource("/graphics/SchoolBoard.png").toExternalForm()));
	}

	private GridPane getDiningRoomGridPane(String colorName) {
		Color color = Color.valueOf(colorName);
		switch (color) {
			case GREEN -> {
				return dr_green;
			}
			case RED -> {
				return dr_red;
			}
			case YELLOW -> {
				return dr_yellow;
			}
			case PINK -> {
				return dr_pink;
			}
			case BLUE -> {
				return dr_blue;
			}
			default -> {
				return null;
			}
		}
	}
}
