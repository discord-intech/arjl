package enums;

public enum PacketTypes
{
    NULL(1), //Placeholder
    DHCP(1),
    WEB(5),
    ARP(1),
    OSPF(3),
    RIP(2),
    FTP(20);

    public int size;
    private PacketTypes(int val)
    {
        this.size = val;
    }

}