package it.polimi.ingsw.eriantys.client.gui.controllers;

import it.polimi.ingsw.eriantys.client.Client;
import it.polimi.ingsw.eriantys.client.gui.GraphicalApplication;
import javafx.fxml.Initializable;

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
}
