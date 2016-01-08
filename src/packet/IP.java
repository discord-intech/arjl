package packet;

/**
 * Classe servant de template pour les adresses IPv4
 */

public class IP
{

    protected int o1;
    protected int o2;
    protected int o3;
    protected int o4;


    public IP(int o1, int o2, int o3, int o4)
    {

        this.o1 = o1;
        this.o2 = o2;
        this.o3 = o3;
        this.o4 = o4;

    }

    /**
     * Renvoie le sous-réseau avec le masque fourni
     * @param address le masque
     * @return l'IP du sous-réseau
     */
    public IP getSubnet(IP address)
    {
        return new IP(address.o1 & this.o1,
                address.o2 & this.o2,
                address.o3 & this.o3,
                address.o4 & this.o4);
    }

    public boolean equals(IP other)
    {
        return ( (this.o1 == other.o1) && (this.o2 == other.o2) && (this.o3 == other.o3) && (this.o4 == other.o4) );
    }

}