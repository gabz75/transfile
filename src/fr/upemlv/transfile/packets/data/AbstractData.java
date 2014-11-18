package fr.upemlv.transfile.packets.data;

import java.nio.ByteBuffer;

import fr.upemlv.transfile.enums.DatasEnum;
import fr.upemlv.transfile.packets.Datas;

public class AbstractData extends Datas
{
    private final DatasEnum datasCode;

    private int totalFragment;

    private final int numFragment;

    private final int id;

    private final int length;

    private final byte[] datas;

    protected AbstractData(DatasEnum dC, int total, int num, int id,
            int lenght, byte[] datas)
    {
        super(dC);
        datasCode = dC;
        this.length = lenght;
        this.totalFragment = total;
        this.numFragment = num;
        this.id = id;
        this.datas = datas;
    }

    public DatasEnum getDatasCode()
    {
        return datasCode;
    }

    public int getTotalFragment()
    {
        return totalFragment;
    }

    public int getNumFragment()
    {
        return numFragment;
    }

    public int getId()
    {
        return id;
    }

    public byte[] getDatas()
    {
        return datas;
    }

    @Override
    public byte[] buildDatas()
    {
        byte[] header = super.buildDatas();

        ByteBuffer bb = ByteBuffer.allocate(header.length + 4
                * (Integer.SIZE / 8) + datas.length);
        bb.put(header);
        bb.putInt(totalFragment);
        bb.putInt(numFragment);
        bb.putInt(id);
        bb.putInt(length);
        bb.put(datas);

        return bb.array();
    }
}