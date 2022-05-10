package it.polimi.ingsw.eriantys.client.cli;

import it.polimi.ingsw.eriantys.client.Client;
import it.polimi.ingsw.eriantys.client.GameStatus;
import it.polimi.ingsw.eriantys.client.UserInterface;

import java.util.Scanner;

public class CommandLineInterface implements UserInterface {
	private Client client;
	private final Scanner scanner;
	private boolean running; // TODO: 10/05/2022 Set to false when "quit" command is typed, also handle client.running

	public CommandLineInterface() {
		this.scanner = new Scanner(System.in);
	}

	@Override
	public void setClient(Client client) {
		this.client = client;
	}

	@Override
	public synchronized void showInfo(String details) {
		System.out.println(details);
	}

	@Override
	public synchronized void showError(String details) {
		System.out.println(ConsoleColors.ANSI_RED + details + ConsoleColors.ANSI_RESET);
	}

	@Override
	public synchronized void showStatus(GameStatus status) {
		// TODO: 03/05/2022 Print formatted game status
		System.out.println(ConsoleColors.ANSI_RED + "Not implemented yet" + ConsoleColors.ANSI_RESET);
	}

	private boolean checkArgsNumber(String[] tokens, int expected) {
		int argsNumber = tokens.length - 1;
		if (argsNumber != expected) {
			showError(String.format("Expected %d arguments, received %d", expected, tokens.length));
			return false;
		}
		return true;
	}

	private boolean checkArgsNumber(String[] tokens, int min, int max) {
		int argsNumber = tokens.length - 1;
		if (argsNumber < min || argsNumber > max) {
			showError(String.format("Expected between %d and %d arguments, received %d", min, max, argsNumber));
			return false;
		}
		return true;
	}

	@Override
	public void getInputs() {
		this.running = true;
		while (running) {
			String line = scanner.nextLine();
			String[] tokens = line.split("( )+", 0);
			try {
				switch (tokens[0]) {
					case "user", "u" -> {
						if (checkArgsNumber(tokens, 1)) client.sendHandshake(tokens[1]);
					}
					case "lobbies", "l" -> {
						if (checkArgsNumber(tokens, 0)) client.askLobbies();
					}
					case "join", "j" -> {
						if (checkArgsNumber(tokens, 1)) client.joinLobby(tokens[1]);
					}
					case "create", "cr" -> {
						if (checkArgsNumber(tokens, 1, 2))
							client.createLobby(tokens[1], tokens.length == 3 ? tokens[2] : "true");
					}
					default -> showError("Invalid command");
				}
			} catch (IndexOutOfBoundsException e) {
				showError("Missing argument");
			}
		}
	}
}
