package table;


import packet.IP;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Classe définissant une table ARP
 * @author J. Desvignes
 */
public class ARPTable implements Serializable
{
	/** Les IPs */
    private ArrayList<IP> ips = new ArrayList<>();
    
    /** Les MACs corrélées aux IPs */
    private ArrayList<Integer> macs = new ArrayList<>();

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