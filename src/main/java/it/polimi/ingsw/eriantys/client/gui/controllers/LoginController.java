package it.polimi.ingsw.eriantys.client.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController extends Controller {
	@FXML private TextField username;
	@FXML private Button login;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		username.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() != KeyCode.ENTER) return;
			sendHandshake();
		});

		login.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> this.sendHandshake());
	}

	@Override
	public void onChangeScene() {

	}

	private void sendHandshake() {
		String chosenUsername = username.getText();
		client.sendHandshake(chosenUsername);
	}
}
