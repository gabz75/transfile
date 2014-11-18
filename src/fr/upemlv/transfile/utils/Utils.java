package fr.upemlv.transfile.utils;

import java.nio.ByteBuffer;
import java.util.Arrays;

import fr.upemlv.transfile.enums.TransfileEnums;
import fr.upemlv.transfile.exceptions.UncompletedPackageException;

/**
 * This class is design to store each usefull conversion method in public static method,
 * accessible and needed everywhere.
 * 
 * @author Gabriel Debeaupuis, Jeremy Foucault
 */
public class Utils
{
    /**
     * Merge two different array of byte, different size are allowed.
     * 
     * @param b1
     *            the first array
     * @param b2
     *            the second array
     * 
     * @return the merged array
     */
    public static byte[] mergeByteArray(byte[] b1, byte[] b2)
    {
        int l1 = b1.length;
        int l2 = b2.length;

        byte[] result = Arrays.copyOfRange(b1, 0, l1 + l2);

        for (int i = 0; i < b2.length; i++) {
            result[i + l1] = b2[i];
        }

        return result;
    }

    /**
     * Merge severals array of byte, different size are allowed
     * 
     * @param b1
     *            the first array
     * @param b2
     *            the others arrays
     * @return the merged array
     */
    public static byte[] mergeByteArrays(byte[] b1, byte[]... b2)
    {
        byte[] result = Arrays.copyOf(b1, b1.length);

        for (byte[] bs : b2) {
            result = mergeByteArray(result, bs);
        }

        return result;
    }

    /**
     * Return the enums corresponding to the byte arrays passed in arguments
     * 
     * @param transfileEnums
     * @param b
     * @return the enum match
     */
    public static TransfileEnums fetch(TransfileEnums[] transfileEnums, byte[] b)
    {
        for (TransfileEnums tEnum : transfileEnums) {
            if (Arrays.equals(tEnum.getBytes(), b)) {
                return tEnum;
            }
        }

        return null;
    }

    /**
     * Return the enums corresponding to the byte passed in arguments
     * 
     * @param transfileEnums
     * @param b
     * @return the enum match
     */
    public static <E> TransfileEnums fetch(TransfileEnums[] transfileEnums,
            byte b)
    {
        return fetch(transfileEnums, new byte[] { b });
    }

    /**
     * Return the enums corresponding to the byte arrays passed in arguments and
     * fetch with the Class of the enums
     * 
     * @param <E>
     * @param cEnums
     * @param b
     * @return the enum match
     */
    public static <E> TransfileEnums fetch(
            Class<? extends TransfileEnums> cEnums, byte[] b)
    {
        if (!cEnums.isEnum()) {
            return null;
        }
        return fetch(cEnums.getEnumConstants(), b);
    }

    /**
     * Return the enums corresponding to the byte passed in arguments and fetch
     * with the Class of the enums
     * 
     * @param <E>
     * @param cEnums
     * @param b
     * @return the enum match
     */
    public static <E> TransfileEnums fetch(
            Class<? extends TransfileEnums> cEnums, byte b)
    {
        return fetch(cEnums, new byte[] { b });
    }

    /**
     * Decode a string from the given ByteBuffer
     * 
     * Warning the delimiter 0 at the end of the string is also read.
     * 
     * @param bbr
     * @throws UncompletedPackageException
     * @return the string decodded
     */
    public static byte[] decodeString(ByteBuffer bbr)
            throws UncompletedPackageException
    {
        byte read;

        int start = bbr.position();

        while (bbr.hasRemaining()) {
            read = bbr.get();
            if (read == 0) {
                break;
            }
            if (read != 0 && !bbr.hasRemaining()) {
                throw new UncompletedPackageException(
                        "String must finish by a 0 byte delimiter");
            }
        }

        int end = bbr.position() - 1;
        int length = end - start;

        if (length < 0) {
            throw new UncompletedPackageException(
                    "String expected, nothing found");
        }

        byte[] buf = new byte[length];

        bbr.position(start);
        bbr.get(buf, 0, length);
        bbr.position(end + 1);

        return buf;
    }
}
