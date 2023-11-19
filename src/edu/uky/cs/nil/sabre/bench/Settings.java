package edu.uky.cs.nil.sabre.bench;

/**
 * Important settings.
 * 
 * @author Stephen G. Ware
 */
public class Settings {

	/** The full name of this software library */
	public static final String TITLE = "The Sabre Benchmark Tool";
	
	/** The list of primary authors */
	public static final String AUTHORS = "Stephen G. Ware";
	
	/** The major version number comes before the decimal points */
	public static final int MAJOR_VERSION_NUMBER = 1;
	
	/** The minor version number comes after the decimal point */
	public static final int MINOR_VERSION_NUMBER = 0;
	
	/** The full version number (major + minor) as a string */
	public static final String VERSION_STRING = MAJOR_VERSION_NUMBER + "." + MINOR_VERSION_NUMBER;
	
	/**
	 * A long encoding the version number which can be used as a serial version UID
	 */
	public static final long VERSION_UID = java.nio.ByteBuffer.allocate(8).putInt(MAJOR_VERSION_NUMBER).putInt(MINOR_VERSION_NUMBER).getLong(0);
	
	/** A URL where more information about this software can be found. */
	public static final String URL = "https://github.com/sgware/sabre-benchmarks";
}