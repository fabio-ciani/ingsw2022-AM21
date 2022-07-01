package it.polimi.ingsw.eriantys.client.gui.controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.eriantys.client.gui.SceneName;
import it.polimi.ingsw.eriantys.model.BoardStatus;
import it.polimi.ingsw.eriantys.model.Color;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A class representing the controller for the {@code CHARACTER_CARDS} scene.
 * @see SceneName#CHARACTER_CARDS
 * @see javafx.scene.Scene
 */
public class CharacterCardsController extends Controller {
	@FXML private BorderPane pane;
	@FXML private GridPane cards;
	@FXML private Button back;
	@FXML private Button confirm;

	private List<String> sourceColors;
	private List<String> destinationColors;
	private String targetColor;
	private String targetIsland;

	private BoardStatus status;
	private List<String> characters;
	private Map<String, Integer> costs;
	private final Map<String, Map<String, String>> info;

	private List<ImageView> characterCoins;
	private List<List<ImageView>> characterStudents;
	private List<ImageView> characterNoEntryTiles;
	private List<Text> characterNoEntryTileTexts;

	/**
	 * Constructs the {@link CharacterCardsController} getting the character card information from the JSON file.
	 * @throws IOException if the file cannot be opened or read
	 */
	public CharacterCardsController() throws IOException {
		try (InputStream in = getClass().getClassLoader().getResourceAsStream("help/characters.json")) {
			if (in == null) throw new FileNotFoundException();
			Gson gson = new Gson();
			info = gson.fromJson(new InputStreamReader(in), new TypeToken<Map<String, Map<String, String>>>(){}.getType());
		}
	}

	/**
	 * Gets all the child nodes representing the elements of the scene from the FXML.
	 * Associates the event handlers with the buttons on the scene.
	 */
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		characterCoins = cards.getChildren().stream()
				.filter(y -> y instanceof ImageView && y.getId().matches("c\\d_coin"))
				.map(x -> (ImageView) x)
				.peek(i -> i.setImage(new Image(getClass().getResource("/graphics/Coin.png").toExternalForm())))
				.collect(Collectors.toList());

		characterStudents = cards.getChildren().stream()
				.filter(y -> y instanceof GridPane && y.getId().matches("c\\d_students"))
				.map(x -> (GridPane) x)
				.map(gp -> gp.getChildren().stream()
						.map(n -> (ImageView) n)
						.toList())
				.toList();

		characterNoEntryTiles = cards.getChildren().stream()
				.filter(y -> y instanceof ImageView && y.getId().matches("c\\d_tile"))
				.map(x -> (ImageView) x)
				.peek(i -> i.setImage(new Image(getClass().getResource("/graphics/NoEntryTile.png").toExternalForm())))
				.toList();

		characterNoEntryTileTexts = cards.getChildren().stream()
				.filter(y -> y instanceof Text && y.getId().matches("c\\d_tile_num"))
				.map(x -> (Text) x)
				.toList();

		back.setOnAction(event -> {
			app.changeScene(SceneName.SCHOOLBOARD);
			event.consume();
		});

		confirm.setOnAction(event -> {
			SchoolBoardController schoolBoardController = (SchoolBoardController) app.getControllerForScene(SceneName.SCHOOLBOARD);
			BoardController boardController = (BoardController) app.getControllerForScene(SceneName.BOARD);
			if (client.getCharacterCard() != null) {
				client.playCharacterCard(sourceColors.toArray(new String[0]),
						destinationColors.toArray(new String[0]),
						targetColor,
						targetIsland);
				schoolBoardController.setSelected(null);
				schoolBoardController.setEventHandlers();
				boardController.load();
				drawImages();
			} else {
				showError.accept("Select a character card first");
			}
		});

