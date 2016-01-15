package packet;

import java.util.ArrayList;

/**
 * Classe servant de template pour les adresses IPv4
 */

public class IP
{

    public int o1;
    public int o2;
    public int o3;
    public int o4;


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
        if(other.o2 > this.o2)
            return true;
        if(other.o3 > this.o3)
            return true;
        if(other.o4 > this.o4)
            return true;
        return false;
    }

    public static ArrayList<IP> getSubnetIPs(IP subnet, IP mask)
    {
        ArrayList<IP> res = new ArrayList<>();

        for(int i=0 ; i<(256-(subnet.o1 | mask.o1)) ; i++)
            for(int j=0 ; i<(256-(subnet.o2 | mask.o2)) ; i++)
                for(int k=0 ; i<(256-(subnet.o3 | mask.o3)) ; i++)
                    for(int l=0 ; i<(256-(subnet.o4 | mask.o4)) ; i++)
                        res.add((new IP(subnet.o1+i,subnet.o2+j,subnet.o3+k,subnet.o4+l)));

        return res;


    }

    @Override
    public boolean equals(Object o)
    {
        IP other = (IP)o;
        return ( (this.o1 == other.o1) && (this.o2 == other.o2) && (this.o3 == other.o3) && (this.o4 == other.o4) );
    }

    @Override
    public String toString()
    {
        return o1+"."+o2+"."+o3+"."+o4;
    }


    public boolean isBroadcast(IP mask)
    {
        if(((o1 | mask.o1)&(o2 | mask.o2)&(o3 | mask.o3)&(o4 | mask.o4)) == 255)
            return true;
        return false;
    }
}