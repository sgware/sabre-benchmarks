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

public class Report {

	public static final String PROBLEMS_NAME = "Name";
	public static final String PROBLEMS_CHARACTERS = "Characters";
	public static final String PROBLEMS_ENTITIES = "Entities";
	public static final String PROBLEMS_FLUENTS = "Fluents";
	public static final String PROBLEMS_ACTIONS = "Actions";
	public static final String PROBLEMS_TRIGGERS = "Triggers";
	public static final String PROBLEMS_GOAL = "Goal";
	public static final String PROBLEMS_ATL = "ATL";
	public static final String PROBLEMS_CTL = "CTL";
	public static final String PROBLEMS_EL = "EL";
	
	public static final String PLANNERS_NAME = "Name";
	public static final String PLANNERS_SEARCH = "Search Method";
	public static final String PLANNERS_COST = "Cost";
	public static final String PLANNERS_HEURISTIC = "Heuristic";
	
	public static final String RESULTS_PROBLEM = "Problem";
	public static final String RESULTS_PLANNER = "Planner";
	public static final String RESULTS_RUN = "Run";
	public static final String RESULTS_SUCCESS = "Success?";
	public static final String RESULTS_PLAN_LENGTH = "Plan Length";
	public static final String RESULTS_VISITED = "Nodes Visited";
	public static final String RESULTS_GENERATED = "Nodes Generated";
	public static final String RESULTS_TIME = "Time (ms)";
	
	public static final String SUMMARY_PROBLEM = "Problem";
	public static final String SUMMARY_PLANNER = "Planner";
	public static final String SUMMARY_AVG_VISITED = "Avg. Nodes Visited";
	public static final String SUMMARY_MIN_VISITED = "Min Nodes Visited";
	public static final String SUMMARY_MAX_VISITED = "Max Nodes Visited";
	public static final String SUMMARY_STD_VISITED = "Nodes Visited Std.";
	public static final String SUMMARY_AVG_GENERATED = "Avg. Nodes Generated";
	public static final String SUMMARY_MIN_GENERATED = "Min Nodes Generated";
	public static final String SUMMARY_MAX_GENERATED = "Max Nodes Generated";
	public static final String SUMMARY_STD_GENERATED = "Nodes Generated Std.";
	public static final String SUMMARY_AVG_TIME = "Avg. Time (ms)";
	public static final String SUMMARY_MIN_TIME = "Min Time (ms)";
	public static final String SUMMARY_MAX_TIME = "Max Time (ms)";
	public static final String SUMMARY_STD_TIME = "Time Std. (ms)";
	
	public static final String BEST_PROBLEM = "Problem";
	public static final String BEST_PLANNER_VISITED = "Best Planner (Visited)";
	public static final String BEST_AVG_VISITED = "Avg. Nodes Visited";
	public static final String BEST_PLANNER_GENERATED = "Best Planner (Generated)";
	public static final String BEST_AVG_GENERATED = "Avg. Nodes Generated";
	public static final String BEST_PLANNER_TIME = "Best Planner (Time)";
	public static final String BEST_AVG_TIME = "Avg. Time (ms)";
	
	public final Table problems = new Table();
	public final Table compiled = new Table();
	public final Table planners = new Table();
	public final Table results = new Table();
	public final Table summary = new Table();
	public final Table best = new Table();
	private long start = -1;
	private long end = -1;
	
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
		summary.addColumn(SUMMARY_AVG_VISITED, Double.class);
		summary.addColumn(SUMMARY_MIN_VISITED, Long.class);
		summary.addColumn(SUMMARY_MAX_VISITED, Long.class);
		summary.addColumn(SUMMARY_STD_VISITED, Double.class);
		summary.addColumn(SUMMARY_AVG_GENERATED, Double.class);
		summary.addColumn(SUMMARY_MIN_GENERATED, Long.class);
		summary.addColumn(SUMMARY_MAX_GENERATED, Long.class);
		summary.addColumn(SUMMARY_STD_GENERATED, Double.class);
		summary.addColumn(SUMMARY_AVG_TIME, Double.class);
		summary.addColumn(SUMMARY_MIN_TIME, Long.class);
		summary.addColumn(SUMMARY_MAX_TIME, Long.class);
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
	
	public long getStart() {
		return start;
	}
	
