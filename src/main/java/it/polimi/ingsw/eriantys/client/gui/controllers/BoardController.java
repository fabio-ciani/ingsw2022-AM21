package it.polimi.ingsw.eriantys.client.gui.controllers;

import it.polimi.ingsw.eriantys.client.gui.SceneName;
import it.polimi.ingsw.eriantys.model.BoardStatus;
import it.polimi.ingsw.eriantys.model.Color;
import it.polimi.ingsw.eriantys.model.TowerColor;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.*;
import java.util.stream.Stream;

public class BoardController extends Controller {
	@FXML private BorderPane pane;
	@FXML private GridPane islands;
	@FXML private GridPane ct_container_up;
	@FXML private GridPane ct_container_down;
	@FXML private ImageView selected_img;
	@FXML private Text selected_text;
	@FXML private Button back;

	private String selectedStudent;

	private Map<String, ImageView> islandTiles;
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

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		islandTiles = new HashMap<>();
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
		setImages();
		initEventHandlers();
	}

	@Override
	public Pane getTopLevelPane() {
		return pane;
	}

	@Override
	public void onChangeScene() {}

	public void load() {
		BoardStatus boardStatus = client.getBoardStatus();

		drawIslands(boardStatus.getIslands(),
				boardStatus.getIslandSizes(),
				boardStatus.getIslandControllers(),
				boardStatus.getPlayerTowerColors(),
				boardStatus.getIslandStudents(),
				boardStatus.getIslandNoEntryTiles(),
				boardStatus.getMotherNatureIsland());
	}

	private void drawIslands(List<String> islands,
							 Map<String, Integer> sizes,
							 Map<String, String> controllers,
							 Map<String, String> playerTowerColors,
							 Map<String, Map<String, Integer>> students,
							 Map<String, Integer> noEntryTiles,
							 String motherNatureIsland) {
		for (String island : islands) {
			ImageView towerImageView = towerImageViews.get(island.substring(0, 2));
			Text towerText = towerTexts.get(island.substring(0, 2));
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
				ImageView studentImageView = studentImageViews.get(island).get(k);
				Text studentText = studentTexts.get(island).get(k);
				studentImageView.setVisible(v > 0);
				studentText.setText(v.toString());
				studentText.setVisible(v > 0);
			});
			ImageView centerImageView = centerImageViews.get(island);
			Text centerText = centerTexts.get(island);
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
		ObservableList<Node> children = islands.getChildren();

		children.stream()
				.filter(n -> n instanceof ImageView && n.getId().startsWith("i"))
				.map(n -> (ImageView) n)
				.forEach(i -> {
					islandTiles.put(i.getId().substring(1), i);
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

		List<ImageView> ct = Stream.concat(ct_container_up.getChildren().stream(), ct_container_down.getChildren().stream())
				.filter(n -> n instanceof ImageView && n.getId().startsWith("ct"))
				.map(n -> (ImageView) n)
				.toList();
		cloudImageViews.put("2p", new HashMap<>());
		cloudImageViews.put("3p", new HashMap<>());
		for (ImageView i : ct) {
			String[] tokens = i.getId().split("_");
			int id = Integer.parseInt(tokens[2]);
			cloudImageViews.get(tokens[1]).put(id, i);
		}

		List<ImageView> s = Stream.concat(ct_container_up.getChildren().stream(), ct_container_down.getChildren().stream())
				.filter(n -> n instanceof ImageView && n.getId().startsWith("s"))
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

	private void setImages() {
		islandTiles.forEach((k, v) -> {
			v.setImage(islandImages.get(Integer.parseInt(k) % 3));
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
	}

	private void initEventHandlers() {
		back.setOnAction(event -> {
			app.changeScene(SceneName.SCHOOLBOARD);
			event.consume();
		});
	}
}
