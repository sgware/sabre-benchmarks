package edu.uky.cs.nil.sabre.bench;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Predicate;

/**
 * A table is a way to store data in {@link Cell cells} that are organized into
 * labeled and numbered {@link Row rows} and {@link Column columns}.
 * 
 * @author Stephen G. Ware
 */
public class Table implements Iterable<Table.Cell> {
	
	/**
	 * The parent class of {@link Column} and {@link Row} that represents a
	 * labeled, order sequence of {@link Cell cells}.
	 */
	public static abstract class Sequence implements Iterable<Cell> {
		
		/** The label for this sequence of cells */
		public final Object label;
		
		/**
		 * Constructs a new sequence of cells with the given label.
		 * 
		 * @param label the label
		 */
		private Sequence(Object label) {
			this.label = label;
		}
		
		/**
		 * Returns the number (starting at 0) of this sequence in its {@link
		 * Group group} or similar sequences.
		 * 
		 * @return this sequence's number
		 */
		public abstract int getIndex();
		
		/**
		 * Returns the {@link Cell cell} from this sequence with the given
		 * index.
		 * 
		 * @param index the index of the cell in this sequence
		 * @return the cell from this sequence with the given index
		 * @throws IndexOutOfBoundsException if no cell in the sequence has the
		 * given index
		 */
		public Cell get(int index) {
			int count = 0;
			for(Cell cell : this)
				if(count++ == index)
					return cell;
			throw new IndexOutOfBoundsException("There is no cell " + index + " in this sequence.");
		}
		
		/**
		 * If this sequence is a {@link Column column}, returns the cell from
		 * the {@link Row row} with the given label; if this sequence is a
		 * {@link Row row}, returns the cell from the {@link Column column
		 * column} with the given label.
		 * 
		 * @param label the label of the intersecting sequence that defines a
		 * cell
		 * @return the cell from this sequence whose intersecting sequence has
		 * the given label
		 * @throws IllegalArgumentException if not intersecting sequence has the
		 * given label
		 */
		public abstract Cell get(Object label);
	}
	
	/**
	 * A group of {@link Sequence sequences} of the same type.
	 * 
	 * @param <S> the type of sequence in this group
	 */
	public static class Group<S extends Sequence> implements Iterable<S> {

		/** The sequences in this group, in order */
		private final ArrayList<S> members = new ArrayList<>();
		
		@Override
		public Iterator<S> iterator() {
			return members.iterator();
		}
		
		/**
		 * Returns the number of sequences in this group.
		 * 
		 * @return the number of sequences
		 */
		public int size() {
			return members.size();
		}
		
		/**
		 * Returns the sequence from this group with the given index.
		 * 
		 * @param index the index of the desired sequence
		 * @return the sequence with the desired index
		 * @throws IndexOutOfBoundsException if the index does not exist
		 */
		public S get(int index) {
			return members.get(index);
		}
		
		/**
		 * Returns the first sequence from this group with the given label.
		 * 
		 * @param label the label of the desired sequence
		 * @return the first sequence with the given label
		 * @throws IllegalArgumentException if no sequence has the given label
		 */
		public S get(Object label) {
			for(S member : members)
				if(member.label.equals(label))
					return member;
			throw new IllegalArgumentException("No sequence in this group has the label \"" + label + "\".");
		}
	}
	
	/**
	 * A vertical {@link Sequence sequence} of {@link Cell cells} in a table,
	 * all of whose values must be of a given {@link #type type}.
	 */
	public class Column extends Sequence {
		
		/**
		 * The {@link Cell#get() values} of all cells in this column must be of
		 * this type
		 */
		public final Class<?> type;
		
		/**
		 * Constructs a new column.
		 * 
		 * @param label the column label
		 * @param type the type of value that all cells in this column must have
		 */
		public Column(Object label, Class<?> type) {
			super(label);
			this.type = type;
			columns.members.add(this);
			for(Row row : rows)
				new Cell(this, row);
			sort();
		}

