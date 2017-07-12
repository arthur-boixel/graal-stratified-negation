package fr.lirmm.graphik;

import java.util.Set;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import fr.lirmm.graphik.graal.api.core.GraphOfRuleDependencies;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.util.graph.scc.StronglyConnectedComponentsGraph;

public class DefaultGraphOfRuleDependenciesViewer {

	private static DefaultGraphOfRuleDependenciesViewer instance;
	
	
	public static synchronized DefaultGraphOfRuleDependenciesViewer instance() {
		if (instance == null)
			instance = new DefaultGraphOfRuleDependenciesViewer();

		return instance;
	}
	
	
	public Graph getGraph(GraphOfRuleDependencies g)
	{
		Graph graphDisp = new SingleGraph("Graph Of Rules Dependencies");
		computeVertex(g.getRules() , graphDisp);
		addDependencies(g , graphDisp);
		
		graphDisp.addAttribute("ui.stylesheet", "node {\n" + 
				"	shape: circle;\n" + 
				"	fill-color: purple;\n" + 
				"	text-color: black;\n" + 
				"	text-mode : normal;\n" + 
				"	text-size : 15px;\n" + 
				"	size-mode : fit;\n" +
				"}\n" + 
				"\n" + 
				"node.rouge {\n" + 
				"	fill-color : red;\n" + 
				"}\n" + 
				"node.neg {\n" + 
				"	fill-color : yellow;\n" + 
				"}\n" + 
				"\n" + 
				"edge.plus {\n" + 
				"	fill-color: black;\n" + 
				"\n" + 
				"}\n" + 
				"\n" + 
				"edge.moins {\n" + 
				"	fill-color : red;\n" + 
				"	text-color : blue;\n" + 
				"}");
		
		graphDisp.addAttribute("ui.quality");
		graphDisp.addAttribute("ui.antialias");
		
		return graphDisp;
	}
	
	public Graph getSCCGraph(GraphOfRuleDependencies graph) {
		StronglyConnectedComponentsGraph<Rule> sccGraph = graph.getStronglyConnectedComponentsGraph();
		Graph graphDisp = new SingleGraph("Graph of Strongly Connected Components");
		
		for(int i = 0 ; i < sccGraph.getNbrComponents() ; i++)
		{
			Node n = graphDisp.addNode("C" + i);
			n.addAttribute("label", "C"+i);
			n.addAttribute("c", sccGraph.getComponent(i).toString());
		}
		
		for(int i = 0 ; i < sccGraph.getNbrComponents() ; i++)
		{
			for(Rule src : sccGraph.getComponent(i))
			{
				for(int j = 0 ; j < sccGraph.getNbrComponents() ; j++)
				{
					Set<Rule> s = sccGraph.getComponent(j);
					String edgeID = "C" + i + "|C" + j;
					
					for(Rule dest : ((DefaultLabeledGraphOfRuleDependencies) graph).getInhibitedRules(src))
					{
						if(s.contains(dest) && graphDisp.getEdge(edgeID) == null)
						{
							Edge e = graphDisp.addEdge(edgeID.toString(),
									"C" + i,
									"C" + j,
									true
									);
							e.setAttribute("label", '-');
							e.addAttribute("ui.class", "moins");
						}
					}
					
					for(Rule dest : graph.getTriggeredRules(src))
					{
						if(s.contains(dest) && graphDisp.getEdge(edgeID) == null)
						{
							Edge e = graphDisp.addEdge(edgeID.toString(),
									"C" + i,
									"C" + j,
									true
									);
							e.setAttribute("label", '+');
							e.addAttribute("ui.class", "plus");
						}
					}
				}
			}
		}
		
		graphDisp.addAttribute("ui.stylesheet", "node {\n" + 
				"	shape: circle;\n" + 
				"	fill-color: purple;\n" + 
				"	text-color: black;\n" + 
				"	text-mode : normal;\n" + 
				"	text-size : 15px;\n" + 
				"	size-mode : fit;\n" +
				"}\n" + 
				"\n" + 
				"node.rouge {\n" + 
				"	fill-color : red;\n" + 
				"}\n" + 
				"node.neg {\n" + 
				"	fill-color : yellow;\n" + 
				"}\n" + 
				"\n" + 
				"edge.plus {\n" + 
				"	fill-color: black;\n" + 
				"\n" + 
				"}\n" + 
				"\n" + 
				"edge.moins {\n" + 
				"	fill-color : red;\n" + 
				"	text-color : blue;\n" + 
				"}");
		
		return graphDisp;
	}
	
	public void display(GraphOfRuleDependencies g)
	{
		Graph graphDisp = new SingleGraph("Graph Of Rules Dependencies");
		computeVertex(g.getRules() , graphDisp);
		addDependencies(g , graphDisp);

	    System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
	    
	    graphDisp.addAttribute("ui.stylesheet", "url('styles.css')");
		graphDisp.addAttribute("ui.quality");
		graphDisp.addAttribute("ui.antialias");
		graphDisp.display();			
	}
	
	private void computeVertex(Iterable<Rule> rules , Graph graphDisp)
	{
		for(Rule r : rules)
		{
			Node n = graphDisp.addNode(r.getLabel());
			n.addAttribute("label", r.getLabel());
			n.addAttribute("rule", r.toString());
		}
	}
	
	private void addDependencies(GraphOfRuleDependencies g , Graph graphDisp)
	{
		for(Rule src : g.getRules())
		{
			for(Rule dest : g.getTriggeredRules(src))
			{
				StringBuilder edgeID = new StringBuilder();
				edgeID.append(src.getLabel());
				edgeID.append("|");
				edgeID.append(dest.getLabel());
					
				Edge e = graphDisp.addEdge(edgeID.toString(),
						src.getLabel(),
						dest.getLabel(),
						true
						);
				e.setAttribute("label", '+');
				e.addAttribute("ui.class", "plus");
			}
			
			if(g instanceof DefaultLabeledGraphOfRuleDependencies)
			{
				for(Rule dest : ((DefaultLabeledGraphOfRuleDependencies)g).getInhibitedRules(src))
				{
					StringBuilder edgeID = new StringBuilder();
					edgeID.append(src.getLabel());
					edgeID.append("|");
					edgeID.append(dest.getLabel());
						
					Edge e = graphDisp.addEdge(edgeID.toString(),
							src.getLabel(),
							dest.getLabel(),
							true
							);
					e.setAttribute("label", '-');
					e.addAttribute("ui.class", "moins");
				}
			}
		}
	}
}
