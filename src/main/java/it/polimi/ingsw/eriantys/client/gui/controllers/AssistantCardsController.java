package it.polimi.ingsw.eriantys.client.gui.controllers;

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

public class AssistantCardsController extends Controller {
	@FXML private GridPane container;
	@FXML private GridPane played_cards;
	@FXML private Button close;
	private List<String> assistants;
	private Map<String, String> played;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
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

					if (!assistants.contains(x.getId().toUpperCase()))
						applyGrayscale(img);
				});
	}

	private void drawPlayedCards() {
		int count = 1;

		for (String player : played.keySet()) {
			int finalCount = count;

			played_cards.getChildren().stream()
					.filter(x -> x instanceof ImageView && x.getId().equals("pc_" + finalCount + "_img"))
					.forEach(x -> {
						ImageView img = (ImageView) x;

						img.setImage(new Image(getClass().getResource("/graphics/AssistantCards/" + played.get(player) + ".png").toExternalForm()));

						roundBorders(img, 10);
					});

			played_cards.getChildren().stream()
					.filter(x -> x instanceof Text && x.getId().equals("pc_" + finalCount + "_user"))
					.forEach(x -> {
						Text label = (Text) x;

						label.setText(player);
					});

			count++;
		}
	}
}
