package packet;

import enums.PacketTypes;

public class Packet
{
    private IP dst_addr;
    private IP dst_mask;

    private IP src_addr;
    private IP src_mask;

    private PacketTypes type;

    public Packet(IP dst_addr, IP dst_mask, IP src_addr, IP src_mask, PacketTypes type)
    {
        this.dst_addr=dst_addr;
        this.dst_mask=dst_mask;
        this.src_addr=src_addr;
        this.src_mask=src_mask;
        this.type=type;
    }
}