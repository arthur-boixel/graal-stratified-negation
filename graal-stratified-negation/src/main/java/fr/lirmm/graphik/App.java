package fr.lirmm.graphik;


public class App {

	public static void main(String[] args) throws Exception {

		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		new Window(true);
	}
}
