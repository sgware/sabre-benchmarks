package edu.uky.cs.nil.sabre.bench;

import java.util.Collection;
import java.util.LinkedHashSet;

import edu.uky.cs.nil.sabre.Action;
import edu.uky.cs.nil.sabre.Character;
import edu.uky.cs.nil.sabre.Problem;
import edu.uky.cs.nil.sabre.Settings;
import edu.uky.cs.nil.sabre.Solution;
import edu.uky.cs.nil.sabre.SolutionGoal;
import edu.uky.cs.nil.sabre.SolutionPlan;
import edu.uky.cs.nil.sabre.Utilities;
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
 * infinite cost} to all {@link Action actions} not in a given solution.
 * <p>
 * This heuristic acts as a wrapper around {@link #parent some other heuristic}.
 * When this heuristic evaluates a {@link ProgressionNode progression node}, if
 * the {@link ProgressionNode#getAction() most recent action} is not one of the
 * actions in the solution, or if it occurs at the wrong {@link
 * ProgressionNode#getTemporalDepth() temporal} or {@link
 * ProgressionNode#getEpistemicDepth() epistemic depth}, this heuristic returns
 * a cost of {@link Double#POSITIVE_INFINITY positive infinity}; otherwise, this
 * heuristic defers to the {@link #parent one it is wrapped around}.
 * <p>
 * If a planner using this heuristic concludes it search without finding a
 * solution, it is guaranteed that the planner cannot find the solution this
 * heuristic is trying to reproduce. However, a planner which can find the
 * solution may run for an unreasonable amount of time or run out of resources
 * before finding the solution if the problem is exceptionally hard.
 * 
 * @author Stephen G. Ware
 */
public class VerificationHeuristic implements ProgressionCost {
	
	/**
	 * A {@link ProgressionCostFactory factory} used to create {@link
	 * VerificationHeuristic verification heuristics}.
	 */
	public static class Factory implements ProgressionCostFactory {
		
		/** Serial version ID */
		private static final long serialVersionUID = Settings.VERSION_UID;
		
		/**
		 * The heuristic factory used to build the heuristic to defer to when
		 * evaluating an action in the solution being reproduced.
		 */
		public final ProgressionCostFactory parent;
		
		/** The set of actions and their context in the solution */
		private final ImmutableSet<SolutionAction> actions;
		
		/**
		 * Constructs a factory for creating verification heuristics that
		 * reproduce a given solution.
		 * 
		 * @param parent the factory for creating the heuristic to defer to when
		 * evaluating an action in the solution being reproduced
		 * @param solution the solution to reproduce
		 */
		public Factory(ProgressionCostFactory parent, Solution<?> solution) {
			this.parent = parent;
			this.actions = toActions(solution);
		}
		
		@Override
		public String toString() {
			return VerificationHeuristic.toString(parent);
		}

		@Override
		public VerificationHeuristic getCost(CompiledProblem problem, Status status) {
			LinkedHashSet<SolutionAction> actions = new LinkedHashSet<>();
			for(SolutionAction action : this.actions)
				actions.add(action.translate(problem));
			return new VerificationHeuristic(parent.getCost(problem, status), new ImmutableSet<>(actions));
		}
	}

	/**
	 * An {@link Action action} annotated with the character, temporal depth,
	 * and epistemic depth at which it occurred in a solution.
	 */
	private static final class SolutionAction {
		
		/** An action from the solution */
		public final Action action;
		
		/** The character whose goal was being planned for */
		public final Character character;
		
		/** The temporal depth of the action */
		public final int temporal;
		
		/** The epistemic depth of the action */
		public final int epistemic;
		
		/**
		 * Constructs a new solution action with its context.
		 * 
		 * @param action the action
		 * @param character the character whose goal is being worked on
		 * @param temporal the temporal depth of the action
		 * @param epistemic the epistemic depth of the action
		 */
		public SolutionAction(Action action, Character character, int temporal, int epistemic) {
			this.action = action;
			this.character = character;
			this.temporal = temporal;
			this.epistemic = epistemic;
		}
		
		@Override
		public boolean equals(Object other) {
			if(other instanceof SolutionAction) {
				SolutionAction otherAction = (SolutionAction) other;
				return action.equals(otherAction.action) &&
					Utilities.equals(character, otherAction.character) &&
					temporal == otherAction.temporal &&
					epistemic == otherAction.epistemic;
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return Utilities.hashCode(getClass(), action, character, temporal, epistemic);
		}
		
		@Override
		public String toString() {
			return "action=" + action + " character=" + character + " temporal=" + temporal + " epistemic=" + epistemic;
		}
		
		/**
		 * Checks whether a given progression node has the same action and same
		 * character at the same temporal and epistemic depth as this solution
		 * action.
		 * 
		 * @param node the progression node to compare against
		 * @return true if the node has the same action and character at the
		 * same temporal and epistemic depth
		 */
		public boolean matches(ProgressionNode<?> node) {
			return Utilities.equals(node.getAction(), action) &&
				Utilities.equals(node.getCharacter(), character) &&
				temporal == node.getTemporalDepth() &&
				epistemic == node.getEpistemicDepth();
		}
		
		/**
		 * Returns a new solution action using action and character objects from
		 * a provided problem.
		 * 
		 * @param problem the problem from which to get the action and character
		 * @return a new solution action using that problem's action and
		 * character
		 */
		public SolutionAction translate(Problem problem) {
			return new SolutionAction(
				problem.getAction(this.action.signature),
				this.character == null ? null : problem.universe.getCharacter(this.character.name),
				temporal,
				epistemic
			);
		}
	}
	
	private static final ImmutableSet<SolutionAction> toActions(Solution<?> solution) {
		LinkedHashSet<SolutionAction> set = new LinkedHashSet<>();
		toActions(solution, 1, 0, set);
		return new ImmutableSet<>(set);
	}
	
	private static final void toActions(Solution<?> solution, int temporal, int epistemic, Collection<SolutionAction> actions) {
		if(solution == null || solution instanceof SolutionGoal)
			return;
		SolutionPlan<?> plan = (SolutionPlan<?>) solution;
		actions.add(new SolutionAction(plan.first, plan.getCharacter(), temporal, epistemic));
		for(Solution<?> explanation : plan.explanations)
			toActions(explanation, 0, epistemic + 1, actions);
		toActions(plan.next(), temporal + 1, epistemic, actions);
	}
	
	private static final String toString(Object parent) {
		return "verification: " + parent;
	}
	
	/**
	 * The heuristic to defer to when evaluating a node whose action that can
	 * have a finite cost
	 */
	public final ProgressionCost parent;
	
	/**
	 * The actions from the solution, annotated with their character, temporal,
	 * and epistemic depths
	 */
	private final ImmutableSet<SolutionAction> actions;
	
	/**
	 * Constructs a new verification heuristic that reproduces a given solution.
	 * 
	 * @param parent the heuristic to defer to when evaluating a node whose
	 * action can have a finite cost
	 * @param actions the set of actions from the solution, annotated with their
	 * character, temporal, and epistemic depths
	 */
	public VerificationHeuristic(ProgressionCost parent, ImmutableSet<SolutionAction> actions) {
		this.parent = parent;
		this.actions = actions;
	}
	
	@Override
	public String toString() {
		return toString(parent);
	}
	
	@Override
	public <N> double evaluate(ProgressionNode<N> node) {
		if(node.getTemporalDepth() == 0)
			return parent.evaluate(node);
		for(SolutionAction action : actions)
			if(action.matches(node))
				return parent.evaluate(node);
		return Double.POSITIVE_INFINITY;
	}
}