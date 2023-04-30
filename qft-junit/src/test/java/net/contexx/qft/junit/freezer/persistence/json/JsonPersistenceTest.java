package net.contexx.qft.junit.freezer.persistence.json;

import com.google.common.jimfs.Jimfs;
import net.contexx.qft.freezer.persistence.json.JsonPersistence;
import net.contexx.qft.junit.QFT;
import net.contexx.qft.annotations.QFTInMemory;
import net.contexx.qft.freezer.model.Freezer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.contexx.qft.junit.assertions.QFTAsserts.assertEquals;

@ExtendWith(QFT.class)
@QFTInMemory(fresh = true)
public class JsonPersistenceTest {

    private static final FileSystem fs = Jimfs.newFileSystem();

    @Test
    void save() throws IOException, URISyntaxException {
        assertEquals("a String", "Sample Data");
        assertEquals("a Integer", 12345);
        assertEquals("a Boolean", true);
        assertEquals("a Byte", 256);
        assertEquals("a Short", 256*128);
        assertEquals("a Long", Long.MAX_VALUE);
        assertEquals("a Float", 0.1234f);
        assertEquals("a Double", 128.12398712874d);
        assertEquals("a Character", 'C');
        assertEquals("a String with\tescapes", 'C');
        assertEquals("a JSON content", "{\"version\":1,\"origin\":\"Foobar\",\"identifier\":\"00000000-0000-0000-0000-000000000001\",\"templates\":[{\"name\":\"Template1\",\"identifier\":\"00000000-0000-0000-0000-000000000002\",\"destination\":[{\"name\":\"TestDestination\"}]},{\"name\":\"Template2\",\"identifier\":\"00000000-0000-0000-0000-000000000003\",\"destination\":[{\"name\":\"TestDestination\"}]},{\"name\":\"Template3\",\"identifier\":\"00000000-0000-0000-0000-000000000004\",\"destination\":[{\"name\":\"TestDestination\"}]}]}");


        final JsonPersistence jsonPersistence = new JsonPersistence();


        final Path saveFile = fs.getPath("jsontest.qft.json");
        jsonPersistence.save(
                QFT.getInMemoryFreezer(),
                saveFile
        );

        System.out.println(Files.readString(saveFile));

        final Freezer readFreezer = jsonPersistence.read(saveFile);

        Assertions.assertEquals(QFT.getInMemoryFreezer(), readFreezer);
    }
}
