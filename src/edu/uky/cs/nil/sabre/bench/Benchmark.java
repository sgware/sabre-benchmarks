package edu.uky.cs.nil.sabre.bench;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import edu.uky.cs.nil.sabre.Problem;
import edu.uky.cs.nil.sabre.ProblemBuilder;
import edu.uky.cs.nil.sabre.Solution;
import edu.uky.cs.nil.sabre.comp.CompiledAction;
import edu.uky.cs.nil.sabre.comp.CompiledProblem;
import edu.uky.cs.nil.sabre.io.DefaultParser;
import edu.uky.cs.nil.sabre.io.ParseException;
import edu.uky.cs.nil.sabre.io.Parser;
import edu.uky.cs.nil.sabre.prog.ProgressionPlanner;
import edu.uky.cs.nil.sabre.util.Worker.Status;

public class Benchmark {

	public final String name;
	public final double goal;
	public final int atl;
	public final int ctl;
	public final int el;
	private final String file;
	private Problem problem = null;
	private CompiledProblem compiled = null;
	private Solution<CompiledAction> solution = null;
	
	public Benchmark(String name, String file, double goal, int atl, int ctl, int el) {
		this.name = name;
		this.file = file;
		this.goal = goal;
		this.atl = atl;
		this.ctl = ctl;
		this.el = el;
	}
	
	@SuppressWarnings("unchecked")
	public void load(Status status) throws IOException, ParseException {
		Parser parser = new DefaultParser();
		problem = parser.parse(new BufferedReader(new FileReader(new File("problems/" + file + ".txt"))), Problem.class);
		ProblemBuilder builder = new ProblemBuilder(problem);
		builder.setName(name);
		problem = new Problem(builder);
		ProgressionPlanner planner = new ProgressionPlanner();
		compiled = planner.compile(problem, status);
		parser.define(compiled);
		File url = new File("solutions/" + name + ".txt");
		if(url.exists())
			solution = parser.parse(url, Solution.class);
	}
	
	public Problem getProblem() {
		return problem;
	}
	
	public CompiledProblem getCompiledProblem() {
		return compiled;
	}
	
	public Solution<CompiledAction> getSolution() {
		return solution;
	}
}