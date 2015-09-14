/**
 * 
 */
package org.budget.logger.helpers;

import java.util.Calendar;
import java.util.Date;

import org.springframework.context.support.MessageSourceAccessor;

/**
 * 
 * @author <sergey.kanshin@gmail.com>
 * @date 14 сент. 2015 г.
 */
public class DateHelper {

	public static Date getNextDay() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 1);
		return c.getTime();
	}
	
	public static Date getPreviousMonth() {
		return getPreviousMonth(1);
	}

	public static Date getPreviousMonth(int count) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, c.get(Calendar.MONTH) - count);
		return c.getTime();
	}

	public static Date getNextMonth() {
		return getNextMonth(1);
	}

	public static Date getNextMonth(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.MONTH, c.get(Calendar.MONTH) + 1);
		return c.getTime();
	}
	
	public static Date getNextMonth(int count) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, c.get(Calendar.MONTH) + count);
		return c.getTime();
	}

	public static Date getMonthStart(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	public static String getShortMonth(Date date, MessageSourceAccessor msa) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return msa.getMessage("jsp.reports.month.shotr." + c.get(Calendar.MONTH));
	}
	
}
