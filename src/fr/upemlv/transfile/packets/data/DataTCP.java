package fr.upemlv.transfile.packets.data;

import java.nio.ByteBuffer;

import fr.upemlv.transfile.enums.DatasEnum;
import fr.upemlv.transfile.exceptions.UncompletedPackageException;

public class DataTCP extends AbstractData
{

    public DataTCP(int total, int number, int id, int length, byte[] datas)
    {
        super(DatasEnum.TCP, total, number, id, length, datas);
    }

    public static DataTCP decode(ByteBuffer bbr)
            throws UncompletedPackageException
    {
        int nbFragment = bbr.getInt();
        int number = bbr.getInt();
        int id = bbr.getInt();
        int length = bbr.getInt();
        byte[] datas = new byte[length];
        for (int i = 0; i < length; i++) {
            datas[i] = bbr.get();
        }

        return new DataTCP(nbFragment, number, id, length, datas);
    }

    @Override
    public String toString()
    {
        return "DataTCP totalSize : " + getTotalFragment() + ", number : "
                + getNumFragment() + ", id : " + getId();
    }

}
