package edu.uky.cs.nil.sabre.bench;

import java.io.Closeable;
import java.io.IOException;

/**
 * Print the {@link Report report} produced by {@link Main the main class} in
 * one of several formats.
 * 
 * @author Stephen G. Ware
 */
public interface ReportPrinter extends Closeable {

	/**
	 * Prints the report to the destination and in the format defined by this
	 * printer.
	 * 
	 * @param report the report to print in this printer's format
	 * @throws IOException in an exception occurs when writing the format to
	 * its destination
	 */
	public void print(Report report) throws IOException;
}