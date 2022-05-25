package it.polimi.ingsw.eriantys.client.gui.controllers;

import it.polimi.ingsw.eriantys.client.Client;
import it.polimi.ingsw.eriantys.client.gui.GraphicalApplication;
import javafx.fxml.Initializable;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.util.function.Consumer;

// The chain of invocation is the following: constructor, @FXML annotations resolution, initialize().
public abstract class Controller implements Initializable {
	protected GraphicalApplication app;
	protected Client client;
	protected Consumer<String> showInfo, showError;

	public void setApp(GraphicalApplication app) {
		this.app = app;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public void setShowInfo(Consumer<String> showInfo) {
		this.showInfo = showInfo;
	}

	public void setShowError(Consumer<String> showError) {
		this.showError = showError;
	}

	public void hideImage(ImageView img) {
		img.setVisible(false);
	}

	public void roundBorders(ImageView img, double arcSize) {
		Rectangle clip = new Rectangle(img.getFitWidth(), img.getFitHeight());
		clip.setArcWidth(arcSize);
		clip.setArcHeight(arcSize);
		img.setClip(clip);
	}

	public void applyGrayscale(ImageView img) {
		ColorAdjust grayscale = new ColorAdjust();
		grayscale.setSaturation(-1);
		img.setEffect(grayscale);
	}

	public void onChangeScene() {}

	public abstract Pane getTopLevelPane();
}
