package fr.lirmm.graphik;

import java.util.LinkedList;

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseHaltingCondition;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.forward_chaining.halting_condition.RestrictedChaseStopCondition;
import fr.lirmm.graphik.graal.forward_chaining.rule_applier.AbstractRuleApplier;

public class DefaultRuleApplierWithNegation<T extends AtomSet> extends AbstractRuleApplier<T> {

	
	public DefaultRuleApplierWithNegation(){
		this(HomomorphismWithNegation.instance());
	}
	
	
	public DefaultRuleApplierWithNegation(Homomorphism<? super ConjunctiveQuery, ? super T> homomorphismSolver) {
		this(homomorphismSolver, new RestrictedChaseStopCondition());
	}
	
	
	public DefaultRuleApplierWithNegation(ChaseHaltingCondition haltingCondition) {
		this(HomomorphismWithNegation.instance(), haltingCondition);
	}
	
	public DefaultRuleApplierWithNegation(Homomorphism<? super ConjunctiveQuery, ? super T> homomorphismSolver,
		    ChaseHaltingCondition haltingCondition) {
			super(homomorphismSolver, haltingCondition);
		}
	
	
	@Override
	protected ConjunctiveQuery generateQuery(Rule rule) {
		
		LinkedList<Term> ans = new LinkedList<Term>();
		ans.addAll(rule.getFrontier());
		
		return new DefaultConjunctiveQueryWithNegation(rule.getBody() ,
				((DefaultRuleWithNegation)rule).getNegativeBody() ,
				ans);
	}

}
