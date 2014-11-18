package fr.upemlv.transfile.packets;

/**
 * This interface define the method to construct the data for each TrasnfilePackets
 * 
 * @author Gabriel Debeaupuis, Jeremy Foucault
 */
public interface TransfilePackets
{
    /**
     * Convert the field of the concrete classes to a byte Array used to send on the network. 
     * @return the byte array of the concrete fields 
     */
    public byte[] buildDatas();
}
