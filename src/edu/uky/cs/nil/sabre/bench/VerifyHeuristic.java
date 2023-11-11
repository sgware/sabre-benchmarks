package edu.uky.cs.nil.sabre.bench;

import java.util.HashSet;
import java.util.Set;

import edu.uky.cs.nil.sabre.Character;
import edu.uky.cs.nil.sabre.Solution;
import edu.uky.cs.nil.sabre.Utilities;
import edu.uky.cs.nil.sabre.comp.CompiledAction;
import edu.uky.cs.nil.sabre.comp.CompiledProblem;
import edu.uky.cs.nil.sabre.prog.ProgressionCost;
import edu.uky.cs.nil.sabre.prog.ProgressionCostFactory;
import edu.uky.cs.nil.sabre.prog.ProgressionNode;
import edu.uky.cs.nil.sabre.util.ImmutableSet;
import edu.uky.cs.nil.sabre.util.Worker.Status;

/**
 * A {@link ProgressionCost heuristic function} used to help a {@link
 * edu.uky.cs.nil.sabre.prog.ProgressionPlanner heuristic progression planner}
 * reproduce a given solution by assigning an {@link Double#POSITIVE_INFINITY
 * infinite cost} to all {@link CompiledAction actions} not in a given solution.
 * <p>
 * This heuristic acts as a wrapper around {@link #parent some other heuristic}.
 * When this heuristic evaluates a {@link ProgressionNode progression node}, if
 * the {@link ProgressionNode#getAction() most recent action} is not one of the
 * actions in the solution this heuristic is trying to reproduce, this heuristic
 * returns a cost of {@link Double#POSITIVE_INFINITY positive infinity};
 * otherwise, this heuristic defer to the heuristic is is wrapped around.
 * <p>
 * If a planner using this heuristic concludes it search without finding a
 * solution, it is guaranteed that the planner cannot find the solution this
 * heuristic is trying to reproduce. However, the planner which can find the
 * solution may run for an unreasonable amount of time or run out of resources
 * before finding the solution if the problem is exceptionally hard or if the
 * solution being reproduced contains most of the actions in the problem.
 * <p>
 * This heuristic may not always cause a planner to reproduce the exact solution
 * it was created for. If another solution exists which uses the same actions
 * (or a subset of actions) in the solution, the planner may find that solution
 * instead. Thus, this heuristic is not designed to reproduce a solution exactly
 * but rather to verify that a planner is capable of solving a problem given a
 * known solution.
 * 
 * @author Stephen G. Ware
 */
public class VerifyHeuristic implements ProgressionCost {
	
	/**
	 * A {@link ProgressionCostFactory factory} used to create {@link
	 * VerifyHeuristic verification heuristics}.
	 */
	public static class Factory implements ProgressionCostFactory {
		
		/** Serial version ID */
		private static final long serialVersionUID = 1L;
		
		/**
		 * The heuristic factory used to build the heuristic to defer to when
		 * evaluating an action in the solution being reproduced.
		 */
		public final ProgressionCostFactory parent;
		
		/** The set of actions that can have a finite cost */
		public final ImmutableSet<CompiledAction> actions;
		
		/**
		 * Constructs a factory for creating verification heuristics based on
		 * a given set of actions.
		 * 
		 * @param parent the factory for creating the heuristic to defer to when
		 * evaluating an action in the solution being reproduced
		 * @param actions the set of actions which can have a finite cost
		 */
		public Factory(ProgressionCostFactory parent, ImmutableSet<CompiledAction> actions) {
			this.parent = parent;
			this.actions = actions;
		}
		
		/**
		 * Constructs a factory for creating verification heuristics based on
		 * a known {@link Solution solution} to a problem.
		 * 
		 * @param parent the factory for creating the heuristic to defer to when
		 * evaluating an action in the solution being reproduced
		 * @param solution the solution which defines the set of actions which
		 * can have a finite cost
		 */
		public Factory(ProgressionCostFactory parent, Solution<CompiledAction> solution) {
			this(parent, collect(solution));
		}
		
		@Override
		public String toString() {
			return VerifyHeuristic.toString(parent);
		}
		
		@Override
		public VerifyHeuristic getCost(CompiledProblem problem, Status status) {
			return new VerifyHeuristic(parent.getCost(problem, status), actions);
		}
	}
	
	private static final ImmutableSet<CompiledAction> collect(Solution<CompiledAction> solution) {
		HashSet<CompiledAction> actions = new HashSet<>();
		collect(solution, actions);
		return new ImmutableSet<>(actions);
	}
	
	private static final void collect(Solution<CompiledAction> solution, Set<CompiledAction> actions) {
		if(solution == null)
			return;
		while(solution.size() > 0) {
			CompiledAction action = solution.get(0);
			actions.add(action);
			for(Character consenting : action.consenting)
				if(!Utilities.equals(consenting, solution.getCharacter()))
					collect(solution.getExplanation(consenting), actions);
			solution = solution.next();
		}
	}
	
	private static final String toString(Object parent) {
		return "debug: " + parent;
	}

	/**
	 * The heuristic to defer to when evaluating a node whose action that can
	 * have a finite cost
	 */
	public final ProgressionCost parent;
	
	/**
	 * The set of actions which appear in the solution this heuristic is
	 * attempting to reproduce; nodes where this action is the most recent
	 * action can have a finite cost
	 */
	public final ImmutableSet<CompiledAction> actions;
	
	/**
	 * Constructs a new verification heuristic.
	 * 
	 * @param parent the heuristic to defer to when evaluating a node whose
	 * action can have a finite cost
	 * @param actions the set of actions in the solution this heuristic is
	 * attempting to reproduce which can have a finite cost
	 */
	public VerifyHeuristic(ProgressionCost parent, ImmutableSet<CompiledAction> actions) {
		this.parent = parent;
		this.actions = actions;
	}
	
	@Override
	public String toString() {
		return toString(parent);
	}
	
	@Override
	public <N> double evaluate(ProgressionNode<N> node) {
		if(node.getAction() == null || actions.contains(node.getAction()))
			return parent.evaluate(node);
		else
			return Double.POSITIVE_INFINITY;
	}
}