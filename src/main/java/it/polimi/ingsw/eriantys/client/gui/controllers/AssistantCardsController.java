package it.polimi.ingsw.eriantys.client.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

public class AssistantCardsController extends Controller implements Initializable {
	@FXML private GridPane cards;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		drawImages();
	}

	private void drawImages() {
		cards.getChildren().stream()
				.forEach(x -> {
					ImageView img = (ImageView) x;

					img.setImage(new Image(getClass().getResource("/graphics/AssistantCards/" + x.getId() + ".png").toExternalForm()));

					Rectangle clip = new Rectangle(img.getFitWidth(), img.getFitHeight());
					clip.setArcWidth(30);
					clip.setArcHeight(30);
					img.setClip(clip);
				});
	}
}
