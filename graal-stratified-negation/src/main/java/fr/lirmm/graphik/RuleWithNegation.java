package fr.lirmm.graphik;

import java.util.Set;

import fr.lirmm.graphik.util.string.AppendableToStringBuilder;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Constant;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.api.core.Literal;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Term;



public interface RuleWithNegation extends Comparable<Rule>, AppendableToStringBuilder, Rule {

	/**
	 * Get the label (the name) of this rule.
	 * 
	 * @return the label of this rule.
	 */
	String getLabel();

	
	/**
	 * Set the label (the name) of this rule.
	 * 
	 * @param label
	 */
	void setLabel(String label);

	
	/**
	 * Get the body (the hypothesis) of this rule.
	 * 
	 * @return the body of this rule.
	 */
	InMemoryAtomSet getBody();
	
	
	/**
	 * Get the negative body (the hypothesis) of this rule.
	 * 
	 * @return the body of this rule.
	 */
	InMemoryAtomSet getNegativeBody();

	
	/**
	 * Get the head (the conclusion) of this rule.
	 * 
	 * @return the head of this rule.
	 */
	InMemoryAtomSet getHead();

	
	/**
	 * Compute and return the set of frontier variables of this rule.
	 * 
	 * @return a Set containing the frontier variables of this rule.
	 */
	Set<Variable> getFrontier();

	
	/**
	 * Compute and return the set of existential variables of this rule.
	 * 
	 * @return a Set containing the existential variables of this rule.
	 */
	Set<Variable> getExistentials();

	
	/**
	 * Get terms by Type.
	 * 
	 * @return a Set of all Term of the specified type related to this Rule.  
	 */
	@Deprecated
	Set<Term> getTerms(Term.Type type);

	
	/**
	 * Get all variables of this rule.
	 * 
	 * @return a Set of all variables related to this Rule.
	 */
	Set<Variable> getVariables();
	
	
	/**
	 * Get all constants of this rule.
	 * 
	 * @return a Set of all constants related to this Rule.
	 */
	Set<Constant> getConstants();
	
	
	/**
	 * Get all literals of this rule.
	 * 
	 * @return a Set of all literals related to this Rule.
	 */
	Set<Literal> getLiterals();
	
	
	/**
	 * Get all terms of this rule.
	 * 
	 * @return a Set of all Term related to this Rule.
	 */
	Set<Term> getTerms();
}
