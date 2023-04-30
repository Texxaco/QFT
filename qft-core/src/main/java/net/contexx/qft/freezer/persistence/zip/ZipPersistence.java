package net.contexx.qft.freezer.persistence.zip;

import net.contexx.qft.freezer.model.Content;
import net.contexx.qft.freezer.model.Datatypes;
import net.contexx.qft.freezer.model.Directory;
import net.contexx.qft.freezer.model.Freezer;
import net.contexx.qft.freezer.persistence.FreezerPersistence;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.*;
import static net.contexx.qft.freezer.model.Datatypes.UNKNOWN;

public class ZipPersistence implements FreezerPersistence {

    private static final String TYPE_DATA = ".data";
    private static final String TYPE_DATATYPE = ".type";
    private static final String TYPE_COMMENT = ".comment";
    private static final String TYPE_FUTURE = ".future";

    @Override
    public void save(Freezer freezer, Path file) throws IOException, URISyntaxException {
        if(!Files.exists(file) || Files.size(file) == 0) {
            try(final InputStream resourceStream = this.getClass().getClassLoader().getResourceAsStream("initial/default-freezer.qft")) {
                Files.copy(resourceStream, file, REPLACE_EXISTING);
            }
        }

        final Map<String, String> env = new HashMap<>();
//        env.put("create", "true");

        StringBuilder uriString = new StringBuilder("jar:file://");
        file.toAbsolutePath().normalize().forEach(path -> uriString.append("/").append(path));

        URI uri = new URI(uriString.toString());

        System.out.println("Writing to file: "+uri.toASCIIString());

        try(FileSystem zip = FileSystems.newFileSystem(uri, env)) {
            listContent(freezer).parallelStream().forEach(content -> {
                System.out.println("Writing: "+content.toPath());
                Path parent = zip.getPath(content.toPath()).getParent();
                try {
                    Files.createDirectories(parent);

                    Files.write(parent.resolve(content.getName()+TYPE_DATA), content.getData(), CREATE, WRITE, TRUNCATE_EXISTING);

                    write(parent, content.getName(), TYPE_DATATYPE, content.getType(), c -> c.getName().getBytes(UTF_8), c -> c != null);
                    write(parent, content.getName(), TYPE_COMMENT,  content.getComment(), c -> c.getBytes(UTF_8), c -> !c.isEmpty());
                    write(parent, content.getName(), TYPE_FUTURE,   content.getFuture(),                          c -> c.length > 0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        synchronized (this) {
            try {
                this.wait(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Freezer read(Path file) throws URISyntaxException, IOException {
        Freezer freezer = new Freezer();

        if(!Files.exists(file) || Files.size(file) == 0) {
            System.out.println("Using empty Freezer.");
            return freezer;
        }

        final Map<String, String> env = new HashMap<>();
        env.put("create", "false");

        final StringBuilder uriString = new StringBuilder("jar:file://");
        file.toAbsolutePath().normalize().forEach(path -> uriString.append("/").append(path));

        final URI uri = new URI(uriString.append("!/").toString());

        try(FileSystem zip = FileSystems.newFileSystem(uri, env)) {

            final Deque<Path> queue = new ConcurrentLinkedDeque<>();
            zip.getRootDirectories().forEach(queue::addLast);

            while(!queue.isEmpty()) {
                Path path = queue.pop();
                if (Files.isDirectory(path)) {
                    try {
                        Files.newDirectoryStream(path).forEach(child -> {
                            if(Files.isDirectory(child)) {
                                if(!(child.getFileName().toString().equals("./")||child.getFileName().toString().equals("../"))){
                                    queue.add(child);
                                }
                            } else if(Files.isReadable(path)){
                                if(child.getFileName().toString().endsWith(TYPE_DATA)) {
                                    queue.add(child);
                                }
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (Files.isReadable(path)) {
                    if(path.getFileName().toString().endsWith(TYPE_DATA)) {
                        extractedContent(freezer, path);
                    }
                }
            }
        }

        return freezer;
    }

    private static String getName(Path child, String type) {
        String filename = child.getFileName().toString();
        return filename.substring(0, filename.length() - type.length());
    }

    private void extractedContent(Freezer freezer, Path path) throws IOException {
        final Path parent = path.getParent();

        String name = getName(path, TYPE_DATA);

        Directory directory = freezer.computeIfAbsent(parent.normalize().toString());

        Content content = directory.addContent(name);

        content.setData(Files.readAllBytes(path));

        Path typePath = parent.resolve(name + TYPE_DATATYPE);
        if(Files.exists(typePath)) content.setType(Datatypes.findByName(new String(Files.readAllBytes(typePath), UTF_8)).orElse(UNKNOWN));
        else content.setType(UNKNOWN);

        Path commentPath = parent.resolve(name + TYPE_COMMENT);
        if(Files.exists(commentPath)) content.setComment(new String(Files.readAllBytes(commentPath), UTF_8));

        Path futurPath = parent.resolve(name + TYPE_FUTURE);
        if(Files.exists(futurPath)) content.setFuture(Files.readAllBytes(futurPath));
    }

    //__________________________________________________________________________________________________________________
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    // util methods

    private Set<Content> listContent(Freezer root) {
        final Set<Content> result = new HashSet<>(root.getContentElements());

        final Deque<Directory> queue = new ConcurrentLinkedDeque<>(root.getDirectorys());

        while(!queue.isEmpty()) {
            Directory directory = queue.pop();
            queue.addAll(directory.getDirectorys());
            result.addAll(directory.getContentElements());
        }

        return result;
    }

    private void write(Path parent, String name, String type, byte[] data, Predicate<byte[]> predicate) throws IOException {
        write(parent, name, type, data, d -> d, predicate);
    }

    private <T> void write(Path parent, String name, String type, T data, Function<T, byte[]> converter, Predicate<T> predicate) throws IOException {
        final Path path = parent.resolve(name + type);
        if(data != null && predicate.test(data)) {
            Files.write(path, converter.apply(data), CREATE, WRITE, TRUNCATE_EXISTING);
        } else {
            Files.deleteIfExists(path);
        }
    }

}
