package it.polimi.ingsw.eriantys.client.cli;

import it.polimi.ingsw.eriantys.client.Client;
import it.polimi.ingsw.eriantys.client.GameStatus;
import it.polimi.ingsw.eriantys.client.UserInterface;
import it.polimi.ingsw.eriantys.controller.GameInfo;
import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.messages.server.*;
import it.polimi.ingsw.eriantys.model.AssistantCard;

import java.util.List;
import java.util.Map;
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
		System.out.println(details + "\n");
	}

	@Override
	public synchronized void showError(String details) {
		System.out.println(ConsoleColors.ANSI_RED + details + ConsoleColors.ANSI_RESET + "\n");
	}

	@Override
	public synchronized void showStatus(GameStatus status) {
		// TODO: 03/05/2022 Print formatted game status
		System.out.println(ConsoleColors.ANSI_RED + "Not implemented yet" + ConsoleColors.ANSI_RESET);
	}

	private boolean checkArgsNumber(String[] tokens, int expected) {
		int argsNumber = tokens.length - 1;
		if (argsNumber != expected) {
			showError(String.format("Expected %d argument(s), received %d", expected, argsNumber));
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
				switch (tokens[0].toLowerCase()) {
					case "help", "h" -> {
						if (checkArgsNumber(tokens, 0)) client.askHelp();
					}
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
					case "leave", "e" -> {
						if (checkArgsNumber(tokens, 0)) client.leaveLobby();
					}
					case "wizard", "w" -> {
						if (checkArgsNumber(tokens, 1)) client.setWizard(tokens[1].toUpperCase());
					}
					case "tower", "t" -> {
						if (checkArgsNumber(tokens, 1)) client.setTowerColor(tokens[1].toUpperCase());
					}
					case "assistant", "a" -> {
						if (checkArgsNumber(tokens, 1)) client.playAssistantCard(tokens[1].toUpperCase());
					}
					case "dining", "sd" -> {
						// TODO: 13/05/2022 Replace "dining room" with GameConstants
						if (checkArgsNumber(tokens, 1)) client.moveStudent(tokens[1], "dining room");
					}
					case "island", "si" -> {
						if (checkArgsNumber(tokens, 2)) client.moveStudent(tokens[1], tokens[2]);
					}
					case "mother", "m" -> {
						if (checkArgsNumber(tokens, 1)) client.moveMotherNature(tokens[1]);
					}
					case "cloud", "cl" -> {
						if (checkArgsNumber(tokens, 1)) client.chooseCloud(Integer.parseInt(tokens[1]));
					}
					case "character", "ch" -> {
						if (checkArgsNumber(tokens, 1)) client.playCharacterCard(Integer.parseInt(tokens[1]));
						// TODO: 16/05/2022
					}
					case "ccarguments", "ccargs" -> {
						// TODO: 16/05/2022
					}
					case "bstat", "bs" -> {

					}
					// TODO: 16/05/2022 printAssistantCards() called by command /acard and message handlers?
					default -> showError("Invalid command");
				}
			} catch (IndexOutOfBoundsException e) {
				showError("Missing argument");
			} catch (NumberFormatException e) {
				showError("Invalid number format");
			}
		}
	}

	@Override
	public void handleMessage(Message message) {
		if (message instanceof Accepted) {
			showInfo("Ok");
			if (message instanceof AcceptedUsername m) {
				client.setUsername(m.getUsername());
			} else if (message instanceof AcceptedJoinLobby m) {
				client.setGameId(m.getGameId());
				client.setPasscode(m.getPasscode());
			}
		} else if (message instanceof Refused refused) {
			showError(refused.getDetails());
		} else if (message instanceof HelpResponse m) {
			showInfo(m.getContent());
		} else if (message instanceof AvailableLobbies availableLobbies) {
			List<GameInfo> lobbies = availableLobbies.getLobbies();
			if (lobbies.isEmpty()) {
				showInfo("No available lobbies");
			} else {
				for (GameInfo lobby : availableLobbies.getLobbies()) {
					showInfo(lobby.toString());
				}
			}
		} else if (message instanceof LobbyUpdate m) {
			StringBuilder output = new StringBuilder("Players in the lobby:");
			for (String player : m.getPlayers()) {
				output.append("\n- ").append(player);
			}
			showInfo(output.toString());
		} else if (message instanceof UserActionUpdate m1) {
			String nextPlayer = m1.getNextPlayer();
			if (message instanceof AssistantCardUpdate m && !m.getPlayedCards().isEmpty()) {
				StringBuilder output = new StringBuilder("Played assistant cards:");
				Map<String, String> playedCards = m.getPlayedCards();
				for (String player : playedCards.keySet()) {
					String cardName = playedCards.get(player);
					AssistantCard card = AssistantCard.valueOf(cardName);
					output.append("\n- ")
							.append(player)
							.append(" \u2192 ")
							.append(cardName)
							.append(" (value = ")
							.append(card.value())
							.append(", movement = ")
							.append(card.movement())
							.append(")");
				}
				showInfo(output.toString());
			}
			if (!nextPlayer.equals(client.getUsername())) {
				showInfo(String.format("Waiting for %s to play", nextPlayer));
				return;
			}
			if (message instanceof UserSelectionUpdate m) {
				StringBuilder output;
				if (client.getTowerColor() == null) {
					output = new StringBuilder();
					output.append("Choose a tower color between:");
					for (String color : m.getAvailableTowerColors()) {
						output.append("\n- ").append(color);
					}
					showInfo(output.toString());
				}
				if (client.getWizard() == null) {
					output = new StringBuilder();
					output.append("Choose a wizard between:");
					for (String wizard : m.getAvailableWizards()) {
						output.append("\n- ").append(wizard);
					}
					showInfo(output.toString());
				}
			} else if (message instanceof AssistantCardUpdate m) {
				StringBuilder output;
				output = new StringBuilder();
				output.append("Play an assistant card:");
				for (String cardName : m.getAvailableCards().get(client.getUsername())) {
					AssistantCard card = AssistantCard.valueOf(cardName);
					output.append("\n- ")
							.append(cardName)
							.append(" (value = ")
							.append(card.value())
							.append(", movement = ")
							.append(card.movement())
							.append(")");
				}
				showInfo(output.toString());
			} else {
				showInfo("Received " + message.getClass());
			}
		} else if (message instanceof InitialBoardStatus m) {
			showInfo("The game has begun!");
			// TODO: 13/05/2022 Print initial board status
		} else {
			showInfo("Received " + message.getClass());
		}
	}
}
