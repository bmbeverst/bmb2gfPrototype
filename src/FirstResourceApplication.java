//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentMap;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

/**
 * @author brooks
 * Mar 24, 2011
 */
public class FirstResourceApplication extends Application {

	static final String couchdburl = "http://plato.cs.virginia.edu:5984/";
    static final String databasename = "bmb2gf";
	
   /**
     * Creates a root Restlet that will receive all incoming calls.
     */
    @Override
    public Restlet createInboundRoot() {
        // Create a router Restlet that defines routes.
        Router router = new Router(getContext());

        // Defines a route for the resource "list of items"
        router.attach("/items", PatientsResources.class);
        // Defines a route for the resource "item"
        router.attach("/items/{itemId}", PatientResource.class);

        return router;
    }

}
