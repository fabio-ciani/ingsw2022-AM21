package it.polimi.ingsw.eriantys.model;

public class GameConstants {
	private final String diningRoom;
	private final GameConfig gameConfig;

	private GameConstants() {
		diningRoom = null;
		gameConfig = new GameConfig();
	}

	public String getDiningRoom() {
		return diningRoom;
	}

	public int getCloudSize() {
		return gameConfig.cloudSize;
	}

	public int getCloudNumber() {
		return gameConfig.cloudNumber;
	}

	public int getEntranceSize() {
		return gameConfig.entranceSize;
	}

	public int getTowerNumber() {
		return gameConfig.towerNumber;
	}

	private static class GameConfig {
		private final int cloudSize;
		private final int cloudNumber;
		private final int entranceSize;
		private final int towerNumber;

		private GameConfig() {
			cloudSize = 0;
			cloudNumber = 0;
			entranceSize = 0;
			towerNumber = 0;
		}
	}
}
