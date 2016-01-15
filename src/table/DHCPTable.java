package table;


import packet.IP;

import java.util.ArrayList;

public class DHCPTable
{
    ArrayList<IP> subnets = new ArrayList<IP>();
    ArrayList<IP[]> ranges = new ArrayList<IP[]>();

    public DHCPTable()
    {

    }

    public IP[] gimmeARange(IP subnet, IP mask)
    {
        if(!subnets.contains(subnet)) //Si la demande est d'un sous-réseau non couvert par le serveur
        {
            IP[] range = new IP[2];
            range[0] = new IP(0,0,0,0);
            range[1] = new IP(0,0,0,0);
            return range;
        }

        return ranges.get(subnets.indexOf(subnet));
    }

    public void addRange(IP subnet, IP[] range)
    {
        this.subnets.add(subnet);
        this.ranges.add(range);
    }

}