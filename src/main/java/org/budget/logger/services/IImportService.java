/**
 * 
 */
package org.budget.logger.services;

import java.io.InputStream;

/**
 * @author kanshin
 *
 */
public interface IImportService {

	void importFromCsv(InputStream in) throws Exception;
	
}
