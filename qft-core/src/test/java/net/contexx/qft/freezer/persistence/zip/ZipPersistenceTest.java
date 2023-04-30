package net.contexx.qft.freezer.persistence.zip;

import net.contexx.qft.freezer.model.Freezer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class ZipPersistenceTest {

    @Test
    void save() throws Exception {
        new ZipPersistence().save(new Freezer(), Paths.get("ziptest.zip"));
    }

    @Disabled("TODO: Der Test löuft ewig, warum?")
    @Test
    void read() throws Exception {
        new ZipPersistence().read(Paths.get("C:\\Users\\texx\\Downloads\\hub-plugin.zip"));
    }
}