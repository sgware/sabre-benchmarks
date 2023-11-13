package edu.uky.cs.nil.sabre.bench;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import edu.uky.cs.nil.sabre.Problem;
import edu.uky.cs.nil.sabre.comp.CompiledAction;
import edu.uky.cs.nil.sabre.comp.CompiledProblem;
import edu.uky.cs.nil.sabre.prog.ProgressionPlanner;
import edu.uky.cs.nil.sabre.search.Result;
import edu.uky.cs.nil.sabre.util.ImmutableArray;

/**
 * A report is a collection of information, mostly organized into {@link Table
 * tables}, about the results of testing many {@link ProgressionPlanner
 * planners} on many {@link Benchmark benchmark problems}.
 * 
 * @author Stephen G. Ware
 */
public class Report {

	/**
	 * Column label in the {@link #problems problems table} and {@link #compiled
	 * compiled problems table} for the {@link Benchmark#name name of a
	 * benchmark problem}
	 */
	public static final String PROBLEMS_NAME = "Name";
	
	/**
	 * Column label in the {@link #problems problems table} and {@link #compiled
	 * compiled problems table} for the number of {@link
	 * edu.uky.cs.nil.sabre.Universe#characters characters} in a problem
	 */
	public static final String PROBLEMS_CHARACTERS = "Characters";
	
	/**
	 * Column label in the {@link #problems problems table} and {@link #compiled
	 * compiled problems table} for the number of {@link
	 * edu.uky.cs.nil.sabre.Universe#entities entities} in a problem
	 */
	public static final String PROBLEMS_ENTITIES = "Entities";
	
	/**
	 * Column label in the {@link #problems problems table} and {@link #compiled
	 * compiled problems table} for the number of {@link Problem#fluents
	 * fluents} in a problem
	 */
	public static final String PROBLEMS_FLUENTS = "Fluents";
	
	/**
	 * Column label in the {@link #problems problems table} and {@link #compiled
	 * compiled problems table} for the number of {@link Problem#actions
	 * actions} in a problem
	 */
	public static final String PROBLEMS_ACTIONS = "Actions";
	
	/**
	 * Column label in the {@link #problems problems table} and {@link #compiled
	 * compiled problems table} for the number of {@link Problem#triggers
	 * triggers} in a problem
	 */
	public static final String PROBLEMS_TRIGGERS = "Triggers";
	
	/**
	 * Column label in the {@link #problems problems table} and {@link #compiled
	 * compiled problems table} for the {@link
	 * edu.uky.cs.nil.sabre.search.Search#getGoal() utility a solution to the
	 * benchmark problem must achieve}
	 */
	public static final String PROBLEMS_GOAL = "Goal";
	
	/**
	 * Column label in the {@link #problems problems table} and {@link #compiled
	 * compiled problems table} for the {@link
	 * edu.uky.cs.nil.sabre.search.Search#authorTemporalLimit author temporal
	 * limit} used for the problem
	 */
	public static final String PROBLEMS_ATL = "ATL";
	
	/**
	 * Column label in the {@link #problems problems table} and {@link #compiled
	 * compiled problems table} for the {@link
	 * edu.uky.cs.nil.sabre.search.Search#characterTemporalLimit character
	 * temporal limit} used for the problem
	 */
	public static final String PROBLEMS_CTL = "CTL";
	
	/**
	 * Column label in the {@link #problems problems table} and {@link #compiled
	 * compiled problems table} for the {@link
	 * edu.uky.cs.nil.sabre.search.Search#epistemicLimit epistemic limit} used
	 * for the problem
	 */
	public static final String PROBLEMS_EL = "EL";
	
	/**
	 * Column label in the {@link #planners planners table} for the {@link
	 * edu.uky.cs.nil.sabre.search.Planner#name name of the planner}
	 */
	public static final String PLANNERS_NAME = "Name";
	
	/**
	 * Column label in the {@link #planners planners table} for the {@link
	 * ProgressionPlanner.Method search method used by the planner}
	 */
	public static final String PLANNERS_SEARCH = "Search Method";
	
