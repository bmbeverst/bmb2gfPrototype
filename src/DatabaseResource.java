import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Client;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/*
 * This class handles all the CRUD requests from the server.
 */

/**
 * @author brooks
 * Mar 25, 2011
 */
public class DatabaseResource {
	
	private final String couchdburl;
	ClientResource dbresource;
	Representation rep;
	JsonRepresentation jrep;
	JSONObject jobj;
	String databasename;
	
	/**
	 * @param url
	 * @param databasename
	 */
	public DatabaseResource(String url, String databasename) {
		couchdburl = url;
		dbresource = new ClientResource(couchdburl);
		this.databasename = databasename;
		dbresource.setNext(new Client(Protocol.HTTP));
	}
	
	/*
	 * Creating a new item in the database. The current implementation maintains
	 * a document with "_id" = "counter" and "value" = "current_value"}.
	 * Whenever a new document is created its "_id" is set to current_value + 1
	 * and the "value" of document with "_id" = "counter" is also incremented by
	 * 1.
	 */

	/**
	 * @param item
	 * @return String
	 */
	public String createItem(Patient item) {
		System.out.println("database name " + databasename);
		JSONObject doc = item.getJSONObject();
		JSONObject tempobj = new JSONObject();
		Long value;
		
		try {
			
			// get the value of counter to generate id for the new item
			dbresource
					.setReference(couchdburl + databasename + "/" + "counter");
			System.out.println("Counter resourse"
					+ dbresource.getReference().toString());
			
			rep = dbresource.get();
			jrep = new JsonRepresentation(rep);
			tempobj = jrep.getJsonObject();
			value = new Long(Long.parseLong(tempobj.getString("value")));
			value++;
			System.out.println("Counter value " + value.toString());
			tempobj.put("value", value.toString());
			
			// This is required to prevent the server from timing out while
			// servicing requests.
			// This only happens occasionally.
			dbresource.setNext(new Client(Protocol.HTTP));
			
			// once you have the id value you can create a new item
			dbresource.setReference(couchdburl + databasename);
			doc.put("_id", value.toString());
			rep = dbresource.post(new JsonRepresentation(doc));
			jrep = new JsonRepresentation(rep);
			jobj = jrep.getJsonObject();
			
			dbresource.setNext(new Client(Protocol.HTTP));
			
			// item successfully created so update the counter value
			dbresource
					.setReference(couchdburl + databasename + "/" + "counter");
			rep = dbresource.put(new JsonRepresentation(tempobj));
			jrep = new JsonRepresentation(rep);
			jobj = jrep.getJsonObject();
			
			return value.toString();
			
		} catch (ResourceException e) {
			e.printStackTrace();
			// code 409 means document already exists
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * @param item
	 * @param itemId
	 * @return itemID
	 */
	public String createItem(Patient item, String itemId) {
		System.out.println("database name " + databasename);
		JSONObject doc = item.getJSONObject();
		JSONObject tempobj = new JSONObject();
		
		try {
			
			// get the value of counter to generate id for the new item
			dbresource
					.setReference(couchdburl + databasename + "/" + "counter");
			System.out.println("Counter resourse"
					+ dbresource.getReference().toString());
			
			rep = dbresource.get();
			jrep = new JsonRepresentation(rep);
			tempobj = jrep.getJsonObject();
			System.out
					.println("creating item with id "
							+ itemId
							+ "\nYes, I know the terminal just barfed. But it works at least.");
			tempobj.put("value", itemId);
			
			// This is required to prevent the server from timing out while
			// servicing requests.
			// This only happens occasionally.
			dbresource.setNext(new Client(Protocol.HTTP));
			
			// once you have the id value you can create a new item
			dbresource.setReference(couchdburl + databasename);
			doc.put("_id", itemId);
			rep = dbresource.post(new JsonRepresentation(doc));
			jrep = new JsonRepresentation(rep);
			jobj = jrep.getJsonObject();
			
			dbresource.setNext(new Client(Protocol.HTTP));
			
			// item successfully created so update the counter value
			dbresource
					.setReference(couchdburl + databasename + "/" + "counter");
			rep = dbresource.put(new JsonRepresentation(tempobj));
			jrep = new JsonRepresentation(rep);
			jobj = jrep.getJsonObject();
			
			return itemId;
			
		} catch (ResourceException e) {
			e.printStackTrace();
			// code 409 means document already exists
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/*
	 * Delete an item with "_id" = id
	 */

	/**
	 * @param id
	 * @return boolean
	 */
	public boolean deleteItem(String id) {
		
		try {
			dbresource.setReference(couchdburl + databasename + "/" + id);
			rep = dbresource.get();
			jrep = new JsonRepresentation(rep);
			jobj = jrep.getJsonObject();
			String rev = jobj.getString("_rev");
			
			dbresource.setReference(couchdburl + databasename + "/" + id
					+ "?rev=" + rev);
			rep = dbresource.delete();
			return true;
			
		} catch (ResourceException e) {
			e.printStackTrace();
			// code 409 means document already exists
			if (e.getStatus().getCode() == 409) {
				return false;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	/*
	 * Update an existing item.
	 */

	/**
	 * @param item
	 * @param id
	 * @return boolean
	 */
	public boolean updateItem(Patient item, String id) {
		JSONObject doc = item.getJSONObject();
		try {
			dbresource.setReference(couchdburl + databasename + "/" + id);
			rep = dbresource.get();
			jrep = new JsonRepresentation(rep);
			jobj = jrep.getJsonObject();
			String rev = jobj.getString("_rev");
			dbresource.setReference(couchdburl + databasename + "/" + id
					+ "?rev=" + rev);
			doc.put("_rev", rev);
			rep = dbresource.put(new JsonRepresentation(doc));
			return true;
			
		} catch (ResourceException e) {
			e.printStackTrace();
			// code 409 means document already exists
			if (e.getStatus().getCode() == 409) {
				return false;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * get an item's representation
	 * 
	 * @param id
	 * @return JSONObject
	 */
	public JSONObject getItem(String id) {
		jobj = null;
		try {
			dbresource.setReference(couchdburl + databasename + "/" + id);
			rep = dbresource.get();
			jrep = new JsonRepresentation(rep);
			jobj = jrep.getJsonObject();
			
		} catch (ResourceException e) {
			e.printStackTrace();
			// code 409 means document already exists
			if (e.getStatus().getCode() == 409) {
				System.out
						.println("Why is there a problem when you get an item the already exists???");
				return null;
			}
			// Not Found (404) - Object Not Found
			if (e.getStatus().getCode() == 404) {
				System.out.println("The item " + id + " is not there");
				return null;
			}
			
		} catch (JSONException e) {
			System.out.println("Error getting JSON rep");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO? WHAT!");
			e.printStackTrace();
		}
		return jobj;
	}
	
	/*
	 * Get all the items (documents) in the database. The special document which
	 * stores the counter value is ignored and is not returned.
	 */

	/**
	 * @return Representation
	 */
	public Representation getAllItems() {
		
		try {
			dbresource.setReference(couchdburl + databasename + "/"
					+ "_all_docs");
			rep = dbresource.get();
			jrep = new JsonRepresentation(rep);
			jobj = jrep.getJsonObject();
			JSONArray row_array = (JSONArray) jobj.get("rows");
			int length = row_array.length();
			
			DomRepresentation representation = new DomRepresentation(
					MediaType.TEXT_XML);
			// Generate a DOM document representing the list of
			// items.
			Document d = representation.getDocument();
			Element r = d.createElement("items");
			d.appendChild(r);
			
			for (int i = 0; i < length; i++) {
				Element eltItem = d.createElement("item");
				JSONObject obj = getItem(row_array.getJSONObject(i).getString(
						"id"));
				
				String counter = "counter";
				if (obj.getString("_id").equals(counter)) {
					continue;
				}
				
				Element eltId = d.createElement("id");
				eltId.appendChild(d.createTextNode(obj.getString("_id")));
				eltItem.appendChild(eltId);
				
				Element eltName = d.createElement("name");
				eltName.appendChild(d.createTextNode(obj.getString("name")));
				eltItem.appendChild(eltName);
				
				Element eltDescription = d.createElement("description");
				eltDescription.appendChild(d.createTextNode(obj
						.getString("description")));
				eltItem.appendChild(eltDescription);
				
				Element eltCardiacstate = d.createElement("cardiacstate");
				eltCardiacstate.appendChild(d.createTextNode(jobj
						.getString("cardiacstate")));
				eltItem.appendChild(eltCardiacstate);
				
				Element eltrRespiratorystate = d
						.createElement("respiratorystate");
				eltrRespiratorystate.appendChild(d.createTextNode(jobj
						.getString("respiratorystate")));
				eltItem.appendChild(eltrRespiratorystate);
				
				Element eltMentalstate = d.createElement("mentalstate");
				eltMentalstate.appendChild(d.createTextNode(jobj
						.getString("mentalstate")));
				eltItem.appendChild(eltMentalstate);
				
				r.appendChild(eltItem);
			}
			d.normalizeDocument();
			
			// Returns the XML representation of this document.
			return representation;
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ResourceException e) {
			e.printStackTrace();
			// code 409 means document already exists
		}
		return null;
		
	}
	
}
