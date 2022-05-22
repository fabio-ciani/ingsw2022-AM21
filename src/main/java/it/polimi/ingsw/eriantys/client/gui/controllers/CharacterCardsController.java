package it.polimi.ingsw.eriantys.client.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class CharacterCardsController extends Controller implements Initializable {
	@FXML private GridPane cards;
	private final String[] characters;

	public CharacterCardsController() {
		characters = new String[3];
		characters[0] = "HerbGranny";
		characters[1] = "MushroomGuy";
		characters[2] = "Jester";
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		drawImages();
		drawLabels();
	}

	private void drawImages() {
		cards.getChildren().stream()
				.filter(x -> x instanceof ImageView && x.getId().matches("^\\w+img\\z"))
				.forEach(x -> {
					ImageView img = (ImageView) x;

					img.setImage(new Image(getClass().getResource("/graphics/CharacterCards/" + characters[Character.getNumericValue(x.getId().charAt(1))] + ".jpg").toExternalForm()));

					Tooltip desc = new Tooltip("You are hovering on the " + characters[Character.getNumericValue(x.getId().charAt(1))] + " card.");
					// desc.setShowDelay(Duration.ZERO);
					Tooltip.install(img, desc);
				});
	}

	private void drawLabels() {
		cards.getChildren().stream()
				.filter(x -> x instanceof Text && x.getId().matches("^\\w+text\\z"))
				.forEach(x -> ((Text) x).setText(characters[Character.getNumericValue(x.getId().charAt(1))]));
	}
}
