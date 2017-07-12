package fr.lirmm.graphik;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Units;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import fr.lirmm.graphik.graal.api.core.GraphOfRuleDependencies;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.util.graph.scc.StronglyConnectedComponentsGraph;

public class Window extends JFrame {
	private static final long serialVersionUID = -4228059556541657661L;

	JMenuBar menu;
	JMenu fileMenu;
	JMenuItem chooser;
	JMenuItem quit;
	JMenu toolMenu;
	JMenuItem rulesText; 
	JMenuItem grdText;
	JMenuItem grdVisu;
	JMenuItem sccText;
	JMenuItem sccVisu;
	JMenu saveMenu;
	JMenuItem saveRules;
	JMenuItem saveGRD;
	JMenuItem saveSCC;

	View view;
	JScrollPane scroll;
	JTextArea displayZone;

	JToolBar info;
	JLabel infoNode;

	DefaultLabeledGraphOfRuleDependencies grd;
	Graph grdDisp;
	Viewer viewer;

	StronglyConnectedComponentsGraph<Rule> scc;
	Graph sccDisp;

	private boolean master;

	public Window(boolean master)
	{
		/* Initialize the Window */

		super();

		this.master = master;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Dependencies Viewer");
		setName("Dependencies Viewer");
		setPreferredSize(new Dimension(1024,720));

		/* Initialize the Menu */
		menu = new JMenuBar();
		menu.setPreferredSize(new Dimension(1024,30));
		menu.setMaximumSize(new Dimension(1024,30));
		menu.setLayout(new GridLayout(1,6));
		menu.setVisible(true);

		/* File */
		fileMenu = new JMenu("File");
		fileMenu.setPreferredSize(new Dimension(120,30));
		fileMenu.setMaximumSize(new Dimension(120, 30));
		fileMenu.setVisible(true);
		chooser = new JMenuItem("Open");
		chooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFile();
			}
		});
		fileMenu.add(chooser);

		quit = new JMenuItem("Quit");
		quit.setMaximumSize(new Dimension(120, 30));
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fermer();
			}
		});
		quit.setVisible(true);
		fileMenu.add(quit);
		menu.add(fileMenu);


		/* Tool */
		toolMenu = new JMenu("Tools");
		toolMenu.setPreferredSize(new Dimension(120,30));
		toolMenu.setMaximumSize(new Dimension(120, 30));
		toolMenu.setVisible(true);
		rulesText = new JMenuItem("Print Rules");

		rulesText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				printRules();
			}
		});

		toolMenu.add(rulesText);

		grdText = new JMenuItem("Print GRD");
		grdText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				printGRD();
			}
		});
		toolMenu.add(grdText);

		grdVisu = new JMenuItem("Display GRD");
		grdVisu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayGRD();
			}
		});
		toolMenu.add(grdVisu);

		sccText = new JMenuItem("Print SCC");
		sccText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				printSCC();
			}
		});
		toolMenu.add(sccText);

		sccVisu = new JMenuItem("Display SCC");
		sccVisu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displaySCC();
			}
		});
		toolMenu.add(sccVisu);

		menu.add(toolMenu);


		/* Save */
		saveMenu = new JMenu("Save");
		saveMenu.setPreferredSize(new Dimension(120,30));
		saveMenu.setMaximumSize(new Dimension(120, 30));
		saveMenu.setVisible(true);

		saveRules = new JMenuItem("Save Rules");
		saveRules.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportRules();
			}
		});
		saveMenu.add(saveRules);

		saveGRD = new JMenuItem("Save GRD");
		saveGRD.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportGRD();		
			}
		});
		saveMenu.add(saveGRD);

		saveSCC = new JMenuItem("Save SCC Graph");
		saveSCC.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportSCC();		
			}
		});
		saveMenu.add(saveSCC);
		menu.add(saveMenu);


		/* Initialize the info zone */

		info = new JToolBar("Information");
		info.setMaximumSize(new Dimension(1024,30));
		info.setFloatable(false);
		info.setLayout(new GridLayout(1,6));
		info.setVisible(true);

		infoNode = new JLabel(" ");
		infoNode.setVisible(true);

		info.add(infoNode);

		/* Build the GUI */

		setLayout(new BorderLayout());

		add(menu , BorderLayout.NORTH);
		add(info , BorderLayout.SOUTH);

		pack();
		setVisible(true);
		setLocationRelativeTo(null);

		this.displayZone = new JTextArea();
	}

	public Window(DefaultLabeledGraphOfRuleDependencies g , boolean master)
	{
		/* Initialize the Window */
		this(master);

		this.grd = g;
		this.grdDisp = null;
		this.scc = this.grd.getStronglyConnectedComponentsGraph();
		this.sccDisp = null;
		
		this.infoNode.setText("Ready\n");
		
		displayGRD();
		pack();
		setVisible(true);
		setLocationRelativeTo(null);
	}




	/*************************************************/
	/********** Methods relted to Listeners **********/
	/*************************************************/

	public void openFile()
	{
		JFileChooser c = new JFileChooser(".");
		FileNameExtensionFilter filter = new FileNameExtensionFilter("DLGP files", "dlgp");
		c.setFileFilter(filter);
		int returnVal = c.showOpenDialog(this);

		if(returnVal == JFileChooser.APPROVE_OPTION)
		{	
			this.clearDrawZone();
			
			this.infoNode.setText("Computing dependencies for " + c.getSelectedFile().getName() + " (this may take a while)...");
			
			this.grd = new DefaultLabeledGraphOfRuleDependencies(c.getSelectedFile());
			this.grdDisp = null;
			this.scc = this.grd.getStronglyConnectedComponentsGraph();
			this.sccDisp = null;
			
			this.infoNode.setText(this.displayZone.getText() + "Ready\n");
		}
	}


	public void fermer()
	{
		dispose();
		if(master)
			System.exit(0);
	}


	public String getRulesText()
	{
		StringBuffer s = new StringBuffer("== Rule Base ==\n");
		for(Rule r : this.grd.getRules())
		{
			s.append(r.toString());
			s.append('\n');
		}

		return s.toString();
	}

	public void printRules()
	{
		if(this.grd != null)
		{
			this.infoNode.setText("Rules : " + grd.getNodeCount());
			this.displayZone.setText(this.getRulesText());
			this.displayZone.setCaretPosition(0);
			this.scroll = new JScrollPane(this.displayZone);
			this.add(this.scroll , BorderLayout.CENTER);
			this.pack();
		}
	}


	public String getGRDText()
	{
		StringBuffer s = new StringBuffer("== Graph of Rule Dependencies ==\n");

		for(Rule r1 : this.grd.getRules())
		{
			for(Rule r2 : this.grd.getTriggeredRules(r1))
			{
				s.append("[");
				s.append(r1.getLabel());
				s.append("] ={+}=> [");
				s.append(r2.getLabel());
				s.append("]\n");
			}
			for(Rule r2 : this.grd.getInhibitedRules(r1))
			{
				s.append("[");
				s.append(r1.getLabel());
				s.append("] ={-}=> [");
				s.append(r2.getLabel());
				s.append("]\n");
			}
		}

		return s.toString();
	}

	public void printGRD()
	{
		if(this.grd != null)
		{
			clearDrawZone();
			
			this.infoNode.setText("Rules : " + grd.getNodeCount() + " | Edges : " + grd.getEdgeCount());
			this.displayZone.setText(this.getGRDText());
			this.displayZone.setCaretPosition(0);
			this.scroll = new JScrollPane(this.displayZone);
			this.add(this.scroll , BorderLayout.CENTER);
			this.pack();
		}
	}

	public void displayGRD()
	{
		if(this.grd != null)
		{
			clearDrawZone();

			if(this.grdDisp == null)
				this.grdDisp = DefaultGraphOfRuleDependenciesViewer.instance().getGraph(grd);
		
			this.infoNode.setText("Rules : " + grd.getNodeCount() + " | Edges : " + grd.getEdgeCount());
			if(grd.hasCircuitWithNegativeEdge())
				this.infoNode.setText(this.infoNode.getText() + " | Not stratifiable");
			else
				this.infoNode.setText(this.infoNode.getText() + " | Stratifiable");
			
			this.viewer = new Viewer(this.grdDisp , Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
			this.viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
			this.viewer.enableAutoLayout();

			this.view = this.viewer.addDefaultView(false);
			this.view.addMouseListener(new MouseListener() {
				public void mouseReleased(MouseEvent e) {}
				public void mousePressed(MouseEvent e) {}
				public void mouseExited(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {}
				public void mouseClicked(MouseEvent e) {
					System.out.println("Click ! " + (e.getX()/view.getCamera().getMetrics().ratioPx2Gu) + "," + (e.getY()/view.getCamera().getMetrics().ratioPx2Gu));
					GraphicElement n = view.findNodeOrSpriteAt(e.getX(), e.getY()+menu.getMaximumSize().getHeight());
					if(n != null)
					{
						System.out.println("Node : " + n.label);
						infoNode.setText(grdDisp.getNode(n.label).getAttribute("rule") + "Class : " + grdDisp.getNode(n.label).getAttribute("ui.class"));
					}
					else
						infoNode.setText("Rules : " + grd.getNodeCount() + " | Edges : " + grd.getEdgeCount());
				}
			});

			((JPanel)this.view).addMouseWheelListener(new MouseWheelListener() {
				public void mouseWheelMoved(MouseWheelEvent e) {
					if(e.getWheelRotation() < 0)
					{
						System.out.println("wheel up");

						double cX = view.getCamera().getMetrics().lengthToGu(e.getPoint().getX() , Units.PX);
						cX = e.getPoint().getX() / view.getCamera().getMetrics().ratioPx2Gu;
						System.out.println("ancien : " + view.getCamera().getViewCenter().toString());
						double cY = view.getCamera().getMetrics().lengthToGu(e.getPoint().getY() , Units.PX);
						cY = e.getPoint().getY() / view.getCamera().getMetrics().ratioPx2Gu;
						double cZ = view.getCamera().getViewCenter().z;
						view.getCamera().setViewCenter(cX , cY , cZ);
						System.out.println("nouveau : " + view.getCamera().getViewCenter().toString());
					}
					else if(e.getWheelRotation() > 0)
						System.out.println("wheel down");

				}
			});

			add((Component) this.view , BorderLayout.CENTER);
			pack();
		}
	}


	public String getSCCText()
	{
		StringBuffer s = new StringBuffer("== Strongly Connected Components ==\n");

		for(int i = 0 ; i < this.scc.getNbrComponents() ; i++)
		{
			boolean first = true;
			s.append("C" + i + " = {");
			for(Rule r : this.scc.getComponent(i))
			{
				if(first)
					first = false;
				else
					s.append(", ");
				s.append(r.getLabel());
			}
			s.append("}\n");
		}

		return s.toString();
	}

	public String getGSCCText()
	{
		StringBuffer s = new StringBuffer("== Graph of Strongly Connected Components ==\n");

		for(Iterator<Node> itNode = sccDisp.getNodeIterator() ; itNode.hasNext() ;)
		{
			Node n = itNode.next();

			for(Iterator<Edge> itEdge = n.getEachLeavingEdge().iterator() ; itEdge.hasNext() ; )
			{
				Edge e = itEdge.next();
				s.append(n.getId());

				if((char)e.getAttribute("label") == '+')
					s.append(" =+=> ");
				else
					s.append(" =-=> ");

				s.append(e.getTargetNode().getId());
				s.append("\n");
			}

		}

		return s.toString();
	}

	public void printSCC()
	{
		System.out.println(this.getSCCText());
		if(this.grd != null)
		{
			if(this.sccDisp == null)
				this.sccDisp = DefaultGraphOfRuleDependenciesViewer.instance().getSCCGraph(this.grd);
			this.infoNode.setText("Strongly Connected Components : " + sccDisp.getNodeCount() + " | Edges : " + sccDisp.getEdgeCount());
			
			
			this.clearDrawZone();
			this.displayZone.setText(this.getSCCText() + this.getGSCCText());
			this.displayZone.setCaretPosition(0);
			this.scroll = new JScrollPane(this.displayZone);
			this.add(this.scroll , BorderLayout.CENTER);
			this.pack();
		}
	}


	public void displaySCC()
	{
		if(this.grd != null)
		{
			clearDrawZone();

			if(this.sccDisp == null)
				this.sccDisp = DefaultGraphOfRuleDependenciesViewer.instance().getSCCGraph(this.grd);

			this.infoNode.setText("Strongly Connected Components : " + sccDisp.getNodeCount() + " | Edges : " + sccDisp.getEdgeCount());
			if(grd.hasCircuitWithNegativeEdge())
				this.infoNode.setText(this.infoNode.getText() + " | Not stratifiable");
			else
				this.infoNode.setText(this.infoNode.getText() + " | Stratifiable");
			
			
			this.viewer = new Viewer(this.sccDisp , Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
			this.viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
			this.viewer.enableAutoLayout();

			this.view = this.viewer.addDefaultView(false);
			this.view.addMouseListener(new MouseListener() {
				public void mouseReleased(MouseEvent e) {}
				public void mousePressed(MouseEvent e) {}
				public void mouseExited(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {}
				public void mouseClicked(MouseEvent e) {
					System.out.println("Click ! " + (e.getX()/view.getCamera().getMetrics().ratioPx2Gu) + "," + (e.getY()/view.getCamera().getMetrics().ratioPx2Gu));
					GraphicElement n = view.findNodeOrSpriteAt(e.getX(), e.getY()+menu.getMaximumSize().getHeight());
					if(n != null)
					{
						System.out.println("Node : " + n.label);
						infoNode.setText(sccDisp.getNode(n.label).getAttribute("c"));
						
						Set<Rule> s = scc.getComponent(Integer.parseInt(n.label.replaceAll("C", "")));
						System.out.println(s);
						//DefaultGraphOfRuleDependenciesViewer.instance().display(grd.getSubGraph(s));
						Window w = new Window((DefaultLabeledGraphOfRuleDependencies)((DefaultLabeledGraphOfRuleDependencies)grd).getSubGraph(s) , false);
					}
					else
						infoNode.setText("Strongly Connected Components : " + sccDisp.getNodeCount() + " | Edges : " + sccDisp.getEdgeCount());
				}
			});

			((JPanel)this.view).addMouseWheelListener(new MouseWheelListener() {
				public void mouseWheelMoved(MouseWheelEvent e) {
					if(e.getWheelRotation() < 0)
					{
						System.out.println("wheel up");

						double cX = view.getCamera().getMetrics().lengthToGu(e.getPoint().getX() , Units.PX);
						cX = e.getPoint().getX() / view.getCamera().getMetrics().ratioPx2Gu;
						System.out.println("ancien : " + view.getCamera().getViewCenter().toString());
						double cY = view.getCamera().getMetrics().lengthToGu(e.getPoint().getY() , Units.PX);
						cY = e.getPoint().getY() / view.getCamera().getMetrics().ratioPx2Gu;
						double cZ = view.getCamera().getViewCenter().z;
						view.getCamera().setViewCenter(cX , cY , cZ);
						System.out.println("nouveau : " + view.getCamera().getViewCenter().toString());
					}
					else if(e.getWheelRotation() > 0)
						System.out.println("wheel down");

				}
			});

			add((Component) this.view , BorderLayout.CENTER);
			pack();
		}
	}


	public void exportRules()
	{
		if(this.grd != null)
		{
			JFileChooser c = new JFileChooser(".");
			FileNameExtensionFilter filter = new FileNameExtensionFilter("DLGP files", "dlgp");
			c.setFileFilter(filter);
			c.setDialogTitle("Export Rules");
			int returnVal = c.showOpenDialog(this);

			if(returnVal == JFileChooser.APPROVE_OPTION)
			{	
				try {
					FileWriter fw = new FileWriter(c.getSelectedFile());
					fw.write(this.getRulesText());
					fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}


	public void exportGRD()
	{
		if(this.grd != null)
		{
			JFileChooser c = new JFileChooser(".");
			FileNameExtensionFilter filter = new FileNameExtensionFilter("DLGP files", "dlgp");
			c.setFileFilter(filter);
			c.setDialogTitle("Export Graph of Rules Dependencies");
			int returnVal = c.showOpenDialog(this);

			if(returnVal == JFileChooser.APPROVE_OPTION)
			{	
				try {
					FileWriter fw = new FileWriter(c.getSelectedFile());
					fw.write(this.getGRDText());
					fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}


	public void exportSCC()
	{
		if(this.grd != null)
		{
			if(this.sccDisp == null)
				this.sccDisp = DefaultGraphOfRuleDependenciesViewer.instance().getSCCGraph(this.grd);
			
			JFileChooser c = new JFileChooser(".");
			FileNameExtensionFilter filter = new FileNameExtensionFilter("DLGP files", "dlgp");
			c.setFileFilter(filter);
			c.setDialogTitle("Export Graph of Strongly Connected Components");
			int returnVal = c.showOpenDialog(this);

			if(returnVal == JFileChooser.APPROVE_OPTION)
			{	
				try {
					FileWriter fw = new FileWriter(c.getSelectedFile());
					fw.write(this.getSCCText());
					fw.write(this.getGSCCText());
					fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}


	public void clearDrawZone()
	{
		if(this.view != null)
			this.remove((Component) this.view);
		if(this.scroll != null)
			this.remove(this.scroll);

		this.repaint();
	}	
}

