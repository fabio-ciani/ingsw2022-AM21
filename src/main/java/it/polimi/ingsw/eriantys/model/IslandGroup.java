package it.polimi.ingsw.eriantys.model;

import it.polimi.ingsw.eriantys.model.exceptions.DuplicateNoEntryTileException;
import it.polimi.ingsw.eriantys.model.exceptions.IncompatibleControllersException;
import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;
import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

/**
 * This class represents a group of islands, which contains at least one island.
 * At the beginning of the game, each {@link IslandGroup} is made up of a single island,
 * but its size can grow during the game as more islands are unified.
 */
public class IslandGroup extends StudentContainer {
	private final String id;
	private final List<String> islandIds;
	private Player controller;
	private final Stack<Integer> noEntryTiles;

	/**
	 * Constructs a new {@link IslandGroup} with the specified {@code id},
	 * containing no students neither no-entry tiles, and with no controller.
	 * @param id the new island's {@code id}
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
		} catch (InvalidArgumentException | NoMovementException e) {
			System.out.println("This is a Throwable#printStackTrace() method call.");
			e.printStackTrace();
		}

		this.islandIds = new ArrayList<>();
		this.islandIds.addAll(i1.getComponents());
		this.islandIds.addAll(i2.getComponents());
	}

	/**
	 * Merges {@code i1} and {@code i2} and returns the resulting {@link IslandGroup}.
	 * @param i1 the first of the islands to merge
	 * @param i2 the second of the islands to merge
	 * @return an {@link IslandGroup} containing all and only the single islands previously contained
	 * in {@code i1} and {@code i2}, and with the same controller as them
	 * @throws IncompatibleControllersException if {@code i1} and {@code i2} are controlled by different {@link Player}s
	 */
	public static IslandGroup merge(IslandGroup i1, IslandGroup i2) throws IncompatibleControllersException {
		if (i1 == null || i2 == null)
			return null;

		if (!i1.hasSameController(i2))
			throw new IncompatibleControllersException("Ids: " + i1.id + ", " + i2.id);	// this should not happen

		return new IslandGroup(i1, i2);
	}

	/**
	 * A getter for the island's {@code id}.
	 * @return the island's {@code id}
	 */
	public String getId() {
		return id;
	}

	/**
	 * A getter for the island's {@code controller}.
	 * @return the island's {@code controller}
	 */
	public Player getController() {
		return controller;
	}

	/**
	 * Sets the island's {@code controller} to {@code newController},
	 * unless it is {@code null}, in which case the {@code controller} stays the same.
	 * @param newController the island's new {@code controller}
	 */
	public void setController(Player newController) {
		if (newController == null)
			return;
		controller = newController;
	}

	/**
	 * A getter for the number of towers on the {@link IslandGroup}.
	 * @return the number of towers on the {@link IslandGroup}
	 */
	public int getTowers() {
		return controller == null ? 0 : islandIds.size();
	}

	/**
	 * Places a no-entry tile on the {@link IslandGroup}, unless there already is a no-entry tile with the same {@code id} on it.
	 * @param id the no-entry tile's identifier
	 * @throws DuplicateNoEntryTileException if there already is a no-entry tile with the specified {@code id} on the island
	 */
	public void putNoEntryTile(int id) throws DuplicateNoEntryTileException {
		if (noEntryTiles.contains(id))
			throw new DuplicateNoEntryTileException("Id: " + id);	// this should not happen
		noEntryTiles.push(id);
	}

	/**
	 * Removes the latest no-entry tile to be placed on the {@link IslandGroup} and returns its {@code id}.
	 * If there are no such tiles on the island, returns {@code null}.
	 * @return the {@code id} of the latest no-entry tile to be added,
	 * or {@code null} if there are no such tiles on the island
	 */
	public Integer popNoEntryTile() {
		if (noEntryTiles.empty())
			return null;
		return noEntryTiles.pop();
	}

	/**
	 * A helper-getter method to fulfill the {@link BoardStatus} creation process.
	 * @return a representation for the number of islands which form the aggregate
	 */
	public Integer getSize() {
		return islandIds.size();
	}

	/**
	 * A helper-getter method to fulfill the {@link BoardStatus} creation process.
	 * @return a representation for the number of no-entry tiles placed on the aggregate
	 */
	public Integer getNoEntryTiles() {
		return noEntryTiles.size();
	}

	/**
	 * A getter for the list of the {@code id}s of the single islands components of an {@link IslandGroup}.
	 * @return a list of the {@code id}s of the single islands making up this {@link IslandGroup}
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