package edu.uky.cs.nil.sabre.bench;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import edu.uky.cs.nil.sabre.Settings;

public class HTMLReportPrinter implements ReportPrinter {

	public static final int DECIMAL_PLACES = 2;
	public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm:ss z");
	private final Writer writer;
	
	public HTMLReportPrinter(Writer writer) {
		this.writer = writer;
	}
	
	public HTMLReportPrinter(File file) throws IOException {
		this(new BufferedWriter(new FileWriter(file)));
	}

	@Override
	public void print(Report report) throws IOException {
		print("<!DOCTYPE html>\n");
		print("<html lang=\"en\">\n");
		print("\t<head>\n");
		print("\t\t<title>Sabre Benchmark Results</title>\n");
		print("\t\t<meta charset=\"utf-8\">\n");
		print("\t\t<style>\n");
		print("\t\t\ttable, th, td { border: 1px solid black; padding: 0em 0.5em; }\n");
		print("\t\t\ttable { border-collapse: collapse; font-family: monospace; }\n");
		print("\t\t\ttr:nth-child(odd) { background-color: lightgray; }\n");
		print("\t\t\t.text { text-align: left; }\n");
		print("\t\t\t.number { text-align: right; }\n");
		print("\t\t</style>\n");
		print("\t</head>\n");
		print("\t<body>\n");
		print("\t\t<h1>Sabre Benchmark Results</h1>\n");
		print("\t\t<p>This report describes benchmark tests for the " + Settings.TITLE + " version " + Settings.VERSION_STRING + " by " + Settings.AUTHORS + ".</p>\n");
		print("\t\t<p>Tests were run on the " + System.getProperty("java.vendor") + " Java Runtime Environment version " + System.getProperty("java.version") + ", ");
		print("running on the " + System.getProperty("os.name") + " operating system version " + System.getProperty("os.version") + ".</p>\n");
		print("\t\t<p>Tests began on ");
		print(toDateTime(report.getStart()));
		print(" and ended on ");
		print(toDateTime(report.getEnd()));
		print("; they took " + toDuration(report.getEnd() - report.getStart()) + ".</p>\n");
		print("\t\t<p>This report was automatically generated by the <a href=\"https://github.com/sgware/sabre-benchmarks\">Sabre Benchmark Tool</a>.</p>\n");
		print("\t\t<h2>Problems</h2>\n");
		print("\t\t<p>Summary statistics for the test problems are given in the table below.</p>\n");
		print(report.problems);
		print("\t\t<p>Summary statistics for the test problems after they were grounded and simplified are given in the table below.</p>\n");
		print(report.compiled);
		print("\t\t<p>ATL stands for Author Temporal Limit. CTL stands for Character Temporal Limit. EL stands for Epistemic Limit.</p>\n");
		print("\t\t<h2>Planners</h2>\n");
		print("\t\t<p>Summary statistics for the planning algorithms tested are given in the table below.</p>");
		print(report.planners);
		print("\t\t<h2>Summary of Results</h2>\n");
		print("\t\t<p>The performance of each planner on each problem is given in the table below.</p>\n");
		print(report.summary);
		print("\t\t<p>The best planner for each problem is given in the table below.</p>\n");
		print(report.best);
		print("\t\t<h2>Full Results</h2>\n");
		print("\t\t<p>Full details for each test are given in the table below.</p>\n");
		print(report.results);
		print("\t</body>\n");
		print("</html>");
	}
	
	private void print(Table table) throws IOException {
		print("\t\t<table>\n");
		print("\t\t\t<tr>\n");
		for(Table.Column column : table.columns) {
			print("\t\t\t\t<th class=\"text\">");
			print(column.label);
			print("</th>\n");
		}
		print("\t\t\t</tr>\n");
		for(Table.Row row : table.rows)
			print(row);
		print("\t\t</table>\n");
	}
	
	private void print(Table.Row row) throws IOException {
		print("\t\t\t<tr>\n");
		for(Table.Cell cell : row)
			print(cell);
		print("\t\t\t</tr>\n");
	}
	
	private void print(Table.Cell cell) throws IOException {
		print("\t\t\t\t<td class=\"" + (Number.class.isAssignableFrom(cell.column.type) ? "number" : "text") + "\">");
		print(cell.get());
		print("</td>\n");
	}
	
	private void print(Object object) throws IOException {
		if(object instanceof Number) {
			if(object instanceof Double || object instanceof Float)
				object = String.format("%." + DECIMAL_PLACES + "f", object);
		}
		else if(object instanceof ZonedDateTime)
			object = DATE_TIME_FORMAT.format((ZonedDateTime) object);
		if(object != null)
			writer.append(object.toString());
	}
	
	private static final ZonedDateTime toDateTime(long timestamp) {
		Instant instant = Instant.ofEpochMilli(timestamp);
		return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
	}
	
	private static final String toDuration(long milliseconds) {
		long days = TimeUnit.MILLISECONDS.toDays(milliseconds);
		milliseconds -= TimeUnit.DAYS.toMillis(days);
		long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
		milliseconds -= TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
		milliseconds -= TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds);
		milliseconds -= TimeUnit.SECONDS.toMillis(seconds);
		String string = "";
		if(days > 0)
			string += days + " days";
		if(hours > 0)
			string += " " + hours + " hours";
		if(minutes > 0)
			string += " " + minutes + " minutes";
		if(seconds > 0)
			string += " " + seconds + " seconds";
		if(milliseconds > 0)
			string += " " + milliseconds + " milliseconds";
		if(string.isEmpty())
			return "0 milliseconds";
		return string.trim();
	}

	@Override
	public void close() throws IOException {
		writer.close();
	}
}