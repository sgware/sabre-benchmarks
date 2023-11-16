package edu.uky.cs.nil.sabre.bench;

import java.util.List;

import edu.uky.cs.nil.sabre.comp.CompiledAction;
import edu.uky.cs.nil.sabre.prog.ProgressionPlanner;
import edu.uky.cs.nil.sabre.prog.ProgressionSearch;
import edu.uky.cs.nil.sabre.search.Result;
import edu.uky.cs.nil.sabre.util.Worker.Status;

/**
 * Each {@link Test test} in a test suite represents one run of one {@link
 * ProgressionPlanner planner} on one {@link Benchmark benchmark problem}; the
 * suite coordinates {@link Main#THREADS some number of threads} to run tests in
 * parallel.
 * 
 * @author Stephen G. Ware
 */
public class TestSuite {
	
	/**
	 * The status of a test in the suite.
	 */
	private enum TestStatus {
		 
		/** Test not yet started */
		PENDING,
		
		/** Test started but not completed */
		RUNNING,
		
		/** Test completed but not yet logged in the report */
		COMPLETE,
		
		/** Test completed and logged in the report */
		LOGGED
	}

	/**
	 * A test represents one run of one {@link ProgressionPlanner planner} on
	 * one {@link Benchmark benchmark problem}.
	 */
	public class Test {
		
		/** The benchmark problem to solve */
		public final Benchmark problem;
		
		/** The planner that will attempt to solve the problem */
		public final ProgressionPlanner planner;
		
		/** The number of times this planner has been run on this problem */
		public final int run;
		
		/** The test's current status */
		private TestStatus status = TestStatus.PENDING;
		
		/** The results of the search */
		private Result<CompiledAction> result = null;
		
		/**
		 * Constructs a new test.
		 * 
		 * @param problem the benchmark problem to solve
		 * @param planner the planner that will attempt to solve it
		 * @param run the number of times this planner has attempted this
		 * problem
		 */
		private Test(Benchmark problem, ProgressionPlanner planner, int run) {
			this.problem = problem;
			this.planner = planner;
			this.run = run;
		}
		
		@Override
		public String toString() {
			return "Run " + run + " of planner \"" + planner.name + "\" on problem \"" + problem.name + "\"";
		}
		
		/**
		 * Creates the search this test will run.
		 * 
		 * @param status a status object to update while creating the search
		 * @return the search
		 */
		private ProgressionSearch getSearch(Status status) {
			return problem.getSearch(planner, run, status);
		}
		
		/**
		 * Returns the results of this test, or throws an exception if the test
		 * is not yet complete.
		 * 
		 * @return the result of the search
		 * @throws IllegalStateException if the test is not yet complete
		 */
		public Result<CompiledAction> getResult() {
			if(result == null)
				throw new IllegalStateException("Test not complete: " + this);
			return result;
		}
	}
	
	/**
	 * A {@link Thread thread} for running tests in parallel that is coordinated
	 * by a test suite.
	 */
	public class Runner extends Thread {
		
		/** Status object for this thread */
		private final Status status = new Status();
		
		/** The exception which stopped this thread */
		private Exception exception = null;
		
		@Override
		public void run() {
			try {
				exception = new RuntimeException("One of the threads running tests did not finish correctly, perhaps due to an out of memory error.");
				Test test = getNext();
				while(test != null) {
					complete(test, test.getSearch(status).get(status));
					test = getNext();
				}
				exception = null;
			}
			catch(Exception e) {
				exception = e;
			}
		}
		
		/**
		 * Waits for this runner to finish. If it is interrupted or if an
		 * exception causes the thread to crash, those exceptions will be thrown
		 * by this method.
		 * 
		 * @throws InterruptedException if the runner is interrupted
		 * @throws Exception if an exception occurred while the runner was
		 * running tests
		 */
		public void waitToFinish() throws Exception {
			try {
				join();
			}
			catch(InterruptedException e) {
				throw e;
			}
			if(exception != null)
				throw exception;
		}
	}
	
	/** All tests in this suite */
	private final Test[] tests;
	
	/** The report to update once each test is complete */
	private final Report report;
	
	/** The number of completed tests */
	private int complete = 0;
	
	/** A status object to update when tests complete */
	private Status status = null;
	
	/**
	 * Constructs a new test suite which will run each planner on each benchmark
	 * problem {@link Main#RUNS some number of times} in parallel.
	 * 
	 * @param problems the benchmark problems to test
	 * @param planners the planner to test on each problem
	 * @param report the report to update as tests complete
	 */
	public TestSuite(List<Benchmark> problems, List<ProgressionPlanner> planners, Report report) {
		tests = new Test[problems.size() * planners.size() * Main.RUNS];
		int index = 0;
		for(Benchmark problem : problems)
			for(ProgressionPlanner planner : planners)
				for(int run=1; run<=Main.RUNS; run++)
					tests[index++] = new Test(problem, planner, run);
		this.report = report;
	}
	
	/**
	 * Runs all tests in parallel, printing updates as tests start and finish.
	 * 
	 * @param status a status object to update as tests complete
	 * @throws InterruptedException if one of the threads running tests is
	 * interrupted
	 */
	public void run(Status status) throws Exception {
		status.setMessage("Running tests: %d of " + tests.length + " complete", 0);
		this.status = status;
		Runner[] runners = new Runner[Main.THREADS];
		for(int i=0; i<runners.length; i++) {
			runners[i] = new Runner();
			runners[i].start();
		}
		for(Runner runner : runners)
			runner.waitToFinish();
		status.setMessage("Test complete.");
	}
	
	private synchronized Test getNext() {
		boolean ordered = true;
		boolean print = false;
		Test result = null;
		for(Test test : tests) {
			if(test.status == TestStatus.PENDING) {
				result = test;
				break;
			}
			else if(test.status == TestStatus.COMPLETE && ordered) {
				report.addResult(test);
				test.status = TestStatus.LOGGED;
				print = true;
			}
			else if(test.status != TestStatus.LOGGED)
				ordered = false;
		}
		if(print)
			System.out.println("\n" + report.results);
		if(result != null) {
			result.status = TestStatus.RUNNING;
			System.out.println("\nStarted: " + result);
		}
		return result;
	}
	
	private synchronized void complete(Test test, Result<CompiledAction> result) {
		test.result = result;
		test.status = TestStatus.COMPLETE;
		complete++;
		if(this.status != null)
			this.status.update(0, complete);
		String message = "Complete: " + test + ": " + result.message;
		if(result.getSuccess())
			message += "\n" + result.solution;
		System.out.println("\n" + message);
	}
}