package table;


import packet.IP;

import java.util.ArrayList;

public class ARPTable
{
    ArrayList<IP> ips = new ArrayList<>();
    ArrayList<Integer> macs = new ArrayList<>();

    public synchronized void addRule(IP address, int mac)
    {
        if(macs.contains(mac))
            return;
        ips.add(address);
        macs.add(mac);
    }

    /**
     * Recherche l'adresse MAC dans sa base de données
     * @param address l'adresse IP à rechercher
     * @return l'adresse MAC ; -1 si non trouvée
     */
    public int findARP(IP address)
    {
        for(int i=0 ; i < ips.size() ; i++)
        {
            if(ips.get(i).equals(address))
                return macs.get(i);
        }
        return -1;
    }
}