	/**
	 * Column label in the {@link #planners planners table} for the {@link
	 * ProgressionPlanner#getCost() cost function used by the planner}
	 */
	public static final String PLANNERS_COST = "Cost";
	
	/**
	 * Column label in the {@link #planners planners table} for the {@link
	 * ProgressionPlanner#getCost() heuristic function used by the planner}
	 */
	public static final String PLANNERS_HEURISTIC = "Heuristic";
	
	/**
	 * Column label in the {@link #results results table} for the {@link
	 * Benchmark#name name of a benchmark problem}
	 */
	public static final String RESULTS_PROBLEM = "Problem";
	
	/**
	 * Column label in the {@link #results results table} for the {@link
	 * ProgressionPlanner#name name of a planner}
	 */
	public static final String RESULTS_PLANNER = "Planner";
	
	/**
	 * Column label in the {@link #results results table} for the index in the
	 * number of runs (min 1, max {@link Main#RUNS}).
	 */
	public static final String RESULTS_RUN = "Run";
	
	/**
	 * Column label in the {@link #results results table} indicating whether or
	 * not the planner found a solution that met or exceeded the required goal
	 * utility
	 */
	public static final String RESULTS_SUCCESS = "Success?";
	
	/**
	 * Column label in the {@link #results results table} for the number of
	 * actions in the author's plan to solve the problem, if a suitable solution
	 * was found
	 */
	public static final String RESULTS_PLAN_LENGTH = "Plan Length";
	
	/**
	 * Column label in the {@link #results results table} for the {@link
	 * edu.uky.cs.nil.sabre.prog.ProgressionSearch#getVisited() number of nodes
	 * visited by the search}
	 */
	public static final String RESULTS_VISITED = "Nodes Visited";
	
	/**
	 * Column label in the {@link #results results table} for the {@link
	 * edu.uky.cs.nil.sabre.prog.ProgressionSearch#getGenerated() number of
	 * nodes generated by the search}
	 */
	public static final String RESULTS_GENERATED = "Nodes Generated";
	
	/**
	 * Column label in the {@link #results results table} for the {@link
	 * Result#time amount of time take by the search} (in milliseconds)
	 */
	public static final String RESULTS_TIME = "Time (ms)";
	
	/**
	 * Column label in the {@link #summary summary table} for the {@link
	 * Benchmark#name name of a benchmark problem}
	 */
	public static final String SUMMARY_PROBLEM = "Problem";
	
	/**
	 * Column label in the {@link #summary summary table} for the {@link
	 * ProgressionPlanner#name name of a planner}
	 */
	public static final String SUMMARY_PLANNER = "Planner";
	
	/**
	 * Column label in the {@link #summary summary table} for the minimum {@link
	 * edu.uky.cs.nil.sabre.prog.ProgressionSearch#getVisited() number of nodes
	 * visited} across all runs of one planner on one problem
	 */
	public static final String SUMMARY_MIN_VISITED = "Min Nodes Visited";
	
	/**
	 * Column label in the {@link #summary summary table} for the maximum {@link
	 * edu.uky.cs.nil.sabre.prog.ProgressionSearch#getVisited() number of nodes
	 * visited} across all runs of one planner on one problem
	 */
	public static final String SUMMARY_MAX_VISITED = "Max Nodes Visited";
	
	/**
	 * Column label in the {@link #summary summary table} for the average {@link
	 * edu.uky.cs.nil.sabre.prog.ProgressionSearch#getVisited() number of nodes
	 * visited} across all runs of one planner on one problem
	 */
	public static final String SUMMARY_AVG_VISITED = "Avg. Nodes Visited";
	
	/**
	 * Column label in the {@link #summary summary table} for the standard
	 * deviation in the {@link
	 * edu.uky.cs.nil.sabre.prog.ProgressionSearch#getVisited() number of nodes
	 * visited} across all runs of one planner on one problem
	 */
	public static final String SUMMARY_STD_VISITED = "Nodes Visited Std.";
	
