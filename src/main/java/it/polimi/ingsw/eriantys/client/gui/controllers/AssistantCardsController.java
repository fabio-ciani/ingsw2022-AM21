package it.polimi.ingsw.eriantys.client.gui.controllers;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.*;
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

					if (Objects.equals(played.get(client.getUsername()), x.getId().toUpperCase())) {
						applyGreenShade(img);
					} else {
						img.setEffect(null);
					}

					roundBorders(img, 30);

					if (!assistants.contains(x.getId().toUpperCase())) {
						applyGrayscale(img);
						img.setOnMouseClicked(Event::consume);
					} else {
						img.setOnMouseClicked(event -> {
							client.playAssistantCard(x.getId().toUpperCase());
							event.consume();
						});
					}
				});
	}

	private void drawPlayedCards() {
		int playerNumber = client.getBoardStatus().getPlayers().size();
		Iterator<String> playerIterator = played.keySet().iterator();
		Iterator<ImageView> imgIterator = playedCards_images.iterator();
		Iterator<Text> labelIterator = playedCards_texts.iterator();

		for (int playerCounter = 0; playerCounter < playerNumber; playerCounter++) {
			String player = playerIterator.hasNext() ? playerIterator.next() : null;
			ImageView img = imgIterator.next();
			Text label = labelIterator.next();

			if (player != null && played.get(player) != null) {
				img.setFitWidth(72);
				img.setFitHeight(106);
				String card = played.get(player);
				img.setImage(new Image(getClass().getResource("/graphics/AssistantCards/" + card.charAt(0) + card.substring(1).toLowerCase() + ".png").toExternalForm()));
				label.setText(player);
				label.setVisible(true);
			} else {
				img.setFitWidth(70);
				img.setFitHeight(106);
				img.setImage(new Image(getClass().getResource("/graphics/CardBack.png").toExternalForm()));
				label.setVisible(false);
			}

			roundBorders(img, 10);

			if (playerNumber == 2 && imgIterator.hasNext()) {
				imgIterator.next();
				labelIterator.next();
			}
		}
	}
}
