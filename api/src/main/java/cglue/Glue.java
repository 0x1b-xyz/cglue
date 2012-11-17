package cglue;

/**
 * Glue binds one or more {@link App}s into a single manageable interface that can be interrogated for
 * {@link State}, {@link #start()}ed and {@link #stop()}ped.
 *
 * @author jstiefel
 */
public interface Glue {

    /**
     * Returns the {@link Artifact} for the {@link App} that GroovyOps is operating upon. This filesystem
     * abstraction could change for various {@link State}s - for example a war {@link Artifact} might be
     * packed during the call to {@link cglue.App#initialize()} but unpacked for deployment during
     * {@link cglue.App#starting()}.
     */
    Artifact getArtifact();

    /**
     * Returns the {@link State} of GroovyOps overall - this could be a summarization of multiple
     * {@link Server} instances but as a best practice should indicating the state of "{@code the}"
     * server.
     */
    State getState();

    void start();

    void stop();

}
