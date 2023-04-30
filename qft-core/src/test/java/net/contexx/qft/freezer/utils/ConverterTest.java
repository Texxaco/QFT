package net.contexx.qft.freezer.utils;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConverterTest {
    @Test
    void byteReadWriteCompatibility() {
        byte[] bytes = Converter.toBytes(Byte.MAX_VALUE);
        int result = Converter.toByte(bytes);
        assertEquals(Byte.MAX_VALUE, result);
    }

    @Test
    void charReadWriteCompatibility() {
        final char given = 'Z';

        byte[] bytes = Converter.toBytes(given);
        char result = Converter.toCharacter(bytes);
        assertEquals(given, result);
    }


    @Test
    void shortReadWriteCompatibility() {
        byte[] bytes = Converter.toBytes(Short.MAX_VALUE);
        short result = Converter.toShort(bytes);
        assertEquals(Short.MAX_VALUE, result);
    }

    @Test
    void intReadWriteCompatibility() {
        byte[] bytes = Converter.toBytes(Integer.MAX_VALUE);
        int result = Converter.toInt(bytes);
        assertEquals(Integer.MAX_VALUE, result);
    }

    @Test
    void longReadWriteCompatibility() {
        byte[] bytes = Converter.toBytes(Long.MAX_VALUE);
        long result = Converter.toLong(bytes);
        assertEquals(Long.MAX_VALUE, result);
    }

    @Test
    void floatReadWriteCompatibility() {
        byte[] bytes = Converter.toBytes(Float.MAX_VALUE);
        float result = Converter.toFloat(bytes);
        assertEquals(Float.MAX_VALUE, result);
    }

    @Test
    void doubleReadWriteCompatibility() {
        byte[] bytes = Converter.toBytes(Double.MAX_VALUE);
        double result = Converter.toDouble(bytes);
        assertEquals(Double.MAX_VALUE, result);
    }

    @Test
    void bigDecimalReadWriteCompatibility() {
        final BigDecimal given = new BigDecimal(Long.MAX_VALUE).subtract(BigDecimal.valueOf(1 / 1000000));

        byte[] bytes = Converter.toBytes(given);
        BigDecimal result = Converter.toBigDecimal(bytes);
        assertEquals(given, result);
    }
}