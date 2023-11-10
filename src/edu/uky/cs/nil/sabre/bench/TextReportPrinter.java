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

public class TextReportPrinter implements ReportPrinter {

	public static final int DECIMAL_PLACES = 2;
	
	private final Writer writer;
	
	public TextReportPrinter(Writer writer) {
		this.writer = writer;
	}
	
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
		print("Start:         " + ZonedDateTime.ofInstant(Instant.ofEpochMilli(report.getStart()), ZoneId.systemDefault()) + "\n");
		print("End:           " + ZonedDateTime.ofInstant(Instant.ofEpochMilli(report.getEnd()), ZoneId.systemDefault()) + "\n");
		print("Duration:      " + (report.getEnd() - report.getStart()) + "ms\n");
		print("\n= Problems =\n\n");
		print(report.problems);
		print("\n\n= Compiled Problems =\n\n");
		print(report.compiled);
		print("\n\n= Planners =\n\n");
		print(report.planners);
		print("\n\n= Results =\n\n");
		print(report.results);
		print("\n\n= Summary =\n\n");
		print(report.summary);
		print("\n\n");
		print(report.best);
	}
	
	public void print(Table table) throws IOException {
		int[] width = new int[table.columns.size()];
		for(Table.Column column : table.columns)
			width[column.index] = Math.max(width[column.index], toString(column.label).length());
		for(Table.Cell cell : table)
			width[cell.column.index] = Math.max(width[cell.column.index], toString(cell.get()).length());
		String line = "+";
		for(int w : width) {
			for(int i=0; i<w; i++)
				line += "-";
			line += "--+";
		}
		print(line + "\n");
		for(Table.Column column : table.columns) {
			if(column.index == 0)
				print("| ");
			else
				print(" | ");
			print(column.label, width[column.index]);
			if(column.index == table.columns.size() - 1)
				print(" |");
		}
		print("\n" + line);
		for(Table.Row row : table.rows) {
			print("\n| ");
			for(Table.Cell cell : row) {
				if(cell.column.index > 0)
					print(" | ");
				print(cell.get(), width[cell.column.index]);
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