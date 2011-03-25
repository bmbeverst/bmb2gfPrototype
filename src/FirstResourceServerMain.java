import org.restlet.Component;
import org.restlet.data.Protocol;

/**
 * @author brooks
 * Mar 24, 2011
 */
public class FirstResourceServerMain {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // Create a new Component.
        Component component = new Component();

        // Add a new HTTP server listening on port 8111.
        component.getServers().add(Protocol.HTTP, 8111);
        
        component.getClients().add(Protocol.HTTP);
        
        component.getDefaultHost().attach("/firstResource",
                new FirstResourceApplication());

        // Start the component.
        component.start();
    }

}
