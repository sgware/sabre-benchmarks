package edu.uky.cs.nil.sabre.bench;

import java.io.Closeable;
import java.io.IOException;

public interface ReportPrinter extends Closeable {

	public void print(Report report) throws IOException;
}