	public void setStart() {
		if(start == -1)
			start = System.currentTimeMillis();
		else
			throw new IllegalStateException("Start time already set.");
	}
	
	public long getEnd() {
		return end;
	}
	
	public void setEnd() {
		if(end == -1) {
			end = System.currentTimeMillis();
			fillSummary();
		}
		else
			throw new IllegalStateException("End time already set.");
	}
	
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
	
	public void addPlanner(ProgressionPlanner planner) {
		planners.addRow(planner);
		planners.set(planner, PLANNERS_NAME, planner.name);
		planners.set(planner, PLANNERS_SEARCH, planner.getMethod().toString());
		planners.set(planner, PLANNERS_COST, planner.getCost().toString());
		planners.set(planner, PLANNERS_HEURISTIC, planner.getHeuristic().toString());
	}
	
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
	
	private static final Function<Result<?>, Number> VISITED = r -> r.visited;
	private static final Function<Result<?>, Number> GENERATED = r -> r.generated;
	private static final Function<Result<?>, Number> TIME = r -> r.time;
	
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
				summary.set(key, SUMMARY_AVG_VISITED, avgVisited);
				summary.set(key, SUMMARY_MIN_VISITED, min(visited));
				summary.set(key, SUMMARY_MAX_VISITED, min(visited));
				summary.set(key, SUMMARY_MAX_VISITED, max(visited));
				summary.set(key, SUMMARY_STD_VISITED, std(visited));
				List<Number> generated = collect(problem.name, planner.name, GENERATED);
				Double avgGenerated = avg(generated);
				if(avgGenerated != null && (bestGeneratedPlanner == null || avgGenerated < bestGenerated)) {
					bestGeneratedPlanner = planner;
					bestGenerated = avgGenerated;
				}
				summary.set(key, SUMMARY_AVG_GENERATED, avgGenerated);
				summary.set(key, SUMMARY_MIN_GENERATED, min(generated));
				summary.set(key, SUMMARY_MAX_GENERATED, min(generated));
				summary.set(key, SUMMARY_MAX_GENERATED, max(generated));
				summary.set(key, SUMMARY_STD_GENERATED, std(generated));
				List<Number> time = collect(problem.name, planner.name, TIME);
				Double avgTime = avg(time);
				if(avgTime != null && (bestTimePlanner == null || avgTime < bestTime)) {
					bestTimePlanner = planner;
					bestTime = avgTime;
				}
				summary.set(key, SUMMARY_AVG_TIME, avgTime);
				summary.set(key, SUMMARY_MIN_TIME, min(time));
				summary.set(key, SUMMARY_MAX_TIME, min(time));
				summary.set(key, SUMMARY_MAX_TIME, max(time));
				summary.set(key, SUMMARY_STD_TIME, std(time));
			}
			best.addRow(problem);
			best.set(problem, BEST_PROBLEM, problem.name);
			best.set(problem, BEST_PLANNER_VISITED, bestVisitedPlanner.name);
			best.set(problem, BEST_AVG_VISITED, bestVisited);
			best.set(problem, BEST_PLANNER_GENERATED, bestGeneratedPlanner.name);
			best.set(problem, BEST_AVG_GENERATED, bestGenerated);
			best.set(problem, BEST_PLANNER_TIME, bestTimePlanner.name);
			best.set(problem, BEST_AVG_TIME, bestTime);
		}
	}
	
	private final List<Number> collect(String problem, String planner, Function<Result<?>, Number> statistic) {
		ArrayList<Number> values = new ArrayList<>();
		for(Table.Row row : results.rows)
			if(row.get(RESULTS_PROBLEM).get().equals(problem) && row.get(RESULTS_PLANNER).get().equals(planner) && ((Result<?>) row.label).getSuccess())
				values.add(statistic.apply((Result<?>) row.label));
		return values;
	}
	
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
	
	private static final Number min(List<Number> values) {
		Number min = null;
		for(Number value : values)
			if(min == null || compare(value, min) < 0)
				min = value;
		return min;
	}
	
	private static final Number max(List<Number> values) {
		Number max = null;
		for(Number value : values)
			if(max == null || compare(value, max) > 0)
				max = value;
		return max;
	}
	
	private static final int compare(Number n1, Number n2) {
		return new BigDecimal(n1.toString()).compareTo(new BigDecimal(n2.toString()));
	}
	
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