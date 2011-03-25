import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author brooks Mar 20, 2011
 */
public class Patient {
	/** Name of the item. */
	private String name = "NONE";
	
	/** A description of the item. */
	private String description = "NONE";
	
	/** Cardiac state of the item. */
	private String cardiacstate = "NONE";
	
	/** Respiratory state of the item. */
	private String respiratorystate = "NONE";
	
	/** Mental state of the item. */
	private String mentalstate = "NONE";
	
	static final String NAME = "name";
	
	static final String DESCRIPTION = "description";
	
	static final String CARDIACSTATE = "cardiacstate";
	
	static final String RESPIRATORYSTATE = "respiratorystate";
	
	static final String MENTALSTATE = "mentalstate";
	
	/**
     * 
     */
	public Patient() {
		/**
         * 
         */
	}
	
	/**
	 * @param name
	 */
	public Patient(String name) {
		setName(name);
	}
	
	/**
	 * @param name
	 * @param description
	 */
	public Patient(String name, String description) {
		setName(name);
		setDescription(description);
	}
	
	/**
	 * @param name
	 * @param description
	 * @param cardiacstate
	 * @param respiratorystate
	 * @param mentalstate
	 */
	public Patient(String name, String description, String cardiacstate,
			String respiratorystate, String mentalstate) {
		setName(name);
		setDescription(description);
		setCardiacstate(cardiacstate);
		setRespiratorystate(respiratorystate);
		setMentalstate(mentalstate);
	}
	
	/**
	 * @return the cardiacstate
	 */
	public String getCardiacstate() {
		return cardiacstate;
	}
	
	/**
	 * @param cardiacstate
	 *            the cardiacstate to set
	 */
	public void setCardiacstate(String cardiacstate) {
		this.cardiacstate = cardiacstate;
	}
	
	/**
	 * @return the respiratorystate
	 */
	public String getRespiratorystate() {
		return respiratorystate;
	}
	
	/**
	 * @param respiratorystate
	 *            the respiratorystate to set
	 */
	public void setRespiratorystate(String respiratorystate) {
		this.respiratorystate = respiratorystate;
	}
	
	/**
	 * @return the mentalstate
	 */
	public String getMentalstate() {
		return mentalstate;
	}
	
	/**
	 * @param mentalstate
	 *            the mentalstate to set
	 */
	public void setMentalstate(String mentalstate) {
		this.mentalstate = mentalstate;
	}
	
	/**
	 * @return description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return a deep copy JSONObject representation of Item or null if
	 *         something went wrong
	 */
	public JSONObject getJSONObject() {
		JSONObject clone = new JSONObject();
		try {
			clone.put("name", getName());
			clone.put("description", getDescription());
			clone.put("cardiacstate", getCardiacstate());
			clone.put("respiratorystate", getRespiratorystate());
			clone.put("mentalstate", getMentalstate());
		} catch (JSONException e) {
			// This case should never occur
			return null;
		}
		return clone;
	}
	
}
