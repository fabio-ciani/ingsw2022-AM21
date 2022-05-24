package it.polimi.ingsw.eriantys.client.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class TowersController extends Controller {
	@FXML private GridPane container;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		drawImages();
	}

	@Override
	public void onChangeScene() {

	}

	private void drawImages() {
		container.getChildren().stream()
				.forEach(x -> {
					ImageView img = (ImageView) x;

					img.setImage(new Image(getClass().getResource("/graphics/Towers/" + x.getId() + "Tower.png").toExternalForm()));
				});
	}
}