		@Override
		public Iterator<Cell> iterator() {
			return cells(c -> c.column == this).iterator();
		}
		
		@Override
		public int getIndex() {
			return columns.members.indexOf(this);
		}
		
		@Override
		public Cell get(Object rowLabel) {
			for(Cell cell : this)
				if(cell.row.label.equals(rowLabel))
					return cell;
			throw new IllegalArgumentException("There is no row with label \"" + rowLabel + "\".");
		}
	}
	
	/**
	 * A horizontal {@link Sequence sequence} of {@link Cell cells} in a table.
	 */
	public class Row extends Sequence {
		
		/**
		 * Constructs a new row.
		 * 
		 * @param label the row label
		 */
		public Row(Object label) {
			super(label);
			rows.members.add(this);
			for(Column column : columns)
				new Cell(column, this);
			sort();
		}
		
		@Override
		public Iterator<Cell> iterator() {
			return cells(c -> c.row == this).iterator();
		}
		
		@Override
		public int getIndex() {
			return rows.members.indexOf(this);
		}
		
		@Override
		public Cell get(Object columnLabel) {
			for(Cell cell : this)
				if(cell.column.label.equals(columnLabel))
					return cell;
			throw new IllegalArgumentException("There is no column with label \"" + columnLabel + "\".");
		}
	}
	
	/**
	 * A cell holds a single value at the intersection of a {@link Column
	 * column} and {@link Row row}.
	 */
	public class Cell {
		
		/** The column the cell is a member of */
		public final Column column;
		
		/** The row the cell is a member of */
		public final Row row;
		
		/**
		 * The value stored in the cell, which must be of {@link #type its
		 * column's type}
		 */
		private Object value;
		
		/**
		 * Constructs a new cell.
		 * 
		 * @param column the column the cell is a member of
		 * @param row the row the cell is a member of
		 */
		private Cell(Column column, Row row) {
			this.column = column;
			this.row = row;
			cells.add(this);
		}
		
		/**
		 * Returns the value stored in the cell, which must be of {@link
		 * Column#type its column's type} or null.
		 * 
		 * @return the value stored in the cell
		 */
		public Object get() {
			return value;
		}
		
		/**
		 * Casts the value stored in the cell to the given type and returns it.
		 * 
		 * @param <T> the type of value to cast the cell's value to
		 * @param type the class for the type of value to cast the cell's value
		 * to
		 * @return the value stored in the cell casted to the given type
		 */
		public <T> T get(Class<T> type) {
			return type.cast(value);
		}
		
		/**
		 * Sets the value stored in the cell.
		 * 
		 * @param value the value to store
		 * @throws ClassCastException if the value is not of {@link Column#type
		 * the correct type} for {@link #column this cell's column}
		 */
		public void set(Object value) {
			this.value = column.type.cast(value);
		}
	}
	
	/**
	 * A comparator used to sort the cells of a table after the rows are
	 * reordered.
	 */
	private static final Comparator<Cell> ORDER = new Comparator<>() {

		@Override
		public int compare(Cell c1, Cell c2) {
			int comparison = c1.row.getIndex() - c2.row.getIndex();
			if(comparison == 0)
				comparison = c1.column.getIndex() - c2.column.getIndex();
			return comparison;
		}
	};
	
	/** The columns in this table, in order */
	public final Group<Column> columns = new Group<>();
	
	/** The rows in this table, in order */
	public final Group<Row> rows = new Group<>();
	
	/** The cells in this table, in order by rows and then columns */
	private final ArrayList<Cell> cells = new ArrayList<>();
	
	@Override
	public String toString() {
		StringWriter string = new StringWriter();
		try(TextReportPrinter printer = new TextReportPrinter(string)) {
			printer.print(this);
		}
		catch(IOException e) {
			// will not happen
		}
		return string.toString();
	}
	
	@Override
	public Iterator<Cell> iterator() {
		return cells(c -> true).iterator();
	}
	
	/**
	 * Returns the total number of cells in this table.
	 * 
	 * @return the number of cells
	 */
	public int size() {
		return cells.size();
	}
	
