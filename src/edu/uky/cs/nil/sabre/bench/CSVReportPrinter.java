package edu.uky.cs.nil.sabre.bench;

import java.io.File;
import java.io.IOException;

public class CSVReportPrinter implements ReportPrinter {
	
	private final File directory;
	
	public CSVReportPrinter(File directory) {
		if(!directory.isDirectory())
			throw new IllegalArgumentException("A CSV report printer created multiple files; it must be initialized with a directory.");
		this.directory = directory;
	}
	
	@Override
	public void print(Report report) throws IOException {
		print(report.problems, "problems");
		print(report.compiled, "compiled");
		print(report.planners, "planners");
		print(report.results, "results");
		print(report.summary, "summary");
		print(report.best, "best");
	}
	
	public void print(Table table, String name) throws IOException {
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