package edu.uky.cs.nil.sabre.bench;

import java.io.IOException;
import java.io.StringWriter;
import java.util.function.Predicate;

import edu.uky.cs.nil.sabre.Problem;
import edu.uky.cs.nil.sabre.comp.CompiledAction;
import edu.uky.cs.nil.sabre.comp.CompiledProblem;
import edu.uky.cs.nil.sabre.comp.Grounder;
import edu.uky.cs.nil.sabre.comp.Simplifier;
import edu.uky.cs.nil.sabre.prog.ProgressionPlanner;
import edu.uky.cs.nil.sabre.search.Result;
import edu.uky.cs.nil.sabre.util.ImmutableArray;
import edu.uky.cs.nil.sabre.util.Worker.Status;

/**
 * A report is a collection of information, mostly organized into {@link Table
 * tables}, about the results of testing many {@link ProgressionPlanner
 * planners} on many {@link Benchmark benchmark problems}.
 * 
 * @author Stephen G. Ware
 */
public class Report {

	/**
	 * Column label in the {@link #problems problems table} for the {@link
	 * Benchmark#name name of a benchmark problem}
	 */
	public static final String PROBLEMS_NAME = "Name";
	
	/**
	 * Column label in the {@link #problems problems table} for the number of
	 * {@link edu.uky.cs.nil.sabre.Universe#characters characters} in a problem
	 */
	public static final String PROBLEMS_CHARACTERS = "Characters";
	
	/**
	 * Column label in the {@link #problems problems table} for the number of
	 * {@link edu.uky.cs.nil.sabre.Universe#entities entities} in a problem
	 */
	public static final String PROBLEMS_ENTITIES = "Entities";
	
	/**
	 * Column label in the {@link #problems problems table} for the number of
	 * {@link Problem#fluents fluents} in a problem before compilation
	 */
	public static final String PROBLEMS_FLUENT_TEMPLATES = "Fluent Templates";
	
	/**
	 * Column label in the {@link #problems problems table} for the number of
	 * {@link Problem#fluents fluents} in a problem after it is grounded and
	 * simplified
	 */
	public static final String PROBLEMS_GROUND_FLUENTS = "Ground Fluents";
	
	/**
	 * Column label in the {@link #problems problems table} for the number of
	 * {@link Problem#actions actions} in a problem before compilation
	 */
	public static final String PROBLEMS_ACTION_TEMPLATES = "Action Templates";
	
	/**
	 * Column label in the {@link #problems problems table} for the number of
	 * {@link Problem#actions actions} in a problem after it is gounded and
	 * simplified
	 */
	public static final String PROBLEMS_GROUND_ACTIONS = "Ground Actions";
	
	/**
	 * Column label in the {@link #problems problems table} for the number of
	 * {@link Problem#triggers triggers} in a problem before compilation
	 */
	public static final String PROBLEMS_TRIGGER_TEMPLATES = "Trigger Templates";
	
	/**
	 * Column label in the {@link #problems problems table} for the number of
	 * {@link Problem#triggers triggers} in a problem after it is grounded and
	 * simplified
	 */
	public static final String PROBLEMS_GROUND_TRIGGERS = "Ground Triggers";
	
	/**
	 * Column label in the {@link #problems problems table} for the {@link
	 * edu.uky.cs.nil.sabre.search.Search#getGoal() utility a solution to the
	 * benchmark problem must achieve}
	 */
	public static final String PROBLEMS_GOAL = "Goal";
	
	/**
	 * Column label in the {@link #problems problems table} for the {@link
	 * edu.uky.cs.nil.sabre.search.Search#authorTemporalLimit author temporal
	 * limit} used for the problem
	 */
	public static final String PROBLEMS_ATL = "Author Temporal Limit";
	
	/**
	 * Column label in the {@link #problems problems table} for the {@link
	 * edu.uky.cs.nil.sabre.search.Search#characterTemporalLimit character
	 * temporal limit} used for the problem
	 */
	public static final String PROBLEMS_CTL = "Character Temporal Limit";
	
