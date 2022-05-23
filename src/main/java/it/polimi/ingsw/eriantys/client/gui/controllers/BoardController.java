package it.polimi.ingsw.eriantys.client.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.*;

public class BoardController extends Controller {
	@FXML private GridPane islands;
	private final List<GridCell> towerCells;
	// TODO: Change Pink and Yellow students text labels row and column (according to the ImageView, in order to simplify drawing)
	private final Map<GridCell, String> studentCells;

	public BoardController() {
		// null is considered as 0.
		towerCells = new ArrayList<>();
		towerCells.add(new GridCell(null, null));
		towerCells.add(new GridCell(null, 4));
		towerCells.add(new GridCell(1, null));
		towerCells.add(new GridCell(1, 4));
		towerCells.add(new GridCell(2, null));
		towerCells.add(new GridCell(2, 4));

		studentCells = new HashMap<>();
		studentCells.put(new GridCell(null, 2), "Red");
		studentCells.put(new GridCell(1, 3), "Yellow");
		studentCells.put(new GridCell(2, 3), "Green");
		studentCells.put(new GridCell(2, 1), "Blue");
		studentCells.put(new GridCell(1, 1), "Pink");
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		drawIslands();
		drawStudents();
		drawTowers();
	}

	private void drawIslands() {
		islands.getChildren().stream()
				.filter(x -> x instanceof ImageView)
				.forEach(x -> {
					// Handle aggregate islands, if present.
					int num = Integer.parseInt(x.getId().substring(1).replace("-", ""));

					// Randomize loaded image.
					ImageView img = (ImageView) x;
					if (num % 3 != 0)
						img.setImage(new Image(getClass().getResource("/graphics/Islands/Island_" + num % 3 + ".png").toExternalForm()));
					else img.setImage(new Image(getClass().getResource("/graphics/Islands/Island_3.png").toExternalForm()));

					img.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
						System.out.println("You clicked on island " + ((ImageView) e.getSource()).getId());
						e.consume();
					});
				});
	}

	private void drawStudents() {
		islands.getChildren().stream()
				.filter(x -> x instanceof Group)
				.forEach(x -> {
					GridPane p = (GridPane) ((Group) x).getChildren().get(0);
					p.getChildren().stream()
							.filter(y -> y instanceof ImageView)
							.filter(this::isStudent)
							.forEach(y -> {
								ImageView img = (ImageView) y;

								img.setImage(new Image(getClass().getResource("/graphics/Students/" + studentImage(y) + "Student.png").toExternalForm()));

								img.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
									System.out.println("You clicked on a " + studentImage(y) + " student");
									e.consume();
								});
							});
				});
	}

	private boolean isStudent(Node n) {
		return studentCells.containsKey(new GridCell(GridPane.getRowIndex(n), GridPane.getColumnIndex(n)));
	}

	private String studentImage(Node n) {
		return studentCells.get(new GridCell(GridPane.getRowIndex(n), GridPane.getColumnIndex(n)));
	}

	private void drawTowers() {
		islands.getChildren().stream()
				.filter(x -> x instanceof Group)
				.forEach(x -> {
					GridPane p = (GridPane) ((Group) x).getChildren().get(0);
					p.getChildren().stream()
							.filter(y -> y instanceof ImageView)
							.filter(this::isTower)
							.forEach(y -> {
								switch ((int) ((ImageView) y).getFitWidth()) {
									case 32 -> ((ImageView) y).setImage(new Image(getClass().getResource("/graphics/Towers/WhiteTower.png").toExternalForm()));
									case 36 -> ((ImageView) y).setImage(new Image(getClass().getResource("/graphics/Towers/BlackTower.png").toExternalForm()));
									case 40 -> ((ImageView) y).setImage(new Image(getClass().getResource("/graphics/Towers/GreyTower.png").toExternalForm()));
								}
							});
				});
	}

	private boolean isTower(Node n) {
		return towerCells.contains(new GridCell(GridPane.getRowIndex(n), GridPane.getColumnIndex(n)));
	}

	private static class GridCell {
		Integer row, column;

		private GridCell(Integer row, Integer column) {
			this.row = row;
			this.column = column;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			GridCell gridCell = (GridCell) o;
			return Objects.equals(row, gridCell.row) && Objects.equals(column, gridCell.column);
		}

		@Override
		public int hashCode() {
			return Objects.hash(row, column);
		}
	}
}
