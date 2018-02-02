package fr.inria.lille.spirals.bikinirepair.reporting;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import spoon.Launcher;

public class Dashboard {

	public static Launcher spoon = new Launcher();

	public Dashboard() {
		spoon.addInputResource("/home/thomas/git/mayocat-shop-test/shop/shipping/internal/src/main/java");
		spoon.addInputResource("/home/thomas/git/mayocat-shop-test/shop/shipping/api/src/main/java");
		//spoon.addInputResource("/home/thomas/git/BroadleafCommerce/admin/broadleaf-open-admin-platform/src/main/java");
		spoon.getEnvironment().setNoClasspath(true);
		spoon.buildModel();
	}

	public void start() {
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");

		Server jettyServer = new Server(9000);
		jettyServer.setHandler(context);

		ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, "/api/*");
		jerseyServlet.setInitOrder(0);

		// Tells the Jersey Servlet which REST service/class to load.
		jerseyServlet.setInitParameter(
				"jersey.config.server.provider.classnames",
				EntryPoint.class.getCanonicalName());

		// add special pathspec of "/home/" content mapped to the homePath
		ServletHolder holderHome = new ServletHolder("static-home", DefaultServlet.class);
		holderHome.setInitParameter("resourceBase", "src/main/resources/reporting/www2/dist");
		holderHome.setInitParameter("dirAllowed","true");
		holderHome.setInitParameter("pathInfoOnly","true");
		context.addServlet(holderHome,"/*");

		try {
			jettyServer.start();
			jettyServer.join();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jettyServer.destroy();
		}
	}

	public static void main(String[] args) {
		new Dashboard().start();
	}
}

