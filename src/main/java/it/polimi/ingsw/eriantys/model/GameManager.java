package it.polimi.ingsw.eriantys.model;

import it.polimi.ingsw.eriantys.model.influence.InfluenceCalculator;
import it.polimi.ingsw.eriantys.model.characters.CharacterCard;

import java.util.Map;

public class GameManager {
    private Board board;
    private PlayerList players;
    private Player currPlayer;
    private ProfessorOwnership professors;
    private InfluenceCalculator calc;
    private CharacterCard[] characters;
    // TODO: Will the constants be managed with a GameConfig object or by declaring them as attributes of GameManager?

    // TODO: Does getCurrPlayer() return a string?

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

    }

    public void handleMovedStudents(String nickname, Tuple<String, String> movedStudents) {

    }

    public void handleMotherNatureMovement(String islandDestination) {

    }

    public boolean resolve(IslandGroup island) {
        return false;
    }

    public void handleSelectedCloud(String nickname, int cloudIndex) {

    }

    public void changeInfluenceState(InfluenceCalculator calculator) {

    }

    public void handleCharacterCard() {

    }

    public boolean gameOver() {
        return false;
    }
}