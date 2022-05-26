package it.polimi.ingsw.eriantys.model;

public class GameConstants {
	private final int cloudSize;
	private final int cloudNumber;
	private final int entranceSize;
	private final int towerNumber;

	public static final String DINING_ROOM = "Dining Room";
	public static final String TIE = "Tie";

	private GameConstants() {
		cloudSize = 0;
		cloudNumber = 0;
		entranceSize = 0;
		towerNumber = 0;
	}

	public int getCloudSize() {
		return cloudSize;
	}

	public int getCloudNumber() {
		return cloudNumber;
	}

	public int getEntranceSize() {
		return entranceSize;
	}

	public int getTowerNumber() {
		return towerNumber;
	}
}