	/**
	 * Column label in the {@link #summary summary table} for the minimum {@link
	 * edu.uky.cs.nil.sabre.prog.ProgressionSearch#getGenerated() number of
	 * nodes generated} across all runs of one planner on one problem
	 */
	public static final String SUMMARY_MIN_GENERATED = "Min Nodes Generated";
	
	/**
	 * Column label in the {@link #summary summary table} for the maximum {@link
	 * edu.uky.cs.nil.sabre.prog.ProgressionSearch#getGenerated() number of
	 * nodes generated} across all runs of one planner on one problem
	 */
	public static final String SUMMARY_MAX_GENERATED = "Max Nodes Generated";
	
	/**
	 * Column label in the {@link #summary summary table} for the average {@link
	 * edu.uky.cs.nil.sabre.prog.ProgressionSearch#getGenerated() number of
	 * nodes generated} across all runs of one planner on one problem
	 */
	public static final String SUMMARY_AVG_GENERATED = "Avg. Nodes Generated";
	
	/**
	 * Column label in the {@link #summary summary table} for the standard
	 * deviation in the {@link
	 * edu.uky.cs.nil.sabre.prog.ProgressionSearch#getGenerated() number of
	 * nodes generated} across all runs of one planner on one problem
	 */
	public static final String SUMMARY_STD_GENERATED = "Nodes Generated Std.";
	
	/**
	 * Column label in the {@link #summary summary table} for the minimum {@link
	 * Result#time amount of time taken} across all runs of one planner on one
	 * problem
	 */
	public static final String SUMMARY_MIN_TIME = "Min Time (ms)";
	
	/**
	 * Column label in the {@link #summary summary table} for the maximum {@link
	 * Result#time amount of time taken} across all runs of one planner on one
	 * problem
	 */
	public static final String SUMMARY_MAX_TIME = "Max Time (ms)";
	
	/**
	 * Column label in the {@link #summary summary table} for the average {@link
	 * Result#time amount of time taken} across all runs of one planner on one
	 * problem
	 */
	public static final String SUMMARY_AVG_TIME = "Avg. Time (ms)";
	
	/**
	 * Column label in the {@link #summary summary table} for the standard
	 * deviation in the {@link Result#time amount of time taken} across all runs
	 * of one planner on one problem
	 */
	public static final String SUMMARY_STD_TIME = "Time Std. (ms)";
	
	/**
	 * Column label in the {@link #best best table} for the {@link
	 * Benchmark#name name of a benchmark problem}
	 */
	public static final String BEST_PROBLEM = "Problem";
	
	/**
	 * Column label in the {@link #best best table} for the {@link
	 * ProgressionPlanner#name name of the planner} with the lowest average
	 * number of nodes visited for the problem
	 */
	public static final String BEST_PLANNER_VISITED = "Best Planner (Visited)";
	
	/**
	 * Column label in the {@link #best best table} for the average {@link
	 * edu.uky.cs.nil.sabre.prog.ProgressionSearch#getVisited() number of nodes
	 * visited} by the planner that had the lowest average number of nodes
	 * visited for the problem
	 */
	public static final String BEST_AVG_VISITED = "Avg. Nodes Visited";
	
	/**
	 * Column label in the {@link #best best table} for the {@link
	 * ProgressionPlanner#name name of the planner} with the lowest average
	 * number of nodes generated for the problem
	 */
	public static final String BEST_PLANNER_GENERATED = "Best Planner (Generated)";
	
	/**
	 * Column label in the {@link #best best table} for the average {@link
	 * edu.uky.cs.nil.sabre.prog.ProgressionSearch#getGenerated() number of
	 * nodes generated} by the planner that had the lowest average number of
	 * nodes generated for the problem
	 */
	public static final String BEST_AVG_GENERATED = "Avg. Nodes Generated";
	
	/**
	 * Column label in the {@link #best best table} for the {@link
	 * ProgressionPlanner#name name of the planner} with the lowest average
	 * time spent for the problem
	 */
	public static final String BEST_PLANNER_TIME = "Best Planner (Time)";
	
