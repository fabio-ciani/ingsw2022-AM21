package it.polimi.ingsw.eriantys.client.gui.controllers;

import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class CharacterCardsController extends Controller {
	@FXML private BorderPane pane;
	@FXML private GridPane cards;
	private final List<String> characters;
	private final Map<String, Map<String, String>> info;

	public CharacterCardsController() throws IOException {
		characters = new ArrayList<>();
		characters.add("Knight");
		characters.add("Minstrel");
		characters.add("Thief");

		try (InputStream in = getClass().getClassLoader().getResourceAsStream("help/characters.json")) {
			if (in == null) throw new FileNotFoundException();
			Gson gson = new Gson();
			info = gson.fromJson(new InputStreamReader(in), LinkedHashMap.class);
		}
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		drawImages();
		drawLabels();
	}

	@Override
	public Pane getTopLevelPane() {
		return pane;
	}

	private void drawImages() {
		cards.getChildren().stream()
				.filter(x -> x instanceof ImageView && x.getId().matches("^\\w+img\\z"))
				.forEach(x -> {
					ImageView img = (ImageView) x;
					String character = characters.get(Character.getNumericValue(x.getId().charAt(1)));

					img.setImage(new Image(getClass().getResource("/graphics/CharacterCards/" + character + ".jpg").toExternalForm()));

					roundBorders(img, 30);

					Tooltip desc = generateDescription(character);
					// desc.setShowDelay(Duration.ZERO);
					Tooltip.install(img, desc);

					// TODO: if result of the difference between the actual cost and the cost from JSON is 1, then draw the coin
					/*
					cards.getChildren().stream()
							.filter(y -> y instanceof ImageView && y.getId().equals("c" + x.getId().charAt(1) + "_coin"))
							.forEach(y -> hideImage((ImageView) y));
					*/

					// TODO: if character card status is not null, then add an event handler to the mouse right click to show an information alert
					x.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
						if (event.getButton() == MouseButton.SECONDARY) {
							Alert alert = new Alert(Alert.AlertType.INFORMATION);
							alert.setTitle("Character card status");
							alert.setHeaderText("You are visualising the " + character + " status.");
							// alert.setContentText("?");
							alert.show();

							event.consume();
						}
					});
				});
	}

	private Tooltip generateDescription(String character) {
		Map<String, String> characterInfo = info.get(character);

		String setup = characterInfo.get("setup");
		String effect = characterInfo.get("effect");

		StringBuilder out = new StringBuilder();

		if (setup != null) out.append(prettifyDesc(setup)).append("\n\n");
		out.append(prettifyDesc(effect));

		return new Tooltip(out.toString());
	}

	private String prettifyDesc(String in) {
		StringBuilder out = new StringBuilder();
		char c;

		for (int i = 0; i < in.length(); i++) {
			c = in.charAt(i);
			if (c == ' ' && in.charAt(i - 1) == '.')
				out.append("\n");
			else out.append(c);
		}

		return out.toString();
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
