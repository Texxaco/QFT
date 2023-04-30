package net.contexx.qft.freezer.model;

import net.contexx.qft.freezer.utils.Converter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.contexx.qft.freezer.utils.Converter.*;

public class Datatypes {
    public static final Datatype<String> STRING = new Datatype<>(
            "String",
            bytes -> new String(bytes, UTF_8),
            str   -> str.getBytes(UTF_8)
    );

    public static final Datatype<Byte> BYTE = new Datatype<>(
            "Byte",
            Converter::toByte,
            Converter::toBytes
    );

    public static final Datatype<Character> CHARACTER = new Datatype<>(
            "Character",
            Converter::toCharacter,
            Converter::toBytes
    );

    public static final Datatype<Short> SHORT = new Datatype<>(
            "Short",
            Converter::toShort,
            Converter::toBytes
    );

    public static final Datatype<Integer> INTEGER = new Datatype<>(
            "Integer",
            Converter::toInt,
            Converter::toBytes
    );

    public static final Datatype<Long> LONG = new Datatype<>(
            "Long",
            Converter::toLong,
            Converter::toBytes
    );

    public static final Datatype<Float> FLOAT = new Datatype<>(
            "Float",
            Converter::toFloat,
            Converter::toBytes
    );

    public static final Datatype<Double> DOUBLE = new Datatype<>(
            "Double",
            Converter::toDouble,
            Converter::toBytes
    );

    @SuppressWarnings("SimplifiableConditionalExpression")
    public static final Datatype<Boolean> BOOLEAN = new Datatype<>(
            "Boolean",
            bytes -> bytes.length > 0 && bytes[0] > 0 ? true : false,
            val   -> val ? new byte[]{1} : new byte[]{0}
    );

    public static final Datatype<Object> UNKNOWN = new Datatype<>(
            "Unknown",
            bytes -> "<uncategorized data>",
            str -> new byte[0]
    );

    public static Optional<Datatype<?>> findByName(String name) {
        return Datatype.findByName(name);
    }

    public static class Datatype<T> {
        private static final Map<String, Datatype<?>> register = new ConcurrentHashMap<>();

        private final String name;
        private final Function<byte[], T> mapperFrom;
        private final Function<T, byte[]> mapperTo;

        Datatype(String name, Function<byte[], T> mapperFrom, Function<T, byte[]> mapperTo) {
            this.name = name;
            this.mapperFrom = mapperFrom;
            this.mapperTo = mapperTo;
            register.put(name, this);
        }

        public String getName() {
            return name;
        }

        public String toString(byte[] rawData) {
            return mapperFrom.apply(rawData).toString();
        }

        static Optional<Datatype<?>> findByName(String name) {
            return Optional.ofNullable(register.get(name));
        }

        public byte[] toBytes(T value) {
            return mapperTo.apply(value);
        }

        public T valueOf(byte[] rawData) {
            return mapperFrom.apply(rawData);
        }
    }
}
