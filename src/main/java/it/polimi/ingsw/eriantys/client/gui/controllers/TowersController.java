package it.polimi.ingsw.eriantys.client.gui.controllers;

import it.polimi.ingsw.eriantys.client.gui.SceneName;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TowersController extends Controller {
	@FXML private BorderPane pane;
	@FXML private GridPane container;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		container.getChildren().forEach(x -> {
			ImageView img = (ImageView) x;
			img.setImage(new Image(getClass().getResource("/graphics/Towers/" + x.getId() + "Tower.png").toExternalForm()));

			img.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
				WaitingRoomController controller = (WaitingRoomController) app.getControllerForScene(SceneName.WAITING_ROOM);
				app.hideStickyPopup();
				controller.setTowerColor(img.getId().toUpperCase());
				event.consume();
			});
		});
	}

	@Override
	public Pane getTopLevelPane() {
		return pane;
	}

	public void populate(List<String> towerColors) {
		container.getChildren().forEach(x -> {
			ImageView img = (ImageView) x;
			String id = img.getId();
			if (!towerColors.contains(id.toUpperCase()))
				img.setVisible(false);
		});
	}
}
