package edu.uky.cs.nil.sabre.bench;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Prints a {@link Table table} to a CSV (comma separated value) file.
 * 
 * @author Stephen G. Ware
 */
public class CSVTablePrinter implements Closeable {

	private final Writer writer;
	
	/**
	 * Constructs a CSV table printer with a given {@link Writer writer}.
	 * 
	 * @param writer a writer that writes to this printer's destination
	 */
	public CSVTablePrinter(Writer writer) {
		this.writer = writer;
	}
	
	/**
	 * Constructs a CSV table printer that writes to a given file.
	 * 
	 * @param file the file to which the table will be written in CSV format
	 * @throws IOException if an exception occurs while opening a writer to the
	 * file
	 */
	public CSVTablePrinter(File file) throws IOException {
		this(new BufferedWriter(new FileWriter(file)));
	}
	
	/**
	 * Prints the given table in comma separate value format to this printer's
	 * destination.
	 * 
	 * @param table the table to print
	 * @throws IOException if an exception occurs while writing the table to
	 * this printer's destination
	 */
	public void print(Table table) throws IOException {
		for(Table.Column column : table.columns) {
			if(column.getIndex() > 0)
				print(",");
			print("\"" + column.label.toString() + "\"");
		}
		for(Table.Row row : table.rows) {
			print("\n");
			for(Table.Cell cell : row) {
				if(cell.column.getIndex() > 0)
					print(",");
				print(cell);
			}
		}
	}
	
	private void print(Table.Cell cell) throws IOException {
		if(cell.column.type.equals(String.class))
			print("\"" + cell.get() + "\"");
		else
			print(cell.get());
	}
	
	private void print(Object object) throws IOException {
		if(object != null)
			writer.append(object.toString());
	}
	
	@Override
	public void close() throws IOException {
		writer.close();
	}
}