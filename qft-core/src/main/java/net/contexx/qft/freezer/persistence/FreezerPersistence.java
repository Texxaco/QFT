package net.contexx.qft.freezer.persistence;

import net.contexx.qft.freezer.model.Freezer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

public interface FreezerPersistence {
    void save(Freezer freezer, Path file) throws IOException, URISyntaxException;

    Freezer read(Path file) throws URISyntaxException, IOException;
}
