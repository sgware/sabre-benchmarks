package edu.uky.cs.nil.sabre.bench;

import java.io.File;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import edu.uky.cs.nil.sabre.comp.ActionShuffler;
import edu.uky.cs.nil.sabre.comp.CompiledAction;
import edu.uky.cs.nil.sabre.comp.CompiledProblem;
import edu.uky.cs.nil.sabre.io.DefaultPrinter;
import edu.uky.cs.nil.sabre.io.Printer;
import edu.uky.cs.nil.sabre.prog.*;
import edu.uky.cs.nil.sabre.prog.ProgressionPlanner.Method;
import edu.uky.cs.nil.sabre.search.Planner;
import edu.uky.cs.nil.sabre.search.Result;
import edu.uky.cs.nil.sabre.util.Worker;
import edu.uky.cs.nil.sabre.util.Worker.Status;

/**
 * Defines the suite of {@link Benchmark benchmark problems} and a set of {@link
 * ProgressionPlanner planners}, runs each planner on each problem, and outputs
 * the results.
 * 
 * @author Stephen G. Ware
 */
public class Main {

	/**
	 * The maximum number of nodes a {@link ProgressionSearch search} may {@link
	 * ProgressionSearch#getVisited() visit} before it fails
	 */
	public static final long SEARCH_LIMIT = 1000000;
	
	/**
	 * The maximum number of nodes a {@link ProgressionSearch search} may
	 * {@link ProgressionSearch#getGenerated() generate} before it fails
	 */
	public static final long SPACE_LIMIT = Planner.UNLIMITED_NODES;
	
	/**
	 * The maximum number of milliseconds a {@link ProgressionSearch search} may
	 * take before it fails
	 */
	public static final long TIME_LIMIT = Planner.UNLIMITED_TIME;
	
	/** The number of times to run each planner on each problem */
	public static final int RUNS = 10;
	
	/**
	 * Whether or not the order of actions should be {@link ActionShuffler
	 * shuffled} between runs
	 */
	public static final boolean SHUFFLE = true;
	
	/**
	 * The seed used by the {@link Random random number generator} that is used
	 * to shuffle actions between runs
	 */
	public static final int RANDOM_SEED = 0;
	
	/**
	 * Returns a list of all the {@link Benchmark benchmark problems} to test.
	 * 
	 * @return a list of benchmark problems
	 */
	private static final List<Benchmark> getProblems() {
		ArrayList<Benchmark> list = new ArrayList<>();
		/*						Name				File				Goal	ATL		CTL		EL  */
		list.add(new Benchmark("bribery", 			"bribery",			1,		5,		5,		2	));
		list.add(new Benchmark("deerhunter_any",	"deerhunter",		1,		10,		6,		1	));
		list.add(new Benchmark("deerhunter_both",	"deerhunter",		2,		10,		6,		1	));
		list.add(new Benchmark("secretagent",		"secretagent",		1,		8,		8,		1	));
		list.add(new Benchmark("aladdin_any",		"aladdin",			1,		13,		10,		3	));
		list.add(new Benchmark("aladdin_both",		"aladdin",			2,		13,		10,		3	));
		list.add(new Benchmark("hospital_any",		"hospital",			1,		11,		5,		3	));
		list.add(new Benchmark("hospital_both",		"hospital",			2,		11,		5,		3	));
		list.add(new Benchmark("basketball_any",	"basketball",		1,		7,		5,		3	));
		list.add(new Benchmark("basketball_both",	"basketball",		2,		7,		5,		3	));
		list.add(new Benchmark("snakebite",			"snakebite",		1,		8,		5,		2	));
		list.add(new Benchmark("fantasy_any",		"fantasy",			1,		9,		3,		3	));
		list.add(new Benchmark("fantasy_two",		"fantasy",			2,		9,		3,		3	));
		list.add(new Benchmark("fantasy_all",		"fantasy",			3,		9,		3,		3	));
		list.add(new Benchmark("space_lose",		"space",			2,		5,		3,		2	));
		list.add(new Benchmark("space_win",			"space",			3,		5,		3,		2	));
		list.add(new Benchmark("raiders",			"raiders",			1,		7,		4,		2	));
		list.add(new Benchmark("treasure",			"treasure",			1,		4,		4,		3	));
		list.add(new Benchmark("gramma_lose",		"gramma",			1,		5,		4,		2	));
		list.add(new Benchmark("gramma_win",		"gramma",			2,		5,		4,		2	));
		list.add(new Benchmark("jailbreak_lose",	"jailbreak",		1,		7,		6,		1	));
		list.add(new Benchmark("jailbreak_escape",	"jailbreak",		3,		7,		6,		1	));
		list.add(new Benchmark("jailbreak_revenge",	"jailbreak",		6,		7,		6,		1	));
		list.add(new Benchmark("lovers",			"lovers",			1,		5,		5,		2	));
		return list;
	}
	
