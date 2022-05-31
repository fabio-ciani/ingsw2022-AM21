package it.polimi.ingsw.eriantys.client.gui.controllers;

import it.polimi.ingsw.eriantys.client.gui.PopupName;
import it.polimi.ingsw.eriantys.client.gui.SceneName;
import it.polimi.ingsw.eriantys.model.BoardStatus;
import it.polimi.ingsw.eriantys.model.Color;
import it.polimi.ingsw.eriantys.model.GameConstants;
import it.polimi.ingsw.eriantys.model.TowerColor;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

public class SchoolBoardController extends Controller {
	@FXML private BorderPane pane;
	@FXML private Group schoolboard;
	@FXML private ImageView background;
	@FXML private Text username;
	@FXML private ImageView selected_img;
	@FXML private Text selected_text;
	@FXML private GridPane professors;
	@FXML private GridPane entrance;
	@FXML private GridPane towers;
	@FXML private ImageView coins;
	@FXML private Text c_text;
	@FXML private Button assistants;
	@FXML private Button characters;
	@FXML private Button board;
	@FXML private ChoiceBox<String> sb_username;
	@FXML private Button sb_button;

	private String currentUsername;
	private String selected;

	private Map<String, GridPane> diningroomPanes;

	private Map<String, Image> studentImages;
	private Map<String, Image> professorImages;
	private Map<String, Image> towerImages;
	private Map<String, Integer> towerSizes;
	private Image towerSlot;
	private Image coinImage;
	private Map<String, Image> characterMiniatures;

	private EventHandler<MouseEvent> selectSource;
	private EventHandler<MouseEvent> selectDestination;
	private EventHandler<MouseEvent> selectColorForCharacterCard;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		studentImages = new HashMap<>();
		professorImages = new HashMap<>();
		towerImages = new HashMap<>();
		towerSizes = new HashMap<>();
		diningroomPanes = new HashMap<>();

		for (String color : Color.stringLiterals()) {
			diningroomPanes.put(color, (GridPane) schoolboard.getChildren().stream()
					.filter(n -> Objects.equals(n.getId(), "dr_" + color.toLowerCase()))
					.findAny().orElse(null));
		}

