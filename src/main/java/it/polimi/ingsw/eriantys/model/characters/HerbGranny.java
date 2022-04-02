package it.polimi.ingsw.eriantys.model.characters;

import it.polimi.ingsw.eriantys.model.Board;
import it.polimi.ingsw.eriantys.model.Color;
import it.polimi.ingsw.eriantys.model.IslandGroup;
import it.polimi.ingsw.eriantys.model.exceptions.DuplicateNoEntryTileException;
import it.polimi.ingsw.eriantys.model.exceptions.ItemNotAvailableException;
import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;

import java.util.List;
import java.util.Stack;

/**
 * Represents the "herb granny" character card and the corresponding effect.
 *
 * @see CharacterCard
 */
public class HerbGranny extends BaseCharacterCard {
    /**
     * No Entry tiles currently available on this card.
     */
    private final Stack<Integer> tiles;

    /**
     * Reference to the {@link Board} object for the current game.
     */
    private final Board board;

    /**
     * Initial cost to activate the {@link HerbGranny} effect.
     */
    private static final int INITIAL_COST = 2;

    /**
     * Total number of No Entry tiles available in the game.
     */
    private static final int MAX_ENTRY_TILES = 4;

    /**
     * Constructs a new {@link HerbGranny} character card.
     *
     * @param board Reference to the {@link Board} object for the current game.
     */
    public HerbGranny(Board board) {
        super(INITIAL_COST);
        this.tiles = new Stack<>();
        this.board = board;
    }

    /**
     * Method called when a No Entry tile is removed from an island to put it back on this card.
     *
     * @param id ID of the tile to put back.
     *
     * @see Board
     * @see IslandGroup
     */
    public void returnTile(int id) {
        if (!tiles.contains(id)) {
            tiles.push(id);
        }
    }

    @Override
    public void setupEffect() throws NoMovementException {
        super.setupEffect();
        for (int i = 0; i < MAX_ENTRY_TILES; i++) {
            tiles.push(i);
        }
        // TODO: 30/03/2022 Give the board (?) a reference to the returnTile method
        // board.setReturnNoEntryTile(this::returnTile);
    }

    @Override
    public void applyEffect(List<Color> sourceColors,
                            List<Color> destinationColors,
                            Color targetColor,
                            IslandGroup targetIsland)
            throws NoMovementException, ItemNotAvailableException {
        if (tiles.empty()) {
            throw new ItemNotAvailableException("There are no entry tiles on the HerbGranny character card.");
        }
        super.applyEffect(sourceColors, destinationColors, targetColor, targetIsland);
        try {
            targetIsland.putNoEntryTile(tiles.pop());
        } catch (DuplicateNoEntryTileException e) {
            // TODO handle exception
            e.printStackTrace();
        }
    }
}
