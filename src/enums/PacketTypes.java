package enums;

/**
 * Enul des différents types de paquets avec le nombre nécéssaire pour effecteur une transaction et le temps avant timeout
 */
public enum PacketTypes
{
    NULL(1,0), //Placeholder
    DHCP(1,0),
    WEB(5,50),
    ARP(1,10),
    OSPF(3,15),
    RIP(2,15),
    FTP(50,100);

    public final int size;
    public final int time;
    PacketTypes(int val, int time)
    {
        this.size = val;
        this.time = time;
    }

}