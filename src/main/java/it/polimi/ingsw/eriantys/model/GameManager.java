package it.polimi.ingsw.eriantys.model;

import it.polimi.ingsw.eriantys.model.characters.*;
import it.polimi.ingsw.eriantys.model.exceptions.IllegalInfluenceStateException;
import it.polimi.ingsw.eriantys.model.exceptions.IslandNotFoundException;
import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;
import it.polimi.ingsw.eriantys.model.influence.CommonInfluence;
import it.polimi.ingsw.eriantys.model.influence.InfluenceCalculator;

import java.util.*;

public class GameManager {
	private final Board board;
  private final PlayerList players;
  private Player currPlayer;
  private final ProfessorOwnership professors;
	private InfluenceCalculator calc;
  private final CharacterCard[] characters;
	protected final int CLOUD_SIZE;
	protected final int CLOUD_NUMBER;
	protected final int ENTRANCE_SIZE;
	protected final int TOWER_NUMBER;
    // TODO: Will the constants be managed with a GameConfig object or by declaring them as attributes of GameManager?

	public GameManager(List<String> nicknames, boolean expertMode) {
		int numPlayers = nicknames.size();
		CLOUD_NUMBER = numPlayers;

		if (numPlayers == 3) {
			CLOUD_SIZE = 4;
			ENTRANCE_SIZE = 9;
			TOWER_NUMBER = 6;
		} else {
			CLOUD_SIZE = 3;
			ENTRANCE_SIZE = 7;
			TOWER_NUMBER = 8;
		}

		board = new Board(CLOUD_NUMBER, CLOUD_SIZE);
		players = new PlayerList(nicknames);
		professors = new ProfessorOwnership(this::currentPlayer);
		calc = new CommonInfluence();

		if (expertMode) {
			characters = new CharacterCard[3];
			initCharacterCards();
		} else
			characters = null;
	}

    public String getCurrPlayer() {
        return currPlayer.getNickname();
    }

    public ProfessorOwnership getOwnerships() {
        return professors;
    }

    public void setupBoard() {
		board.setup();

		for (CharacterCard character : characters) {
			try {
				character.setupEffect();
			} catch (NoMovementException e) {
				// TODO handle exception
				e.printStackTrace();
			}
		}
    }

    public void setupPlayer(String nickname, TowerColor towerColor, Wizard wizard) {

    }

    public void setupRound() {

    }

    public void handleAssistantCards(Map<Player, AssistantCard> playedCards) {
        Player min = null;
        for (Player p : playedCards.keySet())
            if (min == null)
                min = p;
            else if (playedCards.get(p).value() < playedCards.get(min).value())
                min = p;

        players.setFirst(min);
    }

    /**
     * A controller dedicated getter for a {@link List} containing the turn order of the current round.
     * @return the reference to a {@link List} containing the nicknames of the players and stating the turn order
     */
    public List<String> getTurnOrder() {
        List<Player> playerOrder = players.getTurnOrder();

        return playerOrder
                .stream()
                .map(Player::getNickname)
                .toList();
    }

    public void handleMovedStudents(String nickname, Tuple<String, String> movedStudents) {

    }

    public void handleMotherNatureMovement(String islandDestination) {
		IslandGroup destination = tryGetIsland(islandDestination);

		if (destination == null)
			return;

		boolean movementSuccessful = board.moveMotherNature(destination);
		if (movementSuccessful) {
			boolean controllerChanged = resolve(destination);
			if (controllerChanged) {
				try {
					board.unifyIslands(destination);
				} catch (IslandNotFoundException e) {
					// TODO handle exception
					e.printStackTrace();
				}
			}
		}
    }

    public boolean resolve(IslandGroup island) {
		List<Player> players = this.players.getTurnOrder();
	    Player maxInfluencePlayer = players.get(0);
		int maxInfluence = 0;
		try {
			maxInfluence = calc.calculate(maxInfluencePlayer, island, professors.getProfessors(maxInfluencePlayer));
		} catch (IllegalInfluenceStateException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		for (Player player : players) {
			int influence = 0;
			try {
				influence = calc.calculate(player, island, professors.getProfessors(player));
			} catch (IllegalInfluenceStateException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			if (influence > maxInfluence) {
				maxInfluence = influence;
				maxInfluencePlayer = player;
			}
		}

		boolean res = !island.getController().equals(maxInfluencePlayer);
		island.setController(maxInfluencePlayer);
		return res;
    }

	public void handleSelectedCloud(String nickname, int cloudIndex) {
		Player recipient = players.get(nickname);
		try {
			board.drawStudents(cloudIndex, recipient);
		} catch (NoMovementException e) {
			// TODO handle exception
			e.printStackTrace();
		}
	}

    public void changeInfluenceState(InfluenceCalculator calculator) throws IllegalInfluenceStateException {
        if (calculator == null)
            throw new IllegalInfluenceStateException("Parameter should not be null.");
        calc = calculator;
    }

    public void handleCharacterCard() {

    }

    public boolean gameOver() {
        return false;
    }

	private void initCharacterCards() {
		List<Integer> indexes = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
		Collections.shuffle(indexes);
		int first = indexes.remove(0);
		int second = indexes.remove(0);
		int third = indexes.remove(0);

		characters[0] = getCharacter(first);
		characters[1] = getCharacter(second);
		characters[2] = getCharacter(third);
	}

	private CharacterCard getCharacter(int index) {
		return switch (index) {
			case 1 -> new Centaur(this);
			case 2 -> new Farmer(professors, this::currentPlayer);
			case 3 -> new Herald(this);
			case 4 -> new HerbGranny(board);
			case 5 -> new Jester(board.getBag(), this::currentPlayer);
			case 6 -> new Knight(this, this::currentPlayer);
			case 7 -> new MagicPostman(this::currentPlayer);
			case 8 -> new Minstrel(this::currentPlayer);
			case 9 -> new Monk(board.getBag());
			case 10 -> new MushroomGuy(this);
			case 11 -> new SpoiledPrincess(board.getBag(), this::currentPlayer);
			case 12 -> new Thief(players.getTurnOrder(), board.getBag());
			default -> null;
		};
	}

	private Player currentPlayer() {
		return currPlayer;
	}

	private IslandGroup tryGetIsland(String islandId) {
		try {
			return board.getIsland(islandId);
		} catch (IslandNotFoundException e) {
			return null;
		}
	}
}