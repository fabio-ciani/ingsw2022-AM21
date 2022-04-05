package it.polimi.ingsw.eriantys.model;

import it.polimi.ingsw.eriantys.model.characters.CharacterCard;
import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;
import it.polimi.ingsw.eriantys.model.influence.InfluenceCalculator;

import java.util.List;
import java.util.Map;

public class GameManager {
    private Board board;
    private PlayerList players;
    private Player currPlayer;
    private ProfessorOwnership professors;
    private InfluenceCalculator calc;
    private CharacterCard[] characters;
    // TODO: Will the constants be managed with a GameConfig object or by declaring them as attributes of GameManager?

    public String getCurrPlayer() {
        return currPlayer.getNickname();
    }

    public ProfessorOwnership getOwnerships() {
        return professors;
    }

    public void setupBoard() {

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

    }

    public boolean resolve(IslandGroup island) {
        return false;
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

    public void changeInfluenceState(InfluenceCalculator calculator) {
        if (calculator == null)
            throw new IllegalArgumentException("Parameter should not be null.");
        calc = calculator;
    }

    public void handleCharacterCard() {

    }

    public boolean gameOver() {
        return false;
    }
}