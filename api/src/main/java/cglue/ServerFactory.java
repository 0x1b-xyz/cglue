package cglue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * A factory for {@link Server} instances that have a single {@link Glue} argument constructor.
 *
 * @author jstiefel
 */
public class ServerFactory {

    /**
     * Creates a new {@link Server} instance for the specified fully qualified classname.
     *
     * @throws NoSuchMethodException When no {@code Server(Glue)} constructor is found
     */
    <T extends Server> T newInstance(Glue glue, Class<T> serverType, String className)
            throws ClassNotFoundException, NoSuchMethodException, InstantiationException {

        Class<? extends Server> serverClazz = (Class<? extends Server>)Class.forName(className);
        Constructor constructor = serverClazz.getConstructor(Glue.class);
        T server = null;
        try {
            server = (T)constructor.newInstance(glue);
        } catch (IllegalAccessException e) {
            throw new InstantiationException("Could not instantiate " + className + " due to: " + e);
        } catch (InvocationTargetException e) {
            throw new InstantiationException("Could not instantiate " + className + " due to: " + e);
        }

        return server;

    }

}
