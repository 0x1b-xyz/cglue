package cglue;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 *
 * @author jstiefel
 */
public class Main {

//    public static final String GO_HOME_SYS = "go.home";
//    public static final String GO_HOME_ENV = "GO_HOME";

    private final ClassLoader goCl;
    private final File home;

    /**
     * CLI that will extract the common and ops classloaders using {@link #createClassLoader(java.io.File, String, ClassLoader)}.
     * Then we delegate to {@link #execute(String[])}. Calls {@link System#exit(int)} with the result of that
     * call.
     *
     * @see #execute(String[])
     */
    public static void main(String[] args) throws Exception {

        File home = getHome();
        ClassLoader commonCl = createClassLoader(home, "common", null);
        ClassLoader goCl = createClassLoader(home, "cglue", commonCl);
        Main main = new Main(home, goCl);
        System.exit(main.execute(args));

    }

    /**
     * The {@link ClassLoader} to load our {@link code.go.rt.Go} instance from.
     */
    public Main(File home, ClassLoader classLoader) throws Exception {
        this.home = home;
        this.goCl = classLoader;
    }

    /**
     * Delegates to {@link code.go.rt.Go#execute(String[])}.  Returns the corresponding {@link code.go.rt.State#code}.
     */
    public int execute(String[] args) throws Exception {
        ClassLoader originalCl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.goCl);
        try {
            Class<?> goClass = this.goCl.loadClass("code.go.rt.Go");
            File docBase = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            Object go = goClass.getConstructor(File.class, File.class).newInstance(home, docBase);
            return (Integer)goClass.getMethod("execute", new Class[]{String[].class}).invoke(go, new Object[]{args});
        } finally {
            Thread.currentThread().setContextClassLoader(originalCl);
        }
    }

    /**
     * <li>Look for {@code go.home} system property</li>
     * <li>Look for {@code GO_HOME} environment variable</li>
     * <li>Default to {@code ./go}</li>
     */
    public static File getHome() {

        File home;

        if (System.getProperty(GO_HOME_SYS) != null)
            home = new File(System.getProperty(GO_HOME_SYS));
        else if (System.getenv(GO_HOME_ENV) != null)
            home = new File(System.getenv(GO_HOME_ENV));
        else
            home = new File(System.getProperty("user.dir"), "/cglue");

        if (!home.isDirectory()) {
            if (!home.mkdirs())
                throw new IllegalStateException("Could not create go.home: " + home.getAbsolutePath());
        }

        return home;

    }

    /**
     * Loads {@code /META-INF/code/go/go.properties} from the classloader.
     */
    public static Properties getProperties() throws IOException {
        InputStream propsRes = Main.class.getClassLoader().getResourceAsStream("META-INF/go/go.properties");
        if (propsRes == null)
            throw new IllegalStateException("Could not locate go.properties");
        Properties props = new Properties();
        props.load(propsRes);
        return props;
    }

    /**
     * Indicates that a {@code go.debug} system property exists.
     */
    public static boolean isDebug() {
        return System.getProperty("go.debug") != null;
    }

    private static void debug(String msg) {
        if (isDebug())
            System.out.println(msg);
    }

    /**
     * Creates a {@link ClassLoader} for the specified name by lazily extracting the jars from the
     * {@code META-INF/code/go/libs/_NAME_/} location into the {@code {@link #getHome()}/extract/_GO.VERSION_/_NAME_}}
     * location.
     */
    public static ClassLoader createClassLoader(File home, String name, ClassLoader parent) throws IOException {

        CodeSource src = Main.class.getProtectionDomain().getCodeSource();
        if (src == null) {
            if (isDebug())
                System.out.println("Not extracting when CodeSource is null");
            return new URLClassLoader(new URL[]{}, parent);
        }

        File expanded = new File(home, "/work/go/" + getProperties().get("go.version") + "/" + name);
        debug("Creating the " + name + " class loader in " + expanded.getAbsolutePath());

        List<ZipEntry> libs = new LinkedList<ZipEntry>();
        ZipFile zip = new ZipFile(new File(src.getLocation().getFile()));
        Enumeration<? extends ZipEntry> entries = zip.entries();
        for (ZipEntry ze = null; entries.hasMoreElements(); ) {
            ze = entries.nextElement();
            if (ze.getName().startsWith("META-INF/go/lib/" + name) && ze.getName().endsWith(".jar")) {
                debug("Adding " + ze.getName() + " to the " + name + " classloader");
                libs.add(ze);
            }
        }

        if (!expanded.exists() && !expanded.mkdirs())
            throw new FileNotFoundException(expanded.getAbsolutePath());

        List<URL> urls = new LinkedList<URL>();
        for (ZipEntry ze : libs) {

            File target = new File(expanded, new File(ze.getName()).getName());
            urls.add(target.toURI().toURL());

            if (target.exists()) {
                debug("Library " + target.getAbsolutePath() + " has been extracted already");
                continue;
            }

            OutputStream os = null;
            InputStream is = null;
            try {

                debug("Copying " + ze.getName() + " into " + target.getAbsolutePath());

                is = zip.getInputStream(ze);
                os = new FileOutputStream(target);
                copy(is, os);

            } finally {
                if (os != null)
                    os.close();
                if (is != null)
                    is.close();
            }

        }

        return new URLClassLoader(urls.toArray(new URL[urls.size()]), parent);

    }

    /**
     * Copies the input into the output, leaving both streams open.
     */
    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[1024];
        while (true) {
            int len = in.read(buf);
            if (len < 0) break;
            out.write(buf, 0, len);
        }
    }

}