	/**
	 * Column label in the {@link #problems problems table} for the {@link
	 * edu.uky.cs.nil.sabre.search.Search#epistemicLimit epistemic limit} used
	 * for the problem
	 */
	public static final String PROBLEMS_EL = "Epistemic Limit";
	
	/**
	 * Column label in the {@link #problems problems table} for the total number
	 * of times the problem was solved by all planners
	 */
	public static final String PROBLEMS_SOLVED = "Times Solved";
	
	/**
	 * Column label in the {@link #problems problems table} for the minimum
	 * number of nodes visited by any planner when working on the problem
	 */
	public static final String PROBLEMS_MIN_VISITED = "Min Nodes Visited";
	
	/**
	 * Column label in the {@link #problems problems table} for the maximum
	 * number of nodes visited by any planner when working on the problem
	 */
	public static final String PROBLEMS_MAX_VISITED = "Max Nodes Visited";
	
	/**
	 * Column label in the {@link #problems problems table} for the average
	 * number of nodes visited by any planner when working on the problem
	 */
	public static final String PROBLEMS_AVG_VISITED = "Avg. Nodes Visited";
	
	/**
	 * Column label in the {@link #problems problems table} for the standard
	 * deviation in the number of nodes visited by any planner when working on
	 * the problem
	 */
	public static final String PROBLEMS_STD_VISITED = "Nodes Visited Std.";
	
	/**
	 * Column label in the {@link #problems problems table} for the minimum
	 * number of nodes generated by any planner when working on the problem
	 */
	public static final String PROBLEMS_MIN_GENERATED = "Min Nodes Generated";
	
	/**
	 * Column label in the {@link #problems problems table} for the maximum
	 * number of nodes generated by any planner when working on the problem
	 */
	public static final String PROBLEMS_MAX_GENERATED = "Max Nodes Generated";
	
	/**
	 * Column label in the {@link #problems problems table} for the average
	 * number of nodes generated by any planner when working on the problem
	 */
	public static final String PROBLEMS_AVG_GENERATED = "Avg. Nodes Generated";
	
	/**
	 * Column label in the {@link #problems problems table} for the standard
	 * deviation inthe number of nodes generated by any planner when working on
	 * the problem
	 */
	public static final String PROBLEMS_STD_GENERATED = "Nodes Generated Std.";
	
	/**
	 * Column label in the {@link #problems problems table} for the minimum
	 * amount of time spent by any planner when working on the problem
	 */
	public static final String PROBLEMS_MIN_TIME = "Min Time (ms)";
	
	/**
	 * Column label in the {@link #problems problems table} for the maximum
	 * amount of time spent by any planner when working on the problem
	 */
	public static final String PROBLEMS_MAX_TIME = "Max Time (ms)";
	
	/**
	 * Column label in the {@link #problems problems table} for the average
	 * amount of time spent by any planner when working on the problem
	 */
	public static final String PROBLEMS_AVG_TIME = "Avg. Time (ms)";
	
