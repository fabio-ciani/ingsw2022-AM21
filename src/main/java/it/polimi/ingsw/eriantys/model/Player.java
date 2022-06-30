package it.polimi.ingsw.eriantys.model;

import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A class which represents a human being playing the game.
 * Its internal state contains all the game elements that are used and manipulated by the player.
 */
public class Player {
	private final String nickname;
	private final SchoolBoard schoolBoard;
	private TowerColor towerColor;
	private Wizard wizard;
	private final List<AssistantCard> deck;
	private int motherNatureMovements, coins;

	public Player(String nickname, int entranceSize, int towerNumber) {
		this.nickname = nickname;
		schoolBoard = new SchoolBoard(entranceSize, towerNumber);
		towerColor = null;
		wizard = null;

		deck = new ArrayList<>();
		Collections.addAll(deck, AssistantCard.values());

		motherNatureMovements = 0;
		coins = 1;
	}

	/**
	 * A getter for the nickname of a {@code Player}'s object.
	 * @return the internal state for a {@code Player}'s nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * A getter for the {@link AssistantCard} held in the hand by a {@code Player}.
	 * @return the reference to a {@link List} containing the {@code Player}'s cards
	 */
	public List<AssistantCard> getDeck() {
		return new ArrayList<>(deck);
	}

	/**
	 * A getter for the {@link SchoolBoard} entrance of a {@code Player}'s object.
	 * @return the reference to the {@code Player}'s {@link SchoolBoard} entrance
	 */
	public StudentContainer getEntrance() {
		return schoolBoard.getEntrance();
	}

	/**
	 * A getter for the {@link SchoolBoard} dining room of a {@code Player}'s object.
	 * @return the reference to the {@code Player}'s {@link SchoolBoard} dining room
	 */
	public DiningRoom getDiningRoom() {
		return schoolBoard.getDiningRoom();
	}

	/**
	 * A getter for the {@link TowerColor} of a {@code Player}'s object.
	 * @return the internal state for a {@code Player}'s {@link TowerColor}
	 */
	public TowerColor getTowerColor() {
		return towerColor;
	}

	/**
	 * A setter for the {@link TowerColor} of a {@code Player}'s object.
	 * @param towerColor the new internal state for a {@code Player}'s {@link TowerColor}
	 */
	public void setTowerColor(TowerColor towerColor) {
		this.towerColor = towerColor;
	}

	/**
	 * A getter for the {@link Wizard} of a {@code Player}'s object.
	 * @return the internal state for a {@code Player}'s {@link Wizard}
	 */
	public Wizard getWizard() {
		return wizard;
	}

	/**
	 * A setter for the {@link Wizard} of a {@code Player}'s object.
	 * @param wizard the new internal state for a {@code Player}'s {@link Wizard}
	 */
	public void setWizard(Wizard wizard) {
		this.wizard = wizard;
	}

	/**
	 * @see SchoolBoard#deployTower()
	 * @return {@code true} if and only if this {@code SchoolBoard} can deploy an additional tower
	 */
	public boolean deployTower() {
		return schoolBoard.deployTower();
	}

	/**
	 * @see SchoolBoard#returnTower()
	 * @return {@code true} if and only if this {@code SchoolBoard} can contain an additional tower
	 */
	public boolean returnTower() {
		return schoolBoard.returnTower();
	}

	/**
	 * @see SchoolBoard#getTowerQuantity()
	 * @return the number of towers which the {@link Player} owns in the {@link SchoolBoard}
	 */
	public int getTowerQuantity() {
		return schoolBoard.getTowerQuantity();
	}

	/**
	 * A getter for the number of allowed Mother Nature's movements for a {@code Player}'s object during the current turn.
	 * @return the internal state for the number of allowed Mother Nature's movements of a {@code Player}
	 */
	public int getMotherNatureMovements() {
		return motherNatureMovements;
	}

	/**
	 * A setter for the number of allowed Mother Nature's movements for a {@code Player}'s object during the current turn.
	 * @param movements the new internal state for the number of allowed Mother Nature's movements of a {@code Player}
	 */
	public void setMotherNatureMovements(int movements) throws InvalidArgumentException {
		if (movements < 0)
			throw new InvalidArgumentException("Parameter should not be negative.");
		motherNatureMovements = movements;
	}

	/**
	 * A getter for the number of coins for a {@code Player}'s object.
	 * @return the internal state for the number of coins of a {@code Player}
	 */
	public int getCoins() {
		return coins;
	}

	/**
	 * A method to implement the act of playing an {@link AssistantCard} from the hand.
	 * @param playedCard the card which the {@code Player} wants to play
	 */
	public void playAssistantCard(AssistantCard playedCard) {
		deck.remove(playedCard);
		try {
			setMotherNatureMovements(playedCard.movement());
		} catch (InvalidArgumentException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see SchoolBoard#checkForCoins(Color)
	 * @param color the {@link Color} whose amount of students is checked
	 * @return {@code true} if and only if the {@link Player} who owns this {@code DiningRoom} is entitled
	 * to receive a coin based on the number of students of color {@code color} their {@link DiningRoom} contains
	 */
	public boolean checkForCoins(Color color) {
		return schoolBoard.checkForCoins(color);
	}

	/**
	 * A method to assign a variable quantity of coins to a {@code Player}.
	 * @param amount the number of coins (could be negative) to be assigned to the {@code Player}
	 */
	public void updateCoins(int amount) {
		coins += amount;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Player player = (Player) o;
		return Objects.equals(nickname, player.nickname);
	}

	@Override
	public int hashCode() {
		return Objects.hash(nickname);
	}
}