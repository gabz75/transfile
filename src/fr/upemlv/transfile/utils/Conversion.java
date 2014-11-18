package fr.upemlv.transfile.utils;

/**
 * This class is design to store each conversion method in public static method,
 * accessible everywhere.
 * 
 * @author Gabriel Debeaupuis, Jeremy Foucault
 */
public class Conversion
{
    /**
     * Transform an int into a 4 lenght byte array
     * 
     * @param n
     * @return the int converted in byte array
     */
    public static byte[] intToByte(int n)
    {
        byte[] byteArray = new byte[4];
        for (int i = 0; i < 4; i++) {
            byteArray[i] = (byte) (n >> (i * 8));
        }

        return byteArray;
    }
}
