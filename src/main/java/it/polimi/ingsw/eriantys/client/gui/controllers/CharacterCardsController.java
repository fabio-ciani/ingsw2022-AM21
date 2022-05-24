package it.polimi.ingsw.eriantys.client.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CharacterCardsController extends Controller {
	@FXML private GridPane cards;
	private final List<String> characters;

	public CharacterCardsController() {
		characters = new ArrayList<>();
		characters.add("Knight");
		characters.add("Minstrel");
		characters.add("Thief");
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		drawImages();
		drawLabels();
	}

	@Override
	public void onChangeScene() {

	}

	private void drawImages() {
		cards.getChildren().stream()
				.filter(x -> x instanceof ImageView && x.getId().matches("^\\w+img\\z"))
				.forEach(x -> {
					ImageView img = (ImageView) x;

					img.setImage(new Image(getClass().getResource("/graphics/CharacterCards/" + characters.get(Character.getNumericValue(x.getId().charAt(1))) + ".jpg").toExternalForm()));

					roundBorders(img, 30);

					Tooltip desc = new Tooltip("You are hovering on the " + split(characters.get(Character.getNumericValue(x.getId().charAt(1)))) + " card.");
					// desc.setShowDelay(Duration.ZERO);
					Tooltip.install(img, desc);

					// TODO: if result of the difference between the actual cost and the cost from JSON is 1, then draw the coin
				});
	}

	private void drawLabels() {
		cards.getChildren().stream()
				.filter(x -> x instanceof Text && x.getId().matches("^\\w+text\\z"))
				.forEach(x -> ((Text) x).setText(split(characters.get(Character.getNumericValue(x.getId().charAt(1))))));
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
}
