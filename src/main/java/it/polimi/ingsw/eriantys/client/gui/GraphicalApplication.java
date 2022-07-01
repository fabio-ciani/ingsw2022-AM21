package it.polimi.ingsw.eriantys.client.gui;

import it.polimi.ingsw.eriantys.client.Client;
import it.polimi.ingsw.eriantys.client.UserInterface;
import it.polimi.ingsw.eriantys.client.gui.controllers.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * This class represents the JavaFX {@link Application} and handles stages, scenes and controllers.
 */
public class GraphicalApplication extends Application {
	private static GraphicalApplication app;
	private Stage primaryStage;
	private Stage openPopup;
	private static Client client;
	private static Consumer<String> showInfo, showError;
	private final Map<SceneName, Scene> sceneByName;
	private final Map<SceneName, Controller> controllerByScene;
	private final Map<PopupName, Scene> popupByName;
	private final Map<PopupName, Controller> controllerByPopup;
	private SceneName currentScene;

	/**
	 * Constructs a {@link GraphicalApplication} initializing the {@link Map} objects to keep track of scenes and controllers.
	 * Saves a reference to the {@link Application} in a static attribute.
	 */
	public GraphicalApplication() {
		app = this;
		sceneByName = new HashMap<>();
		controllerByScene = new HashMap<>();
		popupByName = new HashMap<>();
		controllerByPopup = new HashMap<>();
	}

	/**
	 * Static getter for the (unique) instance of the JavaFX {@link Application}.
	 * @return a reference to the instance of the application
	 */
	public static GraphicalApplication getInstance() {
		return app;
	}

	/**
	 * This method is called at the beginning of the {@link #start} method.
	 * Initializes all the scenes and controllers and sets the initial scene.
	 *
	 * @throws IOException if the {@link FXMLLoader} fails loading
	 */
	public void initialize() throws IOException {
		for (SceneName scene : SceneName.values()) {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(scene.getPath()));
			sceneByName.put(scene, new Scene(loader.load(), 1280, 720));
			Controller controller = loader.getController();
			controller.setApp(this);
			controller.setClient(client);
			controller.setShowInfo(showInfo);
			controller.setShowError(showError);
			controllerByScene.put(scene, controller);
		}

		for (PopupName popup : PopupName.values()) {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(popup.getPath()));
			popupByName.put(popup, new Scene(loader.load(), Color.TRANSPARENT));
			Controller controller = loader.getController();
			controller.setApp(this);
			controller.setClient(client);
			controller.setShowInfo(showInfo);
			controller.setShowError(showError);
			controllerByPopup.put(popup, controller);
		}

		currentScene = SceneName.LOGIN;
	}

	/**
	 * Calls the {@link #initialize} method and then sets up the primary stage and starts the graphical application.
	 *
	 * @param primaryStage The primary stage for this application, onto which the application scene can be set
	 * @throws IOException if the initialization fails
	 */
	@Override
	public void start(Stage primaryStage) throws IOException {
		initialize();

		this.primaryStage = primaryStage;

		Scene scene = sceneByName.get(currentScene);
		scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
		Font.loadFont(getClass().getResource("/fonts/CelticGaramond.ttf").toExternalForm(), 12);
		primaryStage.setTitle("Eriantys");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		center(scene);

		controllerByScene.get(currentScene).onChangeScene();

		primaryStage.getIcons().add(new Image(getClass().getResource("/icon/icon.png").toExternalForm()));
		primaryStage.show();
		synchronized (client) {
			client.notifyAll();
		}
	}

	/**
	 * Changes the current scene and calls the {@link Controller#onChangeScene()} method
	 * of the controller associated with the new scene.
	 *
	 * @param sceneName the name of the scene to change to
	 */
	public void changeScene(SceneName sceneName) {
		Scene scene = sceneByName.get(sceneName);
		scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
		Font.loadFont(getClass().getResource("/fonts/CelticGaramond.ttf").toExternalForm(), 12);

		primaryStage.setScene(scene);
		currentScene = sceneName;

		controllerByScene.get(currentScene).onChangeScene();

		primaryStage.show();
	}

	/**
	 * Sets the {@link #client} static attribute with a reference to the {@link Client}.
	 *
	 * @param client the reference to the client object
	 */
	public static void setClient(Client client) {
		GraphicalApplication.client = client;
	}

	/**
	 * Sets a reference to the {@link UserInterface#showInfo(String)} method as a {@link Consumer} of {@link String}.
	 *
	 * @param showInfo the reference to the {@code showInfo} method
	 */
	public static void setShowInfo(Consumer<String> showInfo) {
		GraphicalApplication.showInfo = showInfo;
	}

	/**
	 * Sets a reference to the {@link UserInterface#showError(String)} method as a {@link Consumer} of {@link String}.
	 * @param showError the reference to the {@code showError} method
	 */
	public static void setShowError(Consumer<String> showError) {
		GraphicalApplication.showError = showError;
	}

	/**
	 * Getter for the current {@link Scene}.
	 *
	 * @return a reference to the current scene
	 */
	public SceneName getCurrentScene() {
		return currentScene;
	}

	/**
	 * Getter for the current controller, which is the {@link Controller} associated with
	 * the current scene returned from the {@link #getCurrentScene()} method.
	 *
	 * @return a reference to the current controller
	 */
	public Controller getCurrentController() {
		return controllerByScene.get(currentScene);
	}

	/**
	 * Getter for the {@link Controller} associated with a given scene.
	 *
	 * @param sceneName the name of the scene to which the controller is associated
	 * @return the controller associated with the given scene
	 */
	public Controller getControllerForScene(SceneName sceneName) {
		return controllerByScene.get(sceneName);
	}

	/**
	 * Getter for the {@link Controller} associated with a given popup scene.
	 *
	 * @param popupName the name of the popup scene to which the controller is associated
	 * @return the controller associated with the given popup scene
	 */
	public Controller getControllerForPopup(PopupName popupName) {
		return controllerByPopup.get(popupName);
	}

	/**
	 * Shows a popup on the screen (on top of the main scene).
	 *
	 * @param popupName the name of the popup scene to show
	 */
	public void showStickyPopup(PopupName popupName) {
		if (openPopup != null) throw new RuntimeException("A popup is already open");

		Pane background = getCurrentController().getTopLevelPane();
		background.setEffect(new GaussianBlur(3));

		openPopup = new Stage(StageStyle.TRANSPARENT);
		openPopup.initOwner(primaryStage);
		openPopup.initModality(Modality.APPLICATION_MODAL);
		openPopup.setScene(popupByName.get(popupName));
		openPopup.show();
	}

	/**
	 * Hides a popup previously opened on the screen, if present.
	 */
	public void hideStickyPopup() {
		if (openPopup == null) throw new RuntimeException("No popup is currently open");

		Pane background = getCurrentController().getTopLevelPane();
		background.setEffect(null);
		openPopup.hide();
		openPopup = null;
	}

	/**
	 * Centers a scene in the middle of the screen.
	 *
	 * @param scene the scene to center
	 */
	private void center(Scene scene) {
		Rectangle2D screen = Screen.getPrimary().getVisualBounds();

		primaryStage.setX((screen.getWidth() - scene.getWidth()) / 2);
		primaryStage.setY((screen.getHeight() - scene.getHeight()) / 2);
	}
}
