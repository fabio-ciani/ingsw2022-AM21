package it.polimi.ingsw.eriantys.client.gui.controllers;

import it.polimi.ingsw.eriantys.client.gui.SceneName;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class WizardsController extends Controller {
	@FXML private BorderPane pane;
	@FXML private GridPane container;

	private EventHandler<MouseEvent> eventHandler;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		container.getChildren().forEach(x -> {
			ImageView img = (ImageView) x;
			img.setImage(new Image(getClass().getResource("/graphics/Wizards/" + x.getId() + "Wizard.png").toExternalForm()));
			roundBorders(img, 10);

			eventHandler = event -> {
				WaitingRoomController controller = (WaitingRoomController) app.getControllerForScene(SceneName.WAITING_ROOM);
				controller.setWizard(img.getId().toUpperCase());
				app.hideStickyPopup();
				event.consume();
			};
			img.addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler);
		});
	}

	@Override
	public Pane getTopLevelPane() {
		return pane;
	}

	public void populate(List<String> wizards) {
		container.getChildren().forEach(x -> {
			ImageView img = (ImageView) x;
			String id = img.getId();
			if (!wizards.contains(id.toUpperCase())) {
				applyGrayscale(img);
				img.removeEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler);
			}
		});
	}
}