	/**
	 * Returns a list of {@link ProgressionPlanner progression planners} to test
	 * each problem on.
	 * 
	 * @return a list of planners
	 */
	private static final List<ProgressionPlanner> getPlanners() {
		ArrayList<ProgressionPlanner> list = new ArrayList<>();
		ProgressionCostFactory t = ProgressionCostFactory.TEMPORAL;
		ProgressionCostFactory reach = ReachabilityHeuristic.FACTORY;
		ProgressionCostFactory hplus = new RepeatedRootHeuristic.Factory(GraphHeuristic.SUM);
		ProgressionCostFactory rp = new RepeatedRootHeuristic.Factory(RelaxedPlanHeuristic.FACTORY);
		/*					Name		Search Method				Cost	Heuristic	*/
		list.add(getPlanner("BFS",		Method.BEST_FIRST,			t,		reach	));
		list.add(getPlanner("EFS",		Method.EXPLANATION_FIRST,	t,		reach	));
		list.add(getPlanner("GFS",		Method.GOAL_FIRST,			t,		reach	));
		list.add(getPlanner("A* h+",	Method.BEST_FIRST,			t,		hplus	));
		list.add(getPlanner("A* rp",	Method.BEST_FIRST,			t,		rp		));
		list.add(getPlanner("EFS h+",	Method.EXPLANATION_FIRST,	t,		hplus	));
		list.add(getPlanner("EFS rp",	Method.EXPLANATION_FIRST,	t,		rp		));
		list.add(getPlanner("GFS h+",	Method.GOAL_FIRST,			t,		hplus	));
		list.add(getPlanner("GFS rp",	Method.GOAL_FIRST,			t,		rp		));
		return list;
	}
	
	/**
	 * Configures a planner with a given list of settings.
	 * 
	 * @param name the planner's unique name to identify it in the results
	 * @param method the {@link Method search method} the planner will use
	 * @param cost the cost function the planner will use
	 * @param heuristic the heuristic function the planner will use
	 * @return a planner configured with these settings
	 */
	private static final ProgressionPlanner getPlanner(
		String name,
		Method method,
		ProgressionCostFactory cost,
		ProgressionCostFactory heuristic
	) {
		ProgressionPlanner planner = new ProgressionPlanner(name);
		planner.setMethod(method);
		planner.setCost(cost);
		planner.setHeuristic(heuristic);
		planner.setSearchLimit(SEARCH_LIMIT);
		planner.setSpaceLimit(SPACE_LIMIT);
		planner.setTimeLimit(TIME_LIMIT);
		return planner;
	}
	
	/** The random number generator used to shuffle actions */
	private static final Random RANDOM = new Random(RANDOM_SEED);
	
	/**
	 * Runs every planner on every benchmark problem and outputs the results.
	 * 
	 * @param args not used
	 * @throws Exception if an exception occurs while the tests are running
	 */
	public static void main(String[] args) throws Exception {
		Report report = Worker.get(status -> run(status), 1, TimeUnit.MINUTES);
		System.out.println("\n\n" + report);
		try(TextReportPrinter printer = new TextReportPrinter(new File("results.txt"))) {
			printer.print(report);
		}
		try(HTMLReportPrinter printer = new HTMLReportPrinter(new File("results.html"))) {
			printer.print(report);
		}
		try(CSVReportPrinter printer = new CSVReportPrinter(new File("results/"))) {
			printer.print(report);
		}
	}
	
