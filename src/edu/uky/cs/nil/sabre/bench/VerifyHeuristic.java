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

public class VerifyHeuristic implements ProgressionCost {
	
	public static class Factory implements ProgressionCostFactory {
		
		private static final long serialVersionUID = 1L;
		public final ProgressionCostFactory parent;
		public final ImmutableSet<CompiledAction> actions;
		
		public Factory(ProgressionCostFactory parent, ImmutableSet<CompiledAction> actions) {
			this.parent = parent;
			this.actions = actions;
		}
		
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

	public final ProgressionCost parent;
	public final ImmutableSet<CompiledAction> actions;
	
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