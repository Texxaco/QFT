package net.contexx.qft.freezer.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.nio.ByteBuffer;

public class Converter {
    public static BigDecimal toBigDecimal(byte[] bytes) {
        final ByteBuffer buffer = ByteBuffer.wrap(bytes);
        int scale = toInt(buffer);
        int precision = toInt(buffer);
        byte[] intValBytes = new byte[buffer.remaining()];
        buffer.get(intValBytes);
        return new BigDecimal(new BigInteger(intValBytes), scale, new MathContext(precision));
    }

    public static byte[] toBytes(BigDecimal value) {
        byte[] scale = toBytes(value.scale());
        byte[] precision = toBytes(value.precision());
        byte[] intVal = value.toBigIntegerExact().toByteArray();
        ByteBuffer result = ByteBuffer.allocate(scale.length + precision.length + intVal.length);
        result.put(scale).put(precision).put(intVal);
        return result.array();
    }

    public static char toCharacter(byte[] data) {
        return (char)toByte(data);
    }

    public static byte[] toBytes(char value) {
        return toBytes((byte)value);
    }

    public static byte toByte(byte[] data) {
        return data[0];
    }

    public static byte[] toBytes(byte value) {
        return new byte[] {value};
    }

    public static short toShort(byte[] data) {
        short result = 0;
        for (int i = 0; i < 2; i++) {
            result |= ((data[data.length - i - 1] & 0xFF) << (i * 8));
        }
        return result;
    }

    public static byte[] toBytes(short value) {
        return new byte[] {
                (byte)(value >>> 8),
                (byte)value};
    }

    private static int toInt(ByteBuffer buffer) {
        byte[] data = new byte[4];
        buffer.get(data);
        return toInt(data);
    }

    public static int toInt(byte[] data) {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            result |= ((data[data.length - i - 1] & 0xFF) << (i * 8));
        }
        return result;
    }

    public static byte[] toBytes(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }

    public static long toLong(byte[] data) {
        long result = 0;
        for (int i = 0; i < 8; i++) {
            result <<= Byte.SIZE;
            result |= (data[i] & 0xFF);
        }
        return result;
    }

    public static byte[] toBytes(long value) {
        return new byte[] {
                (byte)(value >>> 56),
                (byte)(value >>> 48),
                (byte)(value >>> 40),
                (byte)(value >>> 32),
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }

    public static float toFloat(byte[] data) {
        return Float.intBitsToFloat(toInt(data));
    }

    public static byte[] toBytes(float value) {
        return toBytes(Float.floatToIntBits(value));
    }

    public static double toDouble(byte[] data) {
        return Double.longBitsToDouble(toLong(data));
    }

    public static byte[] toBytes(double value) {
        return toBytes(Double.doubleToLongBits((value)));
    }
}
