package edu.uky.cs.nil.sabre.bench;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import edu.uky.cs.nil.sabre.Settings;
import edu.uky.cs.nil.sabre.Utilities;

/**
 * A {@link ReportPrinter report printer} that writes a {@link Report report} in
 * plain text.
 * 
 * @author Stephen G. Ware
 */
public class TextReportPrinter implements ReportPrinter {

	/**
	 * The number of decimal places to display when writing a non-integer number
	 */
	public static final int DECIMAL_PLACES = 2;
	
	private final Writer writer;
	
	/**
	 * Constructs a new text report printer that prints to a given {@link Writer
	 * writer}.
	 * 
	 * @param writer a writer that writes to this printer's destination
	 */
	public TextReportPrinter(Writer writer) {
		this.writer = writer;
	}
	
	/**
	 * Constructs a new text report printer that prints to a given file.
	 * 
	 * @param file the file to which the report should be printed
	 * @throws IOException if an exception occurs while opening a writer to the
	 * file
	 */
	public TextReportPrinter(File file) throws IOException {
		this(new BufferedWriter(new FileWriter(file)));
	}
	
	@Override
	public void print(Report report) throws IOException {
		print("== Sabre Benchmark Results ==\n\n");
		print("Sabre Version: " + Settings.VERSION_STRING + "\n");
		print("JRE Vendor:    " + System.getProperty("java.vendor") + "\n");
		print("JRE Version:   " + System.getProperty("java.version") + "\n");
		print("OS Name:       " + System.getProperty("os.name") + "\n");
		print("OS Version:    " + System.getProperty("os.version") + "\n");
		print("Memory Used:   " + Runtime.getRuntime().totalMemory() + "B\n");
		print("Max Memory:    " + Runtime.getRuntime().maxMemory() + "B\n");
		print("Threads:       " + Main.THREADS + "\n");
		print("Runs:          " + Main.RUNS + "\n");
		print("Shuffle:       " + Main.SHUFFLE + "\n");
		print("Start:         " + report.getStart() + " (" + ZonedDateTime.ofInstant(Instant.ofEpochMilli(report.getStart()), ZoneId.systemDefault()) + ")\n");
		print("End:           " + report.getEnd() + " (" + ZonedDateTime.ofInstant(Instant.ofEpochMilli(report.getEnd()), ZoneId.systemDefault()) + ")\n");
		long duration = report.getEnd() - report.getStart();
		print("Duration:      " + duration + "ms (" + Utilities.time(duration) + ")\n");
		print("Compute Time:  " + report.getComputeTime() + "ms (" + Utilities.time(report.getComputeTime()) + ")\n");
		print("\n= Problems =\n\n");
		print(report.problems);
		print("\n\n= Planners =\n\n");
		print(report.planners);
		print("\n\n= Results =\n\n");
		print(report.results);
		print("\n\n= Summary =\n\n");
		print(report.summary);
	}
	
	/**
	 * Prints a {@link Table table} in plain text to this printer's destination.
	 * 
	 * @param table the table to print
	 * @throws IOException if an exception occurs while writing the table to
	 * this printer's destination
	 */
	public void print(Table table) throws IOException {
		int[] width = new int[table.columns.size()];
		for(Table.Column column : table.columns)
			width[column.getIndex()] = Math.max(width[column.getIndex()], toString(column.label).length());
		for(Table.Cell cell : table)
			width[cell.column.getIndex()] = Math.max(width[cell.column.getIndex()], toString(cell.get()).length());
		String line = "+";
		for(int w : width) {
			for(int i=0; i<w; i++)
				line += "-";
			line += "--+";
		}
		print(line + "\n");
		for(Table.Column column : table.columns) {
			if(column.getIndex() == 0)
				print("| ");
			else
				print(" | ");
			print(column.label, width[column.getIndex()]);
			if(column.getIndex() == table.columns.size() - 1)
				print(" |");
		}
		print("\n" + line);
		for(Table.Row row : table.rows) {
			print("\n| ");
			for(Table.Cell cell : row) {
				if(cell.column.getIndex() > 0)
					print(" | ");
				print(cell.get(), width[cell.column.getIndex()]);
			}
			print(" |");
		}
		print("\n" + line);
	}
	
	private final String toString(Object object, int padding) {
		if(object instanceof Number) {
			if(object instanceof Double || object instanceof Float)
				object = String.format("%." + DECIMAL_PLACES + "f", object);
		}
		else
			padding *= -1;
		
		return String.format("%" + padding + "s", toString(object));
	}
	
	private final String toString(Object object) {
		if(object == null)
			return "";
		else
			return object.toString();
	}
	
	private final void print(Object object, int padding) throws IOException {
		writer.append(toString(object, padding));
	}
	
	private final void print(Object object) throws IOException {
		writer.append(toString(object));
	}

	@Override
	public void close() throws IOException {
		writer.close();
	}
}