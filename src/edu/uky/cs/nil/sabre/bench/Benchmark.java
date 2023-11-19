package edu.uky.cs.nil.sabre.bench;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import edu.uky.cs.nil.sabre.Number;
import edu.uky.cs.nil.sabre.Problem;
import edu.uky.cs.nil.sabre.ProblemBuilder;
import edu.uky.cs.nil.sabre.Solution;
import edu.uky.cs.nil.sabre.comp.ActionShuffler;
import edu.uky.cs.nil.sabre.comp.CompiledAction;
import edu.uky.cs.nil.sabre.comp.CompiledProblem;
import edu.uky.cs.nil.sabre.io.DefaultParser;
import edu.uky.cs.nil.sabre.io.ParseException;
import edu.uky.cs.nil.sabre.io.Parser;
import edu.uky.cs.nil.sabre.prog.ProgressionPlanner;
import edu.uky.cs.nil.sabre.prog.ProgressionSearch;
import edu.uky.cs.nil.sabre.util.Worker.Status;

/**
 * Defines a planning problem and reasonable limits on the search to solve it.
 * Because a problem defines an {@link Problem#utility author utility}, it may
 * be possible to defines more than one benchmark task for a single problem
 * by setting different goal utilities and limits on the search.
 * 
 * @author Stephen G. Ware
 */
public class Benchmark {

	/** The unique name of the problem for the final {@link Report report} */
	public final String name;
	
	/**
	 * The {@link
	 * edu.uky.cs.nil.sabre.prog.ProgressionSearch#setGoal(edu.uky.cs.nil.sabre.Number)
	 * goal utility} a solution must achieve to solve this problem
	 */
	public final double goal;
	
	/**
	 * The {@link edu.uky.cs.nil.sabre.search.Search#authorTemporalLimit
	 * author temporal limit} to use when solving this problem
	 */
	public final int atl;
	
	/**
	 * The {@link edu.uky.cs.nil.sabre.search.Search#characterTemporalLimit
	 * character temporal limit} to use when solving this problem
	 */
	public final int ctl;
	
	/**
	 * The {@link edu.uky.cs.nil.sabre.search.Search#epistemicLimit epistemc
	 * limit} to use when solving this problem
	 */
	public final int el;
	
	/**
	 * The name of the file in the 'problems' directory that defines this
	 * problem
	 */
	private final String file;
	
	/** The problem before it is compiled */
	private Problem problem = null;
	
	/**
	 * Constructs a new benchmark task.
	 * 
	 * @param name a unique name for the task that will be used in the {@link
	 * Report report}
	 * @param file the name of the file that defines the task in the {@code
	 * problems} directory
	 * @param goal the goal utility that must be achieved
	 * @param atl the author temporal limit to use when searching
	 * @param ctl the character temporal limit to use when searching
	 * @param el the epistemic limit to use when searching
	 */
	public Benchmark(String name, String file, double goal, int atl, int ctl, int el) {
		this.name = name;
		this.file = file;
		this.goal = goal;
		this.atl = atl;
		this.ctl = ctl;
		this.el = el;
	}
	
	/**
	 * Parses the problem file.
	 * 
	 * @param status a status to update while parsing
	 * @throws IOException if an error occurs when reading the problem
	 * @throws ParseException if the problem cannot be parsed
	 */
	public void load(Status status) throws IOException, ParseException {
		Parser parser = new DefaultParser();
		problem = parser.parse(new BufferedReader(new FileReader(new File("problems/" + file + ".txt"))), Problem.class);
		ProblemBuilder builder = new ProblemBuilder(problem);
		builder.setName(name);
		problem = new Problem(builder);
	}
	
	/**
	 * Returns the {@link Problem problem} (before compilation) to be solved in
	 * this benchmark task. This method returns null unless {@link
	 * #load(Status)} has been called.
	 * 
	 * @return the problem before compilation
	 */
	public Problem getProblem() {
		return problem;
	}
	
	/**
	 * Returns a known example {@link Solution solution} to the task if one is
	 * defined. This method returns null unless {@link #load(Status)} has been
	 * called and an example solution is provided. In order to parse a solution,
	 * all the {@link CompiledAction compiled actions} in a ground {@link
	 * CompiledProblem compiled problem} must first be defined. This method
	 * will compile the problem using a provided planner and use the resulting
	 * compiled problem to parse the solution.
	 * 
	 * @param planner the planner to use to compile the problem
	 * @param status a status to update while the problem is compiled
	 * @return an example solution, or null if one is not provided
	 * @throws IOException if an exception occurred while reading the solution
	 * file
	 * @throws ParseException if an exception occurred while parsing the
	 * solution file
	 */
	@SuppressWarnings("unchecked")
	public Solution<CompiledAction> getSolution(ProgressionPlanner planner, Status status) throws IOException, ParseException {
		File solutionFile = new File("solutions/" + file + ".txt");
		if(solutionFile.exists()) {
			CompiledProblem compiled = planner.compile(getProblem(), status);
			Parser parser = new DefaultParser();
			parser.define(compiled);
			return parser.parse(solutionFile, Solution.class);
		}
		return null;
	}
	
	/**
	 * Creates a {@link ProgressionSearch progression search} for this benchmark
	 * problem using the given planner with this benchmark's settings. If the
	 * run is higher than 1 and {@link Main#SHUFFLE} is true, the problem will
	 * be recompiled to shuffle the order of its actions.
	 * 
	 * @param planner the planner that will create the search
	 * @param run the number of times this planner has attempted this problem
	 * (starting at 1)
	 * @param status a status object to update while the search is created
	 * @return the search
	 */
	public ProgressionSearch getSearch(ProgressionPlanner planner, int run, Status status) {
		CompiledProblem compiled = planner.compile(problem, status);
		if(run > 1 && Main.SHUFFLE)
			compiled = ActionShuffler.compile(compiled, new Random(run), status);
		ProgressionSearch search = null;
		synchronized(planner) {
			planner.setAuthorTemporalLimit(atl);
			planner.setCharacterTemporalLimit(ctl);
			planner.setEpistemicLimit(el);
			search = planner.getSearch(compiled, status);
		}
		search.setGoal(Number.get(goal));
		return search;
	}
}