		initImages();
		initEventHandlers();
	}

	@Override
	public Pane getTopLevelPane() {
		return pane;
	}

	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
		drawSelected();
	}

	@Override
	public void onChangeScene() {
		currentUsername = client.getUsername();
		drawSelected();
	}

	public void load() {
		if (currentUsername == null) currentUsername = client.getUsername();
		BoardStatus boardStatus = client.getBoardStatus();
		Map<String, String> professorOwnerships = boardStatus.getProfessors();
		List<String> ownedProfessors = professorOwnerships.keySet().stream()
				.filter(k -> Objects.equals(professorOwnerships.get(k), currentUsername))
				.toList();

		drawUsername();
		drawEntrance(boardStatus.getPlayerEntrances().get(currentUsername));
		drawDiningRoom(boardStatus.getPlayerDiningRooms().get(currentUsername));
		drawProfessors(ownedProfessors);
		drawTowerSlots();
		drawTowers(boardStatus.getPlayerTowers().get(currentUsername), boardStatus.getPlayerTowerColors().get(currentUsername));
		drawCoins(boardStatus.getPlayerCoins().get(currentUsername));
		drawSelected();

		setUsernames(boardStatus.getPlayers());
		setEventHandlers();

		if (boardStatus.getCharacterCards() == null) {
			characters.setDisable(true);
		}
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
			Iterator<ImageView> diningRoomIterator = diningroomPanes.get(color).getChildren().stream()
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
			imageView.setImage(towerImages.get(color));
			imageView.setFitWidth(towerSizes.get(color));
			imageView.setFitHeight(towerSizes.get(color));
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
		if (amount == null) {
			coins.setVisible(false);
			c_text.setVisible(false);
			return;
		}
		if (coins.getImage() == null) {
			coins.setImage(coinImage);
			coins.setVisible(true);
		}
		c_text.setText(amount.toString());
	}

	private void drawSelected() {
		if (selected != null && Objects.equals(currentUsername, client.getUsername())) {
			Image image = studentImages.get(selected);
			if (image == null && characterMiniatures != null) {
				image = characterMiniatures.get(selected);
			}
			selected_img.setImage(image);
			selected_img.setVisible(true);
			selected_text.setVisible(true);
		} else {
			selected_img.setVisible(false);
			selected_text.setVisible(false);
		}
	}

	private void setUsernames(List<String> usernames) {
		List<String> items = sb_username.getItems();
		items.clear();
		items.addAll(usernames);
	}

	protected void setEventHandlers() {
		boolean isCurrentPlayer = Objects.equals(currentUsername, client.getUsername());
		boolean characterCardSelected = selected != null && !Color.stringLiterals().contains(selected.toUpperCase());
		entrance.getChildren().stream()
				.filter(n -> n instanceof ImageView)
				.map(n -> (ImageView) n)
				.forEach(i -> i.setOnMouseClicked(isCurrentPlayer ? (characterCardSelected ? selectColorForCharacterCard : selectSource) : Event::consume));
		for (String color : Color.stringLiterals()) {
			diningroomPanes.get(color).setOnMouseClicked(isCurrentPlayer ? (characterCardSelected ? selectColorForCharacterCard : selectDestination) : Event::consume);
		}
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

		towerImages.put(TowerColor.WHITE.name(), new Image(getClass().getResource("/graphics/Towers/WhiteTower.png").toExternalForm()));
		towerImages.put(TowerColor.BLACK.name(), new Image(getClass().getResource("/graphics/Towers/BlackTower.png").toExternalForm()));
		towerImages.put(TowerColor.GREY.name(), new Image(getClass().getResource("/graphics/Towers/GreyTower.png").toExternalForm()));

		towerSizes.put(TowerColor.WHITE.name(), 50);
		towerSizes.put(TowerColor.BLACK.name(), 54);
		towerSizes.put(TowerColor.GREY.name(), 58);

		coinImage = new Image(getClass().getResource("/graphics/Coin.png").toExternalForm());
		towerSlot = new Image(getClass().getResource("/graphics/Circle.png").toExternalForm());
		background.setImage(new Image(getClass().getResource("/graphics/SchoolBoard.png").toExternalForm()));
		roundBorders(background, 30);
	}

	private void initEventHandlers() {
		assistants.setOnAction(event -> {
			app.showStickyPopup(PopupName.ASSISTANT_CARDS);
			event.consume();
		});

		characters.setOnAction(event -> {
			app.changeScene(SceneName.CHARACTER_CARDS);
			event.consume();
		});

		board.setOnAction(event -> {
			app.changeScene(SceneName.BOARD);
			event.consume();
		});

		sb_button.setOnAction(event -> {
			currentUsername = sb_username.getSelectionModel().getSelectedItem();
			if (currentUsername == null) currentUsername = client.getUsername();
			load();
			event.consume();
		});

		pane.setOnMouseClicked(event -> {
			selected = null;
			drawSelected();
			event.consume();
		});

		selectSource = event -> {
			ImageView imageView = (ImageView) event.getSource();
			selected = studentImages.keySet().stream()
					.filter(k -> studentImages.get(k) == imageView.getImage())
					.findAny().orElse(null);
			drawSelected();
			event.consume();
		};

		selectDestination = event -> {
			if (selected != null) {
				client.moveStudent(selected, GameConstants.DINING_ROOM);
				selected = null;
				drawSelected();
			}
			event.consume();
		};

		selectColorForCharacterCard = event -> {
			Object source = event.getSource();
			String selectedColor = null;
			if (source instanceof ImageView imageView) {
				selectedColor = studentImages.keySet().stream()
						.filter(k -> studentImages.get(k) == imageView.getImage())
						.findAny().orElse(null);
			} else if (source instanceof GridPane gridPane) {
				selectedColor = diningroomPanes.keySet().stream()
						.filter(k -> diningroomPanes.get(k) == gridPane)
						.findAny().orElse(null);
			}
			CharacterCardsController characterCardsController = (CharacterCardsController) app.getControllerForScene(SceneName.CHARACTER_CARDS);
			characterCardsController.selectColor(selectedColor);
		};
	}

	protected void initCharacterMiniatures() {
		BoardStatus boardStatus = client.getBoardStatus();
		characterMiniatures = new HashMap<>();
		for (String character : boardStatus.getCharacterCards()) {
			characterMiniatures.put(
					character,
					new Image(getClass().getResource("/graphics/CharacterCards/" + character + ".jpg").toExternalForm())
			);
		}
	}
}
