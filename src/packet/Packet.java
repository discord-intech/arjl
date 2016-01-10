package packet;

import enums.PacketTypes;

public class Packet
{
    public IP dst_addr;
    public IP dst_mask;
    public int dst_mac;

    public IP src_addr;
    public IP src_mask;
    public int src_mac;

    /**
     * Permet au système de traitement des appareils de savoir par quel port est entré ce paquet (fictif, juste utile
     * pour le broadcast)
     */
    public int lastPort;

    private PacketTypes type;
    public boolean isResponse;

    public Packet(IP dst_addr, IP dst_mask, IP src_addr, IP src_mask, int src_mac, int dst_mac, PacketTypes type, boolean isResponse)
    {
        this.dst_addr=dst_addr;
        this.dst_mask=dst_mask;
        this.src_addr=src_addr;
        this.src_mask=src_mask;
        this.dst_mac=dst_mac;
        this.src_mac=src_mac;
        this.type=type;
        this.isResponse = isResponse;
    }

    public Packet(Packet p)
    {
        this.dst_addr=p.dst_addr;
        this.dst_mask=p.dst_mask;
        this.src_addr=p.src_addr;
        this.src_mask=p.src_mask;
        this.dst_mac=p.dst_mac;
        this.src_mac=p.src_mac;
        this.type=p.type;
        this.isResponse = p.isResponse;
    }

    public PacketTypes getType() {
        return type;
    }
}