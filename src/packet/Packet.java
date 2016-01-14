package packet;

import enums.PacketTypes;
import packet.data.DHCPData;

import java.util.Random;

public class Packet
{
    public IP dst_addr;
    public int dst_mac;

    public IP src_addr;
    public int src_mac;
    private int TTL=128;

    public static final Random RNG = new Random();

    private IP NHR;
    public boolean tracked = false;

    private Object data; // Permet de stocker une objet dans le paquet

    /**
     * Permet au système de traitement des appareils de savoir par quel port est entré ce paquet (fictif, juste utile
     * pour la redirection et le broadcast)
     */
    public int lastPort;

    private PacketTypes type;
    public boolean isResponse;

    public Packet(IP dst_addr, IP src_addr, int src_mac, int dst_mac, PacketTypes type, boolean isResponse)
    {
        this.dst_addr=dst_addr;
        this.src_addr=src_addr;
        this.dst_mac=dst_mac;
        this.src_mac=src_mac;
        this.type=type;
        this.isResponse = isResponse;
        this.NHR = src_addr;

        if(type == PacketTypes.DHCP)
            this.data = new DHCPData(Packet.RNG.nextInt(65533), src_mac);
    }

    public Packet(Packet p)
    {
        this.dst_addr=p.dst_addr;
        this.src_addr=p.src_addr;
        this.dst_mac=p.dst_mac;
        this.src_mac=p.src_mac;
        this.type=p.type;
        this.isResponse = p.isResponse;
        this.NHR = p.getNHR();
        this.tracked=p.tracked;
        this.data=p.getData();
        this.TTL = p.getTTL();

    }

    public Packet(IP dst_addr, IP src_addr, int src_mac, int dst_mac, PacketTypes type, boolean isResponse, boolean tracked)
    {
        this.dst_addr=dst_addr;
        this.src_addr=src_addr;
        this.dst_mac=dst_mac;
        this.src_mac=src_mac;
        this.type=type;
        this.isResponse = isResponse;
        this.NHR = src_addr;
        this.tracked=tracked;

        if(type == PacketTypes.DHCP)
            this.data = new DHCPData(Packet.RNG.nextInt(65533), src_mac);
    }

    public Packet(Packet p, boolean tracked)
    {
        this.dst_addr=p.dst_addr;
        this.src_addr=p.src_addr;
        this.dst_mac=p.dst_mac;
        this.src_mac=p.src_mac;
        this.type=p.type;
        this.isResponse = p.isResponse;
        this.NHR = p.getNHR();
        this.tracked=tracked;
        this.data=p.getData();
        this.TTL = p.getTTL();
    }

    public PacketTypes getType() {
        return type;
    }

    public IP getNHR() {
        return NHR;
    }

    public void setNHR(IP nhr)
    {
        this.NHR = nhr;
    }

    public int getTTL()
    {
        return TTL;
    }

    public boolean TTLdown()
    {
        TTL--;
        if(TTL == 0)
            return true;
        return false;
    }


    public Object getData()
    {
        return this.data;
    }

    public void setData(Object data)
    {
        this.data = data;
    }

}