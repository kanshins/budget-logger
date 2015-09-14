/**
 * StartServer.java
 */
package org.budget.logger;

import java.awt.SplashScreen;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author <sergey.kanshin@gmail.com>
 * @date 14 сент. 2015 г.
 */
public class BudgetLogger {

    private static Logger logger = Logger.getLogger(BudgetLogger.class);

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        logger.info("Starting budget logger");
        SplashScreen splash = SplashScreen.getSplashScreen();
        if (splash == null) {
            logger.fatal("SplashScreen.getSplashScreen() returned null");
            return;
        }
        
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        Thread.sleep(3000);

        splash.close();

    }
}
