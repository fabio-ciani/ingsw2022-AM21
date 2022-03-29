package it.polimi.ingsw.eriantys.model;

public class SchoolBoard {
	private final StudentContainer entrance;
	private final DiningRoom diningRoom;
	private int towers;

	public SchoolBoard() {
		// TODO use constants based on number of players for entrance.maxSize and towers
		entrance = new StudentContainer(7);
		diningRoom = new DiningRoom();
		towers = 8;
	}

	public boolean checkForCoins(Color color) {
		return (diningRoom.getQuantity(color) % 3 == 0);
	}
}
