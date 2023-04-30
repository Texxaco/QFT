package net.contexx.qft.junit;

import net.contexx.qft.annotations.Accept;
import net.contexx.qft.annotations.QFTInMemory;
import net.contexx.qft.freezer.model.Content;
import net.contexx.qft.freezer.model.Datatypes;
import net.contexx.qft.freezer.model.Directory;
import net.contexx.qft.freezer.model.Freezer;
import net.contexx.qft.freezer.persistence.FreezerPersistence;
import net.contexx.qft.freezer.persistence.QFTPeristanceUtil;
import net.contexx.qft.freezer.persistence.json.JsonPersistence;
import net.contexx.qft.freezer.persistence.zip.ZipPersistence;
import net.contexx.qft.settings.QFTSettings;
import org.junit.jupiter.api.extension.*;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;


public class QFT implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback /*, InvocationInterceptor*/ {
//    protected static final String FREEZER_FILE = "qft.freezer.file";

    private static Freezer inMemoryFreezer = new Freezer();
    private static final ThreadLocal<Freezer> threadLocalInMemoryFreezer = ThreadLocal.withInitial(() -> inMemoryFreezer);

    private static final ExtensionContext.Namespace QFT_NAMESPACE = ExtensionContext.Namespace.create("net", "contexx", "QFT");

    public static final ThreadLocal<Directory> directory = new ThreadLocal<>();
    public static final ThreadLocal<Boolean> accapted = new ThreadLocal<>();

    /**
     * For internal testing purposes only. Returns a Freezer that is never been saved.
     * Warning: There is only one in-memory freezer per JVM. So there could be more content from other test cases than
     * expected.
     * @return the in-memory freezer
     */
    public static Freezer getInMemoryFreezer() {
        return threadLocalInMemoryFreezer.get();
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        getFreezer(context);
    }

    private Freezer getFreezer(ExtensionContext context) throws Exception {
        return context.getStore(QFT_NAMESPACE).getOrComputeIfAbsent("freezer", s -> {
            try {
                final Path freezerPath = getFreezerPath(context);

                Freezer result = getFreezerPersistence(context).read(
                        freezerPath
                );
                final Content junit = result.addContent("junit");
                junit.setType(Datatypes.BOOLEAN);
                junit.setData(Datatypes.BOOLEAN.toBytes(true));

                return result;
            } catch (URISyntaxException | IOException e) {
                throw new RuntimeException(e);
            }
        }, Freezer.class);
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        final Freezer freezerToUse;
        if (hasAnnotation(context, QFTInMemory.class)) {
            if(getAnnotation(context, QFTInMemory.class).fresh()) {
                threadLocalInMemoryFreezer.set(new Freezer());
            }
            freezerToUse = threadLocalInMemoryFreezer.get();
        }
        else freezerToUse = getFreezer(context);

        directory.set(freezerToUse.computeIfAbsent(
                context.getTestClass().map(Class::getName).orElse("_solo")
        ).computeIfAbsent(
                context.getUniqueId()
        ));

        accapted.set(hasAnnotation(context, Accept.class) || QFTSettings.getProperty(QFTSettings.ACCEPT_ALL, key -> context.getConfigurationParameter(key).orElse(null)));
    }

    private static boolean hasAnnotation(ExtensionContext context, Class<? extends Annotation> annotationClass) {
        return context.getTestMethod().map(method -> method.getAnnotation(annotationClass) != null).orElse(false)
               || context.getTestClass().map(clazz -> clazz.getAnnotation(annotationClass)!= null).orElseThrow(() -> new RuntimeException("QFT: Method of using JUnit not implemented yet."));
    }

    private static <A extends Annotation> A getAnnotation(ExtensionContext context, Class<A> annotationClass) {
        final A result = context.getTestMethod().map(method -> method.getAnnotation(annotationClass)).orElse(null);
        if(result != null) return result;
        return context.getTestClass().map(clazz -> clazz.getAnnotation(annotationClass)).orElseThrow(() -> new RuntimeException("QFT: Method of using JUnit not implemented yet."));
    }

    @Override
    public void afterEach(ExtensionContext context) {
        directory.set(null);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        if(!hasAnnotation(context, QFTInMemory.class)) {

            final String fileType = QFTSettings.getProperty(QFTSettings.FREEZER_FILE_TYPE, key -> context.getConfigurationParameter(key).orElse(null));

            QFTPeristanceUtil.getPersistance(fileType).orElseThrow(() -> new RuntimeException("Unknown persistance type: '"+fileType+"'. Known types are "+QFTPeristanceUtil.knownPersistanceTypes())).save(
                    getFreezer(context),
                    getFreezerPath(context)
            );
        }
    }

    private Path getFreezerPath(ExtensionContext context) throws URISyntaxException {
        final String freezerFileName = QFTSettings.getProperty(QFTSettings.FREEZER_FILE_NAME, key -> context.getConfigurationParameter(key).orElse(null));

        final URL resource = this.getClass().getClassLoader().getResource(freezerFileName);
        if (resource == null)
            throw new RuntimeException("You need to create a (empty) " + freezerFileName + "-file in classpath. Example in '<project root>/src/main/resources/" + freezerFileName + "'. Or you specify a file through system property (" + QFTSettings.PREFIX + QFTSettings.FREEZER_FILE_NAME + "), 'qft.properties' file or junit platform configuration.");

        //correct maven and gradle build behavior
        final String path = resource.toString();
        try {
            final String fileName = getFileName(freezerFileName);
            final String finalPath = path.replace("/build/resources/test/"+fileName, "/src/test/resources/"+fileName)
                                         .replace("/target/test-classes/"+fileName, "/src/test/resources/"+fileName);
            final Path newPath = Path.of(new URL(finalPath).toURI());
            if (Files.isReadable(newPath)) {
                return newPath;
            }
        } catch (MalformedURLException e) {
        }

        return Path.of(resource.toURI());
    }

    private String getFileName(String path) {
        if(path.contains(File.pathSeparator)) {
            return path.substring(path.lastIndexOf(File.pathSeparator)+1);
        } else {
            return path;
        }
    }

    private FreezerPersistence getFreezerPersistence(ExtensionContext context) {
        final String freezerType = QFTSettings.getProperty(QFTSettings.FREEZER_FILE_TYPE, key -> context.getConfigurationParameter(key).orElse(null));
        switch(freezerType) {
            case "json": return new JsonPersistence();
            case "zip": return  new ZipPersistence();
            default: throw new RuntimeException("Unknown persistence type '"+freezerType+"'.");
        }
    }
}
