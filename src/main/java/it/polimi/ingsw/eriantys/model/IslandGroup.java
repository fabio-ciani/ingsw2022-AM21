package it.polimi.ingsw.eriantys.model;

import it.polimi.ingsw.eriantys.model.exceptions.DuplicateNoEntryTileException;
import it.polimi.ingsw.eriantys.model.exceptions.IncompatibleControllersException;
import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

/**
 * This class represents a group of islands, which contains at least one island. At the beginning of the game, each
 * {@link IslandGroup} is made up of a single island, but its size can grow during the game as more islands are unified.
 */
public class IslandGroup extends StudentContainer {
	private final String id;
	private final List<String> islandIds;
	private Player controller;
	private final Stack<Integer> noEntryTiles;

	/**
	 * Constructs a new {@link IslandGroup} with the specified {@code id}, containing no students and no No Entry tiles,
	 * and with no controller.
	 * @param id the new island's {@code id}.
	 */
	public IslandGroup(String id) {
		super();

		this.id = id;
		this.controller = null;
		this.noEntryTiles = new Stack<>();
		this.islandIds = new ArrayList<>();
		this.islandIds.add(id);
	}

	private IslandGroup(IslandGroup i1, IslandGroup i2) {
		super();

		this.id = i1.id + "-" + i2.id;
		this.controller = i1.controller;
		this.noEntryTiles = new Stack<>();

		try {
			i1.moveAllTo(this);
			i2.moveAllTo(this);
		} catch (NoMovementException e) {
			// TODO handle exception
			e.printStackTrace();
		}

		this.islandIds = new ArrayList<>();
		this.islandIds.addAll(i1.getComponents());
		this.islandIds.addAll(i2.getComponents());
	}

	/**
	 * Merges {@code i1} and {@code i2} and returns the resulting {@link IslandGroup}.
	 * @param i1 the first of the islands to merge.
	 * @param i2 the second of the islands to merge.
	 * @return an {@link IslandGroup} containing all and only the single islands previously contained in {@code i1} and
	 * {@code i2}, and with the same controller as them.
	 * @throws IncompatibleControllersException if {@code i1} and {@code i2} are controlled by different {@link Player}s.
	 */
	public static IslandGroup merge(IslandGroup i1, IslandGroup i2) throws IncompatibleControllersException {
		if (i1 == null || i2 == null)
			return null;

		if (!i1.hasSameController(i2))
			throw new IncompatibleControllersException("Ids: " + i1.id + ", " + i2.id + ".");

		return new IslandGroup(i1, i2);
	}

	/**
	 * Returns the island's {@code id}.
	 * @return the island's {@code id}.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns the island's {@code controller}.
	 * @return the island's {@code controller}.
	 */
	public Player getController() {
		return controller;
	}

	/**
	 * Sets the island's {@code controller} to {@code newController}, unless it is {@code null}, in which case the
	 * {@code controller} stays the same.
	 * @param newController the island's new {@code controller}.
	 */
	public void setController(Player newController) {
		if (newController == null)
			return;
		controller = newController;
	}

	/**
	 * Returns the number of towers on the {@link IslandGroup}.
	 * @return the number of towers on the {@link IslandGroup}.
	 */
	public int getTowers() {
		return islandIds.size();
	}

	/**
	 * Places a No Entry tile on the {@link IslandGroup}.
	 * @param id the No Entry tile's identifier.
	 */
	public void putNoEntryTile(int id) throws DuplicateNoEntryTileException {
		if (noEntryTiles.contains(id))
			throw new DuplicateNoEntryTileException("Id: " + id + ".");
		noEntryTiles.push(id);
	}

	/**
	 * Removes the latest No Entry tile to be placed on the {@link IslandGroup} and returns its {@code id}. If there are
	 * no such tiles on the island, returns {@code null}.
	 * @return the {@code id} of the latest No Entry tile to be added, or {@code null} if there are no such tiles on the
	 * island.
	 */
	public Integer popNoEntryTile() {
		if (noEntryTiles.empty())
			return null;
		return noEntryTiles.pop();
	}

	/**
	 * Returns a list of the {@code id}s of the single islands making up this {@link IslandGroup}.
	 * @return a list of the {@code id}s of the single islands making up this {@link IslandGroup}.
	 */
	protected List<String> getComponents() {
		return new ArrayList<>(islandIds);
	}

	private boolean hasSameController(IslandGroup that) {
		if (this.controller == null || that.controller == null)
			return false;
		return this.controller.equals(that.controller);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		IslandGroup that = (IslandGroup) o;
		return id.equals(that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}