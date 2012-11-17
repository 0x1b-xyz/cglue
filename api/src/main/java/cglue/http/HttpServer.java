package cglue.http;

import cglue.Server;

import java.util.Map;

/**
 * @author jstiefel
 */
public interface HttpServer extends Server {

    void addResource(String name, String auth, String type, String factory, Map<String, String> properties);

}
