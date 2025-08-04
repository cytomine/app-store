package be.cytomine.appstore.handlers.registry.impl;

import be.cytomine.appstore.dto.handlers.registry.DockerImage;
import be.cytomine.appstore.exceptions.RegistryException;
import be.cytomine.appstore.handlers.RegistryHandler;

public class DefaultRegistryHandler implements RegistryHandler {
    @Override
    public boolean checkImage(DockerImage image) throws RegistryException {
        // TODO is the check actually a possible outcome of <pushImage> operation?
        return false;
    }

    @Override
    public void pushImage(DockerImage image) throws RegistryException {

    }
}
