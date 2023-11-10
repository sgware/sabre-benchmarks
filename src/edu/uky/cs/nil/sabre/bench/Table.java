package edu.uky.cs.nil.sabre.bench;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A table is a way to store data in {@link Cell cells} that are organized into
 * labeled and numbered {@link Row rows} and {@link Column columns}.
 */
public class Table implements Iterable<Table.Cell> {

	public static abstract class Sequence implements Iterable<Cell> {

		public final Object label;
		public final int index;
		protected final ArrayList<Cell> cells = new ArrayList<>();
		
		public Sequence(Object label, int index) {
			this.label = label;
			this.index = index;
		}
		
		@Override
		public Iterator<Cell> iterator() {
			return cells.iterator();
		}
		
		public int size() {
			return cells.size();
		}
		
		public abstract Cell get(Object label);
		
		public Cell get(int index) {
			return cells.get(index);
		}
	}
	
	public static class Group<S extends Sequence> implements Iterable<S> {
		
		public final Iterable<Object> labels = new ArrayList<>();
		private final HashMap<Object, S> byLabel = new HashMap<>();
		private final ArrayList<S> byIndex = new ArrayList<>();

		final void add(S sequence) {
			((ArrayList<Object>) labels).add(sequence.label);
			byLabel.put(sequence.label, sequence);
			byIndex.add(sequence);
		}
		
		@Override
		public Iterator<S> iterator() {
			return byIndex.iterator();
		}
		
		public int size() {
			return byIndex.size();
		}
		
		public S get(Object label) {
			return byLabel.get(label);
		}
		
		public S get(int index) {
			return byIndex.get(index);
		}
	}
	
	public class Column extends Sequence {

		public final Class<?> type;
		
		public Column(Object label, Class<?> type) {
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
	
	public class Row extends Sequence {
		
		public Row(Object label) {
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
	
	public class Cell {
		
		public final Column column;
		public final Row row;
		private Object content = null;
		
		public Cell(Column column, Row row) {
			this.column = column;
			this.row = row;
			column.cells.add(this);
			row.cells.add(this);
			cells.add(this);
		}
		
		public Object get() {
			return content;
		}
		
		public void set(Object content) {
			if(content == null || column.type.isAssignableFrom(content.getClass()))
				this.content = content;
			else
				throw new IllegalArgumentException("\"" + content + "\" is not " + column.type.getSimpleName());
		}
	}
	
	public final Group<Column> columns = new Group<>();
	public final Group<Row> rows = new Group<>();
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
	
	public int size() {
		return cells.size();
	}
	
	public Column addColumn(Object label, Class<?> type) {
		return new Column(label, type);
	}
	
	public Row addRow(Object label) {
		return new Row(label);
	}
	
	public Object get(Object rowLabel, Object columnLabel) {
		return rows.get(rowLabel).get(columnLabel).get();
	}
	
	public Object get(int rowIndex, int columnIndex) {
		return rows.get(rowIndex).get(columnIndex).get();
	}
	
	public void set(Object rowLabel, Object columnLabel, Object content) {
		rows.get(rowLabel).get(columnLabel).set(content);
	}
	
	public void set(int rowIndex, int columnIndex, Object content) {
		rows.get(rowIndex).get(columnIndex).set(content);
	}
}