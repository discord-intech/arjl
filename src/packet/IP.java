package packet;

/**
 * Classe servant de template pour les adresses IPv4
 */

public class IP
{

    /** Les différents octets */
    public final int o1;
    public final int o2;
    public final int o3;
    public final int o4;


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

    /**
     * Vérifie que l'adresse fournie est plus grande que celle-ci
     * @param other l'adresse à comparer
     */
    public boolean isSmallerThan(IP other)
    {
        if(other.o1 > this.o1)
            return true;
        else if (other.o1 < this.o2)
            return false;
        else //égalité
        {
            if(other.o2 > this.o2)
                return true;
            else if (other.o2 < this.o2)
                return false;
            else //égalité
            {
                if(other.o3 > this.o3)
                    return true;
                else if (other.o3 < this.o3)
                    return false;
                else //égalité
                {
                    if(other.o4 > this.o4)
                        return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o)
    {
        if(o instanceof IP)
        {
            IP other = (IP) o;
            return ((this.o1 == other.o1) && (this.o2 == other.o2) && (this.o3 == other.o3) && (this.o4 == other.o4));
        }
        return false;
    }

    @Override
    public String toString()
    {
        return o1+"."+o2+"."+o3+"."+o4;
    }

    /**
     * Vérifie s'il s'agit d'une IP de broadcast
     * @param mask masque du sous-réseau
     */
    public boolean isBroadcast(IP mask)
    {
        return ((o1 | mask.o1) & (o2 | mask.o2) & (o3 | mask.o3) & (o4 | mask.o4)) == 255;
    }
}