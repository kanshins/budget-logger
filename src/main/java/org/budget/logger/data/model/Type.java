/**
 * 
 */
package org.budget.logger.data.model;

/**
 * @author kanshin
 *
 */
public enum Type {
	INCOME(1), OUTCOME(2), STORING(3);
	
	private int id;
	
	Type(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public static Type valueOf(int id) {
		switch(id) {
		case 1:
			return INCOME;
		case 2:
			return OUTCOME;
		case 3:
			return STORING;
		default:
			return null;
		}
	}

	public String toString() {
		return String.valueOf(id);
	}
	
}
