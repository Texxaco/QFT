package net.contexx.qft.junit.assertions;

import net.contexx.qft.junit.QFT;
import net.contexx.qft.freezer.model.Content;
import net.contexx.qft.freezer.model.Directory;

import java.util.Arrays;
import java.util.function.Function;

import static net.contexx.qft.freezer.model.Datatypes.*;
import static org.junit.jupiter.api.Assertions.fail;

public class QFTAsserts {

    public static <V> void assertEquals(String descriptor, V actual, Datatype<V> datatype, Function<V, byte[]> converter) {
        Directory directory = QFT.directory.get();
        Content content = directory.getContent(descriptor);
        byte[] actualAsByteArray = converter.apply(actual);
        if(content == null) {
            content = directory.addContent(descriptor);
            content.setData(actualAsByteArray);
            content.setType(datatype);
        } else {
            if(Arrays.equals(content.getData(), actualAsByteArray)) {
                content.setFuture(null);
            } else {
                if(QFT.accapted.get()) {
                    content.setData(actualAsByteArray);
                    content.setFuture(null);
                } else {
                    content.setFuture(actualAsByteArray);
                    FailUtil.failNotEqual(datatype.toString(content.getData()), datatype.toString(actualAsByteArray));
                }
            }
        }
    }

    //todo Message-Parameter einbauen

    public static <V> void assertEquals(String descriptor, V actual, Datatype<V> datatype) {
        assertEquals(descriptor, actual, datatype, datatype::toBytes);
    }

    public static void assertEquals(String descriptor, String actual) {
        assertEquals(descriptor, actual, STRING);
    }

    public static void assertEquals(String descriptor, Byte actual) {
        assertEquals(descriptor, actual, BYTE);
    }

    public static void assertEquals(String descriptor, Short actual) {
        assertEquals(descriptor, actual, SHORT);
    }

    public static void assertEquals(String descriptor, Integer actual) {
        assertEquals(descriptor, actual, INTEGER);
    }

    public static void assertEquals(String descriptor, Long actual) {
        assertEquals(descriptor, actual, LONG);
    }

    public static void assertEquals(String descriptor, Float actual) {
        assertEquals(descriptor, actual, FLOAT);
    }

    public static void assertEquals(String descriptor, Double actual) {
        assertEquals(descriptor, actual, DOUBLE);
    }

    public static void assertEquals(String descriptor, Boolean actual) {
        assertEquals(descriptor, actual, BOOLEAN);
    }

    public static void assertEquals(String descriptor, char actual) {
        assertEquals(descriptor, new String(new char[]{actual}), STRING);
    }

    //_________________________________________________________________________
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    // accessor

    private static <T> T accessValue(String descriptor, Datatype<T> datatype) {
        Directory directory = QFT.directory.get();
        Content content = directory.getContent(descriptor);
        if(content.getType() != datatype) fail("Type mismatch when accessing value of '"+descriptor+"'. Code demands "+datatype.getName()+", but actualy is "+content.getType().getName());
        return datatype.valueOf(content.getData());
    }

    public static String getStringValue(String descriptor) {
        return accessValue(descriptor, STRING);
    }

    public static Byte getByteValue(String descriptor) {
        return accessValue(descriptor, BYTE);
    }

    public static Short getShortValue(String descriptor) {
        return accessValue(descriptor, SHORT);
    }

    public static Integer getIntegerValue(String descriptor) {
        return accessValue(descriptor, INTEGER);
    }

    public static Long getLongValue(String descriptor) {
        return accessValue(descriptor, LONG);
    }

    public static Float getFloatValue(String descriptor) {
        return accessValue(descriptor, FLOAT);
    }

    public static Double getSDoubleValue(String descriptor) {
        return accessValue(descriptor, DOUBLE);
    }

    public static Boolean getBooleanValue(String descriptor) {
        return accessValue(descriptor, BOOLEAN);
    }
}
