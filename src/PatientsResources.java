import java.io.IOException;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Resource that manages a list of items.
 */
public class PatientsResources extends ServerResource {
	
	/**
	 * Handle POST requests: create a new item.
	 * 
	 * @param entity
	 * @return Representation
	 * @throws IOException
	 */
	@Post
	public Representation postItem(Representation entity) throws IOException {
		
		Representation result = null;
		Patient item = new Patient();
		Form form = new Form(entity);
		item.setName(form.getFirstValue(Patient.NAME));
		item.setDescription(form.getFirstValue(Patient.DESCRIPTION));
		item.setCardiacstate(form.getFirstValue(Patient.CARDIACSTATE));
		item.setRespiratorystate(form.getFirstValue(Patient.RESPIRATORYSTATE));
		item.setMentalstate(form.getFirstValue(Patient.MENTALSTATE));
		
		String itemId = null;
		DatabaseResource db = new DatabaseResource(
				FirstResourceApplication.couchdburl,
				FirstResourceApplication.databasename);
		if ((itemId = db.createItem(item)) != null) {
			
			// Set the response's status and entity
			setStatus(Status.SUCCESS_CREATED);
			Representation rep = new StringRepresentation("Item created",
					MediaType.TEXT_PLAIN);
			// Indicates where is located the new resource.
			rep.setLocationRef(getRequest().getResourceRef().getIdentifier()
					+ "/" + itemId);
			result = rep;
		} else { // Item is already registered.
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			result = generateErrorRepresentation("Item " + itemId
					+ " already exists.", "1");
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
	
	/**
	 * Returns a listing of all registered items.
	 */
	/**
	 * @return Representation XML
	 */
	@Get("xml")
	public Representation toXml() {
		// Generate the right representation according to its media type.
		
		DatabaseResource db = new DatabaseResource(
				FirstResourceApplication.couchdburl,
				FirstResourceApplication.databasename);
		Representation rep = db.getAllItems();
		
		return rep;
		
	}
	
}
