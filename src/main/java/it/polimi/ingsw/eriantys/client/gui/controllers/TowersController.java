package it.polimi.ingsw.eriantys.client.gui.controllers;

import it.polimi.ingsw.eriantys.client.gui.PopupName;
import it.polimi.ingsw.eriantys.client.gui.SceneName;
import it.polimi.ingsw.eriantys.model.TowerColor;
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

/**
 * A class representing the controller for the {@code TOWERS} popup scene.
 * @see PopupName#TOWERS
 * @see javafx.scene.Scene
 */
public class TowersController extends Controller {
	@FXML private BorderPane pane;
	@FXML private GridPane container;

	/**
	 * Gets all the child nodes representing the elements of the scene from the FXML.
	 * Associates the event handlers with the images on the scene.
	 */
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

	/**
	 * Gets the information about tower colors from passed parameter and draws all the elements in the scene.
	 * @param towerColors the available {@link TowerColor} literals to choose
	 */
	public void populate(List<String> towerColors) {
		container.getChildren().forEach(x -> {
			ImageView img = (ImageView) x;
			String id = img.getId();
			if (!towerColors.contains(id.toUpperCase()))
				img.setVisible(false);
		});
	}
}
