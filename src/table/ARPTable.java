package table;


import packet.IP;

import java.util.ArrayList;

/**
 * Classe définissant une table ARP
 */
public class ARPTable
{
	/** Les IPs */
    ArrayList<IP> ips = new ArrayList<>();
    
    /** Les MACs corrélées aux IPs */
    ArrayList<Integer> macs = new ArrayList<>();

    /**
     * Ajoute une règle ARP
     * @param address l'adresse IP
     * @param mac la MAC associée
     */
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