	/**
	 * Column label in the {@link #best best table} for the average {@link
	 * Result#time time spent} by the planner with the lowest average amount of
	 * time spent for the problem
	 */
	public static final String BEST_AVG_TIME = "Avg. Time (ms)";
	
	/**
	 * Gives summary statistics about the size, goal, and search limits on each
	 * {@link Benchmark benchmark problem} before the problem is compiled
	 */
	public final Table problems = new Table();
	
	/**
	 * Contains summary statistics about the size, goal, and search limits on
	 * each {@link Benchmark benchmark problem} after the problem is {@link
	 * ProgressionPlanner#compile(Problem, edu.uky.cs.nil.sabre.util.Worker.Status)
	 * compiled}
	 */
	public final Table compiled = new Table();
	
	/**
	 * Contains the name and relevant details of each {@link ProgressionPlanner
	 * planner} to be tested
	 */
	public final Table planners = new Table();
	
	/** Contains the details results of each search */
	public final Table results = new Table();
	
	/**
	 * Contains summary statistics for the performance of each {@link
	 * ProgressionPlanner planner} on each {@link Benchmark benchmark problem}
	 * (this table remains empty until {@link #setEnd() all tests are complete})
	 */
	public final Table summary = new Table();
	
	/**
	 * Contains details on which {@link ProgressionPlanner planner} performed
	 * best on each {@link Benchmark benchmark problem} according to different
	 * metrics (this table remains empty until {@link #setEnd() all tests are
	 * complete})
	 */
	public final Table best = new Table();
	
	/**
	 * The {@link System#currentTimeMillis() time} the tests started, as set by
	 * {@link #setStart()}
	 */
	private long start = -1;
	
	/**
	 * The {@link System#currentTimeMillis() time} the tests ended, as set by
	 * {@link #setEnd()}
	 */
	private long end = -1;
	
	/**
	 * Constructs a new, empty report.
	 */
	public Report() {
		problems.addColumn(PROBLEMS_NAME, String.class);
		problems.addColumn(PROBLEMS_CHARACTERS, Integer.class);
		problems.addColumn(PROBLEMS_ENTITIES, Integer.class);
		problems.addColumn(PROBLEMS_FLUENTS, Integer.class);
		problems.addColumn(PROBLEMS_ACTIONS, Integer.class);
		problems.addColumn(PROBLEMS_TRIGGERS, Integer.class);
		problems.addColumn(PROBLEMS_GOAL, Double.class);
		problems.addColumn(PROBLEMS_ATL, Integer.class);
		problems.addColumn(PROBLEMS_CTL, Integer.class);
		problems.addColumn(PROBLEMS_EL, Integer.class);
		for(Table.Column column : problems.columns)
			compiled.addColumn(column.label, column.type);
		planners.addColumn(PLANNERS_NAME, String.class);
		planners.addColumn(PLANNERS_SEARCH, String.class);
		planners.addColumn(PLANNERS_COST, String.class);
		planners.addColumn(PLANNERS_HEURISTIC, String.class);
		results.addColumn(RESULTS_PROBLEM, String.class);
		results.addColumn(RESULTS_PLANNER, String.class);
		results.addColumn(RESULTS_RUN, Integer.class);
		results.addColumn(RESULTS_SUCCESS, Boolean.class);
		results.addColumn(RESULTS_PLAN_LENGTH, Integer.class);
		results.addColumn(RESULTS_VISITED, Long.class);
		results.addColumn(RESULTS_GENERATED, Long.class);
		results.addColumn(RESULTS_TIME, Long.class);
		summary.addColumn(SUMMARY_PROBLEM, String.class);
		summary.addColumn(SUMMARY_PLANNER, String.class);
		summary.addColumn(SUMMARY_MIN_VISITED, Long.class);
		summary.addColumn(SUMMARY_MAX_VISITED, Long.class);
		summary.addColumn(SUMMARY_AVG_VISITED, Double.class);
		summary.addColumn(SUMMARY_STD_VISITED, Double.class);
		summary.addColumn(SUMMARY_MIN_GENERATED, Long.class);
		summary.addColumn(SUMMARY_MAX_GENERATED, Long.class);
		summary.addColumn(SUMMARY_AVG_GENERATED, Double.class);
		summary.addColumn(SUMMARY_STD_GENERATED, Double.class);
		summary.addColumn(SUMMARY_MIN_TIME, Long.class);
		summary.addColumn(SUMMARY_MAX_TIME, Long.class);
		summary.addColumn(SUMMARY_AVG_TIME, Double.class);
		summary.addColumn(SUMMARY_STD_TIME, Double.class);
		best.addColumn(BEST_PROBLEM, String.class);
		best.addColumn(BEST_PLANNER_VISITED, String.class);
		best.addColumn(BEST_AVG_VISITED, Double.class);
		best.addColumn(BEST_PLANNER_GENERATED, String.class);
		best.addColumn(BEST_AVG_GENERATED, Double.class);
		best.addColumn(BEST_PLANNER_TIME, String.class);
		best.addColumn(BEST_AVG_TIME, Double.class);
	}
	
