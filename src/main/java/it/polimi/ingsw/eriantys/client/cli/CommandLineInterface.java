package it.polimi.ingsw.eriantys.client.cli;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.eriantys.client.Client;
import it.polimi.ingsw.eriantys.client.UserInterface;
import it.polimi.ingsw.eriantys.controller.GameInfo;
import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.messages.Ping;
import it.polimi.ingsw.eriantys.messages.server.*;
import it.polimi.ingsw.eriantys.model.AssistantCard;
import it.polimi.ingsw.eriantys.model.BoardStatus;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class CommandLineInterface implements UserInterface {
	private Client client;
	private final Scanner scanner;
	private boolean running; // TODO: 10/05/2022 Set to false when "quit" command is typed, also handle client.running
	private final JsonObject characterCardInfo;

	public CommandLineInterface() throws IOException {
		this.scanner = new Scanner(System.in);
		try (InputStream in = getClass().getClassLoader().getResourceAsStream("help/characters.json")) {
			if (in == null) throw new FileNotFoundException();
			InputStreamReader reader = new InputStreamReader(in);
			Gson gson = new Gson();
			characterCardInfo = gson.fromJson(reader, JsonObject.class);
		}
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

	private boolean wrongArgNumber(String[] tokens, int expected) {
		int argsNumber = tokens.length - 1;
		if (argsNumber != expected) {
			showError(String.format("Expected %d argument(s), received %d", expected, argsNumber));
			return true;
		}
		return false;
	}

	private boolean wrongArgNumber(String[] tokens, int min, int max) {
		int argsNumber = tokens.length - 1;
		if (argsNumber < min || argsNumber > max) {
			showError(String.format("Expected between %d and %d arguments, received %d", min, max, argsNumber));
			return true;
		}
		return false;
	}

	@Override
	public void getInputs() {
		this.running = true;
		while (running) {
			String line = scanner.nextLine();
			String[] tokens = line.split("( )+");
			try {
				switch (tokens[0].toLowerCase()) {
					case "help", "h" -> {
						if (wrongArgNumber(tokens, 0)) return;
						client.askHelp();
					}
					case "user", "u" -> {
						if (wrongArgNumber(tokens, 1)) return;
						client.sendHandshake(tokens[1]);
					}
					case "lobbies", "l" -> {
						if (wrongArgNumber(tokens, 0)) return;
						client.askLobbies();
					}
					case "join", "j" -> {
						if (wrongArgNumber(tokens, 1)) return;
						client.joinLobby(tokens[1]);
					}
					case "create", "cr" -> {
						if (wrongArgNumber(tokens, 1, 2)) return;
						client.createLobby(tokens[1], tokens.length == 3 ? tokens[2] : "true");
					}
					case "leave", "e" -> {
						if (wrongArgNumber(tokens, 0)) return;
						client.leaveLobby();
					}
					case "wizard", "w" -> {
						if (wrongArgNumber(tokens, 1)) return;
						client.setWizard(tokens[1].toUpperCase());
					}
					case "tower", "t" -> {
						if (wrongArgNumber(tokens, 1)) return;
						client.setTowerColor(tokens[1].toUpperCase());
					}
					case "assistant", "a" -> {
						if (wrongArgNumber(tokens, 1)) return;
						client.playAssistantCard(tokens[1].toUpperCase());
					}
					case "dining", "sd" -> {
						// TODO: 13/05/2022 Replace "dining room" with GameConstants
						if (wrongArgNumber(tokens, 1)) return;
						client.moveStudent(tokens[1].toUpperCase(), "dining room");
					}
					case "island", "si" -> {
						if (wrongArgNumber(tokens, 2)) return;
						client.moveStudent(tokens[2].toUpperCase(), tokens[1]);
					}
					case "mother", "m" -> {
						if (wrongArgNumber(tokens, 1)) return;
						client.moveMotherNature(tokens[1]);
					}
					case "cloud", "cl" -> {
						if (wrongArgNumber(tokens, 1)) return;
						client.chooseCloud(Integer.parseInt(tokens[1]));
					}
					case "character", "ch" -> {
						if (wrongArgNumber(tokens, 1)) return;
						int id = Integer.parseInt(tokens[1]);
						client.selectCharacterCard(id);
						showCharacterCardArgs(id);
					}
					case "ccarguments", "ccargs" -> {
						if (wrongArgNumber(tokens, 0, 4)) return;
						parseCharacterCardArgs(Arrays.copyOfRange(tokens, 1, tokens.length));
					}
					case "bstat", "bs" -> {
						if (wrongArgNumber(tokens, 0)) return;
						showBoard();
					}
					case "sbstat", "sbs" -> {
						if (wrongArgNumber(tokens, 0)) return;
						showSchoolBoard();
					}
					case "acards", "acl" -> {
						if (wrongArgNumber(tokens, 0)) return;
						showAssistantCards();
					}
					case "ccards", "ccl" -> {
						if (wrongArgNumber(tokens, 0)) return;
						showCharacterCards();
					}
					case "cdesc", "cc" -> {
						if (wrongArgNumber(tokens, 1)) return;
						showCharacterDescription(tokens[1]);
					}
					case "pstat", "ps" -> {
						if (wrongArgNumber(tokens, 1)) return;
						showSchoolBoard(tokens[1]);
					}
					case "reconnect", "r" -> {
						if (wrongArgNumber(tokens, 0)) return;
						client.sendReconnect();
					}
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
				if (client.hasReconnectSettings())
					showInfo("Reconnection available, type /r or /reconnect to join");
			} else if (message instanceof AcceptedJoinLobby m) {
				client.setGameId(m.getGameId());
				client.putReconnectSettings(m);
			} else if (message instanceof AcceptedLeaveLobby) {
				client.removeReconnectSettings();
			}
		} else if (message instanceof Refused refused) {
			showError(refused.getDetails());
			if (message instanceof RefusedReconnect) client.removeReconnectSettings();
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
			if (message instanceof AssistantCardUpdate m) {
				client.setAvailableCards(m.getAvailableCards().get(client.getUsername()));
				if (!m.getPlayedCards().isEmpty()) {
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
			} else if (message instanceof BoardUpdate m) {
				client.setBoardStatus(m.getStatus());
			} else if (message instanceof CharacterCardUpdate m) {
				showInfo(m.getCard() + " played");
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
				showAssistantCards();
			} else if (message instanceof BoardUpdate m) {
				showInfo("It's your turn:\n1. move your students\n2. move Mother Nature\n3. select a cloud tile");
				client.setBoardStatus(m.getStatus());
			} else if (message instanceof GameOverUpdate m) {
				client.removeReconnectSettings();
				showInfo("Game over: the winner is " + m.getWinner());
			} else if (!(message instanceof CharacterCardUpdate)) {
				showInfo("Received " + message.getClass());
			}
		} else if (message instanceof InitialBoardStatus m) {
			showInfo("The game has begun!");
			client.setBoardStatus(m.getStatus());
		} else if (message instanceof DisconnectionUpdate m) {
			String subject = m.getSubject();
			int numPlayers = m.getNumPlayers();
			boolean gameIdle = m.isGameIdle();
			showInfo(subject + " has disconnected, " + numPlayers + " players currently connected"
					+ (gameIdle ? "\nGame idle" : ""));
		} else if (message instanceof ReconnectionUpdate m) {
			String subject = m.getSubject();
			int numPlayers = m.getNumPlayers();
			boolean gameResumed = m.isGameResumed();
			showInfo(subject + " has reconnected, " + numPlayers + " players currently connected"
					+ (gameResumed ? "\nGame resumed" : ""));
		} else if (message instanceof Ping) {
			client.write(new Ping());
		} else {
			showInfo("Received " + message.getClass());
		}
	}

	private void parseCharacterCardArgs(String[] args) {
		if (client.getCharacterCard() == null) {
			showError("Select a character card first");
			return;
		}
		List<String> sourceColors = new ArrayList<>();
		List<String> destinationColors = new ArrayList<>();
		String targetColor = null;
		String targetIsland = null;
		boolean source = true;
		for (String arg : args) {
			if (isColor(arg)) {
				if (sourceColors.isEmpty()) {
					if (targetColor == null) {
						targetColor = arg.toUpperCase();
					} else {
						sourceColors.add(targetColor);
						targetColor = null;
						destinationColors.add(arg.toUpperCase());
					}
				} else if (source) {
					sourceColors.add(arg.toUpperCase());
					source = false;
				} else {
					destinationColors.add(arg.toUpperCase());
					source = true;
				}
			} else if (targetIsland == null) {
				targetIsland = arg;
			} else {
				showError("Invalid arguments: more than one argument that is not a valid color");
				return;
			}
		}
		if (sourceColors.size() != destinationColors.size()) {
			showError("Invalid arguments: must provide the same number of source and destination colors");
			return;
		}
		client.playCharacterCard(sourceColors.toArray(new String[0]),
				destinationColors.toArray(new String[0]),
				targetColor,
				targetIsland);
	}

	private static boolean isColor(String s) {
		s = s.toUpperCase();
		return s.equals("YELLOW")
				|| s.equals("BLUE")
				|| s.equals("GREEN")
				|| s.equals("RED")
				|| s.equals("PINK");
	}

	private void showCharacterCards() {
		BoardStatus boardStatus = client.getBoardStatus();
		if (boardStatus == null) return;
		List<String> cards = boardStatus.getCharacterCards();
		if (cards == null) return;
		StringBuilder output = new StringBuilder("Available character cards for this game:");
		for (int i = 0; i < cards.size(); i++) {
			String card = cards.get(i);
			output.append("\n[").append(i).append("] ").append(card);
		}
		showInfo(output.toString());
	}

	private void showCharacterCardArgs(int id) {
		BoardStatus boardStatus = client.getBoardStatus();
		if (boardStatus == null) return;
		String card = boardStatus.getCharacterCards().get(id);
		if (!characterCardInfo.getAsJsonObject(card).has("cmd")) {
			client.playCharacterCard(null, null, null, null);
			return;
		}
		String cmd = characterCardInfo.getAsJsonObject(card).get("cmd").getAsString();
		showInfo("Set the arguments for the " + card + " character card using:\n " + cmd);
	}

	private void showCharacterDescription(String card) {
		if (!characterCardInfo.has(card)) {
			showError("Invalid character card name");
			return;
		}
		JsonObject cardInfo = characterCardInfo.getAsJsonObject(card);
		String setup = Optional.ofNullable(cardInfo.get("setup")).map(j -> "Setup: " + j.getAsString() + "\n").orElse("");
		String effect = "Effect: " + cardInfo.get("effect").getAsString();
		showInfo(setup + effect);
	}

	private void showBoard() {
		BoardStatus boardStatus = client.getBoardStatus();
		if (boardStatus == null) return;
		String output;
		GridBuilder islandGridBuilder = new GridBuilder(3);
		final List<String> islands = boardStatus.getIslands();
		final Map<String, Integer> islandsSizes = boardStatus.getIslandSizes();
		final Map<String, Map<String, Integer>> islandsStudents = boardStatus.getIslandStudents();
		final Map<String, String> islandsControllers = boardStatus.getIslandControllers();
		final String motherNatureIsland = boardStatus.getMotherNatureIsland();
		final Map<String, Integer> islandsNoEntryTiles = boardStatus.getIslandNoEntryTiles();
		for (String island : islands) {
			StringBuilder islandStringBuilder = new StringBuilder();
			String controller = Optional.ofNullable(islandsControllers.get(island)).orElse("none");
			islandStringBuilder.append("[").append(island).append("]")
					.append("\ngroup of ").append(islandsSizes.get(island)).append(" islands")
					.append("\ncontroller: ").append(controller)
					.append("\nstudents:");
			Map<String, Integer> students = islandsStudents.get(island);
			for (String color : students.keySet()) {
				islandStringBuilder.append("\n  ").append(students.get(color)).append(" ").append(color);
			}
			if (island.equals(motherNatureIsland)) {
				islandStringBuilder.append("\nhas Mother Nature");
			}
			Integer noEntryTiles = islandsNoEntryTiles.get(island);
			if (noEntryTiles != null && noEntryTiles > 0) {
				islandStringBuilder.append("\nhas ").append(noEntryTiles).append(" no entry tiles");
			}
			islandGridBuilder.add(islandStringBuilder.toString());
		}
		output = "Islands:\n" + islandGridBuilder;
		Map<String, Map<String, Integer>> cloudTiles = boardStatus.getCloudTiles();
		GridBuilder cloudGridBuilder = new GridBuilder(3);
		for (String cloud : cloudTiles.keySet()) {
			StringBuilder cloudStringBuilder = new StringBuilder();
			cloudStringBuilder.append("[").append(cloud).append("]");
			for (String color : cloudTiles.get(cloud).keySet()) {
				cloudStringBuilder.append("\n  ").append(cloudTiles.get(cloud).get(color)).append(" ").append(color);
			}
			cloudGridBuilder.add(cloudStringBuilder.toString());
		}
		output += "\n\nClouds:\n" + cloudGridBuilder;
		showInfo(output);
	}

	public void showSchoolBoard() {
		showSchoolBoard(client.getUsername());
	}

	public void showSchoolBoard(String player) {
		BoardStatus boardStatus = client.getBoardStatus();
		if (boardStatus == null) return;
		StringBuilder output = new StringBuilder(player).append("'s school board:");
		final List<String> players = boardStatus.getPlayers();
		if (!players.contains(player)) {
			showError(player + " not in player list");
			return;
		}
		final Map<String, Integer> playerEntrance = boardStatus.getPlayerEntrances().get(player);
		final Map<String, Integer> playerDiningRoom = boardStatus.getPlayerDiningRooms().get(player);
		final Integer playerTowers = boardStatus.getPlayerTowers().get(player);
		if (boardStatus.getPlayerCoins() != null) {
			final Integer playerCoins = boardStatus.getPlayerCoins().get(player);
			output.append("\ncoins: ").append(playerCoins);
		}
		final Map<String, String> professors = boardStatus.getProfessors();
		output.append("\ntowers: ").append(playerTowers);
		output.append("\nentrance:");
		for (String color : playerEntrance.keySet()) {
			output.append("\n  ").append(playerEntrance.get(color)).append(" ").append(color);
		}
		output.append("\ndining room:");
		for (String color : playerDiningRoom.keySet()) {
			output.append("\n  ").append(playerDiningRoom.get(color)).append(" ").append(color);
			if (Objects.equals(professors.get(color), player)) {
				output.append(" - PROFESSOR");
			}
		}
		showInfo(output.toString());
	}

	private void showAssistantCards() {
		List<String> cards = client.getAvailableCards();
		if (cards == null) return;
		StringBuilder output = new StringBuilder("Available assistant cards:");
		for (String cardName : cards) {
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
	}
}