	/**
	 * Column label in the {@link #problems problems table} for the standard
	 * deviation in the amount of time spent by any planner when working on the
	 * problem
	 */
	public static final String PROBLEMS_STD_TIME = "Time Std. (ms)";
	
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
	 * Column label in the {@link #planners planners table} for the total number
	 * of problems this planner solved across all tests
	 */
	public static final String PLANNERS_SOLVED = "Problems Solved";
	
	/**
	 * Column label in the {@link #planners planners table} for the total number
	 * of nodes this planner visited across all tests
	 */
	public static final String PLANNERS_VISITED = "Total Nodes Visited";
	
	/**
	 * Column label in the {@link #planners planners table} for the total number
	 * of nodes this planner generated across all tests
	 */
	public static final String PLANNERS_GENERATED = "Total Nodes Generated";
	
	/**
	 * Column label in the {@link #planners planners table} for the total number
	 * of milliseconds this planner spent on all tests
	 */
	public static final String PLANNERS_TIME = "Total Time (ms)";
	
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
	 * Column label in the {@link #summary summary table} for the number of
	 * times one planner succeeded on one problem
	 */
	public static final String SUMMARY_SUCCESSES = "Successes";
	
	/**
	 * Column label in the {@link #summary summary table} for the minimum {@link
	 * edu.uky.cs.nil.sabre.Plan#size() number of actions} in a planner's {@link
	 * Result#solution solution} across all runs of one planner on one problem
	 */
	public static final String SUMMARY_MIN_PLAN_LENGTH = "Min Plan Length";
	
	/**
	 * Column label in the {@link #summary summary table} for the maximum {@link
	 * edu.uky.cs.nil.sabre.Plan#size() number of actions} in a planner's {@link
	 * Result#solution solution} across all runs of one planner on one problem
	 */
	public static final String SUMMARY_MAX_PLAN_LENGTH = "Max Plan Length";
	
	/**
	 * Column label in the {@link #summary summary table} for the average {@link
	 * edu.uky.cs.nil.sabre.Plan#size() number of actions} in a planner's {@link
	 * Result#solution solution} across all runs of one planner on one problem
	 */
	public static final String SUMMARY_AVG_PLAN_LENGTH = "Avg. Plan Length";
	
	/**
	 * Column label in the {@link #summary summary table} for the standard
	 * deviation in {@link edu.uky.cs.nil.sabre.Plan#size() the number of
	 * actions} in a planner's {@link Result#solution solution} across all
	 * runs of one planner on one problem
	 */
	public static final String SUMMARY_STD_PLAN_LENGTH = "Plan Length Std.";
	
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
	 * Gives summary statistics about the size, goal, and search limits on each
	 * {@link Benchmark benchmark problem} before the problem is compiled
	 */
	public final Table problems = new Table();
	
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
		problems.addColumn(PROBLEMS_FLUENT_TEMPLATES, Integer.class);
		problems.addColumn(PROBLEMS_GROUND_FLUENTS, Integer.class);
		problems.addColumn(PROBLEMS_ACTION_TEMPLATES, Integer.class);
		problems.addColumn(PROBLEMS_GROUND_ACTIONS, Integer.class);
		problems.addColumn(PROBLEMS_TRIGGER_TEMPLATES, Integer.class);
		problems.addColumn(PROBLEMS_GROUND_TRIGGERS, Integer.class);
		problems.addColumn(PROBLEMS_GOAL, Double.class);
		problems.addColumn(PROBLEMS_ATL, Integer.class);
		problems.addColumn(PROBLEMS_CTL, Integer.class);
		problems.addColumn(PROBLEMS_EL, Integer.class);
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
		summary.addColumn(SUMMARY_SUCCESSES, Long.class);
		summary.addColumn(SUMMARY_MIN_PLAN_LENGTH, Long.class);
		summary.addColumn(SUMMARY_MAX_PLAN_LENGTH, Long.class);
		summary.addColumn(SUMMARY_AVG_PLAN_LENGTH, Double.class);
		summary.addColumn(SUMMARY_STD_PLAN_LENGTH, Double.class);
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
	 * ended. This method also causes the {@link #summary summary} table to be
	 * populated, additional columns to be added to the {@link #problems
	 * problems} and {@link #planners planners} tables, and the problems and
	 * planners tables to be sorted
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
	 * Returns the sum of all {@link Result#time time spent} on all tests across
	 * all threads.
	 * 
	 * @return the total time for all tests in milliseconds
	 */
	public long getComputeTime() {
		long time = 0;
		for(Table.Row row : results.rows)
			time += (Long) row.get(RESULTS_TIME).get();
		return time;
	}
	
	/**
	 * Adds details for a new benchmark problem to the {@link #problems
	 * problems} table. This method will {@link Grounder ground} and then {@link
	 * Simplifier simplify} a problem so that statistics about the ground
	 * problem can be added to the table.
	 * 
	 * @param benchmark the new benchmark problem to add
	 * @param status a status to update while the problem is compiled
	 */
	public void addProblem(Benchmark benchmark, Status status) {
		Problem problem = benchmark.getProblem();
		CompiledProblem compiled = Grounder.compile(problem, new Status());
		compiled = Simplifier.compile(compiled, new Status());
		problems.addRow(problem);
		problems.set(problem, PROBLEMS_NAME, problem.name);
		problems.set(problem, PROBLEMS_CHARACTERS, problem.universe.characters.size());
		problems.set(problem, PROBLEMS_ENTITIES, problem.universe.entities.size());
		problems.set(problem, PROBLEMS_FLUENT_TEMPLATES, problem.fluents.size());
		problems.set(problem, PROBLEMS_GROUND_FLUENTS, compiled.fluents.size());
		problems.set(problem, PROBLEMS_ACTION_TEMPLATES, problem.actions.size());
		problems.set(problem, PROBLEMS_GROUND_ACTIONS, compiled.actions.size());
		problems.set(problem, PROBLEMS_TRIGGER_TEMPLATES, problem.triggers.size());
		problems.set(problem, PROBLEMS_GROUND_TRIGGERS, compiled.triggers.size());
		problems.set(problem, PROBLEMS_GOAL, benchmark.goal);
		problems.set(problem, PROBLEMS_ATL, benchmark.atl);
		problems.set(problem, PROBLEMS_CTL, benchmark.ctl);
		problems.set(problem, PROBLEMS_EL, benchmark.el);
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
	 * Adds the results of one {@link TestSuite.Test test} to the {@link
	 * #results results table}.
	 * 
	 * @param test a completed test
	 * @throws IllegalStateException if the test has not completed
	 */
	public void addResult(TestSuite.Test test) {
		results.addRow(test);
		Result<CompiledAction> result = test.getResult();
		results.set(test, RESULTS_PROBLEM, test.problem.name);
		results.set(test, RESULTS_PLANNER, test.planner.name);
		results.set(test, RESULTS_RUN, test.run);
		results.set(test, RESULTS_SUCCESS, result.getSuccess());
		if(result.getSuccess())
			results.set(test, RESULTS_PLAN_LENGTH, result.solution.size());
		results.set(test, RESULTS_VISITED, result.visited);
		results.set(test, RESULTS_GENERATED, result.generated);
		results.set(test, RESULTS_TIME, result.time);
	}
	
	private static final class ResultsFilter implements Predicate<Table.Cell> {

		public final Object column;
		public final Problem problem;
		public final ProgressionPlanner planner;
		
		public ResultsFilter(Object column, Problem problem, ProgressionPlanner planner) {
			this.column = column;
			this.problem = problem;
			this.planner = planner;
		}
		
		public ResultsFilter(Object column, Problem problem) {
			this(column, problem, null);
		}
		
		public ResultsFilter(Object column, ProgressionPlanner planner) {
			this(column, null, planner);
		}
		
		@Override
		public boolean test(Table.Cell cell) {
			return cell.column.label.equals(column) &&
				(problem == null || cell.row.get(RESULTS_PROBLEM).get().equals(problem.name)) &&
				(planner == null || cell.row.get(RESULTS_PLANNER).get().equals(planner.name));
		}
	}
	
	/**
	 * Fills in the {@link #summary summary} and {@link #best best} tables.
	 */
	private void fillSummary() {
		for(int i=0; i<problems.rows.size(); i++) {
			Problem problem = (Problem) problems.rows.get(i).label;
			for(int j=0; j<planners.rows.size(); j++) {
				ProgressionPlanner planner = (ProgressionPlanner) planners.rows.get(j).label;
				Object key = new ImmutableArray<>(problem, planner);
				summary.addRow(key);
				summary.set(key, SUMMARY_PROBLEM, problem.name);
				summary.set(key, SUMMARY_PLANNER, planner.name);
				Iterable<Integer> length = results.values(Integer.class, new ResultsFilter(RESULTS_PLAN_LENGTH, problem, planner));
				summary.set(key, SUMMARY_SUCCESSES, Statistic.COUNT.calculate(length));
				summary.set(key, SUMMARY_MIN_PLAN_LENGTH, Statistic.MIN_INTEGER.calculate(length));
				summary.set(key, SUMMARY_MAX_PLAN_LENGTH, Statistic.MAX_INTEGER.calculate(length));
				summary.set(key, SUMMARY_AVG_PLAN_LENGTH, Statistic.AVERAGE.calculate(length));
				summary.set(key, SUMMARY_STD_PLAN_LENGTH, Statistic.STANDARD_DEVIATION.calculate(length));
				Iterable<Long> visited = results.values(Long.class, new ResultsFilter(RESULTS_VISITED, problem, planner));
				summary.set(key, SUMMARY_MIN_VISITED, Statistic.MIN_INTEGER.calculate(visited));
				summary.set(key, SUMMARY_MAX_VISITED, Statistic.MAX_INTEGER.calculate(visited));
				summary.set(key, SUMMARY_AVG_VISITED, Statistic.AVERAGE.calculate(visited));
				summary.set(key, SUMMARY_STD_VISITED, Statistic.STANDARD_DEVIATION.calculate(visited));
				Iterable<Long> generated = results.values(Long.class, new ResultsFilter(RESULTS_GENERATED, problem, planner));
				summary.set(key, SUMMARY_MIN_GENERATED, Statistic.MIN_INTEGER.calculate(generated));
				summary.set(key, SUMMARY_MAX_GENERATED, Statistic.MAX_INTEGER.calculate(generated));
				summary.set(key, SUMMARY_AVG_GENERATED, Statistic.AVERAGE.calculate(generated));
				summary.set(key, SUMMARY_STD_GENERATED, Statistic.STANDARD_DEVIATION.calculate(generated));
				Iterable<Long> time = results.values(Long.class, new ResultsFilter(RESULTS_TIME, problem, planner));
				summary.set(key, SUMMARY_MIN_TIME, Statistic.MIN_INTEGER.calculate(time));
				summary.set(key, SUMMARY_MAX_TIME, Statistic.MAX_INTEGER.calculate(time));
				summary.set(key, SUMMARY_AVG_TIME, Statistic.AVERAGE.calculate(time));
				summary.set(key, SUMMARY_STD_TIME, Statistic.STANDARD_DEVIATION.calculate(time));
			}
		}
		planners.addColumn(PLANNERS_SOLVED, Long.class);
		planners.addColumn(PLANNERS_VISITED, Long.class);
		planners.addColumn(PLANNERS_GENERATED, Long.class);
		planners.addColumn(PLANNERS_TIME, Long.class);
		for(Table.Row row : planners.rows) {
			ProgressionPlanner planner = (ProgressionPlanner) row.label;
			planners.set(planner, PLANNERS_SOLVED, Statistic.SUM_INTEGER.calculate(summary.values(Long.class, cell -> {
				return
					cell.row.get(SUMMARY_PLANNER).get().equals(planner.name) &&
					cell.column.label.equals(SUMMARY_SUCCESSES);
			})));
			planners.set(planner, PLANNERS_VISITED, Statistic.SUM_INTEGER.calculate(results.values(Long.class, new ResultsFilter(RESULTS_VISITED, planner))));
			planners.set(planner, PLANNERS_GENERATED, Statistic.SUM_INTEGER.calculate(results.values(Long.class, new ResultsFilter(RESULTS_GENERATED, planner))));
			planners.set(planner, PLANNERS_TIME, Statistic.SUM_INTEGER.calculate(results.values(Long.class, new ResultsFilter(RESULTS_TIME, planner))));
		}
		planners.sort(PLANNERS_TIME, Long.class, Statistic.INTEGER_ASCENDING);
		planners.sort(PLANNERS_GENERATED, Long.class, Statistic.INTEGER_ASCENDING);
		planners.sort(PLANNERS_VISITED, Long.class, Statistic.INTEGER_ASCENDING);
		planners.sort(PLANNERS_SOLVED, Long.class, Statistic.INTEGER_DESCENDING);
		problems.addColumn(PROBLEMS_SOLVED, Long.class);
		problems.addColumn(PROBLEMS_MIN_VISITED, Long.class);
		problems.addColumn(PROBLEMS_MAX_VISITED, Long.class);
		problems.addColumn(PROBLEMS_AVG_VISITED, Double.class);
		problems.addColumn(PROBLEMS_STD_VISITED, Double.class);
		problems.addColumn(PROBLEMS_MIN_GENERATED, Long.class);
		problems.addColumn(PROBLEMS_MAX_GENERATED, Long.class);
		problems.addColumn(PROBLEMS_AVG_GENERATED, Double.class);
		problems.addColumn(PROBLEMS_STD_GENERATED, Double.class);
		problems.addColumn(PROBLEMS_MIN_TIME, Long.class);
		problems.addColumn(PROBLEMS_MAX_TIME, Long.class);
		problems.addColumn(PROBLEMS_AVG_TIME, Double.class);
		problems.addColumn(PROBLEMS_STD_TIME, Double.class);	
		for(Table.Row row : problems.rows) {
			Problem problem = (Problem) row.label;
			problems.set(problem, PROBLEMS_SOLVED, Statistic.SUM_INTEGER.calculate(summary.values(Long.class, cell -> {
				return
					cell.row.get(SUMMARY_PROBLEM).get().equals(problem.name) &&
					cell.column.label.equals(SUMMARY_SUCCESSES);
			})));
			Iterable<Long> visited = results.values(Long.class, new ResultsFilter(RESULTS_VISITED, problem));
			problems.set(problem, PROBLEMS_MIN_VISITED, Statistic.MIN_INTEGER.calculate(visited));
			problems.set(problem, PROBLEMS_MAX_VISITED, Statistic.MAX_INTEGER.calculate(visited));
			problems.set(problem, PROBLEMS_AVG_VISITED, Statistic.AVERAGE.calculate(visited));
			problems.set(problem, PROBLEMS_STD_VISITED, Statistic.STANDARD_DEVIATION.calculate(visited));
			Iterable<Long> generated = results.values(Long.class, new ResultsFilter(RESULTS_GENERATED, problem));
			problems.set(problem, PROBLEMS_MIN_GENERATED, Statistic.MIN_INTEGER.calculate(generated));
			problems.set(problem, PROBLEMS_MAX_GENERATED, Statistic.MAX_INTEGER.calculate(generated));
			problems.set(problem, PROBLEMS_AVG_GENERATED, Statistic.AVERAGE.calculate(generated));
			problems.set(problem, PROBLEMS_STD_GENERATED, Statistic.STANDARD_DEVIATION.calculate(generated));
			Iterable<Long> time = results.values(Long.class, new ResultsFilter(RESULTS_TIME, problem));
			problems.set(problem, PROBLEMS_MIN_TIME, Statistic.MIN_INTEGER.calculate(time));
			problems.set(problem, PROBLEMS_MAX_TIME, Statistic.MAX_INTEGER.calculate(time));
			problems.set(problem, PROBLEMS_AVG_TIME, Statistic.AVERAGE.calculate(time));
			problems.set(problem, PROBLEMS_STD_TIME, Statistic.STANDARD_DEVIATION.calculate(time));
		}
		problems.sort(PROBLEMS_AVG_TIME, Double.class, Statistic.DECIMAL_ASCENDING);
		problems.sort(PROBLEMS_AVG_GENERATED, Double.class, Statistic.DECIMAL_ASCENDING);
		problems.sort(PROBLEMS_AVG_VISITED, Double.class, Statistic.DECIMAL_ASCENDING);
		problems.sort(PROBLEMS_SOLVED, Long.class, Statistic.INTEGER_DESCENDING);		
	}
}