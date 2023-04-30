package net.contexx.qft.freezer.model;

import net.contexx.qft.freezer.model.Datatypes.Datatype;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Content {

    Content(Directory parentDirectory, String name) {
        patentDirectory = parentDirectory;
        this.name = name;
    }

    //__________________________________________________________________________________________________________________
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    // attributes

    //__________________________________________________________________________________________________________
    // parent directory

    private Directory patentDirectory;

    public Directory getParent() {
        return patentDirectory;
    }

//    protected void detach() {
//        patentDirectory = null;
//    }

    //__________________________________________________________________________________________________________
    // name

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String aName) {
        name = aName;
    }

    //__________________________________________________________________________________________________________
    // name


    private Datatype type;

    public Datatype getType() {
        return type;
    }

    public void setType(Datatype type) {
        this.type = type;
    }

    //__________________________________________________________________________________________________________
    // content

    private byte[] data = new byte[0];

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = (data != null) ? data : new byte[0];
    }

    //__________________________________________________________________________________________________________
    // comment

    private String comment = null;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    //__________________________________________________________________________________________________________
    // future

    private byte[] future;

    public byte[] getFuture() {
        return future;
    }

    public void setFuture(byte[] future) {
        this.future = future;
    }

    //__________________________________________________________________________________________________________________
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    // path convenience

    public String toPath() {
        return patentDirectory.toPath() + Directory.DELIMITER + getName();
    }

    //__________________________________________________________________________________________________________________
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    // equals, hashcode and toString


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Content)) return false;
        Content content = (Content) o;
        return name.equals(content.name) && Arrays.equals(data, content.data) && Objects.equals(comment, content.comment);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name, comment);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }
}
