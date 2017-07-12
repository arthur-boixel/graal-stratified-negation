package fr.lirmm.graphik;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import fr.lirmm.graphik.util.graph.scc.StronglyConnectedComponentsGraph;
import fr.lirmm.graphik.graal.api.core.GraphOfRuleDependencies;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.kb.KBBuilder;
import fr.lirmm.graphik.graal.api.core.Rule;

import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.alg.cycle.SzwarcfiterLauerSimpleCycles;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.DirectedGraph;



public class DefaultLabeledGraphOfRuleDependencies implements GraphOfRuleDependencies {

	private DirectedGraph<Rule, DefaultDirectedLabeledEdge> graph;

	private Iterable<Rule> rules;

	private boolean computeCircuits;
	private List<List<Rule>> circuits;
	
	private boolean computeScc;
	private StronglyConnectedComponentsGraph<Rule> Scc;
	
	private int nbNodes;
	private int nbEdges;
	
	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////
	
	
	public DefaultLabeledGraphOfRuleDependencies(File src)
	{
		this(readRules(src) , true);
	}
	
	public DefaultLabeledGraphOfRuleDependencies(Iterable<Rule> rules , boolean computeDep) {
		System.out.println("###");
		this.graph = new DefaultDirectedGraph<Rule, DefaultDirectedLabeledEdge>(DefaultDirectedLabeledEdge.class);
				
		this.rules = rules;
		
		this.computeCircuits = false;
		this.circuits = new ArrayList<List<Rule>>();
		
		this.computeScc = false;
		this.Scc = new StronglyConnectedComponentsGraph<Rule>();
		
		this.nbNodes = 0;
		for(Rule r : rules)
		{
			graph.addVertex(r);
			this.nbNodes++;
		}
		
		this.nbEdges = 0;
		if(computeDep)
			this.computeDependencies();
		
	}
	
	
	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	
	private void computeDependencies() {
		
		// Building index
		IndexedByBodyPredicateRuleSetWithNegation index = new IndexedByBodyPredicateRuleSetWithNegation(this.rules);
		
		int coeurs = Runtime.getRuntime().availableProcessors();
		
		
		ArrayList<ArrayList<Rule>> l = new ArrayList<ArrayList<Rule>>();
		for(int i = 0 ; i < coeurs ; i++)
			l.add(new ArrayList<Rule>());
		
		
		int k = 0;
		for(Iterator<Rule> itRule = rules.iterator() ; itRule.hasNext() ; )
		{
			l.get(k).add(itRule.next());
			k = (k+1)%coeurs;
		}
		
		
		try {
			ArrayList<ThreadDependency> threads = new ArrayList<>();
			for(int i = 0 ; i < coeurs ; i++)
			{
				ThreadDependency t = new ThreadDependency(l.get(i), index, graph);
				threads.add(t);
				t.start();
			}
		
			for(int i = 0 ; i < coeurs ; i++)
			{
				threads.get(i).join();
				this.nbEdges += threads.get(i).getNbDep();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}
		
	
	
	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	
	public boolean existUnifier(Rule src, Rule dest) {
		
		return (this.graph.getEdge(src, dest) != null);
	}
	
	
	public Set<Substitution> getUnifiers(Rule src, Rule dest) {
		
		return null;
	}
	
	
	public Set<Rule> getTriggeredRules(Rule src) {
		
		Set<Rule> set = new HashSet<Rule>();
		
		for(DefaultDirectedLabeledEdge i : this.graph.outgoingEdgesOf((DefaultRuleWithNegation)src)) {
			if(i.getLabel() == '+')
				set.add(this.graph.getEdgeTarget(i));
		}
		
		return set;
	}
	
	public Set<Rule> getInhibitedRules(Rule src)
	{
		Set<Rule> set = new HashSet<Rule>();
		
		for(DefaultDirectedLabeledEdge i : this.graph.outgoingEdgesOf((DefaultRuleWithNegation)src)) {
			if(i.getLabel() == '-')
				set.add(this.graph.getEdgeTarget(i));
		}
		
		return set;
	}
	
	public Set<Pair<Rule, Substitution>> getTriggeredRulesWithUnifiers(Rule src) {
		
		return null;
	}

	
	public GraphOfRuleDependencies getSubGraph(Iterable<Rule> ruleSet) {
		
		DefaultLabeledGraphOfRuleDependencies subGRD = new DefaultLabeledGraphOfRuleDependencies(ruleSet , false);
		
		for (Rule src : ruleSet) {
			for (Rule target : ruleSet) {
				if (this.graph.getEdge(src, target) != null) {
					
						subGRD.addDependency(src, target, this.graph.getEdge(src, target).getLabel());
				}
			}
		}
		
		return subGRD;
	}
	
	
	protected void addDependency(Rule src , Rule target , char label)
	{
		graph.addEdge(src, target, new DefaultDirectedLabeledEdge(((DefaultRuleWithNegation) src).getIndice(),
                                ((DefaultRuleWithNegation) target).getIndice(),
                                label));
	}
	
	
	public Iterable<Rule> getRules() {
		return this.rules;
	}
	
	
	public StronglyConnectedComponentsGraph<Rule> getStronglyConnectedComponentsGraph() {
		
		if(!computeScc) {
			Scc =  new StronglyConnectedComponentsGraph<Rule>(this.graph);
			
			computeScc = true;
		}
		
		return Scc;
	}
	
	
	public boolean hasCircuit() {
		
		if(!computeCircuits) {
			
			SzwarcfiterLauerSimpleCycles<Rule,DefaultDirectedLabeledEdge> inspector =
				new SzwarcfiterLauerSimpleCycles<Rule, DefaultDirectedLabeledEdge>(this.graph);
		
			
			this.circuits = inspector.findSimpleCycles();
			
			computeCircuits = true;
		}
		
		return !circuits.isEmpty();
	}

	
	public boolean hasCircuitWithNegativeEdge() {
		
		if(!computeCircuits)
			hasCircuit();
		
		for(List<Rule> c : circuits)
		{
			if(containsNegativeEdge(c))
			{
				return true;
			}
		}
		
		return false;
	}
	
	private boolean containsNegativeEdge(List<Rule> circuit) {
		
		for(int i = 0 ; i < circuit.size()-1 ; i++) { // Following the circuit
			
			for(DefaultDirectedLabeledEdge e : this.graph.outgoingEdgesOf(circuit.get(i))) {
				
				if(e.getHead() == ((DefaultRuleWithNegation)circuit.get(i+1)).getIndice()) { // Wanted edge found
					
					if(e.getLabel() == '-')
						return true;
					
					break;
				}
			} 		
		}
		
		return false;
	}
	
	
	public String toString() {
		
		StringBuilder s = new StringBuilder("Rules :\n");
		
		
		for(Rule r : this.rules)
			s.append(r.toString());
			s.append("\n");
		
		return s.append(graph.toString()).toString();
	}
	
	private static Iterable<Rule> readRules(File src)
	{
		KBBuilder kbb = new KBBuilder();
		
		/* Parsing Rules */
		try {
			InputStream ips = null;
			
			ips = new FileInputStream(src);
			
			InputStreamReader ipsr = new InputStreamReader(ips);
			BufferedReader br = new BufferedReader(ipsr);
			String ligne;
			
			while ((ligne = br.readLine()) != null) {
				if(ligne.charAt(0) != '%')
					kbb.add(DlgpParserNeg.parseRule(ligne));
			}
			
			br.close();
			ipsr.close();
			ips.close();
			
		}
		catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return kbb.build().getOntology();
	}
	
	public int getNodeCount()
	{
		return this.nbNodes;
	}
	
	public int getEdgeCount()
	{
		return this.nbEdges;
	}
}
