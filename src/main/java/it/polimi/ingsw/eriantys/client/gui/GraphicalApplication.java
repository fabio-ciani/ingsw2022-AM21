package it.polimi.ingsw.eriantys.client.gui;

import it.polimi.ingsw.eriantys.client.Client;
import it.polimi.ingsw.eriantys.client.gui.controllers.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

	public GraphicalApplication() {
		app = this;
		sceneByName = new HashMap<>();
		controllerByScene = new HashMap<>();
		popupByName = new HashMap<>();
		controllerByPopup = new HashMap<>();
	}

	public static GraphicalApplication getInstance() {
		return app;
	}

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

	public void changeScene(SceneName sceneName) {
		Scene scene = sceneByName.get(sceneName);
		scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
		Font.loadFont(getClass().getResource("/fonts/CelticGaramond.ttf").toExternalForm(), 12);

		primaryStage.setScene(scene);
		currentScene = sceneName;
		center(scene);

		controllerByScene.get(currentScene).onChangeScene();

		primaryStage.show();
	}

	public static void setClient(Client client) {
		GraphicalApplication.client = client;
	}

	public static void setShowInfo(Consumer<String> showInfo) {
		GraphicalApplication.showInfo = showInfo;
	}

	public static void setShowError(Consumer<String> showError) {
		GraphicalApplication.showError = showError;
	}

	public SceneName getCurrentScene() {
		return currentScene;
	}

	public Controller getCurrentController() {
		return controllerByScene.get(currentScene);
	}

	public Controller getControllerForScene(SceneName sceneName) {
		return controllerByScene.get(sceneName);
	}

	public Controller getControllerForPopup(PopupName popupName) {
		return controllerByPopup.get(popupName);
	}

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

	public void hideStickyPopup() {
		if (openPopup == null) throw new RuntimeException("No popup is currently open");

		Pane background = getCurrentController().getTopLevelPane();
		background.setEffect(null);
		openPopup.hide();
		openPopup = null;
	}

	private void center(Scene scene) {
		Rectangle2D screen = Screen.getPrimary().getVisualBounds();

		primaryStage.setX((screen.getWidth() - scene.getWidth()) / 2);
		primaryStage.setY((screen.getHeight() - scene.getHeight()) / 2);
	}
}
