package it.polimi.ingsw.eriantys.client.gui.controllers;

import it.polimi.ingsw.eriantys.client.gui.PopupName;
import it.polimi.ingsw.eriantys.client.gui.SceneName;
import it.polimi.ingsw.eriantys.model.Wizard;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * A class representing the controller for the {@code WIZARDS} popup scene.
 * @see PopupName#WIZARDS
 * @see javafx.scene.Scene
 */
public class WizardsController extends Controller {
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
			img.setImage(new Image(getClass().getResource("/graphics/Wizards/" + x.getId() + "Wizard.png").toExternalForm()));
			roundBorders(img, 10);

			img.setOnMouseClicked(event -> {
				WaitingRoomController controller = (WaitingRoomController) app.getControllerForScene(SceneName.WAITING_ROOM);
				controller.setWizard(img.getId().toUpperCase());
				app.hideStickyPopup();
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
	 * @param wizards the available {@link Wizard} literals to choose
	 */
	public void populate(List<String> wizards) {
		container.getChildren().forEach(x -> {
			ImageView img = (ImageView) x;
			String id = img.getId();
			if (!wizards.contains(id.toUpperCase())) {
				applyGrayscale(img);
				img.setOnMouseClicked(Event::consume);
			}
		});
	}
}
