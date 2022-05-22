package it.polimi.ingsw.eriantys.client.gui;

import it.polimi.ingsw.eriantys.client.gui.controllers.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class JavaFXExample extends Application {
	private Stage stage;

	/*
	public static void main(String[] args) {
		launch();
	}
	*/

	@Override
	public void start(Stage primaryStage) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/character_cards.fxml"));
		Scene scene = new Scene(loader.load(), 1280, 720);

		Controller c = loader.getController();
		c.setApp(this);

		this.stage = primaryStage;

		primaryStage.setTitle("Eryantis");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		center(scene);

		primaryStage.show();
	}

	public void changeScene(String res) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(res));
		Scene scene = new Scene(loader.load(), 1280, 720);

		/*
		Controller c = loader.getController();
		c.setApp(this);
		*/

		stage.setScene(scene);
		center(scene);

		stage.show();
	}

	private void center(Scene scene) {
		Rectangle2D screen = Screen.getPrimary().getVisualBounds();

		stage.setX((screen.getWidth() - scene.getWidth()) / 2);
		stage.setY((screen.getHeight() - scene.getHeight()) / 2);
	}
}
