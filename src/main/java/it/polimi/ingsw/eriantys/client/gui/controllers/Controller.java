package it.polimi.ingsw.eriantys.client.gui.controllers;

import it.polimi.ingsw.eriantys.client.Client;
import it.polimi.ingsw.eriantys.client.gui.GraphicalApplication;
import it.polimi.ingsw.eriantys.client.gui.PopupName;
import it.polimi.ingsw.eriantys.client.gui.SceneName;
import javafx.fxml.Initializable;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.function.Consumer;

// The chain of invocation is the following: constructor, @FXML annotations resolution, initialize().
/**
 * A class representing a GUI application controller which can manage {@code *.fxml} files and
 * display data accordingly inside them.
 * Every element of {@link SceneName} or {@link PopupName} enumerations should have a corresponding declaration
 * as a subclass of this non-specific controller.
 * @see javafx.fxml.Initializable
 */
public abstract class Controller implements Initializable {
	protected GraphicalApplication app;
	protected Client client;
	protected Consumer<String> showInfo, showError;

	/**
	 * Sets an internal reference to the GUI application.
	 * @param app the target app reference
	 * @see GraphicalApplication
	 */
	public void setApp(GraphicalApplication app) {
		this.app = app;
	}

	/**
	 * Sets an internal reference to the client who launched the GUI application.
	 * @param client the target client reference
	 * @see Client
	 */
	public void setClient(Client client) {
		this.client = client;
	}

	/**
	 * Sets the information show process coded within a {@link Consumer}.
	 * @param showInfo the functional interface which stores the show process behaviour
	 */
	public void setShowInfo(Consumer<String> showInfo) {
		this.showInfo = showInfo;
	}

	/**
	 * Sets the error show process coded within a {@link Consumer}.
	 * @param showError the functional interface which stores the show process behaviour
	 */
	public void setShowError(Consumer<String> showError) {
		this.showError = showError;
	}

	/**
	 * A helper method to round borders of an image.
	 * @param img the target image reference
	 * @param arcSize the desired size for the arc of the borders
	 */
	public void roundBorders(ImageView img, double arcSize) {
		Rectangle clip = new Rectangle(img.getFitWidth(), img.getFitHeight());
		clip.setArcWidth(arcSize);
		clip.setArcHeight(arcSize);
		img.setClip(clip);
	}

	/**
	 * A helper method to apply a grayscale filter on an image.
	 * The method could be used to visualize an item which cannot be selected.
	 * @param img the target image reference
	 */
	public void applyGrayscale(ImageView img) {
		ColorAdjust grayscale = new ColorAdjust();
		grayscale.setSaturation(-1);
		img.setEffect(grayscale);
	}

	/**
	 * A helper method to apply a green shade filter on an image.
	 * The method could be used to visualize an item which has been selected.
	 * @param img the target image reference
	 */
	public void applyGreenShade(ImageView img) {
		Blend blend = new Blend();
		Color color = new Color(0.55, 1, 0.35, 0.7);
		ColorInput topInput = new ColorInput(0, 0, img.getImage().getWidth(), img.getImage().getHeight(), color);
		blend.setTopInput(topInput);
		blend.setMode(BlendMode.MULTIPLY);
		img.setEffect(blend);
	}

	/**
	 * Encloses the workflow of {@code this} on a scene change event.
	 * The method is called before the implementation of the controller itself to pre-process eventual data.
	 * The default behaviour of the method returns immediately.
	 * Any controller should override the method if necessary.
	 */
	public void onChangeScene() {}

	/**
	 * A getter for the {@link Pane} object associated with the FXML tag of highest level
	 * in the {@code *.fxml} file associated with {@code this}.
	 * The method is called in order to process a popup.
	 * Any controller should override the method if necessary.
	 * @return the highest level {@link Pane}
	 */
	public abstract Pane getTopLevelPane();
}
