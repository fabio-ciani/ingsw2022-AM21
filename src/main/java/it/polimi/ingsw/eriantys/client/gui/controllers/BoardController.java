package it.polimi.ingsw.eriantys.client.gui.controllers;

import it.polimi.ingsw.eriantys.client.gui.SceneName;
import it.polimi.ingsw.eriantys.model.BoardStatus;
import it.polimi.ingsw.eriantys.model.Color;
import it.polimi.ingsw.eriantys.model.TowerColor;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.*;

public class BoardController extends Controller {
	@FXML private BorderPane pane;
	@FXML private GridPane board;
	@FXML private ImageView selected_img;
	@FXML private Text selected_text;
	@FXML private Button back;

	private Map<String, ImageView> islandImageViews;
	private Map<String, Map<String, ImageView>> studentImageViews;
	private Map<String, Map<String, Text>> studentTexts;
	private Map<String, ImageView> centerImageViews;
	private Map<String, Text> centerTexts;
	private Map<String, ImageView> towerImageViews;
	private Map<String, Text> towerTexts;
	private Map<String, Map<Integer, ImageView>> cloudImageViews;
	private Map<String, Map<Integer, List<ImageView>>> cloudStudentImageViews;

	private List<Image> islandImages;
	private Map<String, Image> studentImages;
	private Image motherNatureImage;
	private Image noEntryTileImage;
	private Map<String, Image> towerImages;
	private Map<String, Integer> towerSizes;
	private Map<String, Map<Integer, Image>> cloudImages;
	private Map<String, Image> characterMiniatures;

	private EventHandler<MouseEvent> selectMoveDestination;
	private EventHandler<MouseEvent> selectCloud;
	private EventHandler<MouseEvent> selectIslandForCharacterCard;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		islandImageViews = new HashMap<>();
		studentImageViews = new HashMap<>();
		studentTexts = new HashMap<>();
		centerImageViews = new HashMap<>();
		centerTexts = new HashMap<>();
		towerImageViews = new HashMap<>();
		towerTexts = new HashMap<>();
		cloudImageViews = new HashMap<>();
		cloudStudentImageViews = new HashMap<>();

		islandImages = new ArrayList<>();
		studentImages = new HashMap<>();
		towerImages = new HashMap<>();
		towerSizes = new HashMap<>();
		cloudImages = new HashMap<>();

