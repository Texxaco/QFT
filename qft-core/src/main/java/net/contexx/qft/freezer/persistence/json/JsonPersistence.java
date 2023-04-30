package net.contexx.qft.freezer.persistence.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.contexx.qft.freezer.model.Content;
import net.contexx.qft.freezer.model.Datatypes;
import net.contexx.qft.freezer.model.Directory;
import net.contexx.qft.freezer.model.Freezer;
import net.contexx.qft.freezer.persistence.FreezerPersistence;

import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static net.contexx.qft.freezer.model.Datatypes.*;

@SuppressWarnings("SuspiciousIndentAfterControlStatement")
public class JsonPersistence implements FreezerPersistence {

    private static final Base64.Encoder base64 = Base64.getEncoder();
    private static final Base64.Decoder base64Decoder = Base64.getDecoder();

    //_________________________________________________________________________
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    // read

    @Override
    public Freezer read(Path file) throws URISyntaxException, IOException {
        final Freezer freezer = new Freezer();

        if(!Files.exists(file) || Files.size(file) == 0) {
            save(freezer, file);
        }

        final ObjectMapper mapper = new ObjectMapper();

        @SuppressWarnings("unchecked")
        final Map<String, ?> root = (Map<String, ?>)mapper.readValue(Files.newBufferedReader(file, UTF_8), Map.class);
        final Map<String, ?> meta = getElement(root, "meta");
        final String engine = getValue(meta, "engine");

        this.<Map<String, Object>>getList(root, "contents").forEach(contentMap -> {
            final String path = (String)contentMap.get("path");
            final int delimIndex = path.lastIndexOf(Directory.DELIMITER);
            if(delimIndex == -1) return; //todo logging

            final String name = path.substring(delimIndex+1);
            final String dirPath = path.substring(0, delimIndex);

            final Directory directory = freezer.computeIfAbsent(dirPath);
            final Content content = directory.addContent(name);
            content.setType(Datatypes.findByName((String)contentMap.get("type")).orElseThrow(() -> new NoSuchElementException("Unknown Datatype: "+contentMap.get("type"))));
            if(contentMap.containsKey("accepted")&&(Boolean)contentMap.get("accepted")) {
                content.setData(decode(content.getType(), (String)contentMap.get("future")));
                content.setFuture(null);
            } else {
                content.setData(decode(content.getType(), (String)contentMap.get("data")));
                content.setFuture(decode(content.getType(), (String)contentMap.get("future")));
            }
            content.setComment((String)contentMap.get("comment"));
        });

        return freezer;
    }

    @SuppressWarnings("unchecked")
    private Map<String, ?> getElement(Map<String, ?> parent, String key) {
        return (Map<String, ?>)parent.get(key);
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> getList(Map<String, ?> parent, String key) {
        return (List<T>)parent.get(key);
    }

    @SuppressWarnings("unchecked")
    private <T> T getValue(Map<String, ?> parent, String key) {
        return (T)parent.get(key);
    }
    //_________________________________________________________________________
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    // save

    @Override
    public void save(Freezer freezer, Path file) throws IOException, URISyntaxException {
        final StringBuilder json = new StringBuilder();

        json.append("{\n" +
                "  \"meta\": {\n" +
                "    \"engine\": \"junit-jupiter\"\n" +
                "  },\n" +
                "  \"contents\": [\n");

        final List<Content> contents = new ArrayList<>();
        final Queue<Directory> workQueue = new LinkedList<>(freezer.getDirectorys());
        for(Directory dir=workQueue.poll();dir!=null;dir=workQueue.poll()) {
            workQueue.addAll(dir.getDirectorys());
            contents.addAll(dir.getContentElements());
        }

        final Map<String, Content> pathedContents = contents.parallelStream().collect(Collectors.toMap(content -> content.toPath(), content -> content));

        json.append(String.join(",\n", pathedContents.keySet().stream().sorted().map(path -> {
            final Content content = pathedContents.get(path);
            final StringBuilder result = new StringBuilder();

            result.append("    ").append("{\"path\":\"").append(escape(path)).append("\",")
                  .append("\"type\":\"").append(content.getType().getName()).append("\",");

            if (content.getFuture() != null) {
                result.append("\"accepted\":false,");
                result.append("\"future\":\"").append(encode(content.getType(), content.getFuture())).append("\",");
            }

            if (content.getComment() != null)
            result.append("\"comment\":\"").append(escape(content.getComment())).append("\",");

            result.append("\"data\":\"").append(encode(content.getType(), content.getData())).append("\"}");

            return result.toString();
        }).collect(Collectors.toList())));

        json.append("\n  ]\n" +
                "}");

        Files.writeString(file, json.toString(), UTF_8, CREATE, TRUNCATE_EXISTING);
    }

    //_________________________________________________________________________
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    // encode & decode

    private static String escape(String input) {
        final StringBuilder result = new StringBuilder(input.length());
        for (char c : input.toCharArray()) {
            switch (c) {
                case '\b': result.append("\\b");  break;
                case '\f': result.append("\\f");  break;
                case '\n': result.append("\\n");  break;
                case '\r': result.append("\\r");  break;
                case '\t': result.append("\\t");  break;
                case '\"': result.append("\\\""); break;
                case '\\': result.append("\\\\"); break;
                default: result.append(c);
            }
        }
        return result.toString();
    }

    private static Map<Datatypes.Datatype<?>, Function<String, byte[]>> unencodedDataTypes = new HashMap<>();
    static {
        unencodedDataTypes.put(STRING,  s -> STRING.toBytes(s));
        unencodedDataTypes.put(INTEGER, s -> INTEGER.toBytes(Integer.valueOf(s)));
        unencodedDataTypes.put(LONG,    s -> LONG.toBytes(Long.valueOf(s)));
        unencodedDataTypes.put(BOOLEAN, s -> BOOLEAN.toBytes(Boolean.valueOf(s)));
    }
    private static String encode(Datatypes.Datatype type, byte[] content) {
        if(unencodedDataTypes.containsKey(type)) {
            return escape(type.toString(content));
        }
        return base64.encodeToString(content);
    }

    private static byte[] decode(Datatypes.Datatype type, String jsonValue) {
        if(jsonValue == null) return null;

        if(unencodedDataTypes.containsKey(type)) {
            return unencodedDataTypes.get(type).apply(jsonValue);
        }
        return base64Decoder.decode(jsonValue);
    }
}
