package edu.uky.cs.nil.sabre.bench;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * A statistic is a calculation to be performed on a collection of numeric
 * values. Statistics that operate on integers all use {@link Long long} values,
 * and statistics that operator on decimal numbers all use {@link Double double}
 * values.
 * 
 * @param <N> the type of number output by the statistic's calculation
 */
public interface Statistic<N extends Number> {

	/** Counts the number of non-null values in a collection of numbers */
	public static final Statistic<Long> COUNT = new Statistic<>() {

		@Override
		public Long calculate(Iterable<? extends Number> values) {
			long count = 0;
			for(Number value : values)
				if(value != null)
					count++;
			return count;
		}
	};
	
	/**
	 * Returns the smallest non-null integer in a collection of longs, or null
	 * if no non-null numbers were given
	 */
	public static final Statistic<Long> MIN_INTEGER = new Statistic<>() {

		@Override
		public Long calculate(Iterable<? extends Number> values) {
			Long min = null;
			for(Long value : toIntegers(values))
				if(value != null && (min == null || compare(value, min) < 0))
					min = value;
			return min;
		}		
	};
	
	/**
	 * Returns the largest non-null integer in a collection of longs, or null if
	 * no non-null numbers were given
	 */
	public static final Statistic<Long> MAX_INTEGER = new Statistic<>() {

		@Override
		public Long calculate(Iterable<? extends Number> values) {
			Long max = null;
			for(Long value : toIntegers(values))
				if(value != null && (max == null || compare(value, max) > 0))
					max = value;
			return max;
		}		
	};
	
	/**
	 * Adds all non-null integers in a collection of integers and returns the
	 * sum as an integer.
	 */
	public static final Statistic<Long> SUM_INTEGER = new Statistic<>() {

		@Override
		public Long calculate(Iterable<? extends Number> values) {
			long sum = 0;
			for(Number value : values)
				if(value != null)
					sum += toInteger(value);
			return sum;
		}
	};
	
	/**
	 * Adds all non-null numbers in a collection and returns the sum as a
	 * double.
	 */
	public static final Statistic<Double> SUM_DECIMAL = new Statistic<>() {

		@Override
		public Double calculate(Iterable<? extends Number> values) {
			double sum = 0;
			for(Number value : values)
				if(value != null)
					sum += toDecimal(value);
			return sum;
		}
	};
	
	/**
	 * Returns the mean, or average, of all non-null numbers in collection, or
	 * null if no non-null numbers were given
	 */
	public static final Statistic<Double> AVERAGE = new Statistic<>() {

		@Override
		public Double calculate(Iterable<? extends Number> values) {
			Long count = COUNT.calculate(values);
			if(count == 0)
				return null;
			return SUM_DECIMAL.calculate(values) / count.doubleValue();
		}
	};
	
	/**
	 * Returns the standard deviation of all non-null numbers in a collection,
	 * or null if no non-null numbers were given
	 */
	public static final Statistic<Double> STANDARD_DEVIATION = new Statistic<>() {

		@Override
		public Double calculate(Iterable<? extends Number> values) {
			Double average = AVERAGE.calculate(values);
			if(average == null)
				return null;
			double sum = 0;
			for(Double value : toDecimals(values)) {
				if(value != null) {
					double difference = value - average;
					sum += difference * difference;
				}
			}
			double count = COUNT.calculate(values).doubleValue();
			return Math.sqrt(sum / count);
		}
	};
	
	/** Orders integer values from smallest to largest */
	public static final Comparator<Long> INTEGER_ASCENDING = new Comparator<Long>() {

		@Override
		public int compare(Long n1, Long n2) {
			return Statistic.compare(n1, n2);
		}
	};
	
	/** Orders integer values from largest to smallest */
	public static final Comparator<Long> INTEGER_DESCENDING = new Comparator<Long>() {

		@Override
		public int compare(Long n1, Long n2) {
			return Statistic.compare(n1, n2) * -1;
		}
	};
	
	/** Orders decimal values from smallest to largest */
	public static final Comparator<Double> DECIMAL_ASCENDING = new Comparator<Double>() {

		@Override
		public int compare(Double n1, Double n2) {
			return Statistic.compare(n1, n2);
		}
	};
	
	/** Orders decimal values from largest to smallest */
	public static final Comparator<Double> DECIMAL_DESCENDING = new Comparator<Double>() {

		@Override
		public int compare(Double n1, Double n2) {
			return Statistic.compare(n1, n2) * -1;
		}
	};
	
	/**
	 * Converts a number to a Java {@code long} integer, or returns null if the
	 * given number is null
	 * 
	 * @param number the number to convert to a {@code long}
	 * @return the number as a {@code long}
	 */
	public static Long toInteger(Number number) {
		if(number == null)
			return null;
		else
			return number.longValue();
	}
	
	/**
	 * Converts a collection of numbers to Java {@code long} integers, leaving
	 * any null values as null.
	 * 
	 * @param numbers the numbers to convert to a {@code long}s
	 * @return the numbers as {@code long}s
	 */
	public static Iterable<Long> toIntegers(Iterable<? extends Number> numbers) {
		ArrayList<Long> list = new ArrayList<>();
		for(Number number : numbers)
			list.add(toInteger(number));
		return list;
	}
	
	/**
	 * Converts a number to a Java {@code double} decimal, or returns null if
	 * the given number is null
	 * 
	 * @param number the number to convert to a {@code double}
	 * @return the number as a {@code double}
	 */
	public static Double toDecimal(Number number) {
		if(number == null)
			return null;
		else
			return number.doubleValue();
	}
	
	/**
	 * Converts a collection of numbers to Java {@code double} decimals, leaving
	 * any null values as null.
	 * 
	 * @param numbers the numbers to convert to a {@code double}s
	 * @return the numbers as {@code double}s
	 */
	public static Iterable<Double> toDecimals(Iterable<? extends Number> numbers) {
		ArrayList<Double> list = new ArrayList<>();
		for(Number number : numbers)
			list.add(toDecimal(number));
		return list;
	}
	
	/**
	 * Returns a negative integer, zero, or a positive integer as the first
	 * number is less than, equal to, or greater than the second.
	 * 
	 * @param n1 the first number to compare
	 * @param n2 the second number to compare
	 * @return a negative integer, zero, or a positive integer as the first
	 * number is less than, equal to, or greater than the second
	 */
	public static int compare(Number n1, Number n2) {
		return new BigDecimal(n1.toString()).compareTo(new BigDecimal(n2.toString()));
	}
	
	/**
	 * Performs that statistic's calculation and returns the result.
	 * 
	 * @param values the collection of numbers on which to perform the
	 * calculation
	 * @return the value of the calculation
	 */
	public N calculate(Iterable<? extends Number> values);
}