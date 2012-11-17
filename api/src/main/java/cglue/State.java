package cglue;

/**
 * Equivalent to a {@link System#exit(int)} code that represents the state of a {@link Server} where a {@link #code}
 * of {@code &lt; 0} (starting with {@link #UNKNOWN}.)
 *
 * @author jstiefel
 */
public enum State {

    /**
     * Indicates an abstract error condition.
     */
    UNKNOWN(-1),

    /**
     * {@link Server} is in an active state
     */
    STOPPED(0),

    /**
     * {@link Server} is in the process of starting
     */
    STARTING(1),

    /**
     * {@link Server} is in a "running" state where the application has indicated a successful deployment
     */
    RUNNING(2);

    final int code;

    private State(int code) {
        this.code = code;
    }

}
