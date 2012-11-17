package cglue;

/**
 * Server is an abstraction of an internal or external process.
 *
 * @author jstiefel
 */
public interface Server {

    /**
     * Indicates the condition of the server process.
     */
    State getState();

}
