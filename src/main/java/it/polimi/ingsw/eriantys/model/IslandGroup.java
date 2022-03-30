package it.polimi.ingsw.eriantys.model;

import it.polimi.ingsw.eriantys.model.exceptions.IncompatibleControllersException;
import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

public class IslandGroup extends StudentContainer {
	private final String id;
	private final List<String> islandIds;
	private final Player controller;
	private final Stack<Integer> noEntryTiles;

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

	public static IslandGroup merge(IslandGroup i1, IslandGroup i2) throws IncompatibleControllersException {
		if (!i1.hasSameController(i2))
			throw new IncompatibleControllersException("Ids: " + i1.id + ", " + i2.id + ".");

		return new IslandGroup(i1, i2);
	}

	public String getId() {
		return id;
	}

	private List<String> getComponents() {
		return new ArrayList<>(islandIds);
	}

	public Player getController() {
		return controller;
	}

	public int getTowers() {
		return islandIds.size();
	}

	private boolean hasSameController(IslandGroup that) {
		return this.controller.equals(that.controller);
	}

	public void putNoEntryTile(int id) {
		noEntryTiles.push(id);
	}

	public Integer popNoEntryTile() {
		if (noEntryTiles.empty())
			return null;
		return noEntryTiles.pop();
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