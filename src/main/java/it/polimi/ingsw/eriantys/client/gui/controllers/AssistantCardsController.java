package it.polimi.ingsw.eriantys.client.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class AssistantCardsController extends Controller {
	@FXML private GridPane container;
	@FXML private GridPane played_cards;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		drawImages();
	}

	@Override
	public void onChangeScene() {

	}

	private void drawImages() {
		container.getChildren().stream()
				.filter(x -> x instanceof ImageView)
				.forEach(x -> {
					ImageView img = (ImageView) x;

					img.setImage(new Image(getClass().getResource("/graphics/AssistantCards/" + x.getId() + ".png").toExternalForm()));

					roundBorders(img, 30);
				});

		/*
		played_cards.getChildren().stream()
				.filter(x -> x instanceof ImageView)
				.forEach(x -> {
					ImageView img = (ImageView) x;

					img.setImage(new Image(getClass().getResource("/graphics/AssistantCards/Lizard.png").toExternalForm()));

					roundBorders(img, 10);
				});
		*/
	}
}
