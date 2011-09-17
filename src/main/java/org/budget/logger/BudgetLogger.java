/**
 * StartServer.java
 */
package org.budget.logger;

import java.awt.SplashScreen;
import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 *
 *
 * @author Sergey Kanshin <kanshin@disi.unitn.it>
 * @date Apr 29, 2010
 */
public class BudgetLogger {

	private static Logger logger = Logger.getLogger(BudgetLogger.class);
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String webappDir = getWebappDir(args);
        PropertyConfigurator.configure(webappDir + "/WEB-INF/log4j.properties");
        
        logger.info("Starting webapp from '" + webappDir + "'");
		SplashScreen splash = SplashScreen.getSplashScreen();
		//splashScreen.setImageURL();
        if (splash == null) {
        	logger.fatal("SplashScreen.getSplashScreen() returned null");
            return;
        }
        String mongoCmd = args.length > 2 ? args[2] : "./mongo/mongod --dbpath ./data/db --rest"; 
        logger.info("Starting mongo [" + mongoCmd + "] ...");
        Process mongo = Runtime.getRuntime().exec(mongoCmd);
        try {
			Server server = new Server(8383);
	        ContextHandlerCollection contexts = new ContextHandlerCollection();
	        WebAppContext springContext = new WebAppContext(contexts, "budget", "/budget");
	        springContext.setWar(webappDir + "/WEB-INF");
	        springContext.setResourceBase(webappDir);
	        server.setHandler(contexts);
	        try {
		        server.start();
	        } catch (Exception e) {
	        	logger.fatal("Cannot start server", e);
	            return;
	        }
	        String prismCmd = args.length > 1 ? args[1] : "prism -webapp ./prism/app.webapp"; 
	        logger.info("Starting prism [" + prismCmd + "] ...");
	        Process prism = null;
	        try {
	        	prism = Runtime.getRuntime().exec(prismCmd);
		        //init charts
				ChartFactory.createBarChart(null, null, null,
						new DefaultCategoryDataset(), PlotOrientation.VERTICAL,
						true, false, false);
		        splash.close();
		        prism.waitFor();
		        logger.info("Prism closed");
	        } catch (Exception e) {
	        	logger.error("Prism error", e);
	        	logger.info("Waiting server");
		        server.join();
	        }
	        logger.info("Stopping server...");
	        server.stop();
	        logger.info("Server stopped");
        } catch (Exception e) {
        	logger.error("Application error", e);
        } finally {
	        mongo.destroy();
	        logger.info("Mongo stopped");
        }
	}

	private static String getWebappDir(String[] args) throws IOException {
		if (args.length > 0) {
			File dir = new File(args[0]);
			if (dir.exists()) {
				return dir.getCanonicalPath();
			} else {
				throw new IOException("Cannot find '" + args[0] + "' directory");
			}
		}
		File dir = new File("./src/main/webapp");
		if (dir.exists()) {
			return dir.getCanonicalPath();
		}
		dir = new File("./webapp");
		if (dir.exists()) {
			return dir.getCanonicalPath();
		}
		dir = new File("./../webapp");
		if (dir.exists()) {
			return dir.getCanonicalPath();
		}
		dir = new File("./target/budget-logget");
		if (dir.exists()) {
			return dir.getCanonicalPath();
		}
	
		throw new IOException("Cannot find webapp dir");
	}
}
