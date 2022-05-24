package it.polimi.ingsw.eriantys.client.gui;

import it.polimi.ingsw.eriantys.client.Client;
import it.polimi.ingsw.eriantys.client.gui.controllers.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class GraphicalApplication extends Application {
	private static GraphicalApplication app;
	private Stage stage;
	private static Client client;
	private static Consumer<String> showInfo, showError;
	private final Map<SceneName, Scene> sceneByName;
	private final Map<SceneName, Controller> controllerByScene;
	private SceneName currentScene;

	public GraphicalApplication() {
		app = this;
		sceneByName = new HashMap<>();
		controllerByScene = new HashMap<>();
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
		currentScene = SceneName.ASSISTANT_CARDS;
	}

	@Override
	public void start(Stage primaryStage) throws IOException {
		initialize();

		this.stage = primaryStage;

		Scene scene = sceneByName.get(currentScene);
		primaryStage.setTitle("Eriantys");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		center(scene);

		primaryStage.show();
		synchronized (client) {
			client.notifyAll();
		}
	}

	public void changeScene(SceneName sceneName) {
		currentScene = sceneName;
		Scene scene = sceneByName.get(currentScene);

		stage.setScene(scene);
		center(scene);

		controllerByScene.get(currentScene).onChangeScene();

		stage.show();
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

	private void center(Scene scene) {
		Rectangle2D screen = Screen.getPrimary().getVisualBounds();

		stage.setX((screen.getWidth() - scene.getWidth()) / 2);
		stage.setY((screen.getHeight() - scene.getHeight()) / 2);
	}
}