package it.polimi.ingsw.eriantys.client.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

public class WizardsController extends Controller {
	@FXML private GridPane container;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		drawImages();
	}

	private void drawImages() {
		container.getChildren().stream()
				.forEach(x -> {
					ImageView img = (ImageView) x;

					img.setImage(new Image(getClass().getResource("/graphics/Wizards/" + x.getId() + "Wizard.png").toExternalForm()));

					Rectangle clip = new Rectangle(img.getFitWidth(), img.getFitHeight());
					clip.setArcWidth(30);
					clip.setArcHeight(30);
					img.setClip(clip);
				});
	}
}
