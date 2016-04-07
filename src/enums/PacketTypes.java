package enums;

/**
 * Enul des différents types de paquets avec le nombre nécéssaire pour effecteur une transaction et le temps (ms) avant timeout
 * ceux ayant "null" comme nom sont interdis d'utilisation par l'utilisateur
 * 1 packet = 1024 octets
 * @author J. Desvignes
 */
public enum PacketTypes
{
    NULL("null",1,0), //Placeholder
    DHCP("null",1,0),
    WEB("web",10,50),
    ARP("null",1,10),
    OSPF("null",3,15),
    RIP("null",2,15),
    FTP("ftp",2000,100);

    public final int size;
    public final int time;
    private final String name;
    PacketTypes(String name, int val, int time)
    {
        this.size = val;
        this.time = time;
        this.name = name;
    }

    public static PacketTypes getType(String text)
    {
        for(PacketTypes i : PacketTypes.values())
        {
            if(i.name.equals(text))
                return i;
        }
        return null;
    }

}