		initNodes();
		initImages();
		setImagesAndTexts();
		initEventHandlers();
	}

	@Override
	public Pane getTopLevelPane() {
		return pane;
	}

	@Override
	public void onChangeScene() {
		drawSelected();
	}

	public void load() {
		BoardStatus boardStatus = client.getBoardStatus();

		drawIslands(boardStatus.getIslands(),
				boardStatus.getIslandSizes(),
				boardStatus.getIslandControllers(),
				boardStatus.getPlayerTowerColors(),
				boardStatus.getIslandStudents(),
				boardStatus.getIslandNoEntryTiles(),
				boardStatus.getMotherNatureIsland());

		drawClouds(boardStatus.getCloudTiles());

		drawSelected();
	}

	protected void drawIslands(List<String> islands,
							 Map<String, Integer> sizes,
							 Map<String, String> controllers,
							 Map<String, String> playerTowerColors,
							 Map<String, Map<String, Integer>> students,
							 Map<String, Integer> noEntryTiles,
							 String motherNatureIsland) {
		for (String island : islands) {
			String id = island.substring(0, 2);
			for (String other : island.split("-")) {
				if (!id.equals(other)) {
					ImageView i = islandImageViews.get(other);
					i.setVisible(false);
					studentImageViews.get(other).forEach((k, v) -> v.setVisible(false));
					studentTexts.get(other).forEach((k, v) -> v.setVisible(false));
					towerImageViews.get(other).setVisible(false);
					towerTexts.get(other).setVisible(false);
					centerImageViews.get(other).setVisible(false);
					centerTexts.get(other).setVisible(false);
					i.setOnMouseClicked(Event::consume);
				}
			}
			ImageView islandImageView = islandImageViews.get(id);
			if (client.getCharacterCard() != null) {
				islandImageView.setOnMouseClicked(selectIslandForCharacterCard);
			} else {
				islandImageView.setOnMouseClicked(selectMoveDestination);
			}
			islandImageView.setId(island);
			ImageView towerImageView = towerImageViews.get(id);
			Text towerText = towerTexts.get(id);
			if (controllers.get(island) != null) {
				String towerColor = playerTowerColors.get(controllers.get(island));
				towerImageView.setImage(towerImages.get(towerColor));
				towerImageView.setFitWidth(towerSizes.get(towerColor));
				towerImageView.setFitHeight(towerSizes.get(towerColor));
				towerImageView.setVisible(true);
				towerText.setText(sizes.get(island).toString());
				towerText.setVisible(true);
			} else {
				towerImageView.setVisible(false);
				towerText.setVisible(false);
			}
			students.get(island).forEach((k, v) -> {
				ImageView studentImageView = studentImageViews.get(id).get(k);
				Text studentText = studentTexts.get(id).get(k);
				studentImageView.setVisible(v > 0);
				studentText.setText(v.toString());
				studentText.setVisible(v > 0);
			});
			ImageView centerImageView = centerImageViews.get(id);
			Text centerText = centerTexts.get(id);
			Integer noEntryTilesAmount = noEntryTiles.get(island);
			if (motherNatureIsland.equals(island)) {
				centerImageView.setImage(motherNatureImage);
				centerImageView.setVisible(true);
				centerText.setVisible(false);
			} else if (noEntryTilesAmount != null && noEntryTilesAmount > 0) {
				centerImageView.setImage(noEntryTileImage);
				centerImageView.setVisible(true);
				centerText.setText(noEntryTilesAmount.toString());
				centerText.setVisible(true);
			} else {
				centerImageView.setVisible(false);
				centerText.setVisible(false);
			}
		}
	}

	private void drawClouds(Map<String, Map<String, Integer>> cloudTiles) {
		String p = cloudTiles.size() == 2 ? "2p" : "3p";
		cloudTiles.forEach((k, v) -> {
			int cloudId = Integer.parseInt(k);
			ImageView imageView = cloudImageViews.get(p).get(cloudId);
			if (imageView.getImage() == null) {
				imageView.setImage(cloudImages.get(p).get(cloudId));
			}
			imageView.setVisible(true);
			imageView.setMouseTransparent(false);
			imageView.setOnMouseClicked(selectCloud);
			Iterator<ImageView> studentIterator = cloudStudentImageViews.get(p).get(cloudId).iterator();
			v.forEach((k1, v1) -> {
				for (int i = 0; i < v1; i++) {
					ImageView studentImageView = studentIterator.next();
					studentImageView.setImage(studentImages.get(k1));
					studentImageView.setVisible(true);
				}
			});
			while (studentIterator.hasNext()) {
				studentIterator.next().setVisible(false);
			}
		});

		cloudImageViews.get(p.equals("2p") ? "3p" : "2p").forEach((k, v) -> {
			v.setOnMouseClicked(selectCloud);
			v.setMouseTransparent(true);
			v.setVisible(false);
		});
	}

	private void drawSelected() {
		SchoolBoardController schoolBoardController = (SchoolBoardController) app.getControllerForScene(SceneName.SCHOOLBOARD);
		String selected = schoolBoardController.getSelected();
		if (selected != null) {
			selected_img.setImage(Optional.ofNullable(studentImages.get(selected)).orElse(characterMiniatures.get(selected)));
			selected_img.setVisible(true);
			selected_text.setVisible(true);
		} else {
			selected_img.setVisible(false);
			selected_text.setVisible(false);
		}
	}

	private void setImagesAndTexts() {
		islandImageViews.forEach((k, v) -> {
			v.setImage(islandImages.get((Integer.parseInt(k) - 1) % 3));
			v.setVisible(true);
		});

		studentImageViews.forEach((k1, v1) -> {
			v1.forEach((k, v) -> {
				v.setImage(studentImages.get(k));
			});
		});

		studentTexts.forEach((k1, v1) -> {
			v1.forEach((k, v) -> {
				v.setText("0");
			});
		});

		centerTexts.forEach((k, v) -> {
			v.setText("0");
		});

		towerTexts.forEach((k, v) -> {
			v.setText("0");
		});

		cloudImageViews.get("2p").forEach((k, v) -> {
			v.setImage(cloudImages.get("2p").get(k));
			v.setVisible(false);
		});

		cloudImageViews.get("3p").forEach((k, v) -> {
			v.setImage(cloudImages.get("3p").get(0));
			v.setVisible(false);
		});

		selected_text.setText("You have selected:");
	}

	private void initImages() {
		islandImages.add(new Image(getClass().getResource("/graphics/Islands/Island_1.png").toExternalForm()));
		islandImages.add(new Image(getClass().getResource("/graphics/Islands/Island_2.png").toExternalForm()));
		islandImages.add(new Image(getClass().getResource("/graphics/Islands/Island_3.png").toExternalForm()));

		studentImages.put(Color.YELLOW.name(), new Image(getClass().getResource("/graphics/Students/YellowStudent.png").toExternalForm()));
		studentImages.put(Color.PINK.name(), new Image(getClass().getResource("/graphics/Students/PinkStudent.png").toExternalForm()));
		studentImages.put(Color.BLUE.name(), new Image(getClass().getResource("/graphics/Students/BlueStudent.png").toExternalForm()));
		studentImages.put(Color.RED.name(), new Image(getClass().getResource("/graphics/Students/RedStudent.png").toExternalForm()));
		studentImages.put(Color.GREEN.name(), new Image(getClass().getResource("/graphics/Students/GreenStudent.png").toExternalForm()));

		motherNatureImage = new Image(getClass().getResource("/graphics/MotherNature.png").toExternalForm());
		noEntryTileImage = new Image(getClass().getResource("/graphics/NoEntryTile.png").toExternalForm());

		towerImages.put(TowerColor.WHITE.name(), new Image(getClass().getResource("/graphics/Towers/WhiteTower.png").toExternalForm()));
		towerImages.put(TowerColor.BLACK.name(), new Image(getClass().getResource("/graphics/Towers/BlackTower.png").toExternalForm()));
		towerImages.put(TowerColor.GREY.name(), new Image(getClass().getResource("/graphics/Towers/GreyTower.png").toExternalForm()));

		towerSizes.put(TowerColor.WHITE.name(), 32);
		towerSizes.put(TowerColor.BLACK.name(), 36);
		towerSizes.put(TowerColor.GREY.name(), 40);

		cloudImages.put("2p", new HashMap<>());
		cloudImages.get("2p").put(0, new Image(getClass().getResource("/graphics/CloudTiles/CloudTile_1.png").toExternalForm()));
		cloudImages.get("2p").put(1, new Image(getClass().getResource("/graphics/CloudTiles/CloudTile_2.png").toExternalForm()));

		cloudImages.put("3p", new HashMap<>());
		cloudImages.get("3p").put(0, new Image(getClass().getResource("/graphics/CloudTiles/CloudTile.png").toExternalForm()));
	}

	private void initNodes() {
		ObservableList<Node> children = board.getChildren();

		children.stream()
				.filter(n -> n instanceof ImageView && n.getId().startsWith("i"))
				.map(n -> (ImageView) n)
				.forEach(i -> {
					islandImageViews.put(i.getId().substring(1), i);
				});

		children.stream()
				.filter(n -> n instanceof Group && n.getId().startsWith("g"))
				.forEach(g -> {
					ObservableList<Node> c = ((GridPane) ((Group) g).getChildren().get(0)).getChildren();
					Iterator<ImageView> studentIterator = c.stream()
							.filter(n -> n instanceof ImageView && n.getId() == null)
							.map(n -> (ImageView) n)
							.iterator();
					Iterator<Text> studentTextIterator = c.stream()
							.filter(n -> n instanceof Text && n.getId() == null)
							.map(n -> (Text) n)
							.iterator();
					Map<String, ImageView> students = new HashMap<>();
					Map<String, Text> texts = new HashMap<>();
					for (String color : Color.stringLiterals()) {
						students.put(color, studentIterator.next());
						texts.put(color, studentTextIterator.next());
					}
					String id = g.getId().substring(1);
					studentImageViews.put(id, students);
					studentTexts.put(id, texts);
					towerImageViews.put(id, (ImageView) c.stream().filter(n -> n instanceof ImageView && n.getId() != null && n.getId().startsWith("t")).findAny().orElse(null));
					towerTexts.put(id, (Text) c.stream().filter(n -> n instanceof Text && n.getId() != null && n.getId().startsWith("t")).findAny().orElse(null));
					centerImageViews.put(id, (ImageView) c.stream().filter(n -> n instanceof ImageView && n.getId() != null && n.getId().startsWith("c")).findAny().orElse(null));
					centerTexts.put(id, (Text) c.stream().filter(n -> n instanceof Text && n.getId() != null && n.getId().startsWith("c")).findAny().orElse(null));
				});

		List<ImageView> ct = board.getChildren().stream()
				.filter(n -> n instanceof ImageView && n.getId().startsWith("ct_"))
				.map(n -> (ImageView) n)
				.toList();
		cloudImageViews.put("2p", new HashMap<>());
		cloudImageViews.put("3p", new HashMap<>());
		for (ImageView i : ct) {
			String[] tokens = i.getId().split("_");
			int id = Integer.parseInt(tokens[2]);
			cloudImageViews.get(tokens[1]).put(id, i);
		}

		List<ImageView> s = board.getChildren().stream()
				.filter(n -> n instanceof ImageView && n.getId().startsWith("s_"))
				.map(n -> (ImageView) n)
				.toList();
		cloudStudentImageViews.put("2p", new HashMap<>());
		cloudStudentImageViews.put("3p", new HashMap<>());
		for (ImageView i : s) {
			String[] tokens = i.getId().split("_");
			Map<Integer, List<ImageView>> students = cloudStudentImageViews.get(tokens[1]);
			int id = Integer.parseInt(tokens[2]);
			students.putIfAbsent(id, new ArrayList<>());
			students.get(id).add(i);
		}
	}

	private void initEventHandlers() {
		back.setOnAction(event -> {
			app.changeScene(SceneName.SCHOOLBOARD);
			event.consume();
		});

		selectMoveDestination = event -> {
			SchoolBoardController schoolBoardController = (SchoolBoardController) app.getControllerForScene(SceneName.SCHOOLBOARD);
			String selectedStudent = schoolBoardController.getSelected();
			ImageView imageView = (ImageView) event.getSource();
			String selectedIsland = imageView.getId();
			if (selectedStudent == null) {
				client.moveMotherNature(selectedIsland);
			} else {
				client.moveStudent(selectedStudent, selectedIsland);
				schoolBoardController.setSelected(null);
				drawSelected();
			}
			event.consume();
		};

		selectCloud = event -> {
			ImageView imageView = (ImageView) event.getSource();
			String p = imageView.getId().split("_")[1];
			int selectedCloud = cloudImageViews.get(p).keySet().stream()
					.filter(k -> imageView.equals(cloudImageViews.get(p).get(k)))
					.findAny().orElse(5);
			client.chooseCloud(selectedCloud);
			event.consume();
		};

		selectIslandForCharacterCard = event -> {
			CharacterCardsController characterCardsController = (CharacterCardsController) app.getControllerForScene(SceneName.CHARACTER_CARDS);
			ImageView imageView = (ImageView) event.getSource();
			String selectedIsland = imageView.getId();
			characterCardsController.selectIsland(selectedIsland);
			event.consume();
		};

		pane.setOnMouseClicked(event -> {
			SchoolBoardController schoolBoardController = (SchoolBoardController) app.getControllerForScene(SceneName.SCHOOLBOARD);
			schoolBoardController.setSelected(null);
			client.setCharacterCard(null);
			drawSelected();
			event.consume();
		});
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
