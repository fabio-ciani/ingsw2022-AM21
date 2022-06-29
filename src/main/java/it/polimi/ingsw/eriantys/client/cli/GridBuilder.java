package it.polimi.ingsw.eriantys.client.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This utility class is used to print various fragments of text in a grid-like structure.
 */
public class GridBuilder {
	private int rows;
	private final int cols;
	private final List<GridElement> elements;
	private int[] rowHeights;
	private final int[] colWidths;
	private String rowSeparator;
	
	private int tabSize = 2;
	private String tab = "  ";
	private char vertical = '|';
	private char horizontal = '-';

	/**
	 * Constructs a new empty {@link GridBuilder} specifying a fixed number of columns.
	 * The default tab size is used.
	 *
	 * @param cols The number of columns of the grid
	 */
	public GridBuilder(int cols) {
		this.rows = 0;
		this.cols = cols;
		this.elements = new ArrayList<>();
		this.rowHeights = new int[0];
		this.colWidths = new int[cols];
	}

	/**
	 * Constructs a new empty {@link GridBuilder} specifying a fixed number of columns and the tab size.
	 *
	 * @param cols The number of columns of the grid
	 * @param tabSize The amount of spaces that make a tab
	 */
	public GridBuilder(int cols, int tabSize) {
		this(cols);
		this.tabSize = tabSize;
		this.tab = " ".repeat(tabSize);
	}

	/**
	 * Adds an element to the grid in the first available spot.
	 *
	 * @param element The text fragment to add to the grid
	 */
	public synchronized void add(String element) {
		elements.add(new GridElement(element));
		rows = (elements.size() + cols - 1) / cols;
	}

	/**
	 * Sets the character used to make horizontal lines.
	 *
	 * @param horizontal The desired character
	 */
	public void setHorizontal(char horizontal) {
		this.horizontal = horizontal;
	}

	/**
	 * Sets the character used to make vertical lines.
	 *
	 * @param vertical The desired character
	 */
	public void setVertical(char vertical) {
		this.vertical = vertical;
	}

	private void updateRowHeights() {
		if (rowHeights.length < rows) {
			rowHeights = new int[rows];
		}
		Arrays.setAll(rowHeights,
				rowIdx -> elements.stream()
				.filter(e -> elements.indexOf(e) / cols == rowIdx)
				.map(e -> e.lines.length)
				.max(Integer::compareTo)
				.orElse(elements.get(0).lines.length));
	}

	private void updateColWidths() {
		Arrays.setAll(colWidths,
				colIdx -> elements.stream()
				.filter(e -> elements.indexOf(e) % cols == colIdx)
				.map(e -> e.width)
				.max(Integer::compareTo)
				.orElse(elements.get(0).width));
		rowSeparator = String.valueOf(horizontal).repeat(Arrays.stream(colWidths).sum() + cols * (2 * tabSize + 1) + 1);
	}

	private String getRow(int rowIdx) {
		if (rowIdx > (elements.size() - 1) / cols) return null;
		StringBuilder rowBuilder = new StringBuilder();
		int fromIdx = rowIdx * cols;
		int toIdx = Math.min((rowIdx + 1) * cols, elements.size());
		List<GridElement> rowElements = elements.subList(fromIdx, toIdx);
		for (int lineIdx = 0; lineIdx < rowHeights[rowIdx]; lineIdx++) {
			rowBuilder.append(vertical);
			for (int colIdx = 0; colIdx < cols; colIdx++) {
				boolean missing = false;
				GridElement element = null;
				try {
					element = rowElements.get(colIdx);
				} catch (IndexOutOfBoundsException e) {
					missing = true;
				}
				if (!missing && lineIdx < element.lines.length) {
					rowBuilder.append(tab)
							.append(element.lines[lineIdx])
							.append(" ".repeat(colWidths[colIdx] - element.lines[lineIdx].length()))
							.append(tab)
							.append(vertical);
				} else {
					rowBuilder.append(" ".repeat(colWidths[colIdx] + 2 * tabSize))
							.append(vertical);
				}
			}
			if (lineIdx < rowHeights[rowIdx] - 1) {
				rowBuilder.append("\n");
			}
		}
		return rowBuilder.toString();
	}

	@Override
	public synchronized String toString() {
		if (elements.isEmpty()) return "";
		updateRowHeights();
		updateColWidths();
		StringBuilder builder = new StringBuilder();
		builder.append(rowSeparator);
		for (int rowIdx = 0; rowIdx < rows; rowIdx++) {
			builder.append("\n");
			builder.append(getRow(rowIdx));
			builder.append("\n");
			builder.append(rowSeparator);
		}
		return builder.toString();
	}
	
	private static class GridElement {
		private final String[] lines;
		private final int width;
		
		private GridElement(String text) {
			lines = text.split("\n");
			width = Arrays.stream(lines).map(String::length).max(Integer::compareTo).orElse(lines[0].length());
		}
	}
}