	@Override
	public String toString() {
		StringWriter string = new StringWriter();
		try(TextReportPrinter printer = new TextReportPrinter(string)) {
			printer.print(this);
		}
		catch(IOException e) {/* will not happen */}
		return string.toString();
	}
	
	/**
	 * Returns the {@link System#currentTimeMillis() timestamp} when the tests
	 * began.
	 * 
	 * @return the time the tests started
	 */
	public long getStart() {
		return start;
	}
	
	/**
	 * Set the {@link #getStart() timestamp} when the tests began to the {@link
	 * System#currentTimeMillis() current system time}.
	 */
	public void setStart() {
		if(start == -1)
			start = System.currentTimeMillis();
		else
			throw new IllegalStateException("Start time already set.");
	}
	
	/**
	 * Returns the {@link System#currentTimeMillis() timestamp} when the tests
	 * ended. This method also causes the {@link #summary summary} and {@link
	 * #best best} tables to be populated.
	 * 
	 * @return the time the tests ended
	 */
	public long getEnd() {
		return end;
	}
	
	/**
	 * Set the {@link #getEnd() timestamp} when the tests ended to the {@link
	 * System#currentTimeMillis() current system time}.
	 */
	public void setEnd() {
		if(end == -1) {
			end = System.currentTimeMillis();
			fillSummary();
		}
		else
			throw new IllegalStateException("End time already set.");
	}
	
	/**
	 * Adds details for a new benchmark problem to the {@link #problems
	 * problems} and {@link #compiled compiled problems} tables.
	 * 
	 * @param problem the new benchmark problem to add
	 */
	public void addProblem(Benchmark problem) {
		addProblem(problem, problem.getProblem(), problems);
		addProblem(problem, problem.getCompiledProblem(), compiled);
	}
	
	private final void addProblem(Benchmark benchmark, Problem problem, Table table) {
		table.addRow(problem);
		table.set(problem, PROBLEMS_NAME, problem.name);
		table.set(problem, PROBLEMS_CHARACTERS, problem.universe.characters.size());
		table.set(problem, PROBLEMS_ENTITIES, problem.universe.entities.size());
		table.set(problem, PROBLEMS_FLUENTS, problem.fluents.size());
		table.set(problem, PROBLEMS_ACTIONS, problem.actions.size());
		table.set(problem, PROBLEMS_TRIGGERS, problem.triggers.size());
		table.set(problem, PROBLEMS_GOAL, benchmark.goal);
		table.set(problem, PROBLEMS_ATL, benchmark.atl);
		table.set(problem, PROBLEMS_CTL, benchmark.ctl);
		table.set(problem, PROBLEMS_EL, benchmark.el);
	}
	
	/**
	 * Adds the details for a new planner to the {@link #planners planners
	 * table}.
	 * 
	 * @param planner the new planner to add
	 */
	public void addPlanner(ProgressionPlanner planner) {
		planners.addRow(planner);
		planners.set(planner, PLANNERS_NAME, planner.name);
		planners.set(planner, PLANNERS_SEARCH, planner.getMethod().toString());
		planners.set(planner, PLANNERS_COST, planner.getCost().toString());
		planners.set(planner, PLANNERS_HEURISTIC, planner.getHeuristic().toString());
	}
	
