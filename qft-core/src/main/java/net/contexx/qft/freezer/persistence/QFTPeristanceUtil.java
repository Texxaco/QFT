package net.contexx.qft.freezer.persistence;

import net.contexx.qft.freezer.persistence.json.JsonPersistence;
import net.contexx.qft.freezer.persistence.zip.ZipPersistence;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class QFTPeristanceUtil {
    private static final Map<String, FreezerPersistence> implementations = new HashMap<>();
    static {
        implementations.put("json", new JsonPersistence());
        implementations.put("ziped", new ZipPersistence());
    }

    public static Optional<FreezerPersistence> getPersistance(String name) {
        return Optional.ofNullable(implementations.get(name));
    }

    public static String knownPersistanceTypes() {
        return String.join(", ", implementations.keySet().stream().sorted().collect(Collectors.toList()));
    }
}
