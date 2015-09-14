/**
 * 
 */
package org.budget.logger.ui.mvc;

import java.io.Serializable;

/**
 * 
 * @author <sergey.kanshin@gmail.com>
 * @date 15 сент. 2015 г.
 */
public class EventType implements Serializable {

	private static final long serialVersionUID = 1L;

	private static int count = 0;

	// needed to use FastMap for much better speed
	final String id;

	private int eventCode = -1;

	/**
	 * Creates a new event type.
	 */
	public EventType() {
		id = String.valueOf(count++);
	}

	/**
	 * Creates a new browser based event type.
	 * 
	 * @param eventCode
	 *            additional information about the event
	 */
	public EventType(int eventCode) {
		this();
		this.eventCode = eventCode;
	}

	/**
	 * Returns the event code.
	 * 
	 * @return the event code
	 */
	public int getEventCode() {
		return eventCode;
	}

	/**
	 * Returns true if the event type represents a browser event type 
	 * 
	 * @return true for browser event types
	 */
	public boolean isBrowserEvent() {
		return eventCode != -1;
	}
}
