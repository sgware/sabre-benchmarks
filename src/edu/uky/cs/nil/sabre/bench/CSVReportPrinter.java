package edu.uky.cs.nil.sabre.bench;

import java.io.File;
import java.io.IOException;

/**
 * A {@link ReportPrinter report printer} that writes the various tables in a
 * {@link Report report} to CSV (comma separated value) files.
 * 
 * @author Stephen G. Ware
 */
public class CSVReportPrinter implements ReportPrinter {
	
	private final File directory;
	
	/**
	 * Constructs a new CSV report printer that will print the tables of a
	 * {@link Report report} to individual CSV files in the given directory.
	 * 
	 * @param directory a directory
	 * @throws IllegalArgumentException if the file provided is not a directory
	 */
	public CSVReportPrinter(File directory) {
		if(!directory.isDirectory())
			throw new IllegalArgumentException("A CSV report printer creates multiple files; it must be initialized with a directory.");
		this.directory = directory;
	}
	
	@Override
	public void print(Report report) throws IOException {
		print(report.problems, "problems");
		print(report.planners, "planners");
		print(report.results, "results");
		print(report.summary, "summary");
	}
	
	private final void print(Table table, String name) throws IOException {
		File file = new File(directory.getPath() + File.separator + name + ".csv");
		try(CSVTablePrinter writer = new CSVTablePrinter(file)) {
			writer.print(table);
		}
	}
	
	@Override
	public void close() throws IOException {
		// do nothing
	}
}