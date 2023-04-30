package net.contexx.qft.freezer.persistence.json;

import net.contexx.qft.freezer.model.Content;
import net.contexx.qft.freezer.model.Directory;
import net.contexx.qft.freezer.model.Freezer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class JsonPersistenceTest {

    @Test
    void readAcceptedChange() throws URISyntaxException, IOException {
        //given
        final JsonPersistence jsonPersistence = new JsonPersistence();
        final URL resource = this.getClass().getClassLoader().getResource("JsonPersistanceTest.readAcceptedChange.qft.json");
        assertNotNull(resource, "Unable to find necessary ressource: JsonPersistanceTest.readAcceptedChange.qft.json");

        //when
        final Freezer freezer = jsonPersistence.read(Paths.get(resource.toURI()));

        //then
        final Directory dir = freezer.locate("foo/bar");
        assertNotNull(dir);

        final Content accContent = dir.getContent("ACC");
        assertNotNull(accContent);
        assertEquals("y", accContent.getType().toString(accContent.getData()));
        assertNull(accContent.getFuture());

        final Content naccContent = dir.getContent("NACC");
        assertNotNull(naccContent);
        assertEquals("a", naccContent.getType().toString(naccContent.getData()));
        assertEquals("b", naccContent.getType().toString(naccContent.getFuture()));

        final Content noFutureContent = dir.getContent("NoFuture");
        assertNotNull(noFutureContent);
        assertEquals("z", noFutureContent.getType().toString(noFutureContent.getData()));
        assertNull(noFutureContent.getFuture());
    }
}