	/**
	 * Runs every planner on every benchmark problem and returns a {@link Report
	 * report} of the results.
	 * 
	 * @param status a status object to update while tests are running
	 * @return a report of the results
	 * @throws Exception if an exception occurs while the tests are running
	 */
	private static Report run(Status status) throws Exception {
		System.out.println("Sabre Benchmark tests started on " + ZonedDateTime.now());
		Report report = new Report();
		Printer printer = new DefaultPrinter();
		// Read and compile problems.
		List<Benchmark> problems = getProblems();
		for(Benchmark problem : problems) {
			problem.load(status);
			report.addProblem(problem);
			System.out.println("\nProblem \"" + problem.name + "\" before compilation:\n\n" + printer.toString(problem.getProblem()));
			System.out.println("\nProblem \"" + problem.name + "\" after compilation:\n\n" + printer.toString(problem.getCompiledProblem()));
		}
		// Print the details of each planner.
		List<ProgressionPlanner> planners = getPlanners();
		for(ProgressionPlanner planner : planners) {
			report.addPlanner(planner);
			System.out.println("\nPlanner " + printer.toString(planner));
		}
		// Verify that each planner can solve each problem.
		for(Benchmark problem : problems) {
			for(ProgressionPlanner planner : planners) {
				if(problem.getSolution() == null)
					System.out.println("\nWarning: Problem \"" + problem.name + "\" does not have an assoicated solution to verify.");
				else {
					ProgressionCostFactory heuristic = planner.getHeuristic();
					planner.setHeuristic(new VerifyHeuristic.Factory(heuristic, problem.getSolution()));
					ProgressionSearch search = getSearch(problem, planner, false, status);
					Result<CompiledAction> result = search.get(status);
					if(result.getSuccess())
						System.out.println("\nPlanner \"" + planner.name + "\" verified this solution to problem \"" + problem.name + "\":\n" + result.solution);
					else
						System.out.println("\nWarning: Planner \"" + planner.name + "\" was not able to verify the solution to problem \"" + problem.name + "\": " + result.message);
					planner.setHeuristic(heuristic);
				}
			}
		}
		// Run each planner on each problem.
		report.setStart();
		for(Benchmark problem : problems) {
			for(ProgressionPlanner planner : planners) {
				for(int run=1; run<=RUNS; run++) {
					ProgressionSearch search = getSearch(problem, planner, run > 1 && SHUFFLE, status);
					System.out.println("\nRun " + run + " for planner \"" + planner.name + "\" on problem \"" + problem.name + "\":");
					System.gc();
					Result<CompiledAction> result = search.get(status);
					report.addResult(problem, planner, run, result);
					if(result.getSuccess())
						System.out.println(result.solution);
					else
						System.out.println(result.message);
					System.out.println("\n" + report.results);
				}
			}
		}
		report.setEnd();
		System.out.println("Sabre Benchmark tests ended on " + ZonedDateTime.now());
		return report;
	}
	
	/**
	 * Uses a planner to {@link
	 * ProgressionPlanner#getSearch(edu.uky.cs.nil.sabre.Problem, Status) create
	 * a search} for a given {@link Benchmark problem} configured according to
	 * that problem's settings.
	 * 
	 * @param problem the problem to solve
	 * @param planner the planner that will create the search
	 * @param shuffle whether or not the order of actions in the problem should
	 * be shuffled before the search is created
	 * @param status a status object to update while the search is created
	 * @return a search created by that planner for that problem
	 */
	private static final ProgressionSearch getSearch(Benchmark problem, ProgressionPlanner planner, boolean shuffle, Status status) {
		planner.setAuthorTemporalLimit(problem.atl);
		planner.setCharacterTemporalLimit(problem.ctl);
		planner.setEpistemicLimit(problem.el);
		CompiledProblem compiled = problem.getCompiledProblem();
		if(shuffle)
			compiled = ActionShuffler.compile(compiled, RANDOM, status);
		ProgressionSearch search = planner.getSearch(compiled, status);
		search.setGoal(edu.uky.cs.nil.sabre.Number.get(problem.goal));
		return search;
	}
}