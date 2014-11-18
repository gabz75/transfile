package fr.upemlv.transfile.structures;

import java.nio.ByteBuffer;

import fr.upemlv.transfile.packets.TransfilePackets;

/**
 * 
 * Represents an IP Address such as 192.168.10.1
 * 
 * It is composed by 4 bytes.
 * 
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 *
 */
public class Address implements TransfilePackets
{

    /**
     * The first byte.
     */
    private final byte one;
    
    /**
     * The second byte
     */
    private final byte two;
    
    /**
     * The third byte 
     */
    private final byte three;
    
    /**
     * The last byte
     */
    private final byte four;
    
    /**
     * The pattern of an IP Address 
     */
    private final String pattern = "([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})";

    /**
     * Constructor
     * @param one the first byte
     * @param two the second byte
     * @param three the third byte
     * @param four the last byte
     */
    public Address(byte one, byte two, byte three, byte four)
    {
        this.one = one;
        this.two = two;
        this.three = three;
        this.four = four;
    }

    /**
     * Tests whether this Address is correct
     * @return true if the toString() method matches with the pattern, false otherwise.
     */
    public boolean isCorrect()
    {
        String ip = toString();
        return ip.matches(pattern);
    }

    @Override
    public byte[] buildDatas()
    {
        return new byte[] { one, two, three, four };
    }

    public static Address decode(ByteBuffer bbr)
    {
        return new Address(bbr.get(), bbr.get(), bbr.get(), bbr.get());
    }

    /**
     * Tests whether this Address is a MultiCast address,
     * in the range allowed for the author's group.
     * @return true if this Address represents a Multicast Address, false otherwise.
     */
    public boolean isMulticast()
    {
        int a = one & 0xff;
        return isCorrect() && a > 224 && a <= 234;
    }

    @Override
    public String toString()
    {
        int a = one & 0xff;
        int b = two & 0xff;
        int c = three & 0xff;
        int d = four & 0xff;
        return a + "." + b + "." + c + "." + d;
    }

}
