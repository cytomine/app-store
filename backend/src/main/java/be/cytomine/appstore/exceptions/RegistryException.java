package be.cytomine.appstore.exceptions;

public class RegistryException extends Exception {

    public RegistryException(Exception e) {
        super(e);
    }

    public RegistryException(String message) {
        super(message);
    }
}
