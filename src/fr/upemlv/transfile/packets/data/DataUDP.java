package fr.upemlv.transfile.packets.data;

import java.nio.ByteBuffer;

import fr.upemlv.transfile.enums.DatasEnum;
import fr.upemlv.transfile.exceptions.UncompletedPackageException;
import fr.upemlv.transfile.utils.Utils;

public class DataUDP extends AbstractData
{
    public DataUDP(int total, int number, int id, int length, byte[] datas)
    {
        super(DatasEnum.UDP, total, number, id, length, datas);
    }

    public static DataUDP decode(ByteBuffer bbr)
            throws UncompletedPackageException
    {
        int nbFragment = bbr.getInt();
        int number = bbr.getInt();
        int id = bbr.getInt();
        int length = bbr.getInt();
        byte[] datas = Utils.decodeString(bbr);

        return new DataUDP(nbFragment, number, id, length, datas);
    }

    @Override
    public String toString()
    {
        return "DataUDP totalSize : " + getTotalFragment() + ", number : "
                + getNumFragment() + ", id : " + getId();
    }
}
