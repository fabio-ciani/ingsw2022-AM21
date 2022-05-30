package it.polimi.ingsw.eriantys.client.gui.controllers;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AssistantCardsController extends Controller {
	@FXML private GridPane container;
	@FXML private GridPane played_cards;
	@FXML private Button close;
	private List<String> assistants;
	private Map<String, String> played;

	private List<ImageView> playedCards_images;
	private List<Text> playedCards_texts;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		close.setOnAction(event -> {
			app.hideStickyPopup();
			event.consume();
		});

		playedCards_images = played_cards.getChildren().stream()
				.filter(x -> x instanceof ImageView && x.getId().matches("^pc\\w+\\z"))
				.map(x -> (ImageView) x)
				.collect(Collectors.toList());

		playedCards_texts = played_cards.getChildren().stream()
				.filter(x -> x instanceof Text && x.getId().matches("^pc\\w+\\z"))
				.map(x -> (Text) x)
				.collect(Collectors.toList());
	}

	@Override
	public Pane getTopLevelPane() {
		return container;
	}

	public void populate(List<String> assistants, Map<String, String> played) {
		this.assistants = assistants;
		this.played = played;
		drawImages();
	}

	private void drawImages() {
		drawDeck();
		drawPlayedCards();
	}

	private void drawDeck() {
		container.getChildren().stream()
				.filter(x -> x instanceof ImageView && !x.getId().matches("^pc\\w+\\z"))
				.forEach(x -> {
					ImageView img = (ImageView) x;

					img.setImage(new Image(getClass().getResource("/graphics/AssistantCards/" + x.getId() + ".png").toExternalForm()));

					roundBorders(img, 30);

					if (!assistants.contains(x.getId().toUpperCase())) {
						img.setEffect(null);
						applyGrayscale(img);
						img.setOnMouseClicked(Event::consume);
					} else {
						img.setOnMouseClicked(event -> {
							client.playAssistantCard(x.getId().toUpperCase());
							Blend blend = new Blend();
							Color color = new Color(0.55, 1, 0.35, 0.7);
							ColorInput topInput = new ColorInput(0, 0, img.getImage().getWidth(), img.getImage().getHeight(), color);
							blend.setTopInput(topInput);
							blend.setMode(BlendMode.MULTIPLY);
							img.setEffect(blend);
							event.consume();
						});
					}
				});
	}

	private void drawPlayedCards() {
		List<String> players = client.getBoardStatus().getPlayers();
		Iterator<ImageView> imgIterator = playedCards_images.iterator();
		Iterator<Text> labelIterator = playedCards_texts.iterator();

		for (String player : players) {
			ImageView img = imgIterator.next();
			Text label = labelIterator.next();

			if (played.get(player) != null) {
				img.setImage(new Image(getClass().getResource("/graphics/AssistantCards/" + played.get(player) + ".png").toExternalForm()));
				label.setText(player);
			} else {
				img.setImage(new Image(getClass().getResource("/graphics/CardBack.png").toExternalForm()));
				label.setText(null);
			}

			roundBorders(img, 10);
		}
	}
}
