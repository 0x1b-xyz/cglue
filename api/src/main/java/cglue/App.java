package cglue;

/**
 * @author jstiefel
 */
public interface App {

    Server initialize();

    void starting();

    void stopped();

}