		pane.setOnMouseClicked(event -> {
			SchoolBoardController schoolBoardController = (SchoolBoardController) app.getControllerForScene(SceneName.SCHOOLBOARD);
			schoolBoardController.setSelected(null);
			client.setCharacterCard(null);
			drawImages();
			event.consume();
		});
	}

	/**
	 * {@inheritDoc}
	 * Draws the images of the character cards and the elements placed on them (if present).
	 */
	@Override
	public void onChangeScene() {
		drawImages();
	}

	@Override
	public Pane getTopLevelPane() {
		return pane;
	}

	/**
	 * Loads the board status and calls {@link #populate(BoardStatus)}.
	 */
	public void load() {
		if (client.getBoardStatus().getCharacterCards() != null)
			populate(client.getBoardStatus());
	}

	/**
	 * Gets the information about character cards from the board status and draws all the elements in the scene.
	 * @param status the updated board status
	 */
	public void populate(BoardStatus status) {
		this.status = status;
		this.characters = status.getCharacterCards();
		this.costs = status.getCharacterCardsCost();
		drawImages();
		drawLabels();
	}

	/**
	 * Adds the selected color to the arguments of the effect of the selected character card.
	 * If only one color is selected, it is considered as the {@code targetColor}.
	 * If more colors are selected they are interpreted in alternating order as {@code sourceColors} and {@code destinationColors}.
	 * @param color the name of the selected {@link Color}
	 */
	public void selectColor(String color) {
		if (sourceColors.isEmpty()) {
			if (targetColor == null) {
				targetColor = color;
			} else {
				sourceColors.add(targetColor);
				destinationColors.add(color);
				targetColor = null;
			}
		} else {
			if (sourceColors.size() > destinationColors.size()) {
				destinationColors.add(color);
			} else {
				sourceColors.add(color);
			}
		}
		showArgs();
	}

	/**
	 * Sets the {@code targetIsland} argument of the effect of the selected character card.
	 * @param island the identifier of the selected island
	 */
	public void selectIsland(String island) {
		targetIsland = island;
		showArgs();
	}

	private void showArgs() {
		StringBuilder message = new StringBuilder("Selected arguments:");
		if (!sourceColors.isEmpty()) message.append("\n").append("Students from source: ").append(sourceColors);
		if (!destinationColors.isEmpty()) message.append("\n").append("Students form destination: ").append(destinationColors);
		if (targetColor != null) message.append("\n").append("Target color/student: ").append(targetColor);
		if (targetIsland != null) message.append("\n").append("Target island: ").append(targetIsland);
		showInfo.accept(message.toString());
	}

	private void drawImages() {
		cards.getChildren().stream()
				.filter(x -> x instanceof ImageView && x.getId().matches("^\\w+img\\z"))
				.forEach(x -> {
					ImageView img = (ImageView) x;
					int cardId = Character.getNumericValue(x.getId().charAt(1));
					String character = characters.get(cardId);

					img.setImage(new Image(getClass().getResource("/graphics/CharacterCards/" + character + ".jpg").toExternalForm()));

					roundBorders(img, 30);

					Tooltip desc = generateDescription(character);
					desc.setShowDelay(Duration.millis(300));
					Tooltip.install(img, desc);

					characterCoins.get(cardId).setVisible(
							costs.get(character) - Integer.parseInt(info.get(character).get("cost")) == 1
					);

					Map<String, Integer> cardStudents = status.getCharacterCardsStudents().get(character);
					if (cardStudents != null) {
						Iterator<ImageView> imageViewIterator = characterStudents.get(cardId).iterator();
						cardStudents.forEach((color, amount) -> {
							Image image = new Image(getClass().getResource("/graphics/Students/" + color.charAt(0) + color.substring(1).toLowerCase() + "Student.png").toExternalForm());
							for (int i = 0; i < amount; i++) {
								ImageView imageView = imageViewIterator.next();
								imageView.setImage(image);
								imageView.setVisible(true);
								imageView.setOnMouseClicked(event -> {
									if (client.getCharacterCard() != null) {
										selectColor(color);
									}
									event.consume();
								});
							}
						});
					}

					Integer cardNoEntryTiles = status.getCharacterCardsNoEntryTiles().get(character);
					if (cardNoEntryTiles != null) {
						characterNoEntryTiles.get(cardId).setVisible(true);
						characterNoEntryTileTexts.get(cardId).setVisible(true);
						characterNoEntryTileTexts.get(cardId).setText(cardNoEntryTiles.toString());
					}

					if (status.getPlayerCoins().get(client.getUsername()) - costs.get(character) < 0) {
						applyGrayscale(img);
						img.setOnMouseClicked(Event::consume);
					} else {
						SchoolBoardController schoolBoardController = (SchoolBoardController) app.getControllerForScene(SceneName.SCHOOLBOARD);
						BoardController boardController = (BoardController) app.getControllerForScene(SceneName.BOARD);
						if (Objects.equals(cardId, client.getCharacterCard())) {
							applyGreenShade(img);
						} else {
							img.setEffect(null);
						}
						img.setOnMouseClicked(event -> {
							client.setCharacterCard(characters.indexOf(character));
							if (info.get(character).get("cmd") == null) {
								client.playCharacterCard(null, null, null, null);
							} else {
								boardController.initCharacterMiniatures();
								boardController.load();
								schoolBoardController.initCharacterMiniatures();
								schoolBoardController.setSelected(character);
								schoolBoardController.setEventHandlers();
								sourceColors = new ArrayList<>();
								destinationColors = new ArrayList<>();
								targetColor = null;
								targetIsland = null;
								drawImages();
							}
							event.consume();
						});
					}
				});
	}

	private Tooltip generateDescription(String character) {
		Map<String, String> characterInfo = info.get(character);

		String setup = characterInfo.get("setup");
		String effect = characterInfo.get("effect");

		StringBuilder out = new StringBuilder();

		if (setup != null) out.append(prettifyDesc(setup)).append("\n\n");
		out.append(prettifyDesc(effect));

		return new Tooltip(out.toString());
	}

	private String prettifyDesc(String in) {
		StringBuilder out = new StringBuilder();
		char c;

		for (int i = 0; i < in.length(); i++) {
			c = in.charAt(i);
			if (c == ' ' && in.charAt(i - 1) == '.')
				out.append("\n");
			else out.append(c);
		}

		return out.toString();
	}

	private void drawLabels() {
		cards.getChildren().stream()
				.filter(x -> x instanceof Text && x.getId().matches("^\\w+text\\z"))
				.forEach(x -> ((Text) x).setText(split(characters.get(Character.getNumericValue(x.getId().charAt(1))))));
	}

	private String split(String in) {
		StringBuilder out = new StringBuilder();
		char c;

		for (int i = 0; i < in.length(); i++) {
			c = in.charAt(i);
			if (i != in.length() - 1 && Character.isLowerCase(c) && Character.isUpperCase(in.charAt(i + 1)))
				out.append(c).append(' ');
			else out.append(c);
		}

		return out.toString();
	}
}