	/**
	 * Adds the results of one search by one planner on one problem to the
	 * {@link #results results table}.
	 * 
	 * @param problem the benchmark problem being solved
	 * @param planner the planner attempting to solve the problem
	 * @param run the index in the number of runs that planner has attempted
	 * this problem
	 * @param result the result of the planner's search on that problem
	 */
	public void addResult(Benchmark problem, ProgressionPlanner planner, int run, Result<CompiledAction> result) {
		results.addRow(result);
		results.set(result, RESULTS_PROBLEM, problem.name);
		results.set(result, RESULTS_PLANNER, planner.name);
		results.set(result, RESULTS_RUN, run);
		results.set(result, RESULTS_SUCCESS, result.getSuccess());
		if(result.getSuccess())
			results.set(result, RESULTS_PLAN_LENGTH, result.solution.size());
		results.set(result, RESULTS_VISITED, result.visited);
		results.set(result, RESULTS_GENERATED, result.generated);
		results.set(result, RESULTS_TIME, result.time);
	}
	
	/**
	 * A function for getting the {@link Result#visited number of nodes visited}
	 * from a result
	 */
	private static final Function<Result<?>, Number> VISITED = r -> r.visited;
	
	/**
	 * A function for getting the {@link Result#generated number of nodes
	 * generated} from a result
	 */
	private static final Function<Result<?>, Number> GENERATED = r -> r.generated;
	
	/**
	 * A function for getting the {@link Result#time amount of time taken} from
	 * a result
	 */
	private static final Function<Result<?>, Number> TIME = r -> r.time;
	
	/**
	 * Fills in the {@link #summary summary} and {@link #best best} tables.
	 */
	private void fillSummary() {
		for(int i=0; i<compiled.rows.size(); i++) {
			CompiledProblem problem = (CompiledProblem) compiled.rows.get(i).label;
			ProgressionPlanner bestVisitedPlanner = null;
			Double bestVisited = null;
			ProgressionPlanner bestGeneratedPlanner = null;
			Double bestGenerated = null;
			ProgressionPlanner bestTimePlanner = null;
			Double bestTime = null;
			for(int j=0; j<planners.rows.size(); j++) {
				ProgressionPlanner planner = (ProgressionPlanner) planners.rows.get(j).label;
				Object key = new ImmutableArray<>(problem, planner);
				summary.addRow(key);
				summary.set(key, SUMMARY_PROBLEM, problem.name);
				summary.set(key, SUMMARY_PLANNER, planner.name);
				List<Number> visited = collect(problem.name, planner.name, VISITED);
				Double avgVisited = avg(visited);
				if(avgVisited != null && (bestVisitedPlanner == null || avgVisited < bestVisited)) {
					bestVisitedPlanner = planner;
					bestVisited = avgVisited;
				}
				summary.set(key, SUMMARY_MIN_VISITED, min(visited));
				summary.set(key, SUMMARY_MAX_VISITED, max(visited));
				summary.set(key, SUMMARY_AVG_VISITED, avgVisited);
				summary.set(key, SUMMARY_STD_VISITED, std(visited));
				List<Number> generated = collect(problem.name, planner.name, GENERATED);
				Double avgGenerated = avg(generated);
				if(avgGenerated != null && (bestGeneratedPlanner == null || avgGenerated < bestGenerated)) {
					bestGeneratedPlanner = planner;
					bestGenerated = avgGenerated;
				}
				summary.set(key, SUMMARY_MIN_GENERATED, min(generated));
				summary.set(key, SUMMARY_MAX_GENERATED, max(generated));
				summary.set(key, SUMMARY_AVG_GENERATED, avgGenerated);
				summary.set(key, SUMMARY_STD_GENERATED, std(generated));
				List<Number> time = collect(problem.name, planner.name, TIME);
				Double avgTime = avg(time);
				if(avgTime != null && (bestTimePlanner == null || avgTime < bestTime)) {
					bestTimePlanner = planner;
					bestTime = avgTime;
				}
				summary.set(key, SUMMARY_MIN_TIME, min(time));
				summary.set(key, SUMMARY_MAX_TIME, max(time));
				summary.set(key, SUMMARY_AVG_TIME, avgTime);
				summary.set(key, SUMMARY_STD_TIME, std(time));
			}
			best.addRow(problem);
			best.set(problem, BEST_PROBLEM, problem.name);
			if(bestVisitedPlanner != null) {
				best.set(problem, BEST_PLANNER_VISITED, bestVisitedPlanner.name);
				best.set(problem, BEST_AVG_VISITED, bestVisited);
			}
			if(bestGeneratedPlanner != null) {
				best.set(problem, BEST_PLANNER_GENERATED, bestGeneratedPlanner.name);
				best.set(problem, BEST_AVG_GENERATED, bestGenerated);
			}
			if(bestTimePlanner != null) {
				best.set(problem, BEST_PLANNER_TIME, bestTimePlanner.name);
				best.set(problem, BEST_AVG_TIME, bestTime);
			}
		}
	}
	
