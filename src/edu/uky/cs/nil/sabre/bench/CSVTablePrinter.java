package edu.uky.cs.nil.sabre.bench;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class CSVTablePrinter implements Closeable {

	private final Writer writer;
	
	public CSVTablePrinter(Writer writer) {
		this.writer = writer;
	}
	
	public CSVTablePrinter(File file) throws IOException {
		this(new BufferedWriter(new FileWriter(file)));
	}
	
	public void print(Table table) throws IOException {
		for(Table.Column column : table.columns) {
			if(column.index > 0)
				print(",");
			print("\"" + column.label.toString() + "\"");
		}
		for(Table.Row row : table.rows) {
			print("\n");
			for(Table.Cell cell : row) {
				if(cell.column.index > 0)
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