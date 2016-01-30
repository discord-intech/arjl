package table;

import java.util.ArrayList;

/**
 * Classe définissant les tables de commutation
 */
public class SwitchingTable
{
    /** Les MACs */
    private ArrayList<Integer> macs = new ArrayList<>();
    /** Les ports associés */
    private ArrayList<Integer> ports = new ArrayList<>();

    /**
     * Ajoute une règle
     */
    public synchronized void addRule(int mac, int port)
    {
        if(macs.contains(mac))
            return;
        macs.add(mac);
        ports.add(port);
    }

    /**
     * Cherche la mac dans sa table et renvoie le port correspondant
     * @param mac la mac
     * @return le port correspondant ; -1 si absente de la table
     */
    public int commute(int mac)
    {
        for(int i=0 ; i<macs.size() ; i++)
            if(macs.get(i).equals(mac))
                return ports.get(i);
        return -1;
    }

}