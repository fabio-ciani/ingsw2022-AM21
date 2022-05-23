package it.polimi.ingsw.eriantys.client.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class CharacterCardsController extends Controller {
	@FXML private GridPane cards;
	private final String[] characters;

	public CharacterCardsController() {
		characters = new String[3];
		characters[0] = "Knight";
		characters[1] = "Minstrel";
		characters[2] = "Thief";
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

					Rectangle clip = new Rectangle(img.getFitWidth(), img.getFitHeight());
					clip.setArcWidth(30);
					clip.setArcHeight(30);
					img.setClip(clip);

					Tooltip desc = new Tooltip("You are hovering on the " + split(characters[Character.getNumericValue(x.getId().charAt(1))]) + " card.");
					// desc.setShowDelay(Duration.ZERO);
					Tooltip.install(img, desc);
				});
	}

	private void drawLabels() {
		cards.getChildren().stream()
				.filter(x -> x instanceof Text && x.getId().matches("^\\w+text\\z"))
				.forEach(x -> ((Text) x).setText(split(characters[Character.getNumericValue(x.getId().charAt(1))])));
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

	private void applyGrayscale(ImageView img) {
		ColorAdjust grayscale = new ColorAdjust();
		grayscale.setSaturation(-1);
		img.setEffect(grayscale);
	}
}
