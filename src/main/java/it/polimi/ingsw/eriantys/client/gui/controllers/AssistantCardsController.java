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
		playedCards_images = played_cards.getChildren().stream()
				.filter(x -> x instanceof ImageView && x.getId().matches("^pc\\w+\\z"))
				.map(x -> (ImageView) x)
				.collect(Collectors.toList());

		playedCards_texts = played_cards.getChildren().stream()
				.filter(x -> x instanceof Text && x.getId().matches("^pc\\w+\\z"))
				.map(x -> (Text) x)
				.collect(Collectors.toList());

		close.setOnAction(event -> {
			app.hideStickyPopup();
			event.consume();
		});
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
						applyGrayscale(img);
						img.setOnMouseClicked(Event::consume);
					} else {
						img.setOnMouseClicked(event -> {
							client.playAssistantCard(x.getId());
							event.consume();
						});
					}
				});
	}

	private void drawPlayedCards() {
		int count = 0;
		ImageView img;
		Text label;

		for (String player : client.getBoardStatus().getPlayers()) {
			img = playedCards_images.get(count);
			label = playedCards_texts.get(count);

			if (!player.equals(client.getUsername())) {
				if (played.get(player) != null) {
					img.setImage(new Image(getClass().getResource("/graphics/AssistantCards/" + played.get(player) + ".png").toExternalForm()));
				} else {
					img.setImage(new Image(getClass().getResource("/graphics/CardBack.png").toExternalForm()));
				}
				label.setText(player);
				roundBorders(img, 10);
			} else {
				if (client.getBoardStatus().getPlayers().size() == 2) {
					img.setImage(new Image(getClass().getResource("/graphics/CardBack.png").toExternalForm()));
					roundBorders(img, 10);
					label.setText(null);
				}
			}

			count++;
		}
	}
}