	/**
	 * Adds a new {@link Column column} to this table with the given label and
	 * type.
	 * 
	 * @param label the label that identifies the column
	 * @param type all cells in the column must have values of this type
	 * @return the new column
	 */
	public Column addColumn(Object label, Class<?> type) {
		return new Column(label, type);
	}
	
	/**
	 * Adds a new {@link Row row} to this table with the given label.
	 * 
	 * @param label the label that identifies the row
	 * @return the new row
	 */
	public Row addRow(Object label) {
		return new Row(label);
	}
	
	/**
	 * Returns the {@link Cell cell} at the intersection of the row and column
	 * with the given labels.
	 * 
	 * @param rowLabel the label of the row the cell is in
	 * @param columnLabel the label of the column the cell is in
	 * @return the cell at that row and column
	 * @throws IllegalArgumentException if no row or no column exists with those
	 * labels
	 */
	public Cell get(Object rowLabel, Object columnLabel) {
		return columns.get(columnLabel).get(rowLabel);
	}
	
	/**
	 * Sets the {@link Cell#get() value stored in the cell} at the intersection
	 * of the row and column with the given labels.
	 * 
	 * @param rowLabel the label of the row the cell is in
	 * @param columnLabel the label of the column the cell is in
	 * @param value the value to store in the cell
	 * @throws IllegalArgumentException if no row or no column exists with those
	 * labels
	 */
	public void set(Object rowLabel, Object columnLabel, Object value) {
		Column column = columns.get(columnLabel);
		Cell cell = column.get(rowLabel);
		cell.set(value);
	}
	
	/**
	 * Returns a {@link Iterable collection} of all {@link Cell cells} from this
	 * table matching a given {@link Predicate predicate}.
	 * 
	 * @param predicate a predicate that returns true of all desired cells
	 * @return a collection of all cells matching the predicate
	 */
	public Iterable<Cell> cells(Predicate<? super Cell> predicate) {
		ArrayList<Cell> result = new ArrayList<>();
		for(Cell cell : cells)
			if(predicate.test(cell))
				result.add(cell);
		return result;
	}
	
	/**
	 * Returns a {@link Iterable collection} of {@link Cell#get() values stored
	 * in all cells} from this table matching a given {@link Predicate
	 * predicate}.
	 * 
	 * @param <T> the type of value stored in the cells the predicate matches
	 * @param type the class for the type of value stored in the cells the
	 * predicate matches
	 * @param predicate a predicate that returns true of all desired cells
	 * @return a collection of values from all cells matching the predicate
	 */
	public <T> Iterable<T> values(Class<T> type, Predicate<? super Cell> predicate) {
		ArrayList<T> result = new ArrayList<>();
		for(Cell cell : cells)
			if(predicate.test(cell))
				result.add(cell.get(type));
		return result;
	}
	
	/**
	 * Reorders the {@link #rows rows} in this table.
	 * 
	 * @param comparator a comparator defining the new order of rows
	 */
	public void sort(Comparator<? super Row> comparator) {
		rows.members.sort(comparator);
		sort();
	}
	
	/**
	 * Reorders the {@link #rows rows} in this table based on the values in one
	 * of its {@link #columns columns}.
	 * 
	 * @param <T> the type of value stored in the cells of the column
	 * @param columnLabel the label of the column whose cell values will be used
	 * to reorder the rows
	 * @param type the class for the type of value stored in the cells of the
	 * column
	 * @param comparator a comparator that defines the new order of rows based
	 * on the values from the cells in the column
	 */
	public <T> void sort(Object columnLabel, Class<T> type, Comparator<T> comparator) {
		sort((row1, row2) -> {
			Cell cell1 = row1.get(columnLabel);
			Cell cell2 = row2.get(columnLabel);
			return comparator.compare(cell1.get(type), cell2.get(type));
		});
	}
	
	/**
	 * Reorders the {@link #cells cells} in the table after the rows have been
	 * reordered.
	 */
	private void sort() {
		cells.sort(ORDER);
	}
}