	/**
	 * Collects all values of a type for one planner on one problem from the
	 * {@link #results results table}. Values will only be collected if the
	 * planner succeeded in solving the problem.
	 * 
	 * @param problem the problem being solved
	 * @param planner the planner attempting to solve the problem
	 * @param statistic the type of value to be collected
	 * @return all values of that type when the planner succeeded in solving
	 * the problem
	 */
	private final List<Number> collect(String problem, String planner, Function<Result<?>, Number> statistic) {
		ArrayList<Number> values = new ArrayList<>();
		for(Table.Row row : results.rows)
			if(row.get(RESULTS_PROBLEM).get().equals(problem) && row.get(RESULTS_PLANNER).get().equals(planner) && ((Result<?>) row.label).getSuccess())
				values.add(statistic.apply((Result<?>) row.label));
		return values;
	}
	
	/**
	 * Finds the minimum in a list of values.
	 * 
	 * @param values the values
	 * @return the smallest value, or null if the list of values was empty
	 */
	private static final Number min(List<Number> values) {
		Number min = null;
		for(Number value : values)
			if(min == null || compare(value, min) < 0)
				min = value;
		return min;
	}
	
	/**
	 * Finds the maximum in a list of values.
	 * 
	 * @param values the values
	 * @return the largest value, or null if the list of values was empty
	 */
	private static final Number max(List<Number> values) {
		Number max = null;
		for(Number value : values)
			if(max == null || compare(value, max) > 0)
				max = value;
		return max;
	}
	
	/**
	 * Compares two {@link Number Java number objects}.
	 * 
	 * @param n1 the first number to compare
	 * @param n2 the second number to compare
	 * @return a negative number if the first number is smaller than the second,
	 * a positive number if the first number is larger than the second, or 0 if
	 * the numbers are the same
	 */
	private static final int compare(Number n1, Number n2) {
		return new BigDecimal(n1.toString()).compareTo(new BigDecimal(n2.toString()));
	}
	
	/**
	 * Calculates the average of a list of values.
	 * 
	 * @param values the values to average
	 * @return the average, or null if the list of values was empty
	 */
	private static final Double avg(List<Number> values) {
		if(values.size() == 0)
			return null;
		double total = 0;
		double count = 0;
		for(Number value : values) {
			total += value.doubleValue();
			count++;
		}
		return total / count;
	}
	
	/**
	 * Calculates the standard deviation in a list of values.
	 * 
	 * @param values the values for which to calculate standard deviation
	 * @return the standard deviation, or null if the list of values was empty
	 */
	private static final Number std(List<Number> values) {
		if(values.size() == 0)
			return null;
		double mean = avg(values);
		double count = 0;
		double sumOfSquares = 0;
		for(Number value : values) {
			count++;
			double difference = value.doubleValue() - mean;
			sumOfSquares += difference * difference;
		}
		return Math.sqrt(sumOfSquares / count);
	}
}