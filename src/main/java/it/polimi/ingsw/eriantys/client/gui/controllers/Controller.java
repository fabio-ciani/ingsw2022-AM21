package it.polimi.ingsw.eriantys.client.gui.controllers;

import javafx.application.Application;
import javafx.fxml.Initializable;

// The chain of invocation is the following: constructor, @FXML annotations resolution, initialize().
public abstract class Controller implements Initializable {
	private Application app;

	public void setApp(Application app) {
		this.app = app;
	}
}
