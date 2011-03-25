import java.io.IOException;

import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author brooks Mar 24, 2011
 */
public class PatientResource extends ServerResource {
	
	/** The underlying Item object. */
	Patient item;
	
	/** The sequence of characters that identifies the resource. */
	String itemId = null;
	
	@Override
	protected void doInit() throws ResourceException {
		
		// Get the "itemId" attribute value taken from the URI template
		// /items/{itemId}.
		this.itemId = (String) getRequest().getAttributes().get("itemId");
		
		// this.item = findItem(Long.parseLong(itemId));
		System.out.println(" " + this.itemId);
		
	}
	
	/*
	 * public Item findItem(long id) { try { DBconnector.
	 * System.out.println(dbresource.getRequest().toString());
	 * dbresource.setReference("http://127.0.0.1:5984/"+"test_database" + "/" +
	 * this.itemId); dbresource.setProtocol(Protocol.HTTP);
	 * System.out.println(dbresource.getRequest().toString()); rep =
	 * dbresource.get(); jrep = new JsonRepresentation(rep); jobj =
	 * jrep.getJsonObject(); System.out.println("exists"); return new
	 * Item(jobj.getString("name"),jobj.getString("description")); } catch
	 * (Exception e) { e.printStackTrace(); System.out.println("inside"); return
	 * null; } }
	 */

	/**
	 * Handle DELETE requests.
	 */
	@Delete
	public void removeItem() {
		if (itemId != null) {
			DatabaseResource db = new DatabaseResource(
					FirstResourceApplication.couchdburl,
					FirstResourceApplication.databasename);
			db.deleteItem(itemId);
			
		}
		
		// Tells the client that the request has been successfully fulfilled.
		setStatus(Status.SUCCESS_NO_CONTENT);
	}
	
	/**
	 * create or replace item
	 * 
	 * @param entity
	 * @return Representation
	 * @throws IOException
	 */
	@Put
	public Representation createItem(Representation entity) throws IOException {
		DatabaseResource db;
		Representation result = null;
		Form form;
		
		// The PUT request updates or creates the resource.
		// Update the description.
		if (item == null) {
			item = new Patient(null, null);
		}
		
		form = new Form(entity);
		item.setName(form.getFirstValue(Patient.NAME, true, "NULL"));
		item.setDescription(form.getFirstValue(Patient.DESCRIPTION, true,
				"NULL"));
		item.setCardiacstate(form.getFirstValue(Patient.CARDIACSTATE, true,
				"NULL"));
		item.setRespiratorystate(form.getFirstValue(Patient.RESPIRATORYSTATE,
				true, "NULL"));
		item.setMentalstate(form.getFirstValue(Patient.MENTALSTATE, true,
				"NULL"));
		
		db = new DatabaseResource(FirstResourceApplication.couchdburl,
				FirstResourceApplication.databasename);
		if (db.getItem(itemId) != null) {
			db.updateItem(item, itemId);
		} else {
			
			if ((itemId = db.createItem(item, itemId)) != null) {
				
				// Set the response's status and entity
				setStatus(Status.SUCCESS_CREATED);
				Representation rep = new StringRepresentation("Item created",
						MediaType.TEXT_PLAIN);
				// Indicates where is located the new resource.
				rep.setLocationRef(getRequest().getResourceRef()
						.getIdentifier()
						+ "/" + itemId);
				result = rep;
			} else { // Item is already registered.
				setStatus(Status.CLIENT_ERROR_NOT_FOUND);
				result = generateErrorRepresentation("Item " + itemId
						+ " PUT error.", "1");
			}
		}
		return result;
		
	}
	
	/*
	 * Handle GET requests
	 */

	/**
	 * @return Returns the XML representation of this document.
	 */
	@Get
	public JSONObject getItem() {
		DatabaseResource db = new DatabaseResource(
				FirstResourceApplication.couchdburl,
				FirstResourceApplication.databasename);
		return db.getItem(itemId);
	}
	
	/**
	 * Handle POST requests: create a new item. TODO update item
	 * 
	 * @param entity
	 * @return Representation
	 * @throws IOException
	 */
	@Post
	public Representation updateItem(Representation entity) throws IOException {
		
		Representation result = null;
		Patient item = new Patient();
		Form form = new Form(entity);
		DatabaseResource db = new DatabaseResource(
				FirstResourceApplication.couchdburl,
				FirstResourceApplication.databasename);
		JSONObject tempJson = db.getItem(itemId);
		
		item.setName(form.getFirstValue(Patient.NAME, true, tempJson
				.optString(Patient.NAME)));
		item.setDescription(form.getFirstValue(Patient.DESCRIPTION, true,
				tempJson.optString(Patient.DESCRIPTION)));
		item.setCardiacstate(form.getFirstValue(Patient.CARDIACSTATE, true,
				tempJson.optString(Patient.CARDIACSTATE)));
		item.setRespiratorystate(form.getFirstValue(Patient.RESPIRATORYSTATE,
				true, tempJson.optString(Patient.RESPIRATORYSTATE)));
		item.setMentalstate(form.getFirstValue(Patient.MENTALSTATE, true,
				tempJson.optString(Patient.MENTALSTATE)));
		
		if ((db.updateItem(item, itemId))) {
			
			// Set the response's status and entity
			setStatus(Status.SUCCESS_CREATED);
			Representation rep = new StringRepresentation("Item updated!",
					MediaType.TEXT_PLAIN);
			// Indicates where is located the new resource.
			rep.setLocationRef(getRequest().getResourceRef().getIdentifier()
					+ "/" + itemId);
			result = rep;
		} else { // Item is already registered.
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			result = generateErrorRepresentation("Item " + itemId
					+ " update failure :(.", "1");
		}
		
		return result;
	}
	
	/**
	 * Generate an XML representation of an error response.
	 * 
	 * @param errorMessage
	 *            the error message.
	 * @param errorCode
	 *            the error code.
	 */
	private Representation generateErrorRepresentation(String errorMessage,
			String errorCode) {
		DomRepresentation result = null;
		// This is an error
		// Generate the output representation
		try {
			result = new DomRepresentation(MediaType.TEXT_XML);
			// Generate a DOM document representing the list of
			// items.
			Document d = result.getDocument();
			
			Element eltError = d.createElement("error");
			
			Element eltCode = d.createElement("code");
			eltCode.appendChild(d.createTextNode(errorCode));
			eltError.appendChild(eltCode);
			
			Element eltMessage = d.createElement("message");
			eltMessage.appendChild(d.createTextNode(errorMessage));
			eltError.appendChild(eltMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
}
