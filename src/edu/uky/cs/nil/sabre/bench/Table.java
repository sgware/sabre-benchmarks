package edu.uky.cs.nil.sabre.bench;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
		
		/** The number (starting at 0) of this sequence of cells */
		public final int index;
		
		/** The cells in this sequence */
		protected final ArrayList<Cell> cells = new ArrayList<>();
		
		/**
		 * Constructs a new sequence of cells with the given label and index.
		 * 
		 * @param label the label
		 * @param index the index
		 */
		public Sequence(Object label, int index) {
			this.label = label;
			this.index = index;
		}
		
		@Override
		public Iterator<Cell> iterator() {
			return cells.iterator();
		}
		
		/**
		 * Returns the number of cells in this sequence.
		 * 
		 * @return the number of cells
		 */
		public int size() {
			return cells.size();
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
		 */
		public abstract Cell get(Object label);
		
		/**
		 * Returns the {@link Cell cell} from this sequence with the given
		 * index.
		 * 
		 * @param index the index of the cell in this sequence
		 * @return the cell from this sequence with the given index
		 */
		public Cell get(int index) {
			return cells.get(index);
		}
	}
	
	/**
	 * A group of {@link Sequence sequences} of the same type.
	 * 
	 * @param <S> the type of sequence in this group
	 */
	public static class Group<S extends Sequence> implements Iterable<S> {
		
		/** The labels of every sequence in this group, in order */
		public final Iterable<Object> labels = new ArrayList<>();
		
		/** A map for looking up sequence by label */
		private final HashMap<Object, S> byLabel = new HashMap<>();
		
		/** A list for looking up sequence by index */
		private final ArrayList<S> byIndex = new ArrayList<>();

		/**
		 * Adds a sequence to this group and registers its label and index.
		 * 
		 * @param sequence the sequence to add to this group
		 */
		final void add(S sequence) {
			((ArrayList<Object>) labels).add(sequence.label);
			if(!byLabel.containsKey(sequence.label))
				byLabel.put(sequence.label, sequence);
			byIndex.add(sequence);
		}
		
		@Override
		public Iterator<S> iterator() {
			return byIndex.iterator();
		}
		
		/**
		 * Returns the number of sequences in this group.
		 * 
		 * @return the number of sequences
		 */
		public int size() {
			return byIndex.size();
		}
		
		/**
		 * Returns the first sequence from this group with the given label.
		 * 
		 * @param label the label of the desired sequence
		 * @return the first sequence with the given label
		 */
		public S get(Object label) {
			return byLabel.get(label);
		}
		
		/**
		 * Returns the sequence from this group with the given index.
		 * 
		 * @param index the index of the desired sequence
		 * @return the sequence with the desired index
		 */
		public S get(int index) {
			return byIndex.get(index);
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
		Column(Object label, Class<?> type) {
			super(label, columns.size());
			this.type = type;
			columns.add(this);
			for(Row row : rows)
				new Cell(this, row);
		}

		@Override
		public Cell get(Object rowLabel) {
			for(Cell cell : cells)
				if(cell.row.label.equals(rowLabel))
					return cell;
			throw new IllegalArgumentException("Row \"" + rowLabel + "\" not found.");
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
		Row(Object label) {
			super(label, rows.size());
			rows.add(this);
			for(Column column : columns)
				new Cell(column, this);
		}

		@Override
		public Cell get(Object columnLabel) {
			for(Cell cell : cells)
				if(cell.column.label.equals(columnLabel))
					return cell;
			throw new IllegalArgumentException("Row \"" + columnLabel + "\" not found.");
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
		
		/** The value stored in the cell, which must be of its column's type */
		private Object content = null;
		
		/**
		 * Constructs a new cell.
		 * 
		 * @param column the column the cell is a member of
		 * @param row the row the cell is a member of
		 */
		Cell(Column column, Row row) {
			this.column = column;
			this.row = row;
			column.cells.add(this);
			row.cells.add(this);
			cells.add(this);
		}
		
		/**
		 * Returns the value stored in the cell, which must be of {@link
		 * Column#type its column's type} or null.
		 * 
		 * @return the value stored in the cell
		 */
		public Object get() {
			return content;
		}
		
		/**
		 * Sets the value stored in the cell.
		 * 
		 * @param content the value to store
		 * @throws IllegalArgumentException if the value is not of {@link
		 * Column#type the correct type for this cell's column}
		 */
		public void set(Object content) {
			if(content == null || column.type.isAssignableFrom(content.getClass()))
				this.content = content;
			else
				throw new IllegalArgumentException("\"" + content + "\" is not " + column.type.getSimpleName());
		}
	}
	
	/** The columns in this table, in order */
	public final Group<Column> columns = new Group<>();
	
	/** The rows in this table, in order */
	public final Group<Row> rows = new Group<>();
	
	/** The cells in this table */
	private final ArrayList<Cell> cells = new ArrayList<>();
	
	@Override
	public String toString() {
		StringWriter string = new StringWriter();
		try(TextReportPrinter printer = new TextReportPrinter(string)) {
			printer.print(this);
		}
		catch(IOException e) {/* will not happen */}
		return string.toString();
	}
	
	@Override
	public Iterator<Cell> iterator() {
		return cells.iterator();
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
	 * Returns the {@link Cell#get() value stored in the cell} at the
	 * intersection of the row and column with the given labels.
	 * 
	 * @param rowLabel the label of the row the cell is in
	 * @param columnLabel the label of the column the cell is in
	 * @return the value stored in the cell at that row and column
	 */
	public Object get(Object rowLabel, Object columnLabel) {
		return rows.get(rowLabel).get(columnLabel).get();
	}
	
	/**
	 * Returns the {@link Cell#get() value stored in the cell} at the
	 * intersection of the row and column with the given indices.
	 * 
	 * @param rowIndex the index of the row the cell is in
	 * @param columnIndex the index of the column the cell is in
	 * @return the value stored in the cell at that row and column
	 */
	public Object get(int rowIndex, int columnIndex) {
		return rows.get(rowIndex).get(columnIndex).get();
	}
	
	/**
	 * Sets the {@link Cell#get() value stored in the cell} at the intersection
	 * of the row and column with the given labels.
	 * 
	 * @param rowLabel the label of the row the cell is in
	 * @param columnLabel the label of the column the cell is in
	 * @param content the value to store in the cell
	 */
	public void set(Object rowLabel, Object columnLabel, Object content) {
		rows.get(rowLabel).get(columnLabel).set(content);
	}
	
	/**
	 * Sets the {@link Cell#get() value stored in the cell} at the intersection
	 * of the row and column with the given indices.
	 * 
	 * @param rowIndex the index of the row the cell is in
	 * @param columnIndex the index of the column the cell is in
	 * @param content the value to store in the cell
	 */
	public void set(int rowIndex, int columnIndex, Object content) {
		rows.get(rowIndex).get(columnIndex).set(